package com.example.lab1;

import com.example.lab1.client.DesktopClient;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
    private String forwarded;
    private String contentType;
    private int status = 200;
    private String log;
    private final StringWriter body = new StringWriter();

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private HttpServletRequest request(String[] values, String accept, String method) {
        ServletContext context = proxy(ServletContext.class, (p, m, a) -> {
            if (m.getName().equals("log")) log = (String) a[0];
            return null;
        });
        return proxy(HttpServletRequest.class, (p, m, a) -> {
            switch (m.getName()) {
                case "getParameterValues": return values;
                case "getMethod": return method;
                case "getRemoteAddr": return "127.0.0.1";
                case "getServletContext": return context;
                case "getParameterMap": return values == null ? Collections.emptyMap()
                        : Collections.singletonMap("value", values);
                case "getHeader":
                    switch ((String) a[0]) {
                        case "Accept": return accept;
                        case "User-Agent": return "Lab1-Test\r\nclient";
                        case "Accept-Language": return "en, ro;q=0.9";
                        default: return null;
                    }
                case "getRequestDispatcher":
                    String path = (String) a[0];
                    return proxy(RequestDispatcher.class, (d, dm, da) -> {
                        if (dm.getName().equals("forward")) forwarded = path;
                        return null;
                    });
                default: return null;
            }
        });
    }

    private HttpServletResponse response() {
        return proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setStatus": status = (Integer) a[0]; break;
                case "setContentType": contentType = (String) a[0]; break;
                case "getWriter": return new PrintWriter(body);
                default: break;
            }
            return null;
        });
    }

    @Test
    void browserPostForwardsPage1() throws Exception {
        new HelloServlet().doPost(request(new String[]{"1"}, "text/html", "POST"), response());
        assertEquals("/page1.html", forwarded);
        assertEquals("", body.toString());
    }

    @Test
    void browserGetForwardsPage2() throws Exception {
        new HelloServlet().doGet(request(new String[]{"2"}, null, "GET"), response());
        assertEquals("/page2.html", forwarded);
    }

    @Test
    void desktopReceivesOnlyParameterForBothValues() throws Exception {
        for (String value : new String[]{"1", "2"}) {
            body.getBuffer().setLength(0);
            new HelloServlet().doPost(request(new String[]{value}, "text/plain", "POST"), response());
            assertEquals(value, body.toString());
            assertEquals("text/plain; charset=UTF-8", contentType);
            assertNull(forwarded);
        }
    }

    @Test
    void missingValueIsRejected() throws Exception {
        assertInvalid(null);
    }

    @Test
    void unsupportedValueIsRejected() throws Exception {
        assertInvalid(new String[]{"3"});
    }

    @Test
    void repeatedValueIsRejected() throws Exception {
        assertInvalid(new String[]{"1", "2"});
    }

    private void assertInvalid(String[] values) throws Exception {
        new HelloServlet().doGet(request(values, "text/plain", "GET"), response());
        assertEquals(400, status);
        assertNull(forwarded);
        assertEquals("Select exactly one value: 1 or 2.", body.toString());
    }

    @Test
    void filterLogsAllRequiredFieldsAndContinuesRequest() throws Exception {
        boolean[] continued = {false};
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            new RequestLoggingFilter().doFilter(request(new String[]{"2"}, "text/plain", "POST"),
                    response(), (req, res) -> continued[0] = true);
        } finally {
            System.setOut(original);
        }
        log = captured.toString(StandardCharsets.UTF_8).stripTrailing();
        assertTrue(continued[0]);
        assertTrue(log.contains("method=POST"));
        assertTrue(log.contains("ip=127.0.0.1"));
        assertTrue(log.contains("user-agent=Lab1-Test  client"));
        assertTrue(log.contains("languages=en, ro;q=0.9"));
        assertTrue(log.contains("parameters={value=[2]}"));
        assertFalse(log.contains("\n"));
        assertFalse(log.contains("\r"));
    }

    @Test
    void desktopClientSendsHttpPostAndPrintsTextResponse() throws Exception {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> accept = new AtomicReference<>();
        AtomicReference<String> parameter = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/controller", exchange -> {
            method.set(exchange.getRequestMethod());
            accept.set(exchange.getRequestHeaders().getFirst("Accept"));
            parameter.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] result = "2".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, result.length);
            try (java.io.OutputStream output = exchange.getResponseBody()) {
                output.write(result);
            }
        });
        server.start();
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            DesktopClient.main(new String[]{"2", "http://127.0.0.1:"
                    + server.getAddress().getPort() + "/controller"});
        } finally {
            System.setOut(original);
            server.stop(0);
        }
        assertEquals("POST", method.get());
        assertEquals("text/plain", accept.get());
        assertEquals("value=2", parameter.get());
        assertEquals("2", captured.toString(StandardCharsets.UTF_8).trim());
    }
}

