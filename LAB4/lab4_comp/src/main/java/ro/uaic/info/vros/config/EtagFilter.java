package ro.uaic.info.vros.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.io.IOException;
import java.util.Collections;

// Intercepteaza raspunsurile API pentru a permite verificarea versiunii prin ETag.
@Component
public class EtagFilter extends OncePerRequestFilter {
    // Se aplica doar cererilor GET catre /api/.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getMethod().equals("GET") || !request.getServletPath().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Retine temporar corpul raspunsului produs de controller.
        var cached = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, cached);

        // Calculeaza eticheta numai pentru raspunsurile reusite, cu status 200.
        if (cached.getStatus() == HttpServletResponse.SC_OK) {
            // ETag-ul depinde de corpul efectiv: JSON si XML au etichete diferite.
            String etag = "\"" + DigestUtils.md5DigestAsHex(cached.getContentAsByteArray()) + "\"";
            cached.setHeader(HttpHeaders.ETAG, etag);
            // Valoarea * verifica daca resursa exista deja.
            boolean wildcard = Collections.list(request.getHeaders(HttpHeaders.IF_NONE_MATCH))
                    .stream().anyMatch(value -> value.trim().equals("*"));
            // Spring verifica si etichete slabe (W/) sau liste de etichete.
            boolean unchanged = wildcard || new ServletWebRequest(request, cached).checkNotModified(etag);
            // Daca reprezentarea este neschimbata, returneaza 304 fara corp.
            if (unchanged) {
                cached.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                cached.resetBuffer();
                cached.setHeader(HttpHeaders.CONTENT_LENGTH, null);
            }
        }
        // Trimite catre client raspunsul final, cu sau fara corp.
        cached.copyBodyToResponse();
    }
}
