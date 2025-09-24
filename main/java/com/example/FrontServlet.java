package com.example; 
 
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet; 
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;

public class FrontServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String fullUrl = request.getRequestURL().toString();
        String uri = request.getRequestURI();

        // Affichage en console
        System.out.println("URL tapée (console) : " + fullUrl);
        System.out.println("URI : " + uri);

        // Affichage sur la page web
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Recuperation URL :</h1>");
        out.println("<p>Full URL : " + fullUrl + "</p>");
        out.println("<p>URL : " + uri + "</p>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
