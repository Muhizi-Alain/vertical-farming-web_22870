package rw.auca.verticalfarmingweb.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.auca.verticalfarmingweb.entity.CropFamily;
import rw.auca.verticalfarmingweb.entity.Grower;
import rw.auca.verticalfarmingweb.repository.CropFamilyRepository;
import rw.auca.verticalfarmingweb.repository.GrowerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CropFamilyService {

    private final CropFamilyRepository cropFamilyRepository;
    private final GrowerRepository growerRepository;

    public CropFamilyService(CropFamilyRepository cropFamilyRepository, GrowerRepository growerRepository) {
        this.cropFamilyRepository = cropFamilyRepository;
        this.growerRepository = growerRepository;
    }
//Insert
    public String save(CropFamily cropFamily) {
        cropFamily.setId(null);
        cropFamilyRepository.save(cropFamily);
        return "Crop family saved successfully";

    }
//Search all
    public List<CropFamily> findAll() {
        return cropFamilyRepository.findAll();
    }
//Search by ID
    public Optional<CropFamily> findById(UUID id) {
        return cropFamilyRepository.findById(id);
    }
//Upadate
    public Optional<CropFamily> update(UUID id, CropFamily body) {
        Optional<CropFamily> found = cropFamilyRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        CropFamily existing = found.get();
        existing.setName(body.getName());
        return Optional.of(cropFamilyRepository.save(existing));
    }
//Delete
    @Transactional
    public String delete(UUID id) {
        Optional<CropFamily> found = cropFamilyRepository.findById(id);
        if (found.isEmpty()) {
            return "The crop family with that id does not exist";
        }
        CropFamily cropFamily = found.get();
        for (Grower grower : new ArrayList<>(cropFamily.getGrowers())) {
            grower.getCropFamilies().remove(cropFamily);
            growerRepository.save(grower);
        }
        cropFamilyRepository.delete(cropFamily);
        return "Crop family deleted successfully";
    }
//Search Unsused cropfamily
    public List<CropFamily> findUnused() {
        return cropFamilyRepository.findUnused();
    }
}
