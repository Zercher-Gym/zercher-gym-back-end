package zercher.be.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import zercher.be.model.entity.*;
import zercher.be.model.enums.Language;

public class ExerciseSpecifications {
    public static Specification<Exercise> languageIs(Language language) {
        return (root, _, cb) -> {
            Join<Exercise, ExerciseLabel> labelJoin = root.join(Exercise_.labels, JoinType.INNER);
            return cb.equal(labelJoin.get(ExerciseLabel_.language), language);
        };

    }

    public static Specification<Exercise> titleContains(String title) {
        return (root, _, cb) -> {
            Join<Exercise, ExerciseLabel> labelJoin = root.join(Exercise_.labels, JoinType.INNER);
            return cb.like(cb.lower(labelJoin.get(ExerciseLabel_.title)), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Exercise> descriptionContains(String description) {
        return (root, _, cb) -> {
            Join<Exercise, ExerciseLabel> labelJoin = root.join(Exercise_.labels, JoinType.INNER);
            return cb.like(cb.lower(labelJoin.get(ExerciseLabel_.description)), "%" + description.toLowerCase() + "%");
        };
    }

    public static Specification<Exercise> identifierContains(String identifier) {
        return (root, _, cb) ->
                cb.like(cb.lower(root.get(Exercise_.identifier)), "%" + identifier.toLowerCase() + "%");
    }

    public static Specification<Exercise> contains(String contains) {
        return (root, _, cb) -> {
            Join<Exercise, ExerciseLabel> labelJoin = root.join(Exercise_.labels, JoinType.INNER);
            return cb.or(
                    cb.like(cb.lower(labelJoin.get(ExerciseLabel_.title)), "%" + contains.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get(Exercise_.identifier)), "%" + contains.toLowerCase() + "%"),
                    cb.like(cb.lower(labelJoin.get(ExerciseLabel_.description)), "%" + contains.toLowerCase() + "%")
            );
        };
    }
}
