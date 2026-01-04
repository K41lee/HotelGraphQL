package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * DTO REST pour une offre d'hôtel (résultat de recherche)
 */
public class OfferDTO {
    
    @JsonProperty("offerId")
    private String offerId;
    
    @JsonProperty("hotelName")
    private String hotelName;
    
    @JsonProperty("address")
    private AddressDTO address;
    
    @JsonProperty("categorie")
    private String categorie;
    
    @JsonProperty("nbEtoiles")
    private int nbEtoiles;
    
    @JsonProperty("room")
    private RoomDTO room;
    
    @JsonProperty("roomNumber")
    private int roomNumber;
    
    @JsonProperty("nbLits")
    private int nbLits;
    
    @JsonProperty("prixTotal")
    private int prixTotal;
    
    @JsonProperty("finalPrice")
    private Double finalPrice; // Prix après remise agence

    @JsonProperty("discountRate")
    private Double discountRate; // Taux de remise (0.0 à 1.0)

    @JsonProperty("start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    
    @JsonProperty("end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
    
    @JsonProperty("agenceApplied")
    private String agenceApplied;

    // Constructeur par défaut
    public OfferDTO() {}

    // Getters et Setters
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getNbEtoiles() {
        return nbEtoiles;
    }

    public void setNbEtoiles(int nbEtoiles) {
        this.nbEtoiles = nbEtoiles;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getNbLits() {
        return nbLits;
    }

    public void setNbLits(int nbLits) {
        this.nbLits = nbLits;
    }

    public int getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(int prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public String getAgenceApplied() {
        return agenceApplied;
    }

    public void setAgenceApplied(String agenceApplied) {
        this.agenceApplied = agenceApplied;
    }

    @Override
    public String toString() {
        return "OfferDTO{" +
                "offerId='" + offerId + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", categorie='" + categorie + '\'' +
                ", nbEtoiles=" + nbEtoiles +
                ", roomNumber=" + roomNumber +
                ", nbLits=" + nbLits +
                ", prixTotal=" + prixTotal +
                ", start=" + start +
                ", end=" + end +
                ", agenceApplied='" + agenceApplied + '\'' +
                '}';
    }
}

