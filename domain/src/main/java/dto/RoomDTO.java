package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO REST pour les informations d'une chambre
 */
public class RoomDTO {
    
    @JsonProperty("numero")
    private int numero;
    
    @JsonProperty("nbLits")
    private int nbLits;
    
    @JsonProperty("prixParNuit")
    private int prixParNuit;
    
    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("imageData")
    private String imageData; // Image encodée en Base64

    // Constructeur par défaut
    public RoomDTO() {}

    public RoomDTO(int numero, int nbLits, int prixParNuit) {
        this.numero = numero;
        this.nbLits = nbLits;
        this.prixParNuit = prixParNuit;
    }

    public RoomDTO(int numero, int nbLits, int prixParNuit, String imageUrl) {
        this.numero = numero;
        this.nbLits = nbLits;
        this.prixParNuit = prixParNuit;
        this.imageUrl = imageUrl;
    }

    // Getters et Setters
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getNbLits() {
        return nbLits;
    }

    public void setNbLits(int nbLits) {
        this.nbLits = nbLits;
    }

    public int getPrixParNuit() {
        return prixParNuit;
    }

    public void setPrixParNuit(int prixParNuit) {
        this.prixParNuit = prixParNuit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    @Override
    public String toString() {
        return "RoomDTO{" +
                "numero=" + numero +
                ", nbLits=" + nbLits +
                ", prixParNuit=" + prixParNuit +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

