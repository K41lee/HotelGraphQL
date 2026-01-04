package org.examples.serverrivage;

import Impl.*;

public class RivageConfig {
    // legacy helper - intentionally not a @Configuration to avoid creating beans
    public Gestionnaire legacyGestionnaire() {
        Gestionnaire g = new Gestionnaire();
        // kept for reference, not registered as bean
        Hotel h = new Hotel("Hôtel Rivage", new Adresse("France","Montpellier","Rue de la Mer",12,"",43.61,3.88), Categorie.MILIEU_DE_GAMME, 3);
        h.addChambre(new Chambre(h, 101, 2, 80));
        h.addChambre(new Chambre(h, 102, 4, 120));
        h.addAgence(new Agence("rivageAgency", 0.10));
        g.addHotel(h);
        return g;
    }
}
