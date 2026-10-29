package rw.auca.verticalfarmingweb.service;

import org.springframework.stereotype.Service;
import rw.auca.verticalfarmingweb.enums.CycleStatus;
import rw.auca.verticalfarmingweb.repository.CropBatchRepository;
import rw.auca.verticalfarmingweb.repository.GrowTowerRepository;
import rw.auca.verticalfarmingweb.repository.GrowerRepository;
import rw.auca.verticalfarmingweb.repository.HarvestRecordRepository;
import rw.auca.verticalfarmingweb.repository.PlantingCycleRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final CropBatchRepository cropBatchRepository;
    private final GrowerRepository growerRepository;
    private final GrowTowerRepository growTowerRepository;
    private final PlantingCycleRepository plantingCycleRepository;
    private final HarvestRecordRepository harvestRecordRepository;

    public DashboardService(CropBatchRepository cropBatchRepository, GrowerRepository growerRepository, GrowTowerRepository growTowerRepository, PlantingCycleRepository plantingCycleRepository, HarvestRecordRepository harvestRecordRepository) {
        this.cropBatchRepository = cropBatchRepository;
        this.growerRepository = growerRepository;
        this.growTowerRepository = growTowerRepository;
        this.plantingCycleRepository = plantingCycleRepository;
        this.harvestRecordRepository = harvestRecordRepository;
    }

    public Map<String, Long> summary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("cropBatches", cropBatchRepository.count());
        summary.put("growers", growerRepository.count());
        summary.put("freeGrowTowers", (long) growTowerRepository.findFreeTowers().size());
        summary.put("cyclesToday", plantingCycleRepository.countByPlantingDate(LocalDate.now()));
        summary.put("planned", plantingCycleRepository.countByStatus(CycleStatus.PLANNED));
        summary.put("harvestRecords", harvestRecordRepository.count());
        return summary;
    }
}
