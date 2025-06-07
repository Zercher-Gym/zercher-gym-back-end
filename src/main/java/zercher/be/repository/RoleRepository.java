package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import zercher.be.model.entity.Role;
import zercher.be.model.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select max(r.exerciseLimit) from User u inner join u.roles r where u=:user")
    Integer getMaxExerciseLimit(User user);

    @Query("select max(r.workoutLimit) from User u inner join u.roles r where u=:user")
    Integer getMaxWorkoutLimit(User user);

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Set<Role> findAllByNameIn(Collection<String> names);
}
