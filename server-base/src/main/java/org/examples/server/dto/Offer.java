package org.examples.server.dto;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="OfferDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="OfferDTO", namespace="http://service.hotel.examples.org/dto")
public class Offer {
    @XmlElement
    public String hotelName;
    public Address address;
    @XmlElement
    public String categorie;
    @XmlElement
    public Integer nbEtoiles;
    public Room room;
    @XmlElement
    public Integer prixTotal;          // prix déjà remisé pour l’agence
    public String agenceApplied;   // nom de l’agence appliquée ou null

    public String offerId;
    @XmlSchemaType(name="date") public XMLGregorianCalendar start;
    @XmlSchemaType(name="date") public XMLGregorianCalendar end;
    public Integer roomNumber;
    public Integer nbLits;

    // Nouveau: URL publique de l'image de la chambre
    @XmlElement
    public String imageUrl;

    public String getOfferId(){ return offerId; }
    public void setOfferId(String id){ this.offerId = id; }
    public String getHotelName(){ return hotelName; }
    public void setHotelName(String s){ this.hotelName = s; }
    public Address getAddress(){ return address; }
    public void setAddress(Address a){ this.address = a; }
    public String getCategorie(){ return categorie; }
    public void setCategorie(String c){ this.categorie = c; }
    public int getNbEtoiles(){ return nbEtoiles!=null? nbEtoiles:0; }
    public void setNbEtoiles(int n){ this.nbEtoiles = n; }
    public Room getRoom(){ return room; }
    public void setRoom(Room r){ this.room = r; }
    public int getPrixTotal(){ return prixTotal!=null? prixTotal:0; }
    public void setPrixTotal(int p){ this.prixTotal = p; }
    public String getAgenceApplied(){ return agenceApplied; }
    public void setAgenceApplied(String a){ this.agenceApplied = a; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
