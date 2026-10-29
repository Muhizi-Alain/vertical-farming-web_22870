package rw.auca.verticalfarmingweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.auca.verticalfarmingweb.entity.CropFamily;

import java.util.List;
import java.util.UUID;

public interface CropFamilyRepository extends JpaRepository<CropFamily, UUID> {
//we are selecting from cropfamily table where everything on grower is empty
    @Query("SELECT c FROM CropFamily c WHERE c.growers IS EMPTY")
    List<CropFamily> findUnused();
}
