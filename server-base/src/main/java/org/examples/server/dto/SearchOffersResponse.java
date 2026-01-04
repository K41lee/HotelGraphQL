package org.examples.server.dto;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="SearchOffersResponseDTO", namespace="http://service.hotel.examples.org/dto")
@XmlType(name="SearchOffersResponseDTO", namespace="http://service.hotel.examples.org/dto")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchOffersResponse {
    public OfferList offers;
    public OfferList getOffers() { return offers; }
    public void setOffers(OfferList offers) { this.offers = offers; }
}
