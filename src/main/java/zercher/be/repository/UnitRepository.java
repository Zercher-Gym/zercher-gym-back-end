package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
}
