package rw.auca.verticalfarmingweb.service;

import org.springframework.stereotype.Service;
import rw.auca.verticalfarmingweb.entity.CropBatch;
import rw.auca.verticalfarmingweb.entity.Grower;
import rw.auca.verticalfarmingweb.repository.CropBatchRepository;
import rw.auca.verticalfarmingweb.repository.GrowerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CropBatchService {

    private final CropBatchRepository cropBatchRepository;
    private final GrowerRepository growerRepository;

    public CropBatchService(CropBatchRepository cropBatchRepository, GrowerRepository growerRepository) {
        this.cropBatchRepository = cropBatchRepository;
        this.growerRepository = growerRepository;
    }
//Insert
    public String save(CropBatch cropBatch) {
        cropBatch.setId(null);
        cropBatchRepository.save(cropBatch);
        return "Crop batch saved successfully";
    }
//Search all
    public List<CropBatch> findAll() {
        return cropBatchRepository.findAll();
    }
//search by ID
    public Optional<CropBatch> findById(UUID id) {
        return cropBatchRepository.findById(id);
    }
//Update
    public Optional<CropBatch> update(UUID id, CropBatch body) {
        Optional<CropBatch> found = cropBatchRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        CropBatch existing = found.get();
        existing.setCropName(body.getCropName());
        existing.setVariety(body.getVariety());
        existing.setBatchCode(body.getBatchCode());
        existing.setSupplier(body.getSupplier());
        return Optional.of(cropBatchRepository.save(existing));
    }
//Delete
    public String delete(UUID id) {
        if (cropBatchRepository.findById(id).isEmpty()) {
            return "The crop batch with that id does not exist";
        }
        cropBatchRepository.deleteById(id);
        return "Crop batch deleted successfully";
    }
//Search by name
    public List<CropBatch> findByCropName(String cropName) {
        return cropBatchRepository.findByCropNameIgnoreCaseOrderByVarietyAsc(cropName);
    }
//Search by GrowerID
    public Object findOfGrower(UUID growerId) {
        Optional<Grower> grower = growerRepository.findById(growerId);
        if (grower.isEmpty()) {
            return "The grower with that id does not exist";
        }
        return cropBatchRepository.findOfGrower(growerId);
    }
}
