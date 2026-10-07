package com.example.lab1.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/** Command-line desktop client; requires only the Java standard library. */
public class DesktopClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2
                || !("1".equals(args[0]) || "2".equals(args[0]))) {
            System.err.println("Usage: DesktopClient <1|2> [controller URL]");
            System.exit(1);
        }
        String endpoint = args.length == 2 ? args[1]
                : "http://localhost:8080/LAB1-1.0-SNAPSHOT/controller";
        HttpURLConnection connection = (HttpURLConnection) URI.create(endpoint).toURL().openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Accept", "text/plain");
            connection.setRequestProperty("User-Agent", "Lab1-DesktopClient/1.0");
            connection.setRequestProperty("Accept-Language", "en, ro;q=0.9");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setDoOutput(true);
            byte[] body = ("value=" + args[0]).getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(body.length);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            int status = connection.getResponseCode();
            InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            StringBuilder result = new StringBuilder();
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
            }
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException("HTTP " + status + ": " + result);
            }
            System.out.println(result);
        } finally {
            connection.disconnect();
        }
    }
}
