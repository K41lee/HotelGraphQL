package org.examples.agency;

import dto.*;
// import org.examples.agency.grpc.HotelGrpcClient;  // TODO: Remplacer par HotelGraphQLClient
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service TCP de l'agence - EN MIGRATION vers GraphQL
 * (gRPC supprimé, SOAP supprimé, REST supprimé)
 */
@Service
public class AgencyService {
  private static final Logger log = LoggerFactory.getLogger(AgencyService.class);

  // @Autowired
  // private HotelGrpcClient grpcClient;  // TODO: Remplacer par HotelGraphQLClient

  @Value("${agency.discount.rate:0.10}")
  private double discountRate;

  // Les hôtels partenaires sont maintenant gérés par HotelGrpcClient
  // Ports gRPC : opera=9090, rivage=9091

  public String handleRequest(String jsonLine) {
    try {
      log.info("[AGENCY-REQ] raw={}", jsonLine);
      Map<String,Object> req = Json.minParse(jsonLine);
      String op = (String) req.get("op");
      if ("ping".equals(op)) return Json.ok(Collections.singletonMap("pong", true));
      if ("catalog.get".equals(op)) { log.info("[AGENCY] op=catalog.get"); return Json.ok(getCatalog()); }
      if ("offers.search".equals(op)) { log.info("[AGENCY] op=offers.search payload={}", req.get("payload")); return Json.ok(searchOffers((Map<String,Object>) req.get("payload"))); }
      if ("reservation.make".equals(op)) { Map<String,Object> p=(Map<String,Object>)req.get("payload");
        String masked = p!=null && p.get("carte")!=null? maskCard(String.valueOf(p.get("carte"))) : null;
        log.info("[AGENCY] op=reservation.make payload={{hotelCode={}, offerId={}, agencyId={}, nom={}, prenom={}, carte={}}}",
                 p!=null? p.get("hotelCode"):null, p!=null? p.get("offerId"):null, p!=null? p.get("agencyId"):null,
                 p!=null? p.get("nom"):null, p!=null? p.get("prenom"):null, masked);
        return Json.ok(makeReservation(p)); }
      return Json.error("unknown op");
    } catch (Exception e) {
      log.warn("[AGENCY] handle error: {}", e.toString());
      return Json.error(e.getMessage());
    }
  }

  private Map<String,Object> getCatalog() {
    Set<String> cities = new LinkedHashSet<>();

    // TODO: Récupérer les catalogues via GraphQL (remplacer gRPC)
    /* try {
      List<CatalogDTO> catalogs = grpcClient.getAllCatalogs();
      log.info("[AGENCY] Got {} catalogs via GraphQL", catalogs.size());

      for (CatalogDTO catalog : catalogs) {
        if (catalog != null && catalog.getCities() != null) {
          cities.addAll(catalog.getCities());
        }
      }
    } catch (Exception e) {
      log.warn("[AGENCY-CALL] getCatalog via GraphQL failed: {}", e.toString());
    } */

    // Données temporaires pour test
    cities.add("Montpellier");
    cities.add("Paris");

    Map<String,Object> data = new LinkedHashMap<>();
    data.put("name", "Agence Centrale");
    data.put("cities", new ArrayList<>(cities));
    data.put("agencies", new ArrayList<>());
    return data;
  }

  private Map<String,Object> searchOffers(Map<String,Object> payload) throws Exception {
    String ville = str(payload.get("ville"));
    String arrivee = str(payload.get("arrivee"));
    String depart = str(payload.get("depart"));
    int nb = num(payload.get("nbPersonnes"), 1);
    String agencyId = str(payload.get("agencyId"));
    log.info("[AGENCY] searchOffers ville='{}' arrivee='{}' depart='{}' nbPersonnes={} agencyId='{}'", ville, arrivee, depart, nb, agencyId);

    LocalDate from = LocalDate.parse(arrivee);
    LocalDate to = LocalDate.parse(depart);

    List<Map<String,Object>> offers = new ArrayList<>();

    // TODO: Utiliser GraphQL pour rechercher sur tous les hôtels (remplacer gRPC)
    /* try {
      Map<String, List<OfferDTO>> allOffers = grpcClient.searchAllOffers(ville, from, to, nb, agencyId);
      log.info("[AGENCY] Got offers from {} hotels via GraphQL", allOffers.size());

      for (Map.Entry<String, List<OfferDTO>> entry : allOffers.entrySet()) {
        for (OfferDTO o : entry.getValue()) {
          // ⭐ Appliquer la remise de l'agence au prix
          int originalPrice = o.getPrixTotal();
          int discountedPrice = (int) (originalPrice * (1 - discountRate));
          o.setPrixTotal(discountedPrice);

          log.debug("[AGENCY] Applying discount: {} -> {} ({}% off)",
                   originalPrice, discountedPrice, (int)(discountRate * 100));

          Map<String,Object> m = new LinkedHashMap<>();
          m.put("hotelName", o.getHotelName());
          m.put("categorie", o.getCategorie());
          m.put("nbEtoiles", o.getNbEtoiles());
          if (o.getAddress() != null) {
            Map<String,Object> a = new LinkedHashMap<>();
            a.put("pays", o.getAddress().getPays());
            a.put("ville", o.getAddress().getVille());
            a.put("rue", o.getAddress().getRue());
            m.put("address", a);
          }
          if (o.getRoom() != null) {
            Map<String,Object> r = new LinkedHashMap<>();
            r.put("numero", o.getRoom().getNumero());
            r.put("nbLits", o.getRoom().getNbLits());
            r.put("prixParNuit", o.getRoom().getPrixParNuit());
            if (o.getRoom().getImageUrl() != null) {
              r.put("imageUrl", o.getRoom().getImageUrl()); // ⭐ Ajouter l'image
            }
            m.put("room", r);
          }
          m.put("nbLits", o.getNbLits());
          m.put("prixTotal", o.getPrixTotal()); // prixTotal contient déjà le prix avec remise
          m.put("offerId", o.getOfferId());
          offers.add(m);
        }
      }
    } catch (Exception e) {
      log.warn("[AGENCY-CALL] searchOffers via GraphQL failed: {}", e.toString());
    } */

    log.warn("[AGENCY] searchOffers temporarily disabled during GraphQL migration");

    Map<String,Object> data = new LinkedHashMap<>();
    data.put("offers", offers);
    return data;
  }

