package zercher.be.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import zercher.be.model.entity.*;
import zercher.be.model.enums.Language;

public class WorkoutSpecifications {
    public static Specification<Workout> languageIs(Language language) {
        return (root, _, cb) -> {
            Join<Workout, WorkoutLabel> labelJoin = root.join(Workout_.labels, JoinType.INNER);
            return cb.equal(labelJoin.get(WorkoutLabel_.language), language);
        };

    }

    public static Specification<Workout> titleContains(String title) {
        return (root, _, cb) -> {
            Join<Workout, WorkoutLabel> labelJoin = root.join(Workout_.labels, JoinType.INNER);
            return cb.like(cb.lower(labelJoin.get(WorkoutLabel_.title)), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Workout> descriptionContains(String description) {
        return (root, _, cb) -> {
            Join<Workout, WorkoutLabel> labelJoin = root.join(Workout_.labels, JoinType.INNER);
            return cb.like(cb.lower(labelJoin.get(WorkoutLabel_.description)), "%" + description.toLowerCase() + "%");
        };
    }

    public static Specification<Workout> identifierContains(String identifier) {
        return (root, _, cb) ->
                cb.like(cb.lower(root.get(Workout_.identifier)), "%" + identifier.toLowerCase() + "%");
    }

    public static Specification<Workout> contains(String contains) {
        return (root, _, cb) -> {
            Join<Workout, WorkoutLabel> labelJoin = root.join(Workout_.labels, JoinType.INNER);
            return cb.or(
                    cb.like(cb.lower(labelJoin.get(WorkoutLabel_.title)), "%" + contains + "%"),
                    cb.like(cb.lower(root.get(Workout_.identifier)), "%" + contains + "%"),
                    cb.like(cb.lower(labelJoin.get(WorkoutLabel_.description)), "%" + contains + "%")
            );
        };
    }
}
