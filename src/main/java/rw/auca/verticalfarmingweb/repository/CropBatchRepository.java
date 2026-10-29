package rw.auca.verticalfarmingweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.verticalfarmingweb.entity.CropBatch;

import java.util.List;
import java.util.UUID;

public interface CropBatchRepository extends JpaRepository<CropBatch, UUID> {

    List<CropBatch> findByCropNameIgnoreCaseOrderByVarietyAsc(String cropName);
//we are selecting all cropbatch on a certain grower using his ID
    @Query("SELECT DISTINCT p.cropBatch FROM PlantingCycle p WHERE p.grower.id = :growerId")
    List<CropBatch> findOfGrower(@Param("growerId") UUID growerId);
}
