package rw.auca.verticalfarmingweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.verticalfarmingweb.entity.HarvestRecord;
import rw.auca.verticalfarmingweb.entity.PlantingCycle;

import java.util.List;
import java.util.UUID;

public interface HarvestRecordRepository extends JpaRepository<HarvestRecord, UUID> {

    boolean existsByPlantingCycle(PlantingCycle plantingCycle);

    @Query("SELECT h FROM HarvestRecord h WHERE h.plantingCycle.cropBatch.batchCode = :batchCode ORDER BY h.recordedDate DESC")
    List<HarvestRecord> findByBatchCode(@Param("batchCode") String batchCode);
}
