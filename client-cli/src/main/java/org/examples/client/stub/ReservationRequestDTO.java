
package org.examples.client.stub;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour ReservationRequestDTO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ReservationRequestDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="auth" type="{http://service.hotel.examples.org/dto}AgencyAuthDTO" minOccurs="0"/>
 *         &lt;element name="offerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="roomNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prenom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="carte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agence" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arrivee" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="depart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReservationRequestDTO", propOrder = {
    "auth",
    "offerId",
    "hotelName",
    "roomNumber",
    "nom",
    "prenom",
    "carte",
    "agence",
    "arrivee",
    "depart"
})
public class ReservationRequestDTO {

    protected AgencyAuthDTO auth;
    protected String offerId;
    protected String hotelName;
    protected int roomNumber;
    protected String nom;
    protected String prenom;
    protected String carte;
    @XmlElementRef(name = "agence", namespace = "http://service.hotel.examples.org/dto", type = JAXBElement.class, required = false)
    protected JAXBElement<String> agence;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar arrivee;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar depart;

    /**
     * Obtient la valeur de la propriété auth.
     * 
     * @return
     *     possible object is
     *     {@link AgencyAuthDTO }
     *     
     */
    public AgencyAuthDTO getAuth() {
        return auth;
    }

    /**
     * Définit la valeur de la propriété auth.
     * 
     * @param value
     *     allowed object is
     *     {@link AgencyAuthDTO }
     *     
     */
    public void setAuth(AgencyAuthDTO value) {
        this.auth = value;
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
     * Obtient la valeur de la propriété roomNumber.
     * 
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Définit la valeur de la propriété roomNumber.
     * 
     */
    public void setRoomNumber(int value) {
        this.roomNumber = value;
    }

    /**
     * Obtient la valeur de la propriété nom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit la valeur de la propriété nom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNom(String value) {
        this.nom = value;
    }

    /**
     * Obtient la valeur de la propriété prenom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit la valeur de la propriété prenom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrenom(String value) {
        this.prenom = value;
    }

    /**
     * Obtient la valeur de la propriété carte.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarte() {
        return carte;
    }

    /**
     * Définit la valeur de la propriété carte.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarte(String value) {
        this.carte = value;
    }

    /**
     * Obtient la valeur de la propriété agence.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAgence() {
        return agence;
    }

    /**
     * Définit la valeur de la propriété agence.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAgence(JAXBElement<String> value) {
        this.agence = value;
    }

    /**
     * Obtient la valeur de la propriété arrivee.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArrivee() {
        return arrivee;
    }

    /**
     * Définit la valeur de la propriété arrivee.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArrivee(XMLGregorianCalendar value) {
        this.arrivee = value;
    }

    /**
     * Obtient la valeur de la propriété depart.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDepart() {
        return depart;
    }

    /**
     * Définit la valeur de la propriété depart.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDepart(XMLGregorianCalendar value) {
        this.depart = value;
    }

}
