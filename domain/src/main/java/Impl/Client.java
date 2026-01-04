package Impl;

public class Client {
    private String nom, prenom, carteCredit;
    public Client(String nom, String prenom, String carteCredit) {
        this.nom = nom; this.prenom = prenom; this.carteCredit = carteCredit;
    }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getCarte() { return carteCredit; }
    public void setCarte(String carte) { this.carteCredit = carte; }
}
