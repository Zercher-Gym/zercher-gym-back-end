package zercher.be.service.exercise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zercher.be.dto.exercise.ExerciseCreateDTO;
import zercher.be.dto.exercise.ExerciseLabelCreateDTO;
import zercher.be.dto.exercise.ExerciseLabelUpdateDTO;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.ExerciseLabelMapper;
import zercher.be.mapper.ExerciseMapper;
import zercher.be.model.entity.Exercise;
import zercher.be.model.entity.ExerciseLabel;
import zercher.be.model.enums.Language;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru ExerciseServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseLabelRepository exerciseLabelRepository;

    @Mock
    private ExerciseMapper exerciseMapper;

    @Mock
    private ExerciseLabelMapper exerciseLabelMapper;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    private UUID existingId;
    private Exercise existingExercise;
    private ExerciseLabel existingLabel;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID();
        existingExercise = new Exercise();
        existingExercise.setId(existingId);
        existingExercise.setIdentifier("TEST_EX");

        existingLabel = new ExerciseLabel();
        existingLabel.setId(1L);
        existingLabel.setExercise(existingExercise);
        existingLabel.setLanguage(Language.en);
        existingLabel.setTitle("Title");
        existingLabel.setDescription("Desc");
    }

    @Test
    void createExercise_ShouldThrowException_WhenIdentifierExists() {
        // Aranjament
        ExerciseCreateDTO createDTO = new ExerciseCreateDTO();
        createDTO.setIdentifier("TEST_EX");
        createDTO.setLabels(new HashSet<>());

        when(exerciseRepository.existsByIdentifier("TEST_EX")).thenReturn(true);

        // Acțiune + Verificare
        assertThatThrownBy(() -> exerciseService.createExercise(createDTO))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage("exerciseWithIdentifierExists");

        verify(exerciseRepository, times(1)).existsByIdentifier("TEST_EX");
        verify(exerciseMapper, never()).createDTOToExercise(any());
        verify(exerciseRepository, never()).save(any());
        verify(exerciseLabelRepository, never()).saveAll(any());
    }


    @Test
    void deleteExercise_ShouldDelete_WhenExerciseExists() {
        // Aranjament
        when(exerciseRepository.findById(existingId)).thenReturn(Optional.of(existingExercise));

        // Acțiune
        exerciseService.deleteExercise(existingId);

        // Verificare
        verify(exerciseRepository, times(1)).findById(existingId);
        verify(exerciseLabelRepository, times(1)).deleteExerciseLabelsByExercise(existingExercise);
        verify(exerciseRepository, times(1)).delete(eq(existingExercise));
    }

    @Test
    void deleteExercise_ShouldThrowException_WhenNotFound() {
        // Aranjament
        UUID fakeId = UUID.randomUUID();
        when(exerciseRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Acțiune + Verificare
        assertThatThrownBy(() -> exerciseService.deleteExercise(fakeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("exerciseWithIdNotFound");

        verify(exerciseRepository, times(1)).findById(fakeId);
        verify(exerciseLabelRepository, never()).deleteExerciseLabelsByExercise(any());
        verify(exerciseRepository, never()).delete(any(Exercise.class));
    }

    @Test
    void updateExerciseLabel_ShouldUpdateAndSave_WhenLabelExists() {
        // Aranjament
        Long labelId = 1L;
        ExerciseLabelUpdateDTO updateDTO = new ExerciseLabelUpdateDTO();
        updateDTO.setTitle("NewTitle");
        updateDTO.setDescription("NewDesc");
        // (nu mai setăm limba, pentru că DTO-ul de actualizare nu conține acest câmp)

        when(exerciseLabelRepository.findById(labelId)).thenReturn(Optional.of(existingLabel));

        // Acțiune
        exerciseService.updateExerciseLabel(labelId, updateDTO);

        // Verificare
        verify(exerciseLabelRepository, times(1)).findById(labelId);
        verify(exerciseLabelMapper, times(1)).updateUserFromDTO(updateDTO, existingLabel);
        verify(exerciseLabelRepository, times(1)).save(existingLabel);
    }

    @Test
    void updateExerciseLabel_ShouldThrowException_WhenLabelNotFound() {
        // Aranjament
        Long fakeId = 99L;
        ExerciseLabelUpdateDTO updateDTO = new ExerciseLabelUpdateDTO();
        updateDTO.setTitle("NewTitle");
        updateDTO.setDescription("NewDesc");
        // (nu setăm limba aici)

        when(exerciseLabelRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Acțiune + Verificare
        assertThatThrownBy(() -> exerciseService.updateExerciseLabel(fakeId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("exerciseLabelWithIdDoesNotExist");

        verify(exerciseLabelRepository, times(1)).findById(fakeId);
        verify(exerciseLabelMapper, never()).updateUserFromDTO(any(), any());
        verify(exerciseLabelRepository, never()).save(any());
    }
}
