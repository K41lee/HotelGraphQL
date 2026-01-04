package org.examples.server.repository;
import org.examples.server.entity.AgenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface AgenceRepository extends JpaRepository<AgenceEntity, Long> {
    List<AgenceEntity> findByHotelId(Long hotelId);
    Optional<AgenceEntity> findByNom(String nom);
}
