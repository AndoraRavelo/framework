package com.example;

import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;

public class FrontServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Force UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String fullUrl = request.getRequestURL().toString();
        String uri = request.getRequestURI();

        System.out.println("URL tapée (console) : " + fullUrl);
        System.out.println("URI : " + uri);

        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());  // Ex. : "/home" ou "/"

        // Nettoie leading slash
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        System.out.println("Path extracted: '" + path + "'");

        if (path.isEmpty()) {
            path = "";
        }

        boolean hasExtension = path.contains(".");
        boolean isDirectory = !hasExtension && (uri.endsWith("/") || path.isEmpty());

        // Essaie forward
        if (forwardToResource(request, response, path, hasExtension, isDirectory)) {
            return;
        }

        // Fallback
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><head><title>URL Info</title><meta charset=\"UTF-8\"></head><body>");
        out.println("<h1>Récupération URL :</h1>");
        out.println("<p>Full URL : " + fullUrl + "</p>");
        out.println("<p>URL : " + uri + "</p>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private boolean forwardToResource(HttpServletRequest request, HttpServletResponse response,
                                      String path, boolean hasExtension, boolean isDirectory)
            throws ServletException, IOException {
        String[] exts = {".jsp", ".html", ".htm"};
        String[] bases = {"", "/WEB-INF/"};

        System.out.println("[Routing] path='" + path + "', hasExtension=" + hasExtension + ", isDirectory=" + isDirectory);

        if (hasExtension) {
            // Direct avec extension
            for (String base : bases) {
                String candidate = base + path;
                String absolute = "/" + candidate.replaceAll("^/+", "");
                System.out.println("[Routing] Checking (hasExt) candidate: " + absolute);
                if (getServletContext().getResource(absolute) != null) {
                    System.out.println("[Routing] Resource found: " + absolute);
                    if (dispatchTo(request, response, absolute)) {
                        return true;
                    }
                } else {
                    System.out.println("[Routing] Resource not found: " + absolute);
                }
            }
        } else {
            // Ajoute extension
            for (String base : bases) {
                for (String ext : exts) {
                    String candidate = base + path + ext;
                    String absolute = "/" + candidate.replaceAll("^/+", "");
                    System.out.println("[Routing] Checking (noExt) candidate: " + absolute);
                    if (getServletContext().getResource(absolute) != null) {
                        System.out.println("[Routing] Resource found: " + absolute);
                        if (dispatchTo(request, response, absolute)) {
                            return true;
                        }
                    } else {
                        System.out.println("[Routing] Resource not found: " + absolute);
                    }
                }
            }

            // Index pour dossier
            if (isDirectory || !path.isEmpty()) {
                String dirPath = path.isEmpty() ? "" : path + "/";
                for (String base : bases) {
                    for (String ext : exts) {
                        String candidate = base + dirPath + "index" + ext;
                        String absolute = "/" + candidate.replaceAll("^/+", "");
                        System.out.println("[Routing] Checking (index) candidate: " + absolute);
                        if (getServletContext().getResource(absolute) != null) {
                            System.out.println("[Routing] Resource found: " + absolute);
                            if (dispatchTo(request, response, absolute)) {
                                return true;
                            }
                        } else {
                            System.out.println("[Routing] Resource not found: " + absolute);
                        }
                    }
                }
            }
        }

        // Root index
        if (path.isEmpty()) {
            for (String base : bases) {
                for (String ext : exts) {
                    String candidate = base + "index" + ext;
                    String absolute = "/" + candidate.replaceAll("^/+", "");
                    System.out.println("[Routing] Checking (root index) candidate: " + absolute);
                    if (getServletContext().getResource(absolute) != null) {
                        System.out.println("[Routing] Resource found: " + absolute);
                        if (dispatchTo(request, response, absolute)) {
                            return true;
                        }
                    } else {
                        System.out.println("[Routing] Resource not found: " + absolute);
                    }
                }
            }
        }

        System.out.println("[Routing] No resource found for path: " + path);
        return false;
    }

    private boolean dispatchTo(HttpServletRequest request, HttpServletResponse response, String absolute)
            throws IOException {
        try {
            RequestDispatcher rd = getServletContext().getRequestDispatcher(absolute);
            if (rd == null) {
                System.out.println("[Routing] No dispatcher for: " + absolute);
                return false;
            }
            rd.forward(request, response);
            System.out.println("[Routing] Successfully forwarded to: " + absolute);
            return true;
        } catch (Exception e) {
            System.out.println("[Routing] Forward failed for " + absolute + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}