package com.controllers;

import com.annotations.WebRoute;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Contrôleur d'exemple avec des méthodes annotées
 * Les méthodes annotées avec @WebRoute sont automatiquement mappées à des URLs
 */
public class AnnotationController {

    /**
     * Méthode de test accessible via l'URL /test-annotation
     * Exemple : http://localhost:8080/todo-app/test-annotation
     */
    @WebRoute(path = "/test-annotation")
    public void methodeDeTest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write("<h1>Mthode de test annotée executee</h1>");
    }
}
