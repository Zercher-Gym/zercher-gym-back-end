package zercher.be.repository.exercise;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import zercher.be.dto.exercise.ExerciseSearchAdminDTO;
import zercher.be.dto.exercise.ExerciseSearchDTO;
import zercher.be.dto.exercise.ExerciseViewDTO;
import zercher.be.dto.exerciselabel.ExerciseLabelViewAdminDTO;
import zercher.be.dto.exerciselabel.ExerciseLabelViewDTO;
import zercher.be.model.entity.*;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import zercher.be.dto.exercise.ExerciseViewAdminDTO;
import zercher.be.model.entity.Exercise;
import zercher.be.model.entity.ExerciseLabel;
import zercher.be.model.enums.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExerciseQueryRepositoryImpl implements ExerciseQueryRepository {
    private static final String EXERCISE_ID = "exerciseId";

    private final EntityManager entityManager;

    @Override
    public ExerciseViewAdminDTO getView(UUID id) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Tuple.class);

        Root<ExerciseLabel> root = cq.from(ExerciseLabel.class);
        Join<ExerciseLabel, Exercise> exerciseJoin = root.join(ExerciseLabel_.exercise, JoinType.INNER);

        cq.multiselect(
                root.get(ExerciseLabel_.id).alias(ExerciseLabel_.ID),
                root.get(ExerciseLabel_.language).alias(ExerciseLabel_.LANGUAGE),
                root.get(ExerciseLabel_.title).alias(ExerciseLabel_.TITLE),
                root.get(ExerciseLabel_.description).alias(ExerciseLabel_.DESCRIPTION),
                exerciseJoin.get(Exercise_.identifier).alias(Exercise_.IDENTIFIER)
        );

        cq.where(cb.equal(exerciseJoin.get(Exercise_.id), id));

        var query = entityManager.createQuery(cq);
        return getViewDTO(id, query.getResultList());
    }

    private ExerciseViewAdminDTO getViewDTO(UUID id, List<Tuple> results) {
        var result = new ExerciseViewAdminDTO();
        result.setId(id);

        for (var tuple : results) {
            if (result.getIdentifier() == null) {
                result.setIdentifier(tuple.get(Exercise_.IDENTIFIER).toString());
            }

            var labelDTO = getLabelViewAdminDTOFromTuple(tuple);
            result.getLabels().add(labelDTO);
        }

        return result;
    }

    @Override
    public Page<ExerciseViewAdminDTO> getSearchAdmin(Pageable pageable, ExerciseSearchAdminDTO searchAdminDTO) {
        var ids = getSearchAdminIds(pageable, searchAdminDTO);
        if (ids.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Tuple.class);

        Root<ExerciseLabel> root = cq.from(ExerciseLabel.class);
        Join<ExerciseLabel, Exercise> exerciseJoin = root.join(ExerciseLabel_.exercise, JoinType.INNER);

        cq.multiselect(
                root.get(ExerciseLabel_.id).alias(ExerciseLabel_.ID),
                root.get(ExerciseLabel_.language).alias(ExerciseLabel_.LANGUAGE),
                root.get(ExerciseLabel_.title).alias(ExerciseLabel_.TITLE),
                root.get(ExerciseLabel_.description).alias(ExerciseLabel_.DESCRIPTION),
                exerciseJoin.get(Exercise_.id).alias(EXERCISE_ID),
                exerciseJoin.get(Exercise_.identifier).alias(Exercise_.IDENTIFIER)
        );

        cq.where(exerciseJoin.get(Exercise_.id).in(ids));

        var query = entityManager.createQuery(cq);
        return getViewAdminDTOPage(pageable, query.getResultList());
    }

    private List<UUID> getSearchAdminIds(Pageable pageable, ExerciseSearchAdminDTO searchAdminDTO) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(UUID.class);

        Root<ExerciseLabel> root = cq.from(ExerciseLabel.class);
        Join<ExerciseLabel, Exercise> exerciseJoin = root.join(ExerciseLabel_.exercise, JoinType.INNER);

        cq.select(exerciseJoin.get(Exercise_.id)).distinct(true);

        var predicates = buildPredicatesForSearchAdmin(cb, root, exerciseJoin, searchAdminDTO);
        cq.where(predicates.toArray(new Predicate[0]));

        var query = entityManager.createQuery(cq);
        if (pageable.isPaged()) {
            query.setMaxResults(pageable.getPageSize());
            query.setFirstResult((int) pageable.getOffset());
        }

        return query.getResultList();
    }

    private List<Predicate> buildPredicatesForSearchAdmin(
            CriteriaBuilder cb,
            Root<ExerciseLabel> root,
            Join<ExerciseLabel, Exercise> exerciseJoin,
            ExerciseSearchAdminDTO searchAdminDTO
    ) {
        var predicates = new ArrayList<Predicate>();

        if (searchAdminDTO.getLanguage() != null) {
            predicates.add(cb.equal(root.get(ExerciseLabel_.language), searchAdminDTO.getLanguage()));
        }
        if (searchAdminDTO.getTitle() != null && !searchAdminDTO.getTitle().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get(ExerciseLabel_.title)), "%" + searchAdminDTO.getTitle().toLowerCase() + "%"));
        }
        if (searchAdminDTO.getDescription() != null && !searchAdminDTO.getDescription().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get(ExerciseLabel_.description)), "%" + searchAdminDTO.getDescription() + "%"));
        }
        if (searchAdminDTO.getIdentifier() != null && !searchAdminDTO.getIdentifier().isEmpty()) {
            predicates.add(cb.like(cb.lower(exerciseJoin.get(Exercise_.identifier)), "%" + searchAdminDTO.getIdentifier().toLowerCase() + "%"));
        }

        return predicates;
    }

    private Page<ExerciseViewAdminDTO> getViewAdminDTOPage(Pageable pageable, List<Tuple> results) {
        var exercises = new HashMap<UUID, ExerciseViewAdminDTO>();
        for (var tuple : results) {
            var id = tuple.get(EXERCISE_ID, UUID.class);
            if (exercises.containsKey(id)) {
                var exercise = exercises.get(id);

                var labelDTO = getLabelViewAdminDTOFromTuple(tuple);
                exercise.getLabels().add(labelDTO);
            } else {
                var exercise = new ExerciseViewAdminDTO();
                exercise.setId(id);
                exercise.setIdentifier(tuple.get(Exercise_.IDENTIFIER, String.class));

                var labelDTO = getLabelViewAdminDTOFromTuple(tuple);
                exercise.getLabels().add(labelDTO);

                exercises.put(id, exercise);
            }
        }
        return new PageImpl<>(exercises.values().stream().toList(), pageable, results.size());
    }

    @Override
    public List<ExerciseViewDTO> getSearch(ExerciseSearchDTO searchDTO) {
        var ids = getSearchIds(searchDTO);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Tuple.class);

        Root<ExerciseLabel> root = cq.from(ExerciseLabel.class);
        Join<ExerciseLabel, Exercise> exerciseJoin = root.join(ExerciseLabel_.exercise, JoinType.INNER);

        cq.multiselect(
                root.get(ExerciseLabel_.language).alias(ExerciseLabel_.LANGUAGE),
                root.get(ExerciseLabel_.title).alias(ExerciseLabel_.TITLE),
                root.get(ExerciseLabel_.description).alias(ExerciseLabel_.DESCRIPTION),
                exerciseJoin.get(Exercise_.identifier).alias(Exercise_.IDENTIFIER),
                exerciseJoin.get(Exercise_.id).alias(Exercise_.ID)
        );

        cq.where(exerciseJoin.get(Exercise_.id).in(ids));

        var query = entityManager.createQuery(cq);
        return getViewDTOList(query.getResultList());
    }

    private List<UUID> getSearchIds(ExerciseSearchDTO searchDTO) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(UUID.class);

        Root<ExerciseLabel> root = cq.from(ExerciseLabel.class);
        Join<ExerciseLabel, Exercise> exerciseJoin = root.join(ExerciseLabel_.exercise, JoinType.INNER);

        cq.select(exerciseJoin.get(Exercise_.id)).distinct(true);

        var predicates = buildPredicatesForSearch(cb, root, exerciseJoin, searchDTO);
        cq.where(predicates.toArray(new Predicate[0]));

        var query = entityManager.createQuery(cq);
        query.setMaxResults(searchDTO.getLimit());

        return query.getResultList();
    }

    private List<ExerciseViewDTO> getViewDTOList(List<Tuple> results) {
        var exercises = new HashMap<UUID, ExerciseViewDTO>();

        for (var tuple : results) {
            var id = tuple.get(Exercise_.ID, UUID.class);
            if (exercises.containsKey(id)) {
                var exercise = exercises.get(id);

                var language = tuple.get(ExerciseLabel_.LANGUAGE, Language.class);
                var labelDTO = getLabelViewDTOFromTuple(tuple);
                exercise.getLabels().put(language, labelDTO);
            } else {
                var exercise = new ExerciseViewDTO();
                exercise.setLabels(new HashMap<>());
                exercise.setIdentifier(tuple.get(Exercise_.IDENTIFIER, String.class));

                var language = tuple.get(ExerciseLabel_.LANGUAGE, Language.class);
                var labelDTO = getLabelViewDTOFromTuple(tuple);
                exercise.getLabels().put(language, labelDTO);

                exercises.put(id, exercise);
            }
        }

        return new ArrayList<>(exercises.values());
    }

    private List<Predicate> buildPredicatesForSearch(
            CriteriaBuilder cb,
            Root<ExerciseLabel> root,
            Join<ExerciseLabel, Exercise> exerciseJoin,
            ExerciseSearchDTO searchDTO
    ) {
        var predicates = new ArrayList<Predicate>();

        if (searchDTO.getContains() != null && !searchDTO.getContains().isEmpty()) {
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get(ExerciseLabel_.title)), "%" + searchDTO.getContains() + "%"),
                    cb.like(cb.lower(exerciseJoin.get(Exercise_.identifier)), "%" + searchDTO.getContains() + "%"),
                    cb.like(cb.lower(root.get(ExerciseLabel_.description)), "%" + searchDTO.getContains() + "%")
            ));
        }

        return predicates;
    }

    private ExerciseLabelViewAdminDTO getLabelViewAdminDTOFromTuple(Tuple tuple) {
        var labelDTO = new ExerciseLabelViewAdminDTO();
        labelDTO.setId(tuple.get(ExerciseLabel_.ID, Long.class));
        labelDTO.setLanguage(tuple.get(ExerciseLabel_.LANGUAGE, Language.class));
        labelDTO.setTitle(tuple.get(ExerciseLabel_.TITLE, String.class));
        labelDTO.setDescription(tuple.get(ExerciseLabel_.DESCRIPTION, String.class));
        return labelDTO;
    }

    private ExerciseLabelViewDTO getLabelViewDTOFromTuple(Tuple tuple) {
        var labelDTO = new ExerciseLabelViewDTO();
        labelDTO.setTitle(tuple.get(ExerciseLabel_.TITLE, String.class));
        labelDTO.setDescription(tuple.get(ExerciseLabel_.DESCRIPTION, String.class));
        return labelDTO;
    }
}
