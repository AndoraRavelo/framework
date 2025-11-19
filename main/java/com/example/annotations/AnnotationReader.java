package com.example.annotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

// Utilitaires orchestrés par AnnotationReader
import com.example.utilitaire.ConfigLoader;
import com.example.utilitaire.ClassScanner;
import com.example.utilitaire.UrlMappingRegistry;
import com.example.utilitaire.MappingInfo;

public class AnnotationReader {
    
    public static void readGetMappingAnnotations(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping annotation = method.getAnnotation(GetMapping.class);
                String url = annotation.value();
                System.out.println("URL trouvée: " + url);
            }
        }
    }
    
    public static List<Class<?>> findClassesWithMethodAnnotations(Class<?>[] classes) {
        List<Class<?>> classesWithAnnotations = new ArrayList<>();
        
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            boolean hasMethodAnnotation = false;
            
            for (Method method : methods) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    hasMethodAnnotation = true;
                    break;
                }
            }
            
            if (hasMethodAnnotation) {
                classesWithAnnotations.add(clazz);
            }
        }
        
        return classesWithAnnotations;
    }
    
    public static void displayClassesWithAnnotations(Class<?>[] classes) {
        List<Class<?>> annotatedClasses = findClassesWithMethodAnnotations(classes);
        
        System.out.println("Classes utilisant l'annotation @GetMapping au niveau méthode:");
        for (Class<?> clazz : annotatedClasses) {
            System.out.println("- " + clazz.getSimpleName());
            
            // Afficher aussi si la classe a l'annotation @Controller
            if (clazz.isAnnotationPresent(Controller.class)) {
                Controller controller = clazz.getAnnotation(Controller.class);
                String value = controller.value().isEmpty() ? "" : " (value: " + controller.value() + ")";
                System.out.println("  └─ Annotée avec @Controller" + value);
            }
            
            // Lister les méthodes avec @GetMapping
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    System.out.println("  └─ Méthode: " + method.getName() + " -> " + mapping.value());
                }
            }
        }
    }

    // ====== Nouvelle partie: Orchestrateur façon framework ======

    // Dépendances (Dependency Injection light)
    private static final ConfigLoader configLoader = new ConfigLoader();
    private static final ClassScanner classScanner = new ClassScanner();
    private static final UrlMappingRegistry urlRegistry = new UrlMappingRegistry();

    /**
     * Variante interne: filtre les classes ayant des méthodes @GetMapping à partir d'une liste
     */
    private static List<Class<?>> findClassesWithMethodAnnotations(List<Class<?>> classes) {
        List<Class<?>> classesWithAnnotations = new ArrayList<>();
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    classesWithAnnotations.add(clazz);
                    break;
                }
            }
        }
        return classesWithAnnotations;
    }

    /**
     * Affiche toutes les classes avec annotations (scan auto du base package)
     */
    public static void displayClassesWithAnnotations() {
        String basePackage = configLoader.getBasePackage();
        System.out.println("Scan du package de base: " + basePackage + "\n");

        List<Class<?>> classes = classScanner.scanPackage(basePackage);

        System.out.println("Classes avec @Controller découvertes: " + classes.size());
        for (Class<?> c : classes) {
            System.out.println("- " + c.getName());
        }
        System.out.println();

        List<Class<?>> annotatedClasses = findClassesWithMethodAnnotations(classes);

        System.out.println("Classes utilisant l'annotation @GetMapping au niveau méthode:");
        for (Class<?> clazz : annotatedClasses) {
            System.out.println("- " + clazz.getSimpleName());

            if (clazz.isAnnotationPresent(Controller.class)) {
                System.out.println("  └─ Annotée avec @Controller");
            }

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    System.out.println("  └─ Méthode: " + method.getName() + " -> " + mapping.value());
                }
            }
        }
    }

    /**
     * Initialise le système en scannant toutes les URLs au démarrage
     */
    public static void init() {
        if (urlRegistry.isInitialized()) {
            System.out.println("AnnotationReader déjà initialisé.");
            return;
        }

        System.out.println("Initialisation du système de mapping d'URLs...");

        // 1. Charger la configuration
        configLoader.loadConfiguration();
        String basePackage = configLoader.getBasePackage();

        // 2. Scanner les classes du package
        List<Class<?>> classes = classScanner.scanPackage(basePackage);
        System.out.println("Classes avec @Controller découvertes: " + classes.size());

        // 3. Construire le registre des URLs
        urlRegistry.buildRegistry(classes);
    }

    /**
     * Recherche une URL et retourne les informations de mapping
     */
    public static MappingInfo findMappingByUrl(String url) {
        if (!urlRegistry.isInitialized()) {
            System.out.println("ATTENTION: AnnotationReader n'est pas initialisé. Appelez init() au démarrage.");
            init();
        }
        MappingInfo info = urlRegistry.findByUrl(url);
        return info != null ? info : new MappingInfo();
    }

    /**
     * Affiche les informations de mapping pour une URL donnée
     */
    public static void displayMappingForUrl(String url) {
        MappingInfo info = findMappingByUrl(url);
        if (info.isFound()) {
            System.out.println("URL: " + url);
            System.out.println("Classe: " + info.getClassName());
            System.out.println("Méthode: " + info.getMethodName());
        } else {
            System.out.println("404 - URL non trouvée: " + url);
        }
    }
}
