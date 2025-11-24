package com.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation WebRoute pour mapper une méthode à une URL
 * Utilisée pour définir les routes dans les contrôleurs
 * 
 * Exemple d'utilisation :
 * @WebRoute(path = "/mon-url")
 * public void maMethode(HttpServletRequest req, HttpServletResponse resp) { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebRoute {
    /**
     * Chemin de l'URL à mapper (ex: "/test-annotation")
     */
    String path();
}
