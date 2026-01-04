package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO REST pour l'adresse d'un hôtel
 */
public class AddressDTO {
    
    @JsonProperty("pays")
    private String pays;
    
    @JsonProperty("ville")
    private String ville;
    
    @JsonProperty("rue")
    private String rue;
    
    @JsonProperty("numero")
    private int numero;
    
    @JsonProperty("lieuDit")
    private String lieuDit;
    
    @JsonProperty("latitude")
    private double latitude;
    
    @JsonProperty("longitude")
    private double longitude;

    // Constructeur par défaut
    public AddressDTO() {}

    public AddressDTO(String pays, String ville, String rue, int numero, String lieuDit, double latitude, double longitude) {
        this.pays = pays;
        this.ville = ville;
        this.rue = rue;
        this.numero = numero;
        this.lieuDit = lieuDit;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters
    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getLieuDit() {
        return lieuDit;
    }

    public void setLieuDit(String lieuDit) {
        this.lieuDit = lieuDit;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return numero + " " + rue + ", " + ville + ", " + pays;
    }
}

