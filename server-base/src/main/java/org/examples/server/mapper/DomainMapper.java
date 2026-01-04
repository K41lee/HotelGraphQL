package org.examples.server.mapper;

import Impl.*;
import org.examples.server.dto.Address;
import org.examples.server.dto.Offer;
import org.examples.server.dto.Room;

public class DomainMapper {
    public static Address toDto(Adresse a){
        Address d = new Address();
        d.pays=a.getPays(); d.ville=a.getVille(); d.rue=a.getRue(); d.lieuDit=a.getLieuDit();
        d.numero=a.getNumero(); d.lat=a.getPositionGps()[0]; d.lon=a.getPositionGps()[1];
        return d;
    }
    public static Room toDto(Chambre c){
        Room r = new Room();
        r.numero=c.getNumero(); r.nbLits=c.getNbLits(); r.prixParNuit=c.getPrixParNuit();
        return r;
    }
    public static Offer toDto(Gestionnaire.Offre o, String agenceApplied){
        Offer d = new Offer();
        d.hotelName = o.hotel().getNom();
        d.address = toDto(o.hotel().getAdresse());
        d.categorie = o.hotel().getCategorie().name();
        d.nbEtoiles = o.hotel().getNbEtoiles();
        d.room = toDto(o.chambre());
        d.prixTotal = o.prixTotal();
        d.agenceApplied = agenceApplied;
        return d;
    }
}
