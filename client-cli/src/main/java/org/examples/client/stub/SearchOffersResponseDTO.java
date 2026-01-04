
package org.examples.client.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SearchOffersResponseDTO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="SearchOffersResponseDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="offers" type="{http://service.hotel.examples.org/dto}OfferListDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchOffersResponseDTO", propOrder = {
    "offers"
})
public class SearchOffersResponseDTO {

    protected OfferListDTO offers;

    /**
     * Obtient la valeur de la propriété offers.
     * 
     * @return
     *     possible object is
     *     {@link OfferListDTO }
     *     
     */
    public OfferListDTO getOffers() {
        return offers;
    }

    /**
     * Définit la valeur de la propriété offers.
     * 
     * @param value
     *     allowed object is
     *     {@link OfferListDTO }
     *     
     */
    public void setOffers(OfferListDTO value) {
        this.offers = value;
    }

}
