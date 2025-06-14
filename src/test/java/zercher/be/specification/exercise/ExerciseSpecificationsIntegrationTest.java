package zercher.be.specification.exercise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import zercher.be.model.entity.Exercise;
import zercher.be.model.entity.ExerciseLabel;
import zercher.be.model.enums.Language;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseRepository;
import zercher.be.specification.ExerciseSpecifications;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class ExerciseSpecificationsIntegrationTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseLabelRepository exerciseLabelRepository;

    private Exercise exAlpha;
    private Exercise exBeta;
    private Exercise exGamma;

    private ExerciseLabel labelAlphaEn;
    private ExerciseLabel labelAlphaRo;
    private ExerciseLabel labelBetaEn;

    @BeforeEach
    void setUp() {
        // Golim tabelele la început de test
        exerciseLabelRepository.deleteAll();
        exerciseRepository.deleteAll();

        // Creăm trei exerciții (fără să setăm manual ID-ul):
        exAlpha = new Exercise();
        exAlpha.setIdentifier("alpha");
        exerciseRepository.save(exAlpha);

        exBeta = new Exercise();
        exBeta.setIdentifier("beta");
        exerciseRepository.save(exBeta);

        exGamma = new Exercise();
        exGamma.setIdentifier("gamma");
        exerciseRepository.save(exGamma);

        // Creăm etichete pentru exAlpha și exBeta (fără să setăm manual ID-uri):
        labelAlphaEn = new ExerciseLabel();
        labelAlphaEn.setExercise(exAlpha);
        labelAlphaEn.setLanguage(Language.en);
        labelAlphaEn.setTitle("Alpha");
        labelAlphaEn.setDescription("First exercise");
        exerciseLabelRepository.save(labelAlphaEn);

        labelAlphaRo = new ExerciseLabel();
        labelAlphaRo.setExercise(exAlpha);
        labelAlphaRo.setLanguage(Language.ro);
        labelAlphaRo.setTitle("Alfa");
        labelAlphaRo.setDescription("Primul exercițiu");
        exerciseLabelRepository.save(labelAlphaRo);

        labelBetaEn = new ExerciseLabel();
        labelBetaEn.setExercise(exBeta);
        labelBetaEn.setLanguage(Language.en);
        labelBetaEn.setTitle("Beta");
        labelBetaEn.setDescription("Second exercise");
        exerciseLabelRepository.save(labelBetaEn);
    }

    @Test
    void whenFilteringByIdentifierContains_thenOnlyMatchingExercisesAreReturned() {
        var pageable = PageRequest.of(0, 10);
        Specification<Exercise> spec = Specification
                .where(ExerciseSpecifications.identifierContains("beta"));

        var page = exerciseRepository.findAll(spec, pageable);
        List<Exercise> content = page.getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getIdentifier()).isEqualTo("beta");
    }

    @Test
    void whenFilteringByTitleContains_thenOnlyExercisesWithThatTitleAppear() {
        var pageable = PageRequest.of(0, 10);
        Specification<Exercise> spec = Specification
                .where(ExerciseSpecifications.titleContains("Alfa"));

        var page = exerciseRepository.findAll(spec, pageable);
        List<Exercise> content = page.getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getIdentifier()).isEqualTo("alpha");
    }

    @Test
    void whenFilteringByDescriptionContains_thenOnlyExercisesWithThatDescriptionAppear() {
        var pageable = PageRequest.of(0, 10);
        Specification<Exercise> spec = Specification
                .where(ExerciseSpecifications.descriptionContains("second"));

        var page = exerciseRepository.findAll(spec, pageable);
        List<Exercise> content = page.getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getIdentifier()).isEqualTo("beta");
    }

    @Test
    void whenFilteringByLanguageIs_thenOnlyExercisesWithThatLanguageAppear() {
        var pageable = PageRequest.of(0, 10);
        Specification<Exercise> spec = Specification
                .where(ExerciseSpecifications.languageIs(Language.ro));

        var page = exerciseRepository.findAll(spec, pageable);
        List<Exercise> content = page.getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getIdentifier()).isEqualTo("alpha");
    }

    @Test
    void whenFilteringByContains_thenOnlyMatchingExercisesAreReturned() {
        var pageable = PageRequest.of(0, 10);
        Specification<Exercise> spec = Specification
                .where(ExerciseSpecifications.contains("first"));

        var page = exerciseRepository.findAll(spec, pageable);
        List<Exercise> content = page.getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getIdentifier()).isEqualTo("alpha");
    }
}
