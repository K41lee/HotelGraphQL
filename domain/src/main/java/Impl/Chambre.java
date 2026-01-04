package Impl;

import Impl.Client;
import Impl.Hotel;
import Impl.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chambre {
    private static final Logger log = LoggerFactory.getLogger(Chambre.class);
    private final Hotel hotel;
    private int nbLits, prixParNuit, numero;
    private String imageUrl; // URL de l'image de la chambre (optionnel)
    private final List<Reservation> reservations = new ArrayList<>();

    public Chambre(Hotel hotel, int numero, int nbLits, int prixParNuit) {
        this.hotel = hotel; this.numero = numero; this.nbLits = nbLits; this.prixParNuit = prixParNuit;
    }

    public Chambre(Hotel hotel, int numero, int nbLits, int prixParNuit, String imageUrl) {
        this(hotel, numero, nbLits, prixParNuit);
        this.imageUrl = imageUrl;
    }
    public int getNbLits() { return nbLits; }
    public int getPrixParNuit() { return prixParNuit; }
    public int getNumero() { return numero; }
    public Hotel getHotel() { return hotel; }
    public String getImageUrl() { return imageUrl; }
    public void setPrixParNuit(int prix) { this.prixParNuit = prix; }
    public void setNumero(int num) { this.numero = num; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<Reservation> getReservations() { return reservations; }

    public boolean isDisponible(LocalDate debut, LocalDate fin) {
        for (Reservation r : reservations) {
            if (Reservation.chevauche(debut, fin, r.getDebut(), r.getFin())) {
                log.info("[ROOM] indisponible: room={} hotel='{}' demande=[{}..{}), existing=[{}..{}]", numero,
                        hotel != null ? hotel.getNom() : "<no-hotel>", debut, fin, r.getDebut(), r.getFin());
                return false;
            }
        }
        log.info("[ROOM] disponible: room={} hotel='{}' periode=[{}..{}), réservationsActives={}", numero,
                hotel != null ? hotel.getNom() : "<no-hotel>", debut, fin, reservations.size());
        return true;
    }

    public int prixTotal(LocalDate debut, LocalDate fin) {
        long nuits = ChronoUnit.DAYS.between(debut, fin);
        if (nuits < 0) throw new IllegalArgumentException("Dates invalides");
        return Math.toIntExact(nuits) * prixParNuit;
    }

    public Reservation reserver(Client c, LocalDate debut, LocalDate fin) {
        if (!isDisponible(debut, fin)) throw new IllegalStateException("Chambre déjà réservée sur la période");
        Reservation res = new Reservation(this, c, debut, fin);
        reservations.add(res);
        log.info("[ROOM] réservation ajoutée: hotel='{}' room={} periode=[{}..{}), totalReservations={}",
                hotel != null ? hotel.getNom() : "<no-hotel>", numero, debut, fin, reservations.size());
        return res;
    }
}
