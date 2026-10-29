package rw.auca.verticalfarmingweb.service;

import org.springframework.stereotype.Service;
import rw.auca.verticalfarmingweb.entity.HarvestRecord;
import rw.auca.verticalfarmingweb.entity.PlantingCycle;
import rw.auca.verticalfarmingweb.enums.CycleStatus;
import rw.auca.verticalfarmingweb.repository.HarvestRecordRepository;
import rw.auca.verticalfarmingweb.repository.PlantingCycleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HarvestRecordService {
//Create variables for accessing both harvestrecord and plantingcycle repository
    private final HarvestRecordRepository harvestRecordRepository;
    private final PlantingCycleRepository plantingCycleRepository;

    public HarvestRecordService(HarvestRecordRepository harvestRecordRepository, PlantingCycleRepository plantingCycleRepository) {
        this.harvestRecordRepository = harvestRecordRepository;
        this.plantingCycleRepository = plantingCycleRepository;
    }
//CRUD method implimentation    
//Insert plantingcycle
    public String save(UUID plantingCycleId, HarvestRecord body) {
        Optional<PlantingCycle> found = plantingCycleRepository.findById(plantingCycleId);
        if (found.isEmpty()) {
            return "The planting cycle with that id does not exist";
        }
        PlantingCycle plantingCycle = found.get();
        if (!(plantingCycle.getStatus() == CycleStatus.HARVESTED)) {
            return "Harvest records are only for HARVESTED planting cycles";
        }
        HarvestRecord record = new HarvestRecord();
        record.setQualityGrade(body.getQualityGrade());
        record.setNotes(body.getNotes());
        record.setKilograms(body.getKilograms());
        record.setRecordedDate(LocalDate.now());
        record.setPlantingCycle(plantingCycle);
        harvestRecordRepository.save(record);
        return "Harvest record saved successfully";
    }
//Search harvestrecord using batchcode
    public List<HarvestRecord> findByBatchCode(String batchCode) {
        return harvestRecordRepository.findByBatchCode(batchCode);
    }
//Delete one harvestrecord refer to its ID
    public String delete(UUID id) {
        if (harvestRecordRepository.findById(id).isEmpty()) {
            return "The harvest record with that id does not exist";
        }
        harvestRecordRepository.deleteById(id);
        return "Harvest record deleted successfully";
    }
}
