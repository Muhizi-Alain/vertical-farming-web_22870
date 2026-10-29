package rw.auca.verticalfarmingweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.auca.verticalfarmingweb.entity.GrowTower;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrowTowerRepository extends JpaRepository<GrowTower, UUID> {

    Optional<GrowTower> findByTowerNumber(int towerNumber);

    @Query("SELECT t FROM GrowTower t WHERE NOT EXISTS (SELECT g FROM Grower g WHERE g.growTower = t) ORDER BY t.towerNumber")
    List<GrowTower> findFreeTowers();
}
