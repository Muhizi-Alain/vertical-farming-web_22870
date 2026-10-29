package rw.auca.verticalfarmingweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.auca.verticalfarmingweb.entity.HarvestRecord;
import rw.auca.verticalfarmingweb.service.HarvestRecordService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/harvest-records")
public class HarvestRecordController {

    private final HarvestRecordService harvestRecordService;

    public HarvestRecordController(HarvestRecordService harvestRecordService) {
        this.harvestRecordService = harvestRecordService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID plantingCycleId, @RequestBody HarvestRecord harvestRecord) {
        String message = harvestRecordService.save(plantingCycleId, harvestRecord);
        if (message.equals("Harvest record saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        if (message.equals("Harvest records are only for HARVESTED planting cycles")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/crop-batch/{batchCode}")
    public ResponseEntity<List<HarvestRecord>> findByBatchCode(@PathVariable String batchCode) {
        return new ResponseEntity<>(harvestRecordService.findByBatchCode(batchCode), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = harvestRecordService.delete(id);
        if (message.equals("Harvest record deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
