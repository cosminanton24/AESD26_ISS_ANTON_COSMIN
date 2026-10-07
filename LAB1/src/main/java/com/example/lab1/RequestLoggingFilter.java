package com.example.lab1;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter(filterName = "requestLoggingFilter", urlPatterns = "/*")
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        HttpServletRequest http = (HttpServletRequest) request;
        StringJoiner parameters = new StringJoiner(", ", "{", "}");
        for (Map.Entry<String, String[]> entry : http.getParameterMap().entrySet()) {
            parameters.add(entry.getKey() + "=" + Arrays.toString(entry.getValue()));
        }
        System.out.println("Request: method=" + safe(http.getMethod())
                + " ip=" + safe(http.getRemoteAddr())
                + " user-agent=" + safe(http.getHeader("User-Agent"))
                + " languages=" + safe(http.getHeader("Accept-Language"))
                + " parameters=" + safe(parameters.toString()));
        chain.doFilter(request, response);
    }

    private static String safe(String value) {
        return value == null ? "(not supplied)" : value.replace('\r', ' ').replace('\n', ' ');
    }
}

