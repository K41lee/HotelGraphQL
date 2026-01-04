// src/main/java/org/examples/server/dto/SearchCriteria.java
package org.examples.server.dto;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SearchCriteriaDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="SearchCriteriaDTO", namespace="http://service.hotel.examples.org/dto")
public class SearchCriteria {
    public String ville;
    public XMLGregorianCalendar arrivee;
    public XMLGregorianCalendar depart;
    public Integer prixMin, prixMax;
    public String categorie;
    public Integer nbEtoiles;
    public int nbPersonnes;
    public String agence;

    public void setVille(String v){ this.ville=v; }
    public void setArrivee(XMLGregorianCalendar a){ this.arrivee=a; }
    public void setDepart(XMLGregorianCalendar d){ this.depart=d; }
    public void setPrixMin(Integer p){ this.prixMin=p; }
    public void setPrixMax(Integer p){ this.prixMax=p; }
    public void setCategorie(String c){ this.categorie=c; }
    public void setNbEtoiles(Integer e){ this.nbEtoiles=e; }
    public void setNbPersonnes(int n){ this.nbPersonnes=n; }
    public void setAgence(String a){ this.agence=a; }
}
