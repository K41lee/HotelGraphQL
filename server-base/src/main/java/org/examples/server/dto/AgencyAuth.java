package org.examples.server.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AgencyAuthDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="AgencyAuthDTO", namespace="http://service.hotel.examples.org/dto")
public class AgencyAuth {
    public String agencyId;
    public String login;
    public String password;

    public String getAgencyId(){ return agencyId; }
    public void setAgencyId(String a){ this.agencyId = a; }
    public String getLogin(){ return login; }
    public void setLogin(String l){ this.login = l; }
    public String getPassword(){ return password; }
    public void setPassword(String p){ this.password = p; }
}
