package org.examples.serveropera;

import Impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperaConfig {
    public Gestionnaire gestionnaire() {
        Gestionnaire g = new Gestionnaire();

        Hotel h = new Hotel("Luxe Opéra", new Adresse("France","Montpellier","Bd Victor",5,"Quartier Opéra",43.61,3.89), Categorie.HAUT_DE_GAMME, 5);
        h.addChambre(new Chambre(h, 201, 2, 220));
        h.addChambre(new Chambre(h, 202, 2, 240));
        h.addAgence(new Agence("operaAgency", 0.05));

        g.addHotel(h);
        return g;
    }
}
