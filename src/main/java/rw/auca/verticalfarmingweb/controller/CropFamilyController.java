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
import rw.auca.verticalfarmingweb.entity.CropFamily;
import rw.auca.verticalfarmingweb.service.CropFamilyService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/crop-families")
public class CropFamilyController {

    private final CropFamilyService cropFamilyService;

    public CropFamilyController(CropFamilyService cropFamilyService) {
        this.cropFamilyService = cropFamilyService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody CropFamily cropFamily) {
        String message = cropFamilyService.save(cropFamily);
        if (message.equals("Crop family saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CropFamily>> findAll() {
        return new ResponseEntity<>(cropFamilyService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/unused")
    public ResponseEntity<List<CropFamily>> findUnused() {
        return new ResponseEntity<>(cropFamilyService.findUnused(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<CropFamily> found = cropFamilyService.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>("The crop family with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found.get(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody CropFamily cropFamily) {
        Optional<CropFamily> updated = cropFamilyService.update(id, cropFamily);
        if (updated.isEmpty()) {
            return new ResponseEntity<>("The crop family with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = cropFamilyService.delete(id);
        if (message.equals("Crop family deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAll() {
        return new ResponseEntity<>(cropFamilyService.deleteAll(), HttpStatus.OK);
    }
}
