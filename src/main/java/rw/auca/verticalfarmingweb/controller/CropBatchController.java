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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.auca.verticalfarmingweb.entity.CropBatch;
import rw.auca.verticalfarmingweb.service.CropBatchService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/crop-batches")
public class CropBatchController {

    private final CropBatchService cropBatchService;

    public CropBatchController(CropBatchService cropBatchService) {
        this.cropBatchService = cropBatchService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody CropBatch cropBatch) {
        String message = cropBatchService.save(cropBatch);
        if (message.equals("Crop batch saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CropBatch>> findAll() {
        return new ResponseEntity<>(cropBatchService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/by-crop-name")
    public ResponseEntity<List<CropBatch>> findByCropName(@RequestParam String cropName) {
        return new ResponseEntity<>(cropBatchService.findByCropName(cropName), HttpStatus.OK);
    }

    @GetMapping("/of-grower/{growerId}")
    public ResponseEntity<?> findOfGrower(@PathVariable UUID growerId) {
        Object result = cropBatchService.findOfGrower(growerId);
        if (result instanceof String message) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<CropBatch> found = cropBatchService.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>("The crop batch with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found.get(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody CropBatch cropBatch) {
        Optional<CropBatch> updated = cropBatchService.update(id, cropBatch);
        if (updated.isEmpty()) {
            return new ResponseEntity<>("The crop batch with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = cropBatchService.delete(id);
        if (message.equals("Crop batch deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
