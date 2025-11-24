package com.urls; 

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.annotations.WebRoute;
import com.controllers.AnnotationController;
import com.controllers.UserController;
public class FrontServlet extends HttpServlet {
    // Map pour stocker les URLs et leurs informations (classe + méthode)
    private final Map<String, Mapping> urlMappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        // Enregistrer les routes annotées depuis les contrôleurs
        scanController(new AnnotationController());
        scanController(new UserController());
    }

    /**
     * Scanne un contrôleur pour trouver les méthodes annotées avec @WebRoute
     */
    private void scanController(Object controllerInstance) {
        Class<?> classe = controllerInstance.getClass();
        
        // Parcourir toutes les méthodes de la classe
        for (Method methode : classe.getDeclaredMethods()) {
            WebRoute annotation = methode.getAnnotation(WebRoute.class);
            
            if (annotation != null) {
                String url = annotation.path();
                
                // S'assurer que l'URL commence par "/"
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                
                // Créer un Mapping avec le nom de la classe et de la méthode
                Mapping mapping = new Mapping(classe.getSimpleName(), methode.getName());
                urlMappings.put(url, mapping);
                
                System.out.println("Route enregistrée: " + url + " -> " + classe.getSimpleName() + "." + methode.getName());
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
       
        String fullUri = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        // Obtenir le chemin relatif
        String relativePath = fullUri.substring(contextPath.length());
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }

        resp.setContentType("text/html;charset=UTF-8");
        
        // Vérifier si l'URL existe dans les mappings
        Mapping mapping = urlMappings.get(relativePath);
        
        if (mapping != null) {
            // Si l'URL existe : instancier le contrôleur et exécuter la méthode associée
            try {
                String controllerClassName = "com.controllers." + mapping.getClassName();
                Class<?> controllerClass = Class.forName(controllerClassName);
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

                Method method = controllerClass.getMethod(mapping.getMethodName(), HttpServletRequest.class, HttpServletResponse.class);
                method.invoke(controllerInstance, req, resp);
            } catch (Exception e) {
                // En cas d'erreur, afficher un message simple
                resp.getWriter().write("<h1>Erreur lors de l'exécution de la route</h1>");
                resp.getWriter().write("<p>" + e.getClass().getSimpleName() + " : " + e.getMessage() + "</p>");
            }
        } else {
            // Si l'URL n'existe pas : afficher l'URL tapée
            resp.getWriter().write("<h1>URL non trouvée</h1>");
            resp.getWriter().write("<p><strong>URL tapée :</strong> " + relativePath + "</p>");
        }
    }
}
