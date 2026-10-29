package rw.auca.verticalfarmingweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.auca.verticalfarmingweb.entity.GrowTower;
import rw.auca.verticalfarmingweb.entity.Grower;

import java.util.List;
import java.util.UUID;

public interface GrowerRepository extends JpaRepository<Grower, UUID> {

    @Query("SELECT DISTINCT g FROM Grower g JOIN g.cropFamilies c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Grower> findByCropFamilyName(@Param("name") String name);

    boolean existsByGrowTowerAndIdNot(GrowTower growTower, UUID id);
}
