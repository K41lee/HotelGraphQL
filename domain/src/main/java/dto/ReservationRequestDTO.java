package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * DTO REST pour une demande de réservation
 */
public class ReservationRequestDTO {
    
    @JsonProperty("auth")
    private AgencyAuthDTO auth;
    
    @JsonProperty("offerId")
    private String offerId;
    
    @JsonProperty("hotelName")
    private String hotelName;
    
    @JsonProperty("roomNumber")
    private int roomNumber;
    
    @JsonProperty("nom")
    private String nom;
    
    @JsonProperty("prenom")
    private String prenom;
    
    @JsonProperty("carte")
    private String carte;
    
    @JsonProperty("agence")
    private String agence;
    
    @JsonProperty("arrivee")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivee;
    
    @JsonProperty("depart")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate depart;

    // Constructeur par défaut
    public ReservationRequestDTO() {}

    // Getters et Setters
    public AgencyAuthDTO getAuth() {
        return auth;
    }

    public void setAuth(AgencyAuthDTO auth) {
        this.auth = auth;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getCarte() {
        return carte;
    }

    public void setCarte(String carte) {
        this.carte = carte;
    }

    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }

    public LocalDate getArrivee() {
        return arrivee;
    }

    public void setArrivee(LocalDate arrivee) {
        this.arrivee = arrivee;
    }

    public LocalDate getDepart() {
        return depart;
    }

    public void setDepart(LocalDate depart) {
        this.depart = depart;
    }

    @Override
    public String toString() {
        return "ReservationRequestDTO{" +
                "hotelName='" + hotelName + '\'' +
                ", roomNumber=" + roomNumber +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", agence='" + agence + '\'' +
                ", arrivee=" + arrivee +
                ", depart=" + depart +
                '}';
    }
}

