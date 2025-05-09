package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import zercher.be.model.entity.ActivityType;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
    boolean existsByName(String name);
}
