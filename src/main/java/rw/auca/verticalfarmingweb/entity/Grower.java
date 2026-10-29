package rw.auca.verticalfarmingweb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "grower")
public class Grower extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "grow_tower_id")
    private GrowTower growTower;

    @ManyToMany
    @JoinTable(name = "grower_crop_family")
    private List<CropFamily> cropFamilies = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public GrowTower getGrowTower() {
        return growTower;
    }

    public void setGrowTower(GrowTower growTower) {
        this.growTower = growTower;
    }

    public List<CropFamily> getCropFamilies() {
        return cropFamilies;
    }

    public void setCropFamilies(List<CropFamily> cropFamilies) {
        this.cropFamilies = cropFamilies;
    }
}
