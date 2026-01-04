package org.examples.serveropera.graphql;
import dto.*;
import org.examples.server.entity.ChambreEntity;
import org.examples.server.entity.HotelEntity;
import org.examples.server.entity.ReservationEntity;
import org.examples.server.repository.ChambreRepository;
import org.examples.server.repository.HotelRepository;
import org.examples.server.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Contrôleur GraphQL pour le serveur Opera
 * Remplace l'ancien HotelGrpcServiceImpl
 */
@Controller
public class HotelGraphQLController {
    private static final Logger log = LoggerFactory.getLogger(HotelGraphQLController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Value("${spring.application.name:server-opera}")
    private String serverId;
    private static final String HOTEL_ID = "opera";
    // ==================== Query : ping ====================
    @QueryMapping
    public Map<String, Object> ping(@Argument String message) {
        log.info("[GraphQL] ping - message={}", message);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message != null ? "pong: " + message : "pong");
        response.put("timestamp", System.currentTimeMillis());
        response.put("serverId", serverId);
        log.info("[GraphQL] ping success");
        return response;
    }
    // ==================== Query : hotelCatalog ====================
    @QueryMapping
    public Map<String, Object> hotelCatalog(
        @Argument String hotelId,
        @Argument Boolean includeImages,
        @Argument Boolean includeUnavailableRooms
    ) {
        log.info("[GraphQL] hotelCatalog - hotelId={}, includeImages={}", hotelId, includeImages);
        Optional<HotelEntity> hotelOpt = hotelRepository.findByNom(HOTEL_ID);
        if (!hotelOpt.isPresent()) {
            throw new RuntimeException("Hotel not found: " + HOTEL_ID);
        }
        HotelEntity hotel = hotelOpt.get();
        List<ChambreEntity> chambres = chambreRepository.findAll();
        // Construire HotelInfo
        Map<String, Object> hotelInfo = new HashMap<>();
        hotelInfo.put("id", HOTEL_ID);
        hotelInfo.put("name", hotel.getNom());
        hotelInfo.put("stars", hotel.getNbEtoiles());
        hotelInfo.put("description", "Hôtel " + hotel.getNom());
        hotelInfo.put("amenities", Arrays.asList("WiFi", "Parking", "Restaurant", "Bar"));
        hotelInfo.put("phone", "+33 4 67 00 00 00");
        hotelInfo.put("email", HOTEL_ID + "@hotel.com");
        // Address
        Map<String, Object> address = new HashMap<>();
        address.put("city", hotel.getVille());
        address.put("street", "Avenue de l'Opéra");
        address.put("postalCode", "34000");
        address.put("country", "France");
        hotelInfo.put("address", address);
        // Location
        Map<String, Object> location = new HashMap<>();
        location.put("latitude", 43.6108);
        location.put("longitude", 3.8767);
        hotelInfo.put("location", location);
        // Images
        if (includeImages != null && includeImages) {
            List<Map<String, Object>> images = new ArrayList<>();
            Map<String, Object> img = new HashMap<>();
            img.put("url", "/images/hotel-" + HOTEL_ID + ".jpg");
            img.put("description", "Vue extérieure");
            img.put("width", 800);
            img.put("height", 600);
            images.add(img);
            hotelInfo.put("images", images);
        } else {
            hotelInfo.put("images", Collections.emptyList());
        }
        // Construire roomTypes
        List<Map<String, Object>> roomTypes = new ArrayList<>();
        for (ChambreEntity chambre : chambres) {
            Map<String, Object> room = new HashMap<>();
            room.put("id", String.valueOf(chambre.getNumero()));
            room.put("category", getRoomCategory(chambre));
            room.put("capacity", chambre.getNbLits());
            room.put("pricePerNight", (double) chambre.getPrixParNuit());
            room.put("description", "Chambre " + getRoomCategory(chambre) + " avec " + chambre.getNbLits() + " lit(s)");
            room.put("features", Arrays.asList("WiFi", "TV", "Climatisation", "Salle de bain privée"));
            room.put("availableCount", 1);
            room.put("surfaceM2", 25.0);
            // Images de la chambre
            List<Map<String, Object>> roomImages = new ArrayList<>();
            if (chambre.getImageUrl() != null && !chambre.getImageUrl().isEmpty()) {
                Map<String, Object> image = new HashMap<>();
                image.put("url", chambre.getImageUrl());
                image.put("description", "Chambre " + getRoomCategory(chambre));
                image.put("width", 800);
                image.put("height", 600);
                roomImages.add(image);
            }
            room.put("images", roomImages);
            roomTypes.add(room);
        }
        // Construire le catalogue
        Map<String, Object> catalog = new HashMap<>();
        catalog.put("hotel", hotelInfo);
        catalog.put("roomTypes", roomTypes);
        catalog.put("totalRooms", chambres.size());
        log.info("[GraphQL] hotelCatalog success - {} room types", roomTypes.size());
        return catalog;
    }
    private String getRoomCategory(ChambreEntity chambre) {
        int nbLits = chambre.getNbLits();
        if (nbLits == 1) return "SINGLE";
        if (nbLits == 2) return "DOUBLE";
        if (nbLits == 3) return "TRIPLE";
        return "FAMILY";
    }
    // ==================== Query : searchOffers ====================
    @QueryMapping
    public Map<String, Object> searchOffers(@Argument Map<String, Object> input) {
        log.info("[GraphQL] ========== searchOffers START ==========");
        log.info("[GraphQL] searchOffers - input={}", input);

        // Extraire le vrai input (peut être imbriqué)
        @SuppressWarnings("unchecked")
        Map<String, Object> actualInput = input.containsKey("input")
            ? (Map<String, Object>) input.get("input")
            : input;

        log.info("[GraphQL] actualInput extracted: {}", actualInput);

        String city = (String) actualInput.get("city");

        // Les dates peuvent être String ou LocalDate selon comment GraphQL les envoie
        Object arrivalDateObj = actualInput.get("arrivalDate");
        Object departureDateObj = actualInput.get("departureDate");

        String arrivalDateStr = null;
        String departureDateStr = null;

        if (arrivalDateObj instanceof String) {
            arrivalDateStr = (String) arrivalDateObj;
        } else if (arrivalDateObj instanceof LocalDate) {
            arrivalDateStr = ((LocalDate) arrivalDateObj).toString();
        }

        if (departureDateObj instanceof String) {
            departureDateStr = (String) departureDateObj;
        } else if (departureDateObj instanceof LocalDate) {
            departureDateStr = ((LocalDate) departureDateObj).toString();
        }

        Integer numPersons = (Integer) actualInput.get("numPersons");
        String agency = (String) actualInput.get("agency");
        // Récupérer l'hôtel
        Optional<HotelEntity> hotelOpt = hotelRepository.findByNom(HOTEL_ID);
        if (!hotelOpt.isPresent()) {
            throw new RuntimeException("Hotel not found: " + HOTEL_ID);
        }
        HotelEntity hotel = hotelOpt.get();

        // Vérifier la ville
        log.info("[GraphQL] City check - requested='{}', hotel='{}'", city, hotel.getVille());
        if (city != null && !city.isEmpty() && !city.equalsIgnoreCase(hotel.getVille())) {
            log.warn("[GraphQL] ⚠️  City mismatch - returning 0 offers");
            return createEmptyOffersResponse();
        }

        // Parser les dates
        LocalDate arrivalDate = null;
        LocalDate departureDate = null;
        try {
            if (arrivalDateStr != null) arrivalDate = LocalDate.parse(arrivalDateStr, DATE_FORMATTER);
            if (departureDateStr != null) departureDate = LocalDate.parse(departureDateStr, DATE_FORMATTER);
            log.info("[GraphQL] Dates OK - arrival={}, departure={}", arrivalDate, departureDate);
        } catch (Exception e) {
            log.error("[GraphQL] ⚠️  Date parsing error", e);
            return createEmptyOffersResponse();
        }
        int numNights = (arrivalDate != null && departureDate != null)
            ? (int) java.time.temporal.ChronoUnit.DAYS.between(arrivalDate, departureDate)
            : 1;
        // Récupérer toutes les chambres disponibles
        List<ChambreEntity> allChambres = chambreRepository.findAll();
        log.info("[GraphQL] 📊 Database has {} total rooms", allChambres.size());

        if (allChambres.isEmpty()) {
            log.error("[GraphQL] ❌ DATABASE IS EMPTY - No rooms found! Check DataInitializer");
        }

        // Filtrer par capacité si numPersons est spécifié
        List<ChambreEntity> availableChambres = allChambres.stream()
            .filter(ch -> numPersons == null || ch.getNbLits() >= numPersons)
            .collect(Collectors.toList());

        log.info("[GraphQL] 🔍 After capacity filter (need {} persons) - {} rooms available", numPersons, availableChambres.size());

        // Construire les offres
        List<Map<String, Object>> offers = new ArrayList<>();

        for (ChambreEntity chambre : availableChambres) {
            // Vérifier la disponibilité (pas de réservation conflictuelle)
            boolean isAvailable = isRoomAvailable(chambre.getNumero(), arrivalDate, departureDate);

            log.debug("[GraphQL] Room {} - capacity:{}, available:{}", chambre.getNumero(), chambre.getNbLits(), isAvailable);

            if (!isAvailable) {
                log.debug("[GraphQL] ⚠️  Room {} not available (conflict with existing reservation)", chambre.getNumero());
                continue;
            }
            Map<String, Object> offer = new HashMap<>();
            offer.put("offerId", HOTEL_ID + "-" + chambre.getNumero() + "-" + System.currentTimeMillis());
            offer.put("available", true);
            // Hotel info
            Map<String, Object> hotelInfo = new HashMap<>();
            hotelInfo.put("id", HOTEL_ID);
            hotelInfo.put("name", hotel.getNom());
            hotelInfo.put("stars", hotel.getNbEtoiles());
            Map<String, Object> address = new HashMap<>();
            address.put("city", hotel.getVille());
            address.put("street", "Avenue de l'Opéra");
            address.put("postalCode", "34000");
            address.put("country", "France");
            hotelInfo.put("address", address);
            offer.put("hotel", hotelInfo);
            // Room info
            Map<String, Object> room = new HashMap<>();
            room.put("id", chambre.getNumero());  // ⭐ Integer, pas String
            room.put("category", getRoomCategory(chambre));
            room.put("capacity", chambre.getNbLits());
            room.put("pricePerNight", (double) chambre.getPrixParNuit());
            room.put("features", Arrays.asList("WiFi", "TV", "Climatisation"));
            List<Map<String, Object>> images = new ArrayList<>();
            if (chambre.getImageUrl() != null) {
                Map<String, Object> img = new HashMap<>();
                img.put("url", chambre.getImageUrl());
                img.put("description", "Chambre " + getRoomCategory(chambre));
                images.add(img);
            }
            room.put("images", images);
            offer.put("room", room);
            // Prix
            double pricePerNight = chambre.getPrixParNuit();
            double totalPrice = pricePerNight * numNights;
            offer.put("arrivalDate", arrivalDateStr);
            offer.put("departureDate", departureDateStr);
            offer.put("numNights", numNights);
            offer.put("pricePerNight", pricePerNight);
            offer.put("totalPrice", totalPrice);
            offer.put("discountRate", 0.0);
            offer.put("finalPrice", totalPrice);
            offer.put("currency", "EUR");
            offers.add(offer);
            log.debug("[GraphQL] ✓ Added offer for room {}", chambre.getNumero());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("offers", offers);
        response.put("totalCount", offers.size());

        log.info("[GraphQL] ========== searchOffers END - {} offers returned ==========", offers.size());
        return response;
    }
    // ==================== Query : reservation ====================
    @QueryMapping
    public Map<String, Object> reservation(
        @Argument String reservationId,
        @Argument String hotelId,
        @Argument String clientEmail
    ) {
        log.info("[GraphQL] reservation - reservationId={}, hotelId={}, clientEmail={}",
                 reservationId, hotelId, clientEmail);
        if (reservationId == null) {
            return null;
        }
        // TODO: Rechercher la réservation dans la base
        // Pour l'instant, retourner null si non trouvé
        log.warn("[GraphQL] reservation lookup not fully implemented yet");
        return null;
    }
    // ==================== Mutation : makeReservation ====================
    @MutationMapping
    public Map<String, Object> makeReservation(@Argument Map<String, Object> input) {
        log.info("[GraphQL] makeReservation - input={}", input);

        // Extraire le vrai input (peut être imbriqué)
        @SuppressWarnings("unchecked")
        Map<String, Object> actualInput = input.containsKey("input")
            ? (Map<String, Object>) input.get("input")
            : input;

        String roomIdStr = (String) actualInput.get("roomId");
        String clientName = (String) actualInput.get("clientName");
        String clientFirstName = (String) actualInput.get("clientFirstName");
        String clientCard = (String) actualInput.get("clientCard");

        // Les dates peuvent être String ou LocalDate
        Object arrivalDateObj = actualInput.get("arrivalDate");
        Object departureDateObj = actualInput.get("departureDate");

        String arrivalDateStr = null;
        String departureDateStr = null;

        if (arrivalDateObj instanceof String) {
            arrivalDateStr = (String) arrivalDateObj;
        } else if (arrivalDateObj instanceof LocalDate) {
            arrivalDateStr = ((LocalDate) arrivalDateObj).toString();
        }

        if (departureDateObj instanceof String) {
            departureDateStr = (String) departureDateObj;
        } else if (departureDateObj instanceof LocalDate) {
            departureDateStr = ((LocalDate) departureDateObj).toString();
        }

        Integer numPersons = (Integer) actualInput.get("numPersons");
        String specialRequests = (String) actualInput.get("specialRequests");
        String agencyName = (String) actualInput.get("agencyName");
        // Parser roomId
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomIdStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid roomId: " + roomIdStr);
        }
        // Parser dates
        LocalDate arrivalDate;
        LocalDate departureDate;
        try {
            arrivalDate = LocalDate.parse(arrivalDateStr, DATE_FORMATTER);
            departureDate = LocalDate.parse(departureDateStr, DATE_FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("Invalid dates: " + arrivalDateStr + " - " + departureDateStr);
        }
        // Vérifier que la chambre existe (chercher par numero, pas par id)
        Optional<ChambreEntity> chambreOpt = chambreRepository.findByNumero(roomNumber);
        if (!chambreOpt.isPresent()) {
            log.error("[GraphQL] makeReservation - Room not found with numero: {}", roomNumber);
            throw new RuntimeException("Room not found with numero: " + roomNumber);
        }
        ChambreEntity chambre = chambreOpt.get();
        log.info("[GraphQL] makeReservation - Found room: id={}, numero={}, nbLits={}",
                 chambre.getId(), chambre.getNumero(), chambre.getNbLits());
        // Vérifier la disponibilité
        if (!isRoomAvailable(roomNumber, arrivalDate, departureDate)) {
            throw new RuntimeException("Room not available for the requested period");
        }
        // Créer la réservation
        ReservationEntity reservation = new ReservationEntity();
        reservation.setChambre(chambre);
        reservation.setClientNom(clientName);
        reservation.setClientPrenom(clientFirstName);
        reservation.setClientCarte(maskCard(clientCard));
        reservation.setDebut(arrivalDate);
        reservation.setFin(departureDate);
        if (agencyName != null && !agencyName.isEmpty()) {
            reservation.setAgence(agencyName);
        }
        // Générer référence
        String reference = "RES-" + HOTEL_ID.toUpperCase() + "-" + System.currentTimeMillis();
        reservation.setReference(reference);
        // Sauvegarder
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        // Calculer le prix total
        int numNights = (int) java.time.temporal.ChronoUnit.DAYS.between(arrivalDate, departureDate);
        double totalPrice = chambre.getPrixParNuit() * numNights;
        // Générer code de confirmation
        String confirmationCode = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // Construire la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("reservationId", "RES-" + savedReservation.getId());
        response.put("hotelId", HOTEL_ID);
        response.put("clientName", clientName);
        response.put("status", "CONFIRMED");
        response.put("totalPrice", totalPrice);
        response.put("createdAt", System.currentTimeMillis());
        response.put("updatedAt", System.currentTimeMillis());
        response.put("confirmationCode", confirmationCode);
        response.put("arrivalDate", arrivalDateStr);
        response.put("departureDate", departureDateStr);
        response.put("numPersons", numPersons);
        if (specialRequests != null) {
            response.put("specialRequests", specialRequests);
        }
        log.info("[GraphQL] makeReservation success - reservationId={}, confirmationCode={}",
                 savedReservation.getId(), confirmationCode);
        return response;
    }
    // ==================== Mutation : cancelReservation ====================
    @MutationMapping
    public Map<String, Object> cancelReservation(@Argument Map<String, Object> input) {
        log.info("[GraphQL] cancelReservation - input={}", input);
        String reservationId = (String) input.get("reservationId");
        String reason = (String) input.get("reason");
        // TODO: Implémenter l'annulation
        log.warn("[GraphQL] cancelReservation not fully implemented yet");
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Cancel reservation not implemented yet");
        return response;
    }
    // ==================== Méthodes utilitaires ====================
    private boolean isRoomAvailable(int roomNumber, LocalDate arrivalDate, LocalDate departureDate) {
        if (arrivalDate == null || departureDate == null) {
            return true;
        }

        // ⭐ Trouver la chambre par son NUMERO pour obtenir son ID
        Optional<ChambreEntity> chambreOpt = chambreRepository.findByNumero(roomNumber);
        if (!chambreOpt.isPresent()) {
            log.warn("[GraphQL] Room with numero {} not found for availability check", roomNumber);
            return false; // Chambre n'existe pas
        }

        ChambreEntity chambre = chambreOpt.get();
        Long chambreId = chambre.getId();

        // Chercher les réservations par l'ID technique de la chambre
        List<ReservationEntity> reservations = reservationRepository.findByChambreId(chambreId);

        log.debug("[GraphQL] Checking availability for room numero={} (id={}) - Found {} existing reservations",
                 roomNumber, chambreId, reservations.size());

        for (ReservationEntity res : reservations) {
            LocalDate resStart = res.getDebut();
            LocalDate resEnd = res.getFin();

            // Vérifier le chevauchement: les périodes se chevauchent si :
            // - La nouvelle réservation commence avant la fin de l'ancienne
            // ET
            // - La nouvelle réservation finit après le début de l'ancienne
            boolean overlap = !(departureDate.isBefore(resStart) ||
                              departureDate.isEqual(resStart) ||
                              arrivalDate.isAfter(resEnd) ||
                              arrivalDate.isEqual(resEnd));

            if (overlap) {
                log.debug("[GraphQL] ❌ Room {} NOT available - Conflict with reservation {} to {}",
                         roomNumber, resStart, resEnd);
                return false;
            }
        }

        log.debug("[GraphQL] ✅ Room {} is available for {} to {}", roomNumber, arrivalDate, departureDate);
        return true;
    }
    private String maskCard(String card) {
        if (card == null || card.length() < 4) {
            return "****";
        }
        return "****" + card.substring(card.length() - 4);
    }
    private Map<String, Object> createEmptyOffersResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("offers", Collections.emptyList());
        response.put("totalCount", 0);
        return response;
    }
}
