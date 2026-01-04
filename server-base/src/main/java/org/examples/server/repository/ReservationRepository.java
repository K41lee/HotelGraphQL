package org.examples.server.repository;
import org.examples.server.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByChambreId(Long chambreId);
    @Query("SELECT r FROM ReservationEntity r WHERE r.chambre.id = :chambreId " +
           "AND r.debut < :fin AND r.fin > :debut")
    List<ReservationEntity> findOverlappingReservations(
        @Param("chambreId") Long chambreId,
        @Param("debut") LocalDate debut,
        @Param("fin") LocalDate fin
    );
}
