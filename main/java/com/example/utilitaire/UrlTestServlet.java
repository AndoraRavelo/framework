package com.example.utilitaire;

import com.example.annotations.AnnotationReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet pour tester la recherche d'URLs et leurs mappings
 */
@WebServlet(urlPatterns = {"/testUrl", "/testUrl/*"})
public class UrlTestServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialiser le système de mapping au démarrage du servlet
        System.out.println("=== Initialisation du servlet de test d'URL ===");
        AnnotationReader.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.trim().isEmpty() && !"/".equals(pathInfo)) {
            String urlToSearch = pathInfo.startsWith("/") ? pathInfo : "/" + pathInfo;

            MappingInfo mapping = AnnotationReader.findMappingByUrl(urlToSearch);

            request.setAttribute("searchUrl", urlToSearch);
            request.setAttribute("found", mapping.isFound());

            if (mapping.isFound()) {
                request.setAttribute("result", "success");
                request.setAttribute("className", mapping.getClassName());
                request.setAttribute("methodName", mapping.getMethodName());
            } else {
                request.setAttribute("result", "error");
            }

            request.getRequestDispatcher("/test-url.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/test-url.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getParameter("url");

        if (url == null || url.trim().isEmpty()) {
            request.setAttribute("result", "error");
            request.setAttribute("found", false);
            request.setAttribute("searchUrl", "");
            request.getRequestDispatcher("/test-url.jsp").forward(request, response);
            return;
        }

        MappingInfo mapping = AnnotationReader.findMappingByUrl(url);

        request.setAttribute("searchUrl", url);
        request.setAttribute("found", mapping.isFound());

        if (mapping.isFound()) {
            request.setAttribute("result", "success");
            request.setAttribute("className", mapping.getClassName());
            request.setAttribute("methodName", mapping.getMethodName());
        } else {
            request.setAttribute("result", "error");
        }

        request.getRequestDispatcher("/test-url.jsp").forward(request, response);
    }
}
