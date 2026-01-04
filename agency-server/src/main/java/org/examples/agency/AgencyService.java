package org.examples.agency;

import dto.*;
import org.examples.agency.graphql.HotelGraphQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service TCP de l'agence - MIGRATION vers GraphQL COMPLÉTÉE
 * (gRPC supprimé, remplacé par GraphQL)
 */
@Service
public class AgencyService {
  private static final Logger log = LoggerFactory.getLogger(AgencyService.class);

  @Autowired
  private HotelGraphQLClient graphqlClient;

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

    // Récupérer les catalogues via GraphQL
    try {
      Map<String, Object> operaCatalog = graphqlClient.getCatalog("opera");
      Map<String, Object> rivageCatalog = graphqlClient.getCatalog("rivage");

      log.info("[AGENCY] Got catalogs via GraphQL");

      // Extraire les villes des catalogues
      @SuppressWarnings("unchecked")
      Map<String, Object> operaHotel = (Map<String, Object>) operaCatalog.get("hotel");
      if (operaHotel != null) {
        @SuppressWarnings("unchecked")
        Map<String, Object> address = (Map<String, Object>) operaHotel.get("address");
        if (address != null && address.get("city") != null) {
          cities.add((String) address.get("city"));
        }
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> rivageHotel = (Map<String, Object>) rivageCatalog.get("hotel");
      if (rivageHotel != null) {
        @SuppressWarnings("unchecked")
        Map<String, Object> address = (Map<String, Object>) rivageHotel.get("address");
        if (address != null && address.get("city") != null) {
          cities.add((String) address.get("city"));
        }
      }
    } catch (Exception e) {
      log.warn("[AGENCY-CALL] getCatalog via GraphQL failed: {}", e.toString());
      // Fallback sur données par défaut
      cities.add("Montpellier");
    }

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

    // Utiliser GraphQL pour rechercher sur tous les hôtels
    try {
      log.info("[AGENCY] Calling graphqlClient.searchOffers...");
      List<Map<String, Object>> allOffers = graphqlClient.searchOffers(ville, from, to, nb, agencyId);
      log.info("[AGENCY] Got {} offers via GraphQL", allOffers.size());

      if (allOffers.isEmpty()) {
        log.warn("[AGENCY] ⚠️  No offers received from GraphQL - check hotel servers and GraphQL client logs");
      }

      for (Map<String, Object> offerData : allOffers) {
        log.debug("[AGENCY] Processing offer: {}", offerData);
        // Extraire le prix total
        Object totalPriceObj = offerData.get("totalPrice");
        double originalPrice = 0.0;
        if (totalPriceObj instanceof Double) {
          originalPrice = (Double) totalPriceObj;
        } else if (totalPriceObj instanceof Integer) {
          originalPrice = ((Integer) totalPriceObj).doubleValue();
        }

        // Appliquer la remise de l'agence
        double discountedPrice = originalPrice * (1 - discountRate);

        log.debug("[AGENCY] Applying discount: {} -> {} ({}% off)",
                 originalPrice, discountedPrice, (int)(discountRate * 100));

        // Construire l'offre pour le client TCP
        Map<String,Object> m = new LinkedHashMap<>();

        // Hotel info
        @SuppressWarnings("unchecked")
        Map<String, Object> hotel = (Map<String, Object>) offerData.get("hotel");
        if (hotel != null) {
          m.put("hotelName", hotel.get("name"));
          m.put("nbEtoiles", hotel.get("stars"));

          @SuppressWarnings("unchecked")
          Map<String, Object> address = (Map<String, Object>) hotel.get("address");
          if (address != null) {
            m.put("ville", address.get("city"));
          }
        }

        // Room info
        @SuppressWarnings("unchecked")
        Map<String, Object> room = (Map<String, Object>) offerData.get("room");
        if (room != null) {
          m.put("categorie", room.get("category"));

          // ⭐ IMPORTANT: Ajouter le numéro de chambre directement dans l'offre pour le GUI
          m.put("numero", room.get("id"));

          Map<String,Object> r = new LinkedHashMap<>();
          r.put("numero", room.get("id"));
          r.put("nbLits", room.get("capacity"));
          r.put("prixParNuit", room.get("pricePerNight"));

          @SuppressWarnings("unchecked")
          List<Map<String, Object>> images = (List<Map<String, Object>>) room.get("images");
          if (images != null && !images.isEmpty()) {
            r.put("imageUrl", images.get(0).get("url"));
          }
          m.put("room", r);
          m.put("nbLits", room.get("capacity"));
        }

        m.put("prixTotal", (int) discountedPrice);
        m.put("offerId", offerData.get("offerId"));
        offers.add(m);
      }
    } catch (Exception e) {
      log.error("[AGENCY-CALL] searchOffers via GraphQL failed: {}", e.getMessage(), e);
      // L'exception est catchée mais on continue avec une liste vide
    }

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

    // Extraire hotelCode et roomNumber de l'offerId (format: opera-201-timestamp)
    String roomId = null;
    if (offerId != null && offerId.contains("-")) {
      String[] parts = offerId.split("-");
      if (parts.length >= 2) {
        if (hotelCode == null || hotelCode.isEmpty()) {
          hotelCode = parts[0];
          log.info("[AGENCY] hotelCode extrait de offerId: '{}'", hotelCode);
        }
        roomId = parts[1];
      }
    }

    log.info("[AGENCY] makeReservation APRÈS extraction - hotelCode='{}' roomId={}", hotelCode, roomId);

    // Extraire les dates du payload
    String arriveeStr = str(payload.get("arrivee"));
    String departStr = str(payload.get("depart"));
    LocalDate arrivee = null;
    LocalDate depart = null;
    int nbPersonnes = num(payload.get("nbPersonnes"), 1);

    try {
      if (arriveeStr != null) arrivee = LocalDate.parse(arriveeStr);
      if (departStr != null) depart = LocalDate.parse(departStr);
    } catch (Exception e) {
      log.warn("[AGENCY] Invalid dates: {} - {}", arriveeStr, departStr);
    }

    // Appeler le service GraphQL pour faire la réservation
    try {
      Map<String, Object> confirmation = graphqlClient.makeReservation(
        hotelCode, roomId, nom, prenom, carte, arrivee, depart, nbPersonnes
      );

      log.info("[AGENCY] ✅ Reservation completed via GraphQL: reference={}", confirmation.get("confirmationCode"));

      Map<String,Object> data = new LinkedHashMap<>();
      data.put("success", true);
      data.put("message", "Reservation confirmed");
      data.put("reference", confirmation.get("confirmationCode"));
      data.put("reservationId", confirmation.get("reservationId"));
      data.put("totalPrice", confirmation.get("totalPrice"));
      data.put("status", confirmation.get("status"));

      return data;
    } catch (Exception e) {
      log.warn("[AGENCY] ❌ Reservation failed: {}", e.getMessage());

      Map<String,Object> data = new LinkedHashMap<>();
      data.put("success", false);
      data.put("message", "Reservation failed: " + e.getMessage());
      data.put("reference", null);

      return data;
    }
  }


  private static String str(Object o){ return o==null? null : String.valueOf(o); }
  private static int num(Object o, int d){ try { return o==null? d : Integer.parseInt(String.valueOf(o)); } catch(Exception e){ return d; } }
  private static String maskCard(String c) { if (c==null) return null; String n=c.replaceAll("[^0-9]", ""); if (n.length()<4) return "****"; return "**** **** **** "+n.substring(n.length()-4); }
}
