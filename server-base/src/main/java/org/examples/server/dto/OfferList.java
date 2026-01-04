// OfferList.java
package org.examples.server.dto;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="OfferListDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="OfferListDTO", namespace="http://service.hotel.examples.org/dto")
public class OfferList {
    private java.util.List<Offer> offers = new java.util.ArrayList<>();
    public java.util.List<Offer> getOffers() { return offers; }
    public void setOffers(java.util.List<Offer> offers) { this.offers = offers; }
}