  private Map<String,Object> makeReservation(Map<String,Object> payload) throws Exception {
    String hotelCode = str(payload.get("hotelCode"));
    String offerId = str(payload.get("offerId"));
    String agencyId = str(payload.get("agencyId"));
    String nom = str(payload.get("nom"));
    String prenom = str(payload.get("prenom"));
    String carte = str(payload.get("carte"));

    log.info("[AGENCY] makeReservation REÇU depuis TCP - hotelCode='{}' offerId='{}' agencyId='{}' nom='{}' prenom='{}' carte='{}'",
             hotelCode, offerId, agencyId, nom, prenom, maskCard(carte));
    log.info("[AGENCY] ⭐ TRACE agencyId depuis client TCP: '{}'", agencyId);

    // ⭐ Extraire hotelCode et roomNumber de l'offerId (format: opera-201-timestamp)
    int roomNumber = 0;
    if (offerId != null && offerId.contains("-")) {
      String[] parts = offerId.split("-");
      if (parts.length >= 2) {
        // ⭐ Si hotelCode n'est pas fourni, l'extraire de l'offerId
        if (hotelCode == null || hotelCode.isEmpty()) {
          hotelCode = parts[0];
          log.info("[AGENCY] hotelCode extrait de offerId: '{}'", hotelCode);
        }
        try {
          roomNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
          log.warn("[AGENCY] Invalid room number in offerId: {}", offerId);
        }
      }
    }

    log.info("[AGENCY] makeReservation APRÈS extraction - hotelCode='{}' roomNumber={}", hotelCode, roomNumber);

    // Extraire les dates du payload
    String arriveeStr = str(payload.get("arrivee"));
    String departStr = str(payload.get("depart"));
    LocalDate arrivee = null;
    LocalDate depart = null;

    try {
      if (arriveeStr != null) arrivee = LocalDate.parse(arriveeStr);
      if (departStr != null) depart = LocalDate.parse(departStr);
    } catch (Exception e) {
      log.warn("[AGENCY] Invalid dates: {} - {}", arriveeStr, departStr);
    }

    // TODO: Appeler le service GraphQL pour faire la réservation (remplacer gRPC)
    /* ReservationConfirmationDTO confirmation = grpcClient.makeReservation(
      hotelCode, roomNumber, nom, prenom, carte, arrivee, depart, agencyId
    ); */

    // Réponse temporaire pendant la migration
    log.warn("[AGENCY] makeReservation temporarily disabled during GraphQL migration");

    Map<String,Object> data = new LinkedHashMap<>();
    data.put("success", false);
    data.put("message", "Service temporarily unavailable during GraphQL migration");
    data.put("reference", "TEMP-000");

    /* if (confirmation.isSuccess()) {
      log.info("[AGENCY] ✅ Reservation completed via GraphQL: reference={}", confirmation.getId());
    } else {
      log.warn("[AGENCY] ❌ Reservation failed: {}", confirmation.getMessage());
    } */

    return data;
  }


  private static String str(Object o){ return o==null? null : String.valueOf(o); }
  private static int num(Object o, int d){ try { return o==null? d : Integer.parseInt(String.valueOf(o)); } catch(Exception e){ return d; } }
  private static String maskCard(String c) { if (c==null) return null; String n=c.replaceAll("[^0-9]", ""); if (n.length()<4) return "****"; return "**** **** **** "+n.substring(n.length()-4); }
}
