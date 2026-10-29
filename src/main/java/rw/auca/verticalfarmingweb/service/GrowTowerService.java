package rw.auca.verticalfarmingweb.service;

import org.springframework.stereotype.Service;
import rw.auca.verticalfarmingweb.entity.GrowTower;
import rw.auca.verticalfarmingweb.repository.GrowTowerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GrowTowerService {
// Create variable for accessing grow tower repository
    private final GrowTowerRepository growTowerRepository;

    public GrowTowerService(GrowTowerRepository growTowerRepository) {
        this.growTowerRepository = growTowerRepository;
    }
//CRUD method implimentation
//Insert 
    public String save(GrowTower growTower) {
        growTower.setId(null);
        growTowerRepository.save(growTower);
        return "Grow tower saved successfully";
    }
//Search all Growtowers
    public List<GrowTower> findAll() {
        return growTowerRepository.findAll();
    }
//Search one growertower using its ID
    public Optional<GrowTower> findById(UUID id) {
        return growTowerRepository.findById(id);
    }
//Update one growtower by ID
    public Optional<GrowTower> update(UUID id, GrowTower body) {
        Optional<GrowTower> found = growTowerRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        GrowTower existing = found.get();
        existing.setTowerNumber(body.getTowerNumber());
        existing.setName(body.getName());
        return Optional.of(growTowerRepository.save(existing));
    }
//Delete one growtower refer to its ID
    public String delete(UUID id) {
        if (growTowerRepository.findById(id).isEmpty()) {
            return "The grow tower with that id does not exist";
        }
        growTowerRepository.deleteById(id);
        return "Grow tower deleted successfully";
    }
//Search free tower
    public List<GrowTower> findFreeTowers() {
        return growTowerRepository.findFreeTowers();
    }
}
