package rw.auca.verticalfarmingweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.auca.verticalfarmingweb.entity.GrowTower;
import rw.auca.verticalfarmingweb.service.GrowTowerService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/grow-towers")
public class GrowTowerController {

    private final GrowTowerService growTowerService;

    public GrowTowerController(GrowTowerService growTowerService) {
        this.growTowerService = growTowerService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody GrowTower growTower) {
        String message = growTowerService.save(growTower);
        if (message.equals("Grow tower saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GrowTower>> findAll() {
        return new ResponseEntity<>(growTowerService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/free")
    public ResponseEntity<List<GrowTower>> findFree() {
        return new ResponseEntity<>(growTowerService.findFreeTowers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<GrowTower> found = growTowerService.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>("The grow tower with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found.get(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody GrowTower growTower) {
        Optional<GrowTower> updated = growTowerService.update(id, growTower);
        if (updated.isEmpty()) {
            return new ResponseEntity<>("The grow tower with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = growTowerService.delete(id);
        if (message.equals("Grow tower deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
