package org.examples.server.repository;
import org.examples.server.entity.ChambreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface ChambreRepository extends JpaRepository<ChambreEntity, Long> {
    List<ChambreEntity> findByHotelId(Long hotelId);
    java.util.Optional<ChambreEntity> findByNumero(int numero);
    @Query("SELECT c FROM ChambreEntity c WHERE c.hotel.id = :hotelId AND c.nbLits >= :nbPersonnes " +
           "AND c.id NOT IN (" +
           "  SELECT DISTINCT r.chambre.id FROM ReservationEntity r " +
           "  WHERE r.debut < :fin AND r.fin > :debut" +
           ")")
    List<ChambreEntity> findAvailableChambres(
        @Param("hotelId") Long hotelId,
        @Param("nbPersonnes") int nbPersonnes,
        @Param("debut") LocalDate debut,
        @Param("fin") LocalDate fin
    );
}
