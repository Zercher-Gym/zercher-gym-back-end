package zercher.be.repository.customexercise;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import zercher.be.model.entity.*;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomExerciseQueryRepositoryImpl implements CustomExerciseQueryRepository {
    private final EntityManager entityManager;

    @Override
    public Integer getMaxExerciseLimit(UUID userId) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Integer.class);

        Root<User> root = cq.from(User.class);
        Join<User, Role> roleJoin = root.join(User_.ROLES, JoinType.INNER);

        cq.select(cb.max(roleJoin.get(Role_.EXERCISE_LIMIT)));
        cq.where(cb.equal(root.get(User_.id), userId));

        var query = entityManager.createQuery(cq);
        return query.getSingleResult();
    }

    @Override
    public Long getCustomExerciseCount(UUID userId) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);

        Root<CustomExercise> root = cq.from(CustomExercise.class);
        Join<CustomExercise, User> userJoin = root.join(CustomExercise_.USER, JoinType.INNER);

        cq.select(cb.count(root.get(CustomExercise_.id)));
        cq.where(cb.equal(userJoin.get(User_.id), userId));

        var query = entityManager.createQuery(cq);
        return query.getSingleResult();
    }
}
