package org.examples.server.dto;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ReservationRequestDTO",
        namespace = "http://service.hotel.examples.org/dto",
        propOrder = { "auth", "offerId", "hotelName", "roomNumber", "nom", "prenom", "carte", "agence", "arrivee", "depart" }
)
@XmlRootElement(name = "ReservationRequestDTO", namespace = "http://service.hotel.examples.org/dto")
public class ReservationRequest {
    public AgencyAuth auth;   // AJOUT 2.2 : auth obligatoire côté WS2

    // AJOUT 2.2 : réservé à la réservation WS2. Si présent, il a priorité
    public String offerId;

    // historiques / compat : utilisés uniquement si offerId est null
    public String hotelName;
    public int roomNumber;

    public String nom, prenom, carte;

    @XmlElement(name = "agence", required = false, nillable = true)
    public String agence;

    // historiques / compat (fallback si pas d’offerId)
    @XmlSchemaType(name="date") public XMLGregorianCalendar arrivee;
    @XmlSchemaType(name="date") public XMLGregorianCalendar depart;

    public void setHotelName(String s){ this.hotelName = s; }
    public void setRoomNumber(int n){ this.roomNumber = n; }
    public void setArrivee(XMLGregorianCalendar d){ this.arrivee = d; }
    public void setDepart(XMLGregorianCalendar d){ this.depart = d; }
    public void setNom(String s){ this.nom = s; }
    public void setPrenom(String s){ this.prenom = s; }
    public void setCarte(String s){ this.carte = s; }
    public void setAuth(AgencyAuth a){ this.auth = a; }
    public void setAgence(String a){ this.agence = a; }
}
