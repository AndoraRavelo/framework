package com.example.utilitaire;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Responsable du chargement de la configuration depuis config.properties
 * (SRP)
 */
public class ConfigLoader {

    private String basePackage;

    /**
     * Charge le package de base depuis le fichier config.properties
     */
    public void loadConfiguration() {
        if (basePackage != null) {
            return;
        }

        Properties props = new Properties();
        InputStream input = null;

        try {
            // Essayer de charger depuis le classpath
            input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties");

            if (input == null) {
                // Fallback: chemin relatif courant si lancé hors conteneur
                input = new FileInputStream("config.properties");
            }

            if (input != null) {
                props.load(input);
                basePackage = props.getProperty("base.package");
                if (basePackage != null) {
                    basePackage = basePackage.trim();
                    System.out.println("[Config] Package de base chargé: " + basePackage);
                }
            } else {
                System.out.println("[Config] Fichier config.properties introuvable, utilisation de la valeur par défaut.");
                basePackage = "com.example"; // Valeur par défaut sûre
            }
        } catch (Exception e) {
            System.out.println("[Config] Erreur lors du chargement du config.properties: " + e.getMessage());
            basePackage = "com.example"; // Valeur par défaut
        } finally {
            if (input != null) {
                try { input.close(); } catch (Exception ignore) {}
            }
        }
    }

    public String getBasePackage() {
        if (basePackage == null) {
            loadConfiguration();
        }
        return basePackage;
    }
}
