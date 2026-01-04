package org.examples.agency.grpc;

import io.grpc.stub.StreamObserver;
import org.examples.hotel.grpc.*;
import org.examples.hotel.grpc.agency.*;
import org.examples.hotel.grpc.util.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import dto.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implémentation gRPC du AgencyService avec streaming
 * Expose l'API de l'agence via gRPC pour les clients externes
 */
@Component
public class AgencyGrpcServiceImpl extends AgencyServiceGrpc.AgencyServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(AgencyGrpcServiceImpl.class);

    @Autowired
    private HotelGrpcClient hotelGrpcClient;

    @Value("${agency.name:MegaAgence}")
    private String agencyName;

    @Value("${agency.discount.rate:0.10}")
    private double discountRate;

    // Pool de threads pour le streaming parallèle
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // ==================== SearchAllHotels (STREAMING SERVER-SIDE) ====================

    @Override
    public void searchAllHotels(AgencySearchRequest request, StreamObserver<Offer> responseObserver) {
        long startTime = System.currentTimeMillis();

        log.info("[AGENCY-gRPC-STREAM] SearchAllHotels (STREAMING) started");

        try {
            // Extraire les critères de recherche
            SearchRequest baseSearch = request.getBaseSearch();
            String ville = baseSearch.getCity();
            LocalDate arrivee = baseSearch.hasArrivalDate()
                    ? DateConverter.fromProto(baseSearch.getArrivalDate())
                    : null;
            LocalDate depart = baseSearch.hasDepartureDate()
                    ? DateConverter.fromProto(baseSearch.getDepartureDate())
                    : null;
            int nbPersonnes = baseSearch.getNumPersons();
            String agencyId = baseSearch.getAgency();

            log.info("[AGENCY-gRPC-STREAM] Criteria: city={}, arrival={}, departure={}, persons={}",
                    ville, arrivee, depart, nbPersonnes);

            // Compteur d'offres
            AtomicInteger offerCount = new AtomicInteger(0);

            // Liste des futures pour attendre la fin de toutes les recherches
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // Recherche parallèle sur tous les hôtels
            for (String hotelCode : hotelGrpcClient.getPartnerCodes()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        log.info("[AGENCY-gRPC-STREAM] Searching on hotel: {}", hotelCode);

                        // Rechercher les offres sur cet hôtel
                        List<OfferDTO> offers = hotelGrpcClient.searchOffers(
                                hotelCode, ville, arrivee, depart, nbPersonnes, agencyId);

                        // Envoyer chaque offre au client en temps réel (streaming)
                        for (OfferDTO offerDto : offers) {
                            Offer protoOffer = convertDTOToProtoOffer(offerDto);

                            // Appliquer la commission de l'agence
                            protoOffer = applyAgencyDiscount(protoOffer);

                            synchronized (responseObserver) {
                                responseObserver.onNext(protoOffer);
                            }

                            offerCount.incrementAndGet();

                            log.debug("[AGENCY-gRPC-STREAM] Streamed offer: {} from {}",
                                    protoOffer.getOfferId(), hotelCode);
                        }

                        log.info("[AGENCY-gRPC-STREAM] Completed search on {}: {} offers",
                                hotelCode, offers.size());

                    } catch (Exception e) {
                        log.error("[AGENCY-gRPC-STREAM] Error searching on {}: {}", hotelCode, e.getMessage());
                    }
                }, executorService);

                futures.add(future);
            }

            // Attendre que toutes les recherches soient terminées
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long duration = System.currentTimeMillis() - startTime;

            log.info("[AGENCY-gRPC-STREAM] SearchAllHotels completed: {} total offers streamed in {}ms",
                    offerCount.get(), duration);

            // Terminer le stream
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[AGENCY-gRPC-STREAM] Error in searchAllHotels: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Search failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== SearchAllHotelsSync (SYNCHRONE) ====================

    @Override
    public void searchAllHotelsSync(AgencySearchRequest request,
                                   StreamObserver<AgencySearchResponse> responseObserver) {
        long startTime = System.currentTimeMillis();

        log.info("[AGENCY-gRPC] SearchAllHotelsSync started");

        try {
            // Extraire les critères
            SearchRequest baseSearch = request.getBaseSearch();
            String ville = baseSearch.getCity();
            LocalDate arrivee = baseSearch.hasArrivalDate()
                    ? DateConverter.fromProto(baseSearch.getArrivalDate())
                    : null;
            LocalDate depart = baseSearch.hasDepartureDate()
                    ? DateConverter.fromProto(baseSearch.getDepartureDate())
                    : null;
            int nbPersonnes = baseSearch.getNumPersons();
            String agencyId = baseSearch.getAgency();

            // Recherche synchrone sur tous les hôtels
            Map<String, List<OfferDTO>> allOffers = hotelGrpcClient.searchAllOffers(
                    ville, arrivee, depart, nbPersonnes, agencyId);

            // Construire la réponse
            AgencySearchResponse.Builder responseBuilder = AgencySearchResponse.newBuilder()
                    .setOriginalRequest(request);

            int totalOffers = 0;
            for (Map.Entry<String, List<OfferDTO>> entry : allOffers.entrySet()) {
                for (OfferDTO offerDto : entry.getValue()) {
                    Offer protoOffer = convertDTOToProtoOffer(offerDto);

                    // Appliquer la commission de l'agence
                    protoOffer = applyAgencyDiscount(protoOffer);

                    responseBuilder.addOffers(protoOffer);
                    totalOffers++;
                }
            }

            responseBuilder.setTotalCount(totalOffers);

            long duration = System.currentTimeMillis() - startTime;
            log.info("[AGENCY-gRPC] SearchAllHotelsSync completed: {} offers in {}ms",
                    totalOffers, duration);

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[AGENCY-gRPC] Error in searchAllHotelsSync: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Search failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== CompareOffers ====================

    @Override
    public void compareOffers(ComparisonRequest request,
                             StreamObserver<ComparisonResponse> responseObserver) {
        log.info("[AGENCY-gRPC] CompareOffers - {} offers to compare", request.getOfferIdsCount());

        try {
            ComparisonResponse.Builder responseBuilder = ComparisonResponse.newBuilder();

            // Créer une recommandation simple
            OfferRecommendation.Builder recommendationBuilder = OfferRecommendation.newBuilder()
                    .setReason("Meilleur rapport qualité-prix")
                    .setConfidenceScore(0.85);

            responseBuilder.setRecommendation(recommendationBuilder.build());

            log.info("[AGENCY-gRPC] CompareOffers completed");

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[AGENCY-gRPC] Error in compareOffers: {}", e.getMessage());
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Comparison failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== GetPartnerHotels ====================

    @Override
    public void getPartnerHotels(PartnerHotelsRequest request,
                                StreamObserver<PartnerHotelsResponse> responseObserver) {
        log.info("[AGENCY-gRPC] GetPartnerHotels");

        try {
            PartnerHotelsResponse.Builder responseBuilder = PartnerHotelsResponse.newBuilder();

            // Récupérer les catalogues de tous les hôtels
            List<CatalogDTO> catalogs = hotelGrpcClient.getAllCatalogs();

            for (CatalogDTO catalog : catalogs) {
                if (catalog != null && catalog.getName() != null) {
                    HotelInfo.Builder hotelInfoBuilder = HotelInfo.newBuilder()
                            .setId(catalog.getName().toLowerCase())
                            .setName(catalog.getName())
                            .setStars(5);

                    PartnerHotel.Builder partnerBuilder = PartnerHotel.newBuilder()
                            .setHotel(hotelInfoBuilder.build())
                            .setCommissionRate(discountRate)
                            .setActive(true);

                    responseBuilder.addHotels(partnerBuilder.build());
                }
            }

            responseBuilder.setTotalPartners(responseBuilder.getHotelsCount());

            log.info("[AGENCY-gRPC] GetPartnerHotels completed: {} hotels",
                    responseBuilder.getTotalPartners());

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[AGENCY-gRPC] Error in getPartnerHotels: {}", e.getMessage());
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to get partner hotels: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== MakeReservationViaAgency ====================

    @Override
    public void makeReservationViaAgency(AgencyReservationRequest request,
                                        StreamObserver<Reservation> responseObserver) {
        ReservationRequest baseReservation = request.getBaseReservation();

        log.info("[AGENCY-gRPC] MakeReservationViaAgency - hotel={}, room={}, client={}",
                baseReservation.getHotelId(), baseReservation.getRoomId(), baseReservation.getClientName());

        try {
            // Convertir les dates
            LocalDate arrivee = baseReservation.hasArrivalDate()
                    ? DateConverter.fromProto(baseReservation.getArrivalDate())
                    : null;
            LocalDate depart = baseReservation.hasDepartureDate()
                    ? DateConverter.fromProto(baseReservation.getDepartureDate())
                    : null;

            // Faire la réservation via le client gRPC
            ReservationConfirmationDTO confirmation = hotelGrpcClient.makeReservation(
                    baseReservation.getHotelId(),
                    Integer.parseInt(baseReservation.getRoomId()),
                    baseReservation.getClientName(),
                    baseReservation.getClientFirstName(),
                    baseReservation.getClientCard(),
                    arrivee,
                    depart,
                    request.getAgencyName());

            if (confirmation != null && confirmation.isSuccess()) {
                // Construire la réponse
                Reservation reservation = Reservation.newBuilder()
                        .setReservationId(confirmation.getId())
                        .setHotelId(baseReservation.getHotelId())
                        .setClientName(baseReservation.getClientName())
                        .setStatus(Reservation.ReservationStatus.RESERVATION_STATUS_CONFIRMED)
                        .setConfirmationCode(confirmation.getId())
                        .setCreatedAt(System.currentTimeMillis())
                        .setUpdatedAt(System.currentTimeMillis())
                        .build();

                log.info("[AGENCY-gRPC] Reservation confirmed: {}", confirmation.getId());

                responseObserver.onNext(reservation);
                responseObserver.onCompleted();
            } else {
                log.warn("[AGENCY-gRPC] Reservation failed");
                responseObserver.onError(io.grpc.Status.INTERNAL
                        .withDescription("Reservation failed")
                        .asRuntimeException());
            }

        } catch (Exception e) {
            log.error("[AGENCY-gRPC] Error in makeReservationViaAgency: {}", e.getMessage());
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Reservation failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== GetAgencyStats ====================

    @Override
    public void getAgencyStats(StatsRequest request, StreamObserver<AgencyStats> responseObserver) {
        log.info("[AGENCY-gRPC] GetAgencyStats");

        try {
            AgencyStats stats = AgencyStats.newBuilder()
                    .setAgencyName(agencyName)
                    .setTotalSearches(0)
                    .setTotalReservations(0)
                    .setTotalRevenue(0.0)
                    .setTotalCommission(0.0)
                    .setAverageReservationValue(0.0)
                    .setConversionRate(0.0)
                    .build();

            log.info("[AGENCY-gRPC] Stats: agency={}, searches={}, reservations={}",
                    stats.getAgencyName(), stats.getTotalSearches(), stats.getTotalReservations());

            responseObserver.onNext(stats);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[AGENCY-gRPC] Error in getAgencyStats: {}", e.getMessage());
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to get stats: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Convertir un OfferDTO en Offer (Proto)
     */
    private Offer convertDTOToProtoOffer(OfferDTO dto) {
        Offer.Builder builder = Offer.newBuilder()
                .setOfferId(dto.getOfferId() != null ? dto.getOfferId() : "")
                .setAvailable(true)
                .setCurrency("EUR");

        // Hotel info
        if (dto.getHotelName() != null) {
            HotelInfo.Builder hotelBuilder = HotelInfo.newBuilder()
                    .setId(dto.getOfferId() != null ? dto.getOfferId().split("-")[0] : "hotel")
                    .setName(dto.getHotelName())
                    .setStars(dto.getNbEtoiles());

            if (dto.getAddress() != null) {
                org.examples.hotel.grpc.common.Address.Builder addressBuilder = org.examples.hotel.grpc.common.Address.newBuilder()
                        .setCity(dto.getAddress().getVille() != null ? dto.getAddress().getVille() : "")
                        .setStreet(dto.getAddress().getRue() != null ? dto.getAddress().getRue() : "")
                        .setCountry(dto.getAddress().getPays() != null ? dto.getAddress().getPays() : "");
                hotelBuilder.setAddress(addressBuilder.build());
            }

            builder.setHotel(hotelBuilder.build());
        }

        // Room info
        if (dto.getRoom() != null) {
            RoomType.Builder roomBuilder = RoomType.newBuilder()
                    .setId(String.valueOf(dto.getRoomNumber()))
                    .setCategory(dto.getCategorie() != null ? dto.getCategorie() : "STANDARD")
                    .setCapacity(dto.getNbLits())
                    .setPricePerNight(dto.getRoom().getPrixParNuit());
            builder.setRoom(roomBuilder.build());
        }

        // Prix
        builder.setTotalPrice(dto.getPrixTotal());
        builder.setFinalPrice(dto.getPrixTotal());
        builder.setPricePerNight(dto.getPrixTotal());

        // Dates
        if (dto.getStart() != null) {
            builder.setArrivalDate(DateConverter.toProto(dto.getStart()));
        }
        if (dto.getEnd() != null) {
            builder.setDepartureDate(DateConverter.toProto(dto.getEnd()));
        }

        return builder.build();
    }

    /**
     * Appliquer la commission de l'agence à une offre
     */
    private Offer applyAgencyDiscount(Offer offer) {
        double discountedPrice = offer.getTotalPrice() * (1 - discountRate);

        return offer.toBuilder()
                .setFinalPrice(discountedPrice)
                .build();
    }

    /**
     * Arrêt propre du pool de threads
     */
    public void shutdown() {
        log.info("[AGENCY-gRPC] Shutting down executor service...");
        executorService.shutdown();
    }
}

