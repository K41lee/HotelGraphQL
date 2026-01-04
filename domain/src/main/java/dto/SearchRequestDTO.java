package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * DTO REST pour les critères de recherche d'offres d'hôtel
 */
public class SearchRequestDTO {
    
    @JsonProperty("ville")
    private String ville;
    
    @JsonProperty("arrivee")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivee;
    
    @JsonProperty("depart")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate depart;
    
    @JsonProperty("prixMin")
    private Integer prixMin;
    
    @JsonProperty("prixMax")
    private Integer prixMax;
    
    @JsonProperty("categorie")
    private String categorie;
    
    @JsonProperty("nbEtoiles")
    private Integer nbEtoiles;
    
    @JsonProperty("nbPersonnes")
    private int nbPersonnes;
    
    @JsonProperty("agence")
    private String agence;

    // Constructeur par défaut (requis pour Jackson)
    public SearchRequestDTO() {}

    public SearchRequestDTO(String ville, LocalDate arrivee, LocalDate depart, int nbPersonnes) {
        this.ville = ville;
        this.arrivee = arrivee;
        this.depart = depart;
        this.nbPersonnes = nbPersonnes;
    }

    // Getters et Setters
    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
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

    public Integer getPrixMin() {
        return prixMin;
    }

    public void setPrixMin(Integer prixMin) {
        this.prixMin = prixMin;
    }

    public Integer getPrixMax() {
        return prixMax;
    }

    public void setPrixMax(Integer prixMax) {
        this.prixMax = prixMax;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getNbEtoiles() {
        return nbEtoiles;
    }

    public void setNbEtoiles(Integer nbEtoiles) {
        this.nbEtoiles = nbEtoiles;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(int nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }

    @Override
    public String toString() {
        return "SearchRequestDTO{" +
                "ville='" + ville + '\'' +
                ", arrivee=" + arrivee +
                ", depart=" + depart +
                ", nbPersonnes=" + nbPersonnes +
                ", prixMin=" + prixMin +
                ", prixMax=" + prixMax +
                ", categorie='" + categorie + '\'' +
                ", nbEtoiles=" + nbEtoiles +
                ", agence='" + agence + '\'' +
                '}';
    }
}

