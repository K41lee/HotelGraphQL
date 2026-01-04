package org.examples.client;

import org.examples.client.stub.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogLister {
    public static void main(String[] args) throws Exception {
        String wsdlUrl = System.getProperty("wsdl.url", "http://localhost:8081/hotel-rivage/hotel?wsdl");
        System.out.println("[LIST] WSDL=" + wsdlUrl);
        HotelService_Service svc = new HotelService_Service(new URL(wsdlUrl));
        HotelService port = svc.getHotelServiceImplPort();
        CatalogDTO cat = port.getCatalog();
        List<String> cities = new ArrayList<>();
        List<String> agencies = new ArrayList<>();
        if (cat.getCities() != null && cat.getCities().getCity() != null) {
            cities.addAll(cat.getCities().getCity());
        }
        if (cat.getAgencies() != null && cat.getAgencies().getAgency() != null) {
            agencies.addAll(cat.getAgencies().getAgency());
        }
        System.out.println("[LIST] Raw Catalog: name=" + cat.getName());
        System.out.println("[LIST] Cities=" + (cities.isEmpty() ? "(none)" : String.join(", ", cities)));
        System.out.println("[LIST] Agencies=" + (agencies.isEmpty() ? "(none)" : String.join(", ", agencies)));
    }
}
