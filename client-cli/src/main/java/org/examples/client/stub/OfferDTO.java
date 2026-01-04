
package org.examples.client.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour OfferDTO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="OfferDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="address" type="{http://service.hotel.examples.org/dto}AddressDTO" minOccurs="0"/>
 *         &lt;element name="categorie" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nbEtoiles" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="room" type="{http://service.hotel.examples.org/dto}RoomDTO" minOccurs="0"/>
 *         &lt;element name="prixTotal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="agenceApplied" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="offerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="start" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="end" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="roomNumber" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nbLits" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OfferDTO", propOrder = {
    "hotelName",
    "address",
    "categorie",
    "nbEtoiles",
    "room",
    "prixTotal",
    "agenceApplied",
    "offerId",
    "start",
    "end",
    "roomNumber",
    "nbLits"
})
public class OfferDTO {

    @XmlElement(required = true)
    protected String hotelName;
    protected AddressDTO address;
    @XmlElement(required = true)
    protected String categorie;
    protected int nbEtoiles;
    protected RoomDTO room;
    protected int prixTotal;
    protected String agenceApplied;
    protected String offerId;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar start;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar end;
    protected Integer roomNumber;
    protected Integer nbLits;

    /**
     * Obtient la valeur de la propriété hotelName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Définit la valeur de la propriété hotelName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelName(String value) {
        this.hotelName = value;
    }

    /**
     * Obtient la valeur de la propriété address.
     * 
     * @return
     *     possible object is
     *     {@link AddressDTO }
     *     
     */
    public AddressDTO getAddress() {
        return address;
    }

    /**
     * Définit la valeur de la propriété address.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressDTO }
     *     
     */
    public void setAddress(AddressDTO value) {
        this.address = value;
    }

    /**
     * Obtient la valeur de la propriété categorie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * Définit la valeur de la propriété categorie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategorie(String value) {
        this.categorie = value;
    }

    /**
     * Obtient la valeur de la propriété nbEtoiles.
     * 
     */
    public int getNbEtoiles() {
        return nbEtoiles;
    }

    /**
     * Définit la valeur de la propriété nbEtoiles.
     * 
     */
    public void setNbEtoiles(int value) {
        this.nbEtoiles = value;
    }

    /**
     * Obtient la valeur de la propriété room.
     * 
     * @return
     *     possible object is
     *     {@link RoomDTO }
     *     
     */
    public RoomDTO getRoom() {
        return room;
    }

    /**
     * Définit la valeur de la propriété room.
     * 
     * @param value
     *     allowed object is
     *     {@link RoomDTO }
     *     
     */
    public void setRoom(RoomDTO value) {
        this.room = value;
    }

    /**
     * Obtient la valeur de la propriété prixTotal.
     * 
     */
    public int getPrixTotal() {
        return prixTotal;
    }

    /**
     * Définit la valeur de la propriété prixTotal.
     * 
     */
    public void setPrixTotal(int value) {
        this.prixTotal = value;
    }

    /**
     * Obtient la valeur de la propriété agenceApplied.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgenceApplied() {
        return agenceApplied;
    }

    /**
     * Définit la valeur de la propriété agenceApplied.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgenceApplied(String value) {
        this.agenceApplied = value;
    }

    /**
     * Obtient la valeur de la propriété offerId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * Définit la valeur de la propriété offerId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfferId(String value) {
        this.offerId = value;
    }

    /**
     * Obtient la valeur de la propriété start.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStart() {
        return start;
    }

    /**
     * Définit la valeur de la propriété start.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStart(XMLGregorianCalendar value) {
        this.start = value;
    }

    /**
     * Obtient la valeur de la propriété end.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEnd() {
        return end;
    }

    /**
     * Définit la valeur de la propriété end.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEnd(XMLGregorianCalendar value) {
        this.end = value;
    }

    /**
     * Obtient la valeur de la propriété roomNumber.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRoomNumber() {
        return roomNumber;
    }

    /**
     * Définit la valeur de la propriété roomNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRoomNumber(Integer value) {
        this.roomNumber = value;
    }

    /**
     * Obtient la valeur de la propriété nbLits.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNbLits() {
        return nbLits;
    }

    /**
     * Définit la valeur de la propriété nbLits.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNbLits(Integer value) {
        this.nbLits = value;
    }

}
