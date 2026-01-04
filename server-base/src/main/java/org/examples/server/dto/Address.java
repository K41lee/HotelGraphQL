package org.examples.server.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AddressDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="AddressDTO", namespace="http://service.hotel.examples.org/dto")
public class Address {
    public String pays, ville, rue, lieuDit;
    public int numero;
    public double lat, lon;
    public String getPays(){ return pays; }
    public void setPays(String p){ this.pays=p; }
    public String getVille(){ return ville; }
    public void setVille(String v){ this.ville=v; }
    public String getRue(){ return rue; }
    public void setRue(String r){ this.rue=r; }
    public int getNumero(){ return numero; }
    public void setNumero(int n){ this.numero = n; }
}
