package framework.utilitaire;

import framework.annotation.GetMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Responsable de la gestion du registre des mappings URL -> Classe/Méthode
 * Principe de Responsabilité Unique (SRP)
 */
public class UrlMappingRegistry {
    
    private Map<String, MappingInfo> urlMappings;
    private boolean initialized;
    
    public UrlMappingRegistry() {
        this.urlMappings = new HashMap<>();
        this.initialized = false;
    }
    
    /**
     * Construit le registre des mappings URL à partir des classes scannées
     * @param controllerClasses Liste des classes décorées avec @Controller
     */
    public void buildRegistry(List<Class<?>> controllerClasses) {
        if (initialized) {
            System.out.println("[REGISTRY] Le registre est déjà initialisé - opération ignorée");
            return;
        }
        
        // Nettoyage préalable du registre
        urlMappings.clear();
        int mappingsCount = 0;
        
        System.out.println("[REGISTRY] Début de la construction du registre d'URLs...");
        
        // Parcours de toutes les classes contrôleurs
        for (Class<?> controllerClass : controllerClasses) {
            Method[] declaredMethods = controllerClass.getDeclaredMethods();
            
            // Analyse de chaque méthode pour trouver les annotations @GetMapping
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping mappingAnnotation = method.getAnnotation(GetMapping.class);
                    String mappedUrl = mappingAnnotation.value();
                    
                    // Création et enregistrement du mapping
                    MappingInfo mappingInfo = new MappingInfo(controllerClass, method, mappedUrl);
                    urlMappings.put(mappedUrl, mappingInfo);
                    mappingsCount++;
                    
                    System.out.println("[REGISTRY] URL mappée: " + mappedUrl + 
                            " -> " + controllerClass.getSimpleName() + "." + method.getName() + "()");
                }
            }
        }
        
        // Finalisation de l'initialisation
        initialized = true;
        System.out.println("[REGISTRY] Construction terminée: " + mappingsCount + 
                " mapping(s) enregistré(s) avec succès.\n");
    }
    
    /**
     * Recherche un mapping par URL
     * @param url L'URL à rechercher
     * @return MappingInfo ou null si non trouvé
     */
    public MappingInfo findByUrl(String url) {
        return urlMappings.get(url);
    }
    
    /**
     * Vérifie si le registre est initialisé
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Retourne le nombre d'URLs enregistrées
     */
    public int size() {
        return urlMappings.size();
    }
}
