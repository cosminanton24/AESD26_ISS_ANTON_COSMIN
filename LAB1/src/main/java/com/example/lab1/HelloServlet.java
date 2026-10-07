package com.example.lab1;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "controllerServlet", urlPatterns = {"/controller", "/hello-servlet"})
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleSelection(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        handleSelection(request, response);
    }

    private void handleSelection(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] values = request.getParameterValues("value");
        if (values == null || values.length != 1
                || !("1".equals(values[0]) || "2".equals(values[0]))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().print("Select exactly one value: 1 or 2.");
            return;
        }

        // Desktop clients explicitly request the plain-text representation.
        String accept = request.getHeader("Accept");
        boolean plainText = false;
        if (accept != null) {
            for (String mediaType : accept.split(",")) {
                if ("text/plain".equalsIgnoreCase(mediaType.trim())) {
                    plainText = true;
                    break;
                }
            }
        }
        if (plainText) {
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().print(values[0]);
        } else {
            request.getRequestDispatcher("/page" + values[0] + ".html").forward(request, response);
        }
    }
}
