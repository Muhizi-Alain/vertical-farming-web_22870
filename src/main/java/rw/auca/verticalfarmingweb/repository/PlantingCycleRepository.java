package rw.auca.verticalfarmingweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.verticalfarmingweb.entity.Grower;
import rw.auca.verticalfarmingweb.entity.PlantingCycle;
import rw.auca.verticalfarmingweb.enums.CycleStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PlantingCycleRepository extends JpaRepository<PlantingCycle, UUID> {

    List<PlantingCycle> findByStatusOrderByPlantingDateAsc(CycleStatus status);

    List<PlantingCycle> findByPlantingDateBetweenOrderByPlantingDateAsc(LocalDate start, LocalDate end);

    boolean existsByGrowerAndPlantingDateAndStatusNot(Grower grower, LocalDate plantingDate, CycleStatus status);

    long countByStatus(CycleStatus status);

    long countByPlantingDate(LocalDate plantingDate);

    @Query("SELECT p.status, COUNT(p) FROM PlantingCycle p GROUP BY p.status")
    List<Object[]> countGroupedByStatus();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PlantingCycle p SET p.status = :failed, p.modificationDate = :modifiedAt WHERE p.grower.id = :growerId AND p.plantingDate = :date AND p.status <> :harvested")
    int cancelDay(@Param("growerId") UUID growerId, @Param("date") LocalDate date, @Param("failed") CycleStatus failed, @Param("harvested") CycleStatus harvested, @Param("modifiedAt") LocalDateTime modifiedAt);
}
