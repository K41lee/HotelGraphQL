package org.examples.server.config;

import Impl.Hotel;

import java.util.List;

/**
 * Fabrique de données par hôtel (ou groupe d’hôtels).
 * Chaque sous-module serveur fournit sa propre implémentation.
 */
public interface DataFactory {

    /**
     * Construit et retourne la liste des hôtels servie par ce module.
     */
    List<Hotel> build();

    /**
     * Nom logique (optionnel) de l’ensemble de données fourni.
     */
    default String name() {
        return "default";
    }
}

