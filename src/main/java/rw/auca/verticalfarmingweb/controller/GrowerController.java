package rw.auca.verticalfarmingweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.auca.verticalfarmingweb.entity.Grower;
import rw.auca.verticalfarmingweb.service.GrowerService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/growers")
public class GrowerController {

    private final GrowerService growerService;

    public GrowerController(GrowerService growerService) {
        this.growerService = growerService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Grower grower) {
        String message = growerService.save(grower);
        if (message.equals("Grower saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        if (message.equals("The grow tower with that id does not exist") || message.equals("That crop family does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Grower>> findAll() {
        return new ResponseEntity<>(growerService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/by-crop-family")
    public ResponseEntity<List<Grower>> findByCropFamily(@RequestParam String name) {
        return new ResponseEntity<>(growerService.findByCropFamilyName(name), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<Grower> found = growerService.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>("The grower with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found.get(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Grower grower) {
        Optional<Grower> updated = growerService.update(id, grower);
        if (updated.isEmpty()) {
            return new ResponseEntity<>("The grower with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = growerService.delete(id);
        if (message.equals("Grower deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{growerId}/crop-families/{cropFamilyId}")
    public ResponseEntity<String> addCropFamily(@PathVariable UUID growerId, @PathVariable UUID cropFamilyId) {
        String message = growerService.addCropFamily(growerId, cropFamilyId);
        if (message.equals("Crop family added to the grower")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals("The grower already has that crop family")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{growerId}/grow-tower")
    public ResponseEntity<String> changeTower(@PathVariable UUID growerId, @RequestParam int towerNumber) {
        String message = growerService.changeTower(growerId, towerNumber);
        if (message.equals("Grow tower changed successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals("That grow tower is taken by another grower")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
