package org.examples.serveropera.grpc;

import dto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.examples.hotel.grpc.*;
import org.examples.hotel.grpc.util.DateConverter;
import org.examples.hotel.grpc.util.ErrorHandler;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation gRPC du HotelService pour server-opera
 * Utilise directement les repositories (REST/SOAP supprimés)
 */
@GrpcService
public class HotelGrpcServiceImpl extends HotelServiceGrpc.HotelServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(HotelGrpcServiceImpl.class);

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Value("${spring.application.name:server-opera}")
    private String serverId;

    private static final String HOTEL_ID = "opera";

    // ==================== GetCatalog ====================

    @Override
    public void getCatalog(CatalogRequest request, StreamObserver<HotelCatalog> responseObserver) {
        try {
            logger.info("[gRPC] GetCatalog - hotel_id={}", request.getHotelId());

            // Récupérer l'hôtel depuis la base de données
            Optional<HotelEntity> hotelOpt = hotelRepository.findByNom(HOTEL_ID);
            if (!hotelOpt.isPresent()) {
                logger.error("[gRPC] Hotel not found: {}", HOTEL_ID);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Hotel not found")
                        .asRuntimeException());
                return;
            }

            HotelEntity hotel = hotelOpt.get();
            List<ChambreEntity> chambres = chambreRepository.findAll();

            // Créer le catalogue
            CatalogDTO catalogDto = new CatalogDTO();
            catalogDto.setName(hotel.getNom());

            // Ajouter la ville de l'hôtel
            catalogDto.setCities(java.util.Arrays.asList(hotel.getVille()));

            // Ajouter l'agence par défaut
            catalogDto.setAgencies(java.util.Arrays.asList("operaAgency"));

            // Convertir en Proto
            HotelCatalog catalog = ProtoMapper.toProtoCatalog(catalogDto, HOTEL_ID);

            logger.info("[gRPC] GetCatalog success - hotel={}, city={}, rooms={}",
                    catalogDto.getName(), hotel.getVille(), chambres.size());

            responseObserver.onNext(catalog);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("[gRPC] Error in getCatalog", e);
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }

    // ==================== SearchOffers ====================

    @Override
    public void searchOffers(SearchRequest request, StreamObserver<OffersResponse> responseObserver) {
        try {
            logger.info("[gRPC] SearchOffers - city={} arrival={} departure={} persons={} agency={}",
                    request.getCity(),
                    request.hasArrivalDate() ? request.getArrivalDate() : "null",
                    request.hasDepartureDate() ? request.getDepartureDate() : "null",
                    request.getNumPersons(),
                    request.getAgency());

            // Convertir la requête Proto en DTO
            SearchRequestDTO searchDto = ProtoMapper.fromProtoSearchRequest(request);

            // Récupérer l'hôtel
            Optional<HotelEntity> hotelOpt = hotelRepository.findByNom(HOTEL_ID);
            if (!hotelOpt.isPresent()) {
                logger.error("[gRPC] Hotel not found: {}", HOTEL_ID);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Hotel not found")
                        .asRuntimeException());
                return;
            }

            HotelEntity hotel = hotelOpt.get();

            // Vérifier si la ville correspond (filtrage)
            if (!request.getCity().isEmpty()) {
                String requestCity = request.getCity().trim().toLowerCase();
                String hotelCity = hotel.getVille() != null ? hotel.getVille().trim().toLowerCase() : "";

                if (!hotelCity.isEmpty() && !hotelCity.equals(requestCity)) {
                    logger.info("[gRPC] Hotel city '{}' does not match requested city '{}' - returning empty results",
                            hotel.getVille(), request.getCity());

                    OffersResponse response = ProtoMapper.toProtoOffersResponse(new ArrayList<>(), request);
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }
            }

            // Rechercher les chambres disponibles (excluant les chambres déjà réservées)
            List<ChambreEntity> chambres;

            if (searchDto.getArrivee() != null && searchDto.getDepart() != null && request.getNumPersons() > 0) {
                // ⭐ Utiliser findAvailableChambres qui vérifie les réservations existantes
                chambres = chambreRepository.findAvailableChambres(
                    hotel.getId(),
                    request.getNumPersons(),
                    searchDto.getArrivee(),
                    searchDto.getDepart()
                );
                logger.info("[gRPC] Found {} available chambres for hotel {} in city {} (dates: {} to {}, persons: {})",
                        chambres.size(), hotel.getNom(), hotel.getVille(),
                        searchDto.getArrivee(), searchDto.getDepart(), request.getNumPersons());
            } else {
                // Pas de dates spécifiées, retourner toutes les chambres
                chambres = chambreRepository.findByHotelId(hotel.getId());
                logger.info("[gRPC] Found {} chambres for hotel {} (no date filter)",
                        chambres.size(), hotel.getNom());
            }

            List<OfferDTO> offers = new ArrayList<>();

            // Créer des offres pour chaque chambre disponible
            for (ChambreEntity chambre : chambres) {
                // Note: le filtre sur nbLits est déjà fait par findAvailableChambres
                // Mais on garde cette vérification pour le cas sans dates

                OfferDTO offer = new OfferDTO();
                offer.setOfferId(HOTEL_ID + "-" + chambre.getNumero() + "-" + System.currentTimeMillis());
                offer.setHotelName(hotel.getNom());
                offer.setNbEtoiles(hotel.getNbEtoiles());

                // Créer l'adresse
                AddressDTO address = new AddressDTO();
                address.setVille(hotel.getVille());
                address.setPays(hotel.getPays());
                address.setRue(hotel.getRue());
                try {
                    address.setNumero(Integer.parseInt(hotel.getNumero()));
                } catch (NumberFormatException e) {
                    address.setNumero(0);
                }
                offer.setAddress(address);

                // Créer les infos de chambre
                RoomDTO room = new RoomDTO();
                room.setNumero(chambre.getNumero());
                room.setNbLits(chambre.getNbLits());
                room.setPrixParNuit(chambre.getPrixParNuit());
                room.setImageUrl(chambre.getImageUrl()); // ⭐ Ajouter l'image
                offer.setRoom(room);

                offer.setNbLits(chambre.getNbLits());
                offer.setCategorie(hotel.getCategorie());
                offer.setRoomNumber(chambre.getNumero());

                // Calculer le prix total
                if (searchDto.getArrivee() != null && searchDto.getDepart() != null) {
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(
                            searchDto.getArrivee(), searchDto.getDepart());
                    offer.setPrixTotal(chambre.getPrixParNuit() * (int) nights);
                    offer.setStart(searchDto.getArrivee());
                    offer.setEnd(searchDto.getDepart());
                } else {
                    offer.setPrixTotal(chambre.getPrixParNuit());
                }

                offers.add(offer);
                logger.info("[gRPC] Created offer: {} - Room {} - {}€",
                        offer.getOfferId(), chambre.getNumero(), offer.getPrixTotal());
            }

            // Convertir la réponse en Proto
            OffersResponse response = ProtoMapper.toProtoOffersResponse(offers, request);

            logger.info("[gRPC] SearchOffers success - found {} offers", offers.size());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("[gRPC] Error in searchOffers", e);
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }

    // ==================== MakeReservation ====================

    @Override
    public void makeReservation(ReservationRequest request, StreamObserver<Reservation> responseObserver) {
        try {
            logger.info("[gRPC] MakeReservation - hotel={} room={} client={} arrival={} departure={}",
                    request.getHotelId(),
                    request.getRoomId(),
                    request.getClientName(),
                    request.hasArrivalDate() ? request.getArrivalDate() : "null",
                    request.hasDepartureDate() ? request.getDepartureDate() : "null");

            // Convertir la requête Proto en DTO
            ReservationRequestDTO reservationDto = ProtoMapper.fromProtoReservationRequest(request);

            // ⭐ Chercher la chambre
            int roomNumber = Integer.parseInt(request.getRoomId());
            Optional<ChambreEntity> chambreOpt = chambreRepository.findByNumero(roomNumber);

            if (!chambreOpt.isPresent()) {
                logger.error("[gRPC] Chambre {} non trouvée", roomNumber);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Chambre non trouvée: " + roomNumber)
                        .asRuntimeException());
                return;
            }

            ChambreEntity chambre = chambreOpt.get();

            // ⭐ Vérifier les dates
            LocalDate arrival = DateConverter.fromProto(request.getArrivalDate());
            LocalDate departure = DateConverter.fromProto(request.getDepartureDate());

            // Vérifier les chevauchements
            List<ReservationEntity> overlapping = reservationRepository.findOverlappingReservations(
                chambre.getId(), arrival, departure);

            if (!overlapping.isEmpty()) {
                logger.error("[gRPC] Chambre {} déjà réservée pour ces dates", roomNumber);
                responseObserver.onError(Status.ALREADY_EXISTS
                        .withDescription("Chambre déjà réservée pour ces dates")
                        .asRuntimeException());
                return;
            }

            // ⭐ Log de la requête reçue
            logger.info("[HOTEL:opera] ⭐ Requête gRPC reçue - agencyName='{}'", request.getAgencyName());

            // ⭐ Créer et sauvegarder la réservation
            ReservationEntity reservationEntity = new ReservationEntity();
            reservationEntity.setChambre(chambre);
            reservationEntity.setClientNom(request.getClientName());
            reservationEntity.setClientPrenom(request.getClientFirstName() != null && !request.getClientFirstName().isEmpty() ? request.getClientFirstName() : "");
            reservationEntity.setClientCarte(request.getClientCard() != null && !request.getClientCard().isEmpty() ? request.getClientCard() : null);
            reservationEntity.setDebut(arrival);
            reservationEntity.setFin(departure);
            String agencyName = request.getAgencyName() != null && !request.getAgencyName().isEmpty() ? request.getAgencyName() : "DIRECT";
            reservationEntity.setAgence(agencyName);
            logger.info("[HOTEL:opera] ⭐ Agence enregistrée en BDD: '{}'", agencyName);

            // Générer une référence unique
            String reference = String.format("%s-%s-%d",
                HOTEL_ID.toUpperCase(),
                roomNumber,
                System.currentTimeMillis());
            reservationEntity.setReference(reference);

            // ⭐ SAUVEGARDER dans la base H2
            ReservationEntity savedReservation = reservationRepository.save(reservationEntity);

            logger.info("[gRPC] ✅ Réservation sauvegardée en base H2 - ID: {}, Reference: {}, Chambre: {}, Client: {} {}, Carte: {}, Dates: {} → {}",
                savedReservation.getId(),
                reference,
                roomNumber,
                request.getClientName(),
                request.getClientFirstName() != null ? request.getClientFirstName() : "",
                request.getClientCard() != null && request.getClientCard().length() >= 4 ? "****" + request.getClientCard().substring(request.getClientCard().length()-4) : "N/A",
                arrival,
                departure);

            // Créer la confirmation
            ReservationConfirmationDTO confirmation = new ReservationConfirmationDTO();
            confirmation.setId(reference);
            confirmation.setSuccess(true);
            confirmation.setMessage("Reservation successful - saved in database");

            if (confirmation == null) {
                logger.error("[gRPC] Reservation failed - no confirmation received");
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Reservation failed")
                        .asRuntimeException());
                return;
            }

            // Convertir la confirmation en Proto
            Reservation protoReservation = ProtoMapper.toProtoReservation(confirmation, HOTEL_ID);

            logger.info("[gRPC] MakeReservation success - reservation_id={}", confirmation.getId());

            responseObserver.onNext(protoReservation);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            logger.error("[gRPC] Invalid argument in makeReservation", e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            logger.error("[gRPC] Error in makeReservation", e);
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }

    // ==================== GetReservation ====================

    @Override
    public void getReservation(ReservationQuery request, StreamObserver<Reservation> responseObserver) {
        try {
            logger.info("[gRPC] GetReservation - reservation_id={} hotel_id={} email={}",
                    request.getReservationId(),
                    request.getHotelId(),
                    request.getClientEmail());

            // TODO: Implémenter la récupération de réservation quand le service sera disponible
            // Pour l'instant, retourner NOT_FOUND
            logger.warn("[gRPC] GetReservation not yet implemented");
            responseObserver.onError(Status.UNIMPLEMENTED
                    .withDescription("GetReservation not yet implemented")
                    .asRuntimeException());

        } catch (Exception e) {
            logger.error("[gRPC] Error in getReservation", e);
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }

    // ==================== CancelReservation ====================

    @Override
    public void cancelReservation(CancellationRequest request, StreamObserver<CancellationResponse> responseObserver) {
        try {
            logger.info("[gRPC] CancelReservation - reservation_id={} reason={}",
                    request.getReservationId(),
                    request.getReason());

            // TODO: Implémenter l'annulation quand le service sera disponible
            // Pour l'instant, retourner une réponse négative
            logger.warn("[gRPC] CancelReservation not yet implemented");

            CancellationResponse response = CancellationResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("CancelReservation not yet implemented")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("[gRPC] Error in cancelReservation", e);
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }

    // ==================== Ping ====================

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        try {
            logger.debug("[gRPC] Ping - message={}", request.getMessage());

            PingResponse response = ProtoMapper.createPingResponse(
                    request.getMessage(),
                    serverId
            );

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("[gRPC] Error in ping", e);
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }
}

