package org.examples.serveropera.grpc;

import dto.*;
import org.examples.hotel.grpc.*;
import org.examples.hotel.grpc.common.*;
import org.examples.hotel.grpc.util.DateConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre les DTOs du domaine et les messages Protocol Buffers
 */
public class ProtoMapper {

    // ==================== CatalogDTO ↔ HotelCatalog ====================

    public static HotelCatalog toProtoCatalog(CatalogDTO dto, String hotelId) {
        HotelCatalog.Builder builder = HotelCatalog.newBuilder()
                .setTotalRooms(0); // À calculer selon les données disponibles

        // Créer HotelInfo de base
        HotelInfo.Builder hotelBuilder = HotelInfo.newBuilder()
                .setId(hotelId)
                .setName(dto.getName() != null ? dto.getName() : "Hôtel");

        // Ajouter l'adresse avec la ville si disponible
        if (dto.getCities() != null && !dto.getCities().isEmpty()) {
            Address.Builder addressBuilder = Address.newBuilder()
                    .setCity(dto.getCities().get(0)); // Utiliser la première ville

            hotelBuilder.setAddress(addressBuilder.build());
        }

        builder.setHotel(hotelBuilder.build());

        return builder.build();
    }

    // ==================== OfferDTO ↔ Offer ====================

    public static Offer toProtoOffer(OfferDTO dto) {
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

            // Address
            if (dto.getAddress() != null) {
                hotelBuilder.setAddress(toProtoAddress(dto.getAddress()));
            }

            builder.setHotel(hotelBuilder.build());
        }

        // Room info
        if (dto.getRoom() != null) {
            // Utiliser le numéro de chambre du RoomDTO si roomNumber n'est pas défini
            int roomNum = dto.getRoomNumber();
            if (roomNum == 0 && dto.getRoom().getNumero() != 0) {
                roomNum = dto.getRoom().getNumero();
            }

            RoomType.Builder roomBuilder = RoomType.newBuilder()
                    .setId(String.valueOf(roomNum))
                    .setCategory(dto.getCategorie() != null ? dto.getCategorie() : "STANDARD")
                    .setCapacity(dto.getNbLits())
                    .setPricePerNight(dto.getRoom().getPrixParNuit()); // ⭐ FIX: Utiliser le prix par nuit de la chambre

            // ⭐ Ajouter l'image si disponible
            if (dto.getRoom() != null && dto.getRoom().getImageUrl() != null && !dto.getRoom().getImageUrl().isEmpty()) {
                ImageInfo imageInfo = ImageInfo.newBuilder()
                        .setUrl(dto.getRoom().getImageUrl())
                        .setDescription("Image de la chambre " + roomNum)
                        .build();
                roomBuilder.addImages(imageInfo);
                // Log pour déboguer
                System.out.println("[ProtoMapper] Image ajoutée pour chambre " + roomNum + ": " +
                    dto.getRoom().getImageUrl().substring(0, Math.min(50, dto.getRoom().getImageUrl().length())) + "...");
            } else {
                System.out.println("[ProtoMapper] PAS d'image pour chambre " + roomNum +
                    " - room=" + (dto.getRoom() != null) +
                    ", imageUrl=" + (dto.getRoom() != null ? dto.getRoom().getImageUrl() : "null"));
            }

            builder.setRoom(roomBuilder.build());
        }

        // Dates
        if (dto.getStart() != null) {
            builder.setArrivalDate(DateConverter.toProto(dto.getStart()));
        }
        if (dto.getEnd() != null) {
            builder.setDepartureDate(DateConverter.toProto(dto.getEnd()));
        }

        // Prix
        builder.setTotalPrice(dto.getPrixTotal());
        builder.setFinalPrice(dto.getPrixTotal());
        builder.setPricePerNight(dto.getPrixTotal());

        // Calculer nombre de nuits
        if (dto.getStart() != null && dto.getEnd() != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(dto.getStart(), dto.getEnd());
            builder.setNumNights((int) nights);
        }

