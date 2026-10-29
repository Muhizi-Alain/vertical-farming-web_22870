package rw.auca.verticalfarmingweb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.auca.verticalfarmingweb.entity.CropBatch;
import rw.auca.verticalfarmingweb.entity.Grower;
import rw.auca.verticalfarmingweb.entity.PlantingCycle;
import rw.auca.verticalfarmingweb.enums.CycleStatus;
import rw.auca.verticalfarmingweb.repository.CropBatchRepository;
import rw.auca.verticalfarmingweb.repository.GrowerRepository;
import rw.auca.verticalfarmingweb.repository.HarvestRecordRepository;
import rw.auca.verticalfarmingweb.repository.PlantingCycleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlantingCycleService {
//Create variables for accessing plantingcyclerepository,cropbatchrepository,growertowerrepository,harvestrecordrepository
    private final PlantingCycleRepository plantingCycleRepository;
    private final CropBatchRepository cropBatchRepository;
    private final GrowerRepository growerRepository;
    private final HarvestRecordRepository harvestRecordRepository;

    public PlantingCycleService(PlantingCycleRepository plantingCycleRepository, CropBatchRepository cropBatchRepository, GrowerRepository growerRepository, HarvestRecordRepository harvestRecordRepository) {
        this.plantingCycleRepository = plantingCycleRepository;
        this.cropBatchRepository = cropBatchRepository;
        this.growerRepository = growerRepository;
        this.harvestRecordRepository = harvestRecordRepository;
    }
//CRUD method implimentation    
//Insert Plantingcycle
    public String save(PlantingCycle body) {
        if (body.getPlantingDate() != null && body.getPlantingDate().isBefore(LocalDate.now())) {
            return "Planting cycle date cannot be in the past";
        }
        PlantingCycle cycle = new PlantingCycle();
        cycle.setPlantingDate(body.getPlantingDate());
        cycle.setTrays(body.getTrays());
        cycle.setStatus(CycleStatus.PLANNED);
        String missing = attachRelations(cycle, body);
        if (missing != null) {
            return missing;
        }
        if (plantingCycleRepository.existsByGrowerAndPlantingDateAndStatusNot(cycle.getGrower(), cycle.getPlantingDate(), CycleStatus.FAILED)) {
            return "Grower already starts a planting cycle on that date";
        }
        plantingCycleRepository.save(cycle);
        return "Planting cycle saved successfully";
    }
//Search all Plantingcycle
    public List<PlantingCycle> findAll() {
        return plantingCycleRepository.findAll();
    }
//Search one plantingcycle refer to its ID
    public Optional<PlantingCycle> findById(UUID id) {
        return plantingCycleRepository.findById(id);
    }
//Update platingcycle using its ID
    public Optional<PlantingCycle> update(UUID id, PlantingCycle body) {
        Optional<PlantingCycle> found = plantingCycleRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        if (body.getCropBatch() == null || body.getCropBatch().getId() == null || body.getGrower() == null || body.getGrower().getId() == null) {
            return Optional.empty();
        }
        Optional<CropBatch> batch = cropBatchRepository.findById(body.getCropBatch().getId());
        Optional<Grower> grower = growerRepository.findById(body.getGrower().getId());
        if (batch.isEmpty() || grower.isEmpty()) {
            return Optional.empty();
        }
        PlantingCycle existing = found.get();
        existing.setPlantingDate(body.getPlantingDate());
        existing.setTrays(body.getTrays());
        existing.setCropBatch(batch.get());
        existing.setGrower(grower.get());
        return Optional.of(plantingCycleRepository.save(existing));
    }
//Delete one plantingcycle using its ID
    public String delete(UUID id) {
        Optional<PlantingCycle> found = plantingCycleRepository.findById(id);
        if (found.isEmpty()) {
            return "The planting cycle with that id does not exist";
        }
        if (harvestRecordRepository.existsByPlantingCycle(found.get())) {
            return "That planting cycle has harvest records, delete them first";
        }
        plantingCycleRepository.delete(found.get());
        return "Planting cycle deleted successfully";
    }
//Search plantingcycle status
    public List<PlantingCycle> findByStatus(CycleStatus status) {
        return plantingCycleRepository.findByStatusOrderByPlantingDateAsc(status);
    }
//Search plantingcycle refer to start date and end date
    public List<PlantingCycle> findBetween(LocalDate start, LocalDate end) {
        return plantingCycleRepository.findByPlantingDateBetweenOrderByPlantingDateAsc(start, end);
    }
//Update plantingcycle refering to its ID and Status
    public String updateStatus(UUID id, CycleStatus nextStatus) {
        Optional<PlantingCycle> found = plantingCycleRepository.findById(id);
        if (found.isEmpty()) {
            return "The planting cycle with that id does not exist";
        }
        PlantingCycle cycle = found.get();
        CycleStatus current = cycle.getStatus();
        if (!isAllowed(current, nextStatus)) {
            return "Cannot change status from " + current + " to " + nextStatus;
        }
        cycle.setStatus(nextStatus);
        plantingCycleRepository.save(cycle);
        return "Planting cycle status updated successfully";
    }
//count all existing plantingcycle by their group
    public List<Object[]> countGroupedByStatus() {
        return plantingCycleRepository.countGroupedByStatus();
    }
//canceling plantingcycle referring to its grower ID and date
    @Transactional
    public String cancelDay(UUID growerId, LocalDate date) {
        int count = plantingCycleRepository.cancelDay(growerId, date, CycleStatus.FAILED, CycleStatus.HARVESTED, LocalDateTime.now());
        return count + " planting cycles cancelled";
    }
//search plantingcycle using its page
    public Page<PlantingCycle> findPage(Pageable pageable) {
        return plantingCycleRepository.findAll(pageable);
    }
//flagging cyclestatus according on its enum (planned,growing,harvested and failed)
    private boolean isAllowed(CycleStatus current, CycleStatus nextStatus) {
        if (current == CycleStatus.PLANNED) {
            return nextStatus == CycleStatus.GROWING || nextStatus == CycleStatus.FAILED;
        }
        if (current == CycleStatus.GROWING) {
            return nextStatus == CycleStatus.HARVESTED || nextStatus == CycleStatus.FAILED;
        }
        return false;
    }
//attaching cropbatch and its grower
    private String attachRelations(PlantingCycle cycle, PlantingCycle body) {
        if (body.getCropBatch() == null || body.getCropBatch().getId() == null) {
            return "The crop batch with that id does not exist";
        }
        Optional<CropBatch> batch = cropBatchRepository.findById(body.getCropBatch().getId());
        if (batch.isEmpty()) {
            return "The crop batch with that id does not exist";
        }
        if (body.getGrower() == null || body.getGrower().getId() == null) {
            return "The grower with that id does not exist";
        }
        Optional<Grower> grower = growerRepository.findById(body.getGrower().getId());
        if (grower.isEmpty()) {
            return "The grower with that id does not exist";
        }
        cycle.setCropBatch(batch.get());
        cycle.setGrower(grower.get());
        return null;
    }
}
