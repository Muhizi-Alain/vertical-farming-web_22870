package rw.auca.verticalfarmingweb.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import rw.auca.verticalfarmingweb.entity.PlantingCycle;
import rw.auca.verticalfarmingweb.enums.CycleStatus;
import rw.auca.verticalfarmingweb.service.PlantingCycleService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/planting-cycles")
public class PlantingCycleController {

    private final PlantingCycleService plantingCycleService;

    public PlantingCycleController(PlantingCycleService plantingCycleService) {
        this.plantingCycleService = plantingCycleService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody PlantingCycle plantingCycle) {
        String message = plantingCycleService.save(plantingCycle);
        if (message.equals("Planting cycle saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        if (message.equals("Planting cycle date cannot be in the past")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (message.equals("Grower already starts a planting cycle on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        if (message.equals("The crop batch with that id does not exist") || message.equals("The grower with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlantingCycle>> findAll() {
        return new ResponseEntity<>(plantingCycleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<PlantingCycle>> findByStatus(@RequestParam CycleStatus status) {
        return new ResponseEntity<>(plantingCycleService.findByStatus(status), HttpStatus.OK);
    }

    @GetMapping("/between")
    public ResponseEntity<List<PlantingCycle>> findBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(plantingCycleService.findBetween(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> countByStatus() {
        return new ResponseEntity<>(plantingCycleService.countGroupedByStatus(), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PlantingCycle>> findPage(@RequestParam int page, @RequestParam int size, @RequestParam String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return new ResponseEntity<>(plantingCycleService.findPage(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<PlantingCycle> found = plantingCycleService.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>("The planting cycle with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found.get(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody PlantingCycle plantingCycle) {
        Optional<PlantingCycle> updated = plantingCycleService.update(id, plantingCycle);
        if (updated.isEmpty()) {
            return new ResponseEntity<>("The planting cycle with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = plantingCycleService.delete(id);
        if (message.equals("Planting cycle deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals("That planting cycle has harvest records, delete them first")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam CycleStatus status) {
        String message = plantingCycleService.updateStatus(id, status);
        if (message.equals("Planting cycle status updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals("The planting cycle with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID growerId, @RequestParam String date) {
        LocalDate day = LocalDate.parse(date);
        return new ResponseEntity<>(plantingCycleService.cancelDay(growerId, day), HttpStatus.OK);
    }
}