        return builder.build();
    }

    public static OfferDTO fromProtoOffer(Offer proto) {
        OfferDTO dto = new OfferDTO();
        dto.setOfferId(proto.getOfferId());

        if (proto.hasHotel()) {
            dto.setHotelName(proto.getHotel().getName());
            dto.setNbEtoiles(proto.getHotel().getStars());
            if (proto.getHotel().hasAddress()) {
                dto.setAddress(fromProtoAddress(proto.getHotel().getAddress()));
            }
        }

        if (proto.hasRoom()) {
            RoomDTO roomDto = new RoomDTO();
            roomDto.setNbLits(proto.getRoom().getCapacity());
            roomDto.setPrixParNuit((int) proto.getRoom().getPricePerNight());
            try {
                roomDto.setNumero(Integer.parseInt(proto.getRoom().getId()));
                dto.setRoomNumber(Integer.parseInt(proto.getRoom().getId()));
            } catch (NumberFormatException e) {
                roomDto.setNumero(0);
                dto.setRoomNumber(0);
            }
            dto.setRoom(roomDto);
            dto.setCategorie(proto.getRoom().getCategory());
        }

        dto.setPrixTotal((int) proto.getTotalPrice());

        if (proto.hasArrivalDate()) {
            dto.setStart(DateConverter.fromProto(proto.getArrivalDate()));
        }
        if (proto.hasDepartureDate()) {
            dto.setEnd(DateConverter.fromProto(proto.getDepartureDate()));
        }

        return dto;
    }

    // ==================== AddressDTO ↔ Address ====================

    public static Address toProtoAddress(AddressDTO dto) {
        Address.Builder builder = Address.newBuilder();

        if (dto.getRue() != null) {
            builder.setStreet(dto.getRue() + " " + dto.getNumero());
        }
        if (dto.getVille() != null) {
            builder.setCity(dto.getVille());
        }
        if (dto.getLieuDit() != null) {
            builder.setPostalCode(dto.getLieuDit());
        }
        if (dto.getPays() != null) {
            builder.setCountry(dto.getPays());
        }

        return builder.build();
    }

    public static AddressDTO fromProtoAddress(Address proto) {
        AddressDTO dto = new AddressDTO();

        // Mapper street -> rue (en ignorant le numéro pour simplifier)
        if (!proto.getStreet().isEmpty()) {
            dto.setRue(proto.getStreet());
            dto.setNumero(0); // Valeur par défaut
        }

        dto.setVille(proto.getCity());
        dto.setLieuDit(proto.getPostalCode());
        dto.setPays(proto.getCountry());
        dto.setLatitude(0.0);
        dto.setLongitude(0.0);

        return dto;
    }

    // ==================== SearchRequestDTO ↔ SearchRequest ====================

    public static SearchRequestDTO fromProtoSearchRequest(SearchRequest proto) {
        SearchRequestDTO dto = new SearchRequestDTO();

        if (!proto.getCity().isEmpty()) {
            dto.setVille(proto.getCity());
        }

        dto.setNbPersonnes(proto.getNumPersons());

        if (proto.hasArrivalDate()) {
            dto.setArrivee(DateConverter.fromProto(proto.getArrivalDate()));
        }
        if (proto.hasDepartureDate()) {
            dto.setDepart(DateConverter.fromProto(proto.getDepartureDate()));
        }

        if (!proto.getCategory().isEmpty()) {
            dto.setCategorie(proto.getCategory());
        }

        if (proto.getMinStars() > 0) {
            dto.setNbEtoiles(proto.getMinStars());
        }

        if (proto.hasPriceRange()) {
            dto.setPrixMin(proto.getPriceRange().getMinPrice());
            dto.setPrixMax(proto.getPriceRange().getMaxPrice());
        }

        if (!proto.getAgency().isEmpty()) {
            dto.setAgence(proto.getAgency());
        }

        return dto;
    }

    // ==================== ReservationRequestDTO ↔ ReservationRequest ====================

    public static ReservationRequestDTO fromProtoReservationRequest(ReservationRequest proto) {
        ReservationRequestDTO dto = new ReservationRequestDTO();

        // Mapper les noms (client_name -> nom)
        dto.setNom(proto.getClientName());

        // Extraire le numéro de chambre du room_id
        try {
            dto.setRoomNumber(Integer.parseInt(proto.getRoomId()));
        } catch (NumberFormatException e) {
            // Si ce n'est pas un nombre, on le laisse à 0
            dto.setRoomNumber(0);
        }

        // Mapper les dates
        if (proto.hasArrivalDate()) {
            dto.setArrivee(DateConverter.fromProto(proto.getArrivalDate()));
        }
        if (proto.hasDepartureDate()) {
            dto.setDepart(DateConverter.fromProto(proto.getDepartureDate()));
        }

        // Mapper l'agence si fournie
        if (!proto.getAgencyName().isEmpty()) {
            dto.setAgence(proto.getAgencyName());
        }

        return dto;
    }

    public static Reservation toProtoReservation(ReservationConfirmationDTO dto, String hotelId) {
        Reservation.Builder builder = Reservation.newBuilder()
                .setReservationId(dto.getId() != null ? dto.getId() : "")
                .setHotelId(hotelId)
                .setStatus(dto.isSuccess() ?
                    Reservation.ReservationStatus.RESERVATION_STATUS_CONFIRMED :
                    Reservation.ReservationStatus.RESERVATION_STATUS_PENDING)
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis());

        // Si l'offre est disponible, extraire les infos
        if (dto.getOffer() != null) {
            OfferDTO offer = dto.getOffer();
            builder.setTotalPrice(offer.getPrixTotal());

            // Ajouter les infos d'hôtel si disponibles
            if (offer.getHotelName() != null) {
                HotelInfo.Builder hotelBuilder = HotelInfo.newBuilder()
                        .setId(hotelId)
                        .setName(offer.getHotelName())
                        .setStars(offer.getNbEtoiles());
                if (offer.getAddress() != null) {
                    hotelBuilder.setAddress(toProtoAddress(offer.getAddress()));
                }
                builder.setHotel(hotelBuilder.build());
            }

            // Ajouter les infos de chambre
            if (offer.getRoom() != null) {
                RoomType.Builder roomBuilder = RoomType.newBuilder()
                        .setId(String.valueOf(offer.getRoomNumber()))
                        .setCategory(offer.getCategorie() != null ? offer.getCategorie() : "STANDARD")
                        .setCapacity(offer.getNbLits())
                        .setPricePerNight(offer.getPrixTotal());
                builder.setRoom(roomBuilder.build());
            }

            // Ajouter les dates
            if (offer.getStart() != null) {
                builder.setArrivalDate(DateConverter.toProto(offer.getStart()));
            }
            if (offer.getEnd() != null) {
                builder.setDepartureDate(DateConverter.toProto(offer.getEnd()));
            }
        }

        // Utiliser l'ID comme code de confirmation si disponible
        if (dto.getId() != null) {
            builder.setConfirmationCode(dto.getId());
        }

        return builder.build();
    }

    // ==================== OfferListDTO ↔ OffersResponse ====================

    public static OffersResponse toProtoOffersResponse(List<OfferDTO> offers, SearchRequest originalRequest) {
        OffersResponse.Builder builder = OffersResponse.newBuilder()
                .setTotalCount(offers.size());

        for (OfferDTO offer : offers) {
            builder.addOffers(toProtoOffer(offer));
        }

        if (originalRequest != null) {
            builder.setOriginalRequest(originalRequest);
        }

        return builder.build();
    }

    // ==================== Helpers ====================

    public static PingResponse createPingResponse(String message, String serverId) {
        return PingResponse.newBuilder()
                .setMessage("Pong: " + message)
                .setTimestamp(System.currentTimeMillis())
                .setServerId(serverId)
                .build();
    }
}

