package org.examples.server.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ReservationConfirmationDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="ReservationConfirmationDTO", namespace="http://service.hotel.examples.org/dto")
public class ReservationConfirmation {
    @XmlElement(required=true)
    public String id;
    @XmlElement(required=true)
    public String message;
    public Offer offer;

    // Champ attendu par le code
    public boolean success;

    // Alias/compatibilité pour 'reference' attendu par le code
    // nous gardons 'id' comme stocker principal mais exposons setReference/getReference

    public String getMessage(){ return message; }
    public void setMessage(String m){ this.message = m; }
    public String getId(){ return id; }
    public void setId(String i){ this.id = i; }
    public Offer getOffer(){ return offer; }
    public void setOffer(Offer o){ this.offer = o; }

    // New compatibility methods
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean s) { this.success = s; }

    public String getReference() { return this.id; }
    public void setReference(String ref) { this.id = ref; }
}
