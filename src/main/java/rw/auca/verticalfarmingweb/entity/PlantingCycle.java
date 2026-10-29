package rw.auca.verticalfarmingweb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import rw.auca.verticalfarmingweb.enums.CycleStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "planting_cycle")
public class PlantingCycle extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate plantingDate;

    @Enumerated(EnumType.STRING)
    private CycleStatus status;

    private int trays;

    @ManyToOne
    @JoinColumn(name = "crop_batch_id")
    private CropBatch cropBatch;

    @ManyToOne
    @JoinColumn(name = "grower_id")
    private Grower grower;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public CycleStatus getStatus() {
        return status;
    }

    public void setStatus(CycleStatus status) {
        this.status = status;
    }

    public int getTrays() {
        return trays;
    }

    public void setTrays(int trays) {
        this.trays = trays;
    }

    public CropBatch getCropBatch() {
        return cropBatch;
    }

    public void setCropBatch(CropBatch cropBatch) {
        this.cropBatch = cropBatch;
    }

    public Grower getGrower() {
        return grower;
    }

    public void setGrower(Grower grower) {
        this.grower = grower;
    }
}
