package rw.auca.verticalfarmingweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "crop_family")
public class CropFamily extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "cropFamilies")
    @JsonIgnore
    private List<Grower> growers = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Grower> getGrowers() {
        return growers;
    }

    public void setGrowers(List<Grower> growers) {
        this.growers = growers;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CropFamily cropFamily)) {
            return false;
        }
        return id != null && id.equals(cropFamily.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
