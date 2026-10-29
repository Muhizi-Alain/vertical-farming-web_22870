package rw.auca.verticalfarmingweb.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.auca.verticalfarmingweb.entity.CropFamily;
import rw.auca.verticalfarmingweb.entity.GrowTower;
import rw.auca.verticalfarmingweb.entity.Grower;
import rw.auca.verticalfarmingweb.repository.CropFamilyRepository;
import rw.auca.verticalfarmingweb.repository.GrowTowerRepository;
import rw.auca.verticalfarmingweb.repository.GrowerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GrowerService {

    private final GrowerRepository growerRepository;
    private final GrowTowerRepository growTowerRepository;
    private final CropFamilyRepository cropFamilyRepository;

    public GrowerService(GrowerRepository growerRepository, GrowTowerRepository growTowerRepository, CropFamilyRepository cropFamilyRepository) {
        this.growerRepository = growerRepository;
        this.growTowerRepository = growTowerRepository;
        this.cropFamilyRepository = cropFamilyRepository;
    }
// Insert
    public String save(Grower body) {
        Grower grower = new Grower();
        grower.setFirstName(body.getFirstName());
        grower.setLastName(body.getLastName());
        grower.setPhone(body.getPhone());
        String missing = attachTowerAndFamilies(grower, body);
        if (missing != null) {
            return missing;
        }
        growerRepository.save(grower);
        return "Grower saved successfully";
    }
// Search all
    public List<Grower> findAll() {
        return growerRepository.findAll();
    }
// Search by ID
    public Optional<Grower> findById(UUID id) {
        return growerRepository.findById(id);
    }
// Update
    public Optional<Grower> update(UUID id, Grower body) {
        Optional<Grower> found = growerRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        Grower existing = found.get();
        existing.setFirstName(body.getFirstName());
        existing.setLastName(body.getLastName());
        existing.setPhone(body.getPhone());
        return Optional.of(growerRepository.save(existing));
    }
// Delete
    public String delete(UUID id) {
        if (growerRepository.findById(id).isEmpty()) {
            return "The grower with that id does not exist";
        }
        growerRepository.deleteById(id);
        return "Grower deleted successfully";
    }
// Search by cropfamilyName
    public List<Grower> findByCropFamilyName(String name) {
        return growerRepository.findByCropFamilyName(name);
    }
//Create
    @Transactional
    public String addCropFamily(UUID growerId, UUID cropFamilyId) {
        Optional<Grower> growerOpt = growerRepository.findById(growerId);
        if (growerOpt.isEmpty()) {
            return "The grower with that id does not exist";
        }
        Optional<CropFamily> familyOpt = cropFamilyRepository.findById(cropFamilyId);
        if (familyOpt.isEmpty()) {
            return "That crop family does not exist";
        }
        Grower grower = growerOpt.get();
        CropFamily cropFamily = familyOpt.get();
        if (grower.getCropFamilies() == null) {
            grower.setCropFamilies(new ArrayList<>());
        }
        if (grower.getCropFamilies().contains(cropFamily)) {
            return "The grower already has that crop family";
        }
        grower.getCropFamilies().add(cropFamily);
        growerRepository.save(grower);
        return "Crop family added to the grower";
    }
// Modify the tower method
    @Transactional
    public String changeTower(UUID growerId, int towerNumber) {
        Optional<Grower> growerOpt = growerRepository.findById(growerId);
        if (growerOpt.isEmpty()) {
            return "The grower with that id does not exist";
        }
        Optional<GrowTower> towerOpt = growTowerRepository.findByTowerNumber(towerNumber);
        if (towerOpt.isEmpty()) {
            return "The grow tower with that towerNumber does not exist";
        }
        Grower grower = growerOpt.get();
        GrowTower tower = towerOpt.get();
        if (growerRepository.existsByGrowTowerAndIdNot(tower, grower.getId())) {
            return "That grow tower is taken by another grower";
        }
        grower.setGrowTower(tower);
        growerRepository.save(grower);
        return "Grow tower changed successfully";
    }
// Attach tower on its family
    private String attachTowerAndFamilies(Grower grower, Grower body) {
        if (body.getGrowTower() != null && body.getGrowTower().getId() != null) {
            Optional<GrowTower> tower = growTowerRepository.findById(body.getGrowTower().getId());
            if (tower.isEmpty()) {
                return "The grow tower with that id does not exist";
            }
            grower.setGrowTower(tower.get());
        }
        List<CropFamily> families = new ArrayList<>();
        if (body.getCropFamilies() != null) {
            for (CropFamily incoming : body.getCropFamilies()) {
                if (incoming.getId() == null) {
                    return "That crop family does not exist";
                }
                Optional<CropFamily> family = cropFamilyRepository.findById(incoming.getId());
                if (family.isEmpty()) {
                    return "That crop family does not exist";
                }
                families.add(family.get());
            }
        }
        grower.setCropFamilies(families);
        return null;
    }
}
