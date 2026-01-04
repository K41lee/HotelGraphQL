package org.examples.agency.grpc;

import dto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.examples.hotel.grpc.*;
import org.examples.hotel.grpc.util.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Client gRPC pour communiquer avec les serveurs d'hôtels
 * Remplace HotelRestClient (REST) pour de meilleures performances
 */
@Component
public class HotelGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(HotelGrpcClient.class);

    // Configuration des partenaires (hôtels avec gRPC)
    private static class GrpcPartner {
        final String code;
        final String host;
        final int port;
        final String defaultAgency;
        ManagedChannel channel;
        HotelServiceGrpc.HotelServiceBlockingStub blockingStub;

        GrpcPartner(String code, String host, int port, String defaultAgency) {
            this.code = code;
            this.host = host;
            this.port = port;
            this.defaultAgency = defaultAgency;
        }
    }

    private final List<GrpcPartner> partners = Arrays.asList(
        new GrpcPartner("opera", "localhost", 9090, "operaAgency"),
        new GrpcPartner("rivage", "localhost", 9091, "rivageAgency")
    );

    @PostConstruct
    public void init() {
        log.info("[gRPC-CLIENT] Initializing gRPC connections to hotels...");

        for (GrpcPartner partner : partners) {
            try {
                partner.channel = ManagedChannelBuilder
                    .forAddress(partner.host, partner.port)
                    .usePlaintext()
                    .build();

                partner.blockingStub = HotelServiceGrpc.newBlockingStub(partner.channel);

                log.info("[gRPC-CLIENT] ✓ Connected to {} ({}:{})",
                        partner.code, partner.host, partner.port);
            } catch (Exception e) {
                log.error("[gRPC-CLIENT] ✗ Failed to connect to {} ({}:{}): {}",
                        partner.code, partner.host, partner.port, e.getMessage());
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("[gRPC-CLIENT] Shutting down gRPC connections...");

        for (GrpcPartner partner : partners) {
            if (partner.channel != null && !partner.channel.isShutdown()) {
                try {
                    partner.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                    log.info("[gRPC-CLIENT] ✓ Disconnected from {}", partner.code);
                } catch (InterruptedException e) {
                    log.warn("[gRPC-CLIENT] Error shutting down channel for {}", partner.code);
                    partner.channel.shutdownNow();
                }
            }
        }
    }

    /**
     * Trouver un partenaire par son code
     */
    private GrpcPartner findPartner(String hotelCode) {
        return partners.stream()
                .filter(p -> p.code.equalsIgnoreCase(hotelCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * Test de connectivité avec un hôtel (Ping)
     */
    public boolean ping(String hotelCode) {
        try {
            GrpcPartner partner = findPartner(hotelCode);
            if (partner == null || partner.blockingStub == null) {
                log.warn("[gRPC-CLIENT] Unknown hotel code or not connected: {}", hotelCode);
                return false;
            }

            PingRequest request = PingRequest.newBuilder()
                    .setMessage("ping-from-agency")
                    .build();

            PingResponse response = partner.blockingStub.ping(request);

            log.info("[gRPC-CLIENT] Ping {} -> {}", hotelCode, response.getMessage());
            return true;

        } catch (StatusRuntimeException e) {
            log.warn("[gRPC-CLIENT] Ping {} failed: {} - {}",
                    hotelCode, e.getStatus().getCode(), e.getStatus().getDescription());
            return false;
        } catch (Exception e) {
            log.error("[gRPC-CLIENT] Ping {} error: {}", hotelCode, e.getMessage());
            return false;
        }
    }

    /**
     * Obtenir le catalogue d'un hôtel
     */
    public CatalogDTO getCatalog(String hotelCode) {
        try {
            GrpcPartner partner = findPartner(hotelCode);
            if (partner == null || partner.blockingStub == null) {
                log.warn("[gRPC-CLIENT] Unknown hotel code or not connected: {}", hotelCode);
                return null;
            }

            CatalogRequest request = CatalogRequest.newBuilder()
                    .setHotelId(hotelCode)
                    .setIncludeImages(false)
                    .setIncludeUnavailableRooms(false)
                    .build();

            log.info("[AGENCY->HOTEL:{}] GetCatalog via gRPC", partner.code);

            HotelCatalog catalog = partner.blockingStub.getCatalog(request);

            // Convertir HotelCatalog (Proto) en CatalogDTO
            CatalogDTO dto = new CatalogDTO();
            if (catalog.hasHotel()) {
                dto.setName(catalog.getHotel().getName());

                // ⭐ Ajouter la ville depuis l'adresse (et non la description)
                if (catalog.getHotel().hasAddress() && !catalog.getHotel().getAddress().getCity().isEmpty()) {
                    dto.setCities(Arrays.asList(catalog.getHotel().getAddress().getCity()));
                }

                // Ajouter les agences
                dto.setAgencies(Arrays.asList(partner.defaultAgency));
            }

            log.info("[HOTEL:{}->AGENCY] Catalog received via gRPC: name={}",
                    partner.code, dto.getName());

            return dto;

        } catch (StatusRuntimeException e) {
            log.error("[gRPC-CLIENT] GetCatalog {} failed: {} - {}",
                    hotelCode, e.getStatus().getCode(), e.getStatus().getDescription());
            return null;
        } catch (Exception e) {
            log.error("[gRPC-CLIENT] GetCatalog {} error: {}", hotelCode, e.getMessage());
            return null;
        }
    }

    /**
     * Rechercher des offres d'hôtel
     */
    public List<OfferDTO> searchOffers(String hotelCode, String ville, LocalDate arrivee,
                                       LocalDate depart, int nbPersonnes, String agencyId) {
        try {
            GrpcPartner partner = findPartner(hotelCode);
            if (partner == null || partner.blockingStub == null) {
                log.warn("[gRPC-CLIENT] Unknown hotel code or not connected: {}", hotelCode);
                return Collections.emptyList();
            }

            // Construire la requête gRPC
            SearchRequest.Builder requestBuilder = SearchRequest.newBuilder()
                    .setCity(ville != null ? ville : "")
                    .setNumPersons(nbPersonnes);

            if (arrivee != null) {
                requestBuilder.setArrivalDate(DateConverter.toProto(arrivee));
            }
            if (depart != null) {
                requestBuilder.setDepartureDate(DateConverter.toProto(depart));
            }
            if (agencyId != null && !agencyId.isEmpty()) {
                requestBuilder.setAgency(agencyId);
            }

            SearchRequest request = requestBuilder.build();

            log.info("[AGENCY->HOTEL:{}] SearchOffers via gRPC: ville={}, arrivee={}, depart={}, persons={}",
                    partner.code, ville, arrivee, depart, nbPersonnes);

            // Appel gRPC
            OffersResponse response = partner.blockingStub.searchOffers(request);

            // Convertir les offres Proto en DTO
            List<OfferDTO> offers = new ArrayList<>();
            for (Offer protoOffer : response.getOffersList()) {
                OfferDTO dto = convertOfferToDTO(protoOffer, hotelCode);
                if (dto != null) {
                    offers.add(dto);
                }
            }

            log.info("[HOTEL:{}->AGENCY] Found {} offers via gRPC", partner.code, offers.size());

            return offers;

        } catch (StatusRuntimeException e) {
            log.error("[gRPC-CLIENT] SearchOffers {} failed: {} - {}",
                    hotelCode, e.getStatus().getCode(), e.getStatus().getDescription());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("[gRPC-CLIENT] SearchOffers {} error: {}", hotelCode, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Faire une réservation
     */
    public ReservationConfirmationDTO makeReservation(String hotelCode, int roomNumber,
                                                     String nom, String prenom, String carte,
                                                     LocalDate arrivee, LocalDate depart,
                                                     String agencyId) {
        try {
            GrpcPartner partner = findPartner(hotelCode);
            if (partner == null || partner.blockingStub == null) {
                log.warn("[gRPC-CLIENT] Unknown hotel code or not connected: {}", hotelCode);
                return ReservationConfirmationDTO.failure("Hotel not connected");
            }

            // Construire la requête gRPC avec nom, prénom et carte séparés
            ReservationRequest.Builder requestBuilder = ReservationRequest.newBuilder()
                    .setHotelId(hotelCode)
                    .setRoomId(String.valueOf(roomNumber))
                    .setClientName(nom != null ? nom : "")
                    .setClientFirstName(prenom != null ? prenom : "")
                    .setClientCard(carte != null ? carte : "")
                    .setNumPersons(2);

            if (arrivee != null) {
                requestBuilder.setArrivalDate(DateConverter.toProto(arrivee));
            }
            if (depart != null) {
                requestBuilder.setDepartureDate(DateConverter.toProto(depart));
            }
            if (agencyId != null && !agencyId.isEmpty()) {
                requestBuilder.setAgencyName(agencyId);
                log.info("[AGENCY->HOTEL] ⭐ agencyId dans requête gRPC: '{}'", agencyId);
            } else {
                log.warn("[AGENCY->HOTEL] ⚠️ agencyId est null ou vide, ne sera pas envoyé dans gRPC");
            }

            ReservationRequest request = requestBuilder.build();

            log.info("[AGENCY->HOTEL:{}] MakeReservation via gRPC: room={}, client={} {}, agencyName='{}'",
                    partner.code, roomNumber, nom, prenom, request.getAgencyName());

            // Appel gRPC
            Reservation reservation = partner.blockingStub.makeReservation(request);

            // Convertir en DTO
            ReservationConfirmationDTO dto = ReservationConfirmationDTO.success(
                    reservation.getReservationId(),
                    "Reservation confirmed via gRPC",
                    null
            );

            log.info("[HOTEL:{}->AGENCY] Reservation confirmed via gRPC: id={}",
                    partner.code, reservation.getReservationId());

            return dto;

        } catch (StatusRuntimeException e) {
            log.error("[gRPC-CLIENT] MakeReservation {} failed: {} - {}",
                    hotelCode, e.getStatus().getCode(), e.getStatus().getDescription());
            return ReservationConfirmationDTO.failure("Reservation failed: " + e.getStatus().getDescription());
        } catch (Exception e) {
            log.error("[gRPC-CLIENT] MakeReservation {} error: {}", hotelCode, e.getMessage());
            return ReservationConfirmationDTO.failure("Reservation error: " + e.getMessage());
        }
    }

    /**
     * Convertir une Offer (Proto) en OfferDTO
     */
    private OfferDTO convertOfferToDTO(Offer protoOffer, String hotelCode) {
        try {
            OfferDTO dto = new OfferDTO();

            dto.setOfferId(protoOffer.getOfferId());

            // Informations hôtel
            if (protoOffer.hasHotel()) {
                dto.setHotelName(protoOffer.getHotel().getName());
                dto.setNbEtoiles(protoOffer.getHotel().getStars());

                if (protoOffer.getHotel().hasAddress()) {
                    AddressDTO addressDto = new AddressDTO();
                    addressDto.setVille(protoOffer.getHotel().getAddress().getCity());
                    addressDto.setRue(protoOffer.getHotel().getAddress().getStreet());
                    addressDto.setPays(protoOffer.getHotel().getAddress().getCountry());
                    dto.setAddress(addressDto);
                }
            }

            // Informations chambre
            if (protoOffer.hasRoom()) {
                dto.setCategorie(protoOffer.getRoom().getCategory());
                dto.setNbLits(protoOffer.getRoom().getCapacity());

                int roomNumber = 0;
                try {
                    roomNumber = Integer.parseInt(protoOffer.getRoom().getId());
                    dto.setRoomNumber(roomNumber);
                } catch (NumberFormatException e) {
                    dto.setRoomNumber(0);
                }

                RoomDTO roomDto = new RoomDTO();
                roomDto.setNumero(roomNumber); // ⭐ FIX: Ajouter le numéro de chambre
                roomDto.setNbLits(protoOffer.getRoom().getCapacity());
                roomDto.setPrixParNuit((int) protoOffer.getRoom().getPricePerNight());

                // ⭐ Ajouter l'image si disponible
                if (protoOffer.getRoom().getImagesCount() > 0) {
                    roomDto.setImageUrl(protoOffer.getRoom().getImages(0).getUrl());
                }

                dto.setRoom(roomDto);
            }

            // Prix (sera modifié par l'agence avec sa remise)
            dto.setPrixTotal((int) protoOffer.getTotalPrice());

            // Dates
            if (protoOffer.hasArrivalDate()) {
                dto.setStart(DateConverter.fromProto(protoOffer.getArrivalDate()));
            }
            if (protoOffer.hasDepartureDate()) {
                dto.setEnd(DateConverter.fromProto(protoOffer.getDepartureDate()));
            }

            return dto;

        } catch (Exception e) {
            log.error("[gRPC-CLIENT] Error converting offer to DTO: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtenir tous les catalogues (tous les hôtels) via gRPC
     */
    public List<CatalogDTO> getAllCatalogs() {
        List<CatalogDTO> catalogs = new ArrayList<>();

        for (GrpcPartner partner : partners) {
            CatalogDTO catalog = getCatalog(partner.code);
            if (catalog != null) {
                catalogs.add(catalog);
            }
        }

        log.info("[gRPC-CLIENT] Retrieved {} catalogs via gRPC", catalogs.size());
        return catalogs;
    }

    /**
     * Rechercher des offres sur tous les hôtels via gRPC (agrégation parallèle)
     */
    public Map<String, List<OfferDTO>> searchAllOffers(String ville, LocalDate arrivee,
                                                        LocalDate depart, int nbPersonnes,
                                                        String agencyId) {
        Map<String, List<OfferDTO>> allOffers = new LinkedHashMap<>();

        log.info("[gRPC-CLIENT] Searching offers across all hotels via gRPC (parallel)");
        long startTime = System.currentTimeMillis();

        // Recherche parallèle sur tous les hôtels
        for (GrpcPartner partner : partners) {
            List<OfferDTO> offers = searchOffers(
                partner.code, ville, arrivee, depart, nbPersonnes, agencyId);

            if (!offers.isEmpty()) {
                allOffers.put(partner.code, offers);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        int totalOffers = allOffers.values().stream().mapToInt(List::size).sum();

        log.info("[gRPC-CLIENT] Found {} total offers from {} hotels in {}ms via gRPC",
                totalOffers, allOffers.size(), duration);

        return allOffers;
    }

    /**
     * Obtenir la liste des hôtels partenaires
     */
    public List<String> getPartnerCodes() {
        return Arrays.asList("opera", "rivage");
    }

    /**
     * Vérifier si un hôtel est connecté
     */
    public boolean isConnected(String hotelCode) {
        GrpcPartner partner = findPartner(hotelCode);
        return partner != null &&
               partner.channel != null &&
               !partner.channel.isShutdown() &&
               partner.blockingStub != null;
    }
}

