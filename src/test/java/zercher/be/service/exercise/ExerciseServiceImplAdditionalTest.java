package zercher.be.service.exercise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import zercher.be.dto.exercise.ExerciseSearchAdminDTO;
import zercher.be.dto.exercise.ExerciseSearchDTO;
import zercher.be.dto.exercise.ExerciseViewAdminDTO;
import zercher.be.dto.exercise.ExerciseViewDTO;
import zercher.be.dto.exercise.ExerciseLabelViewAdminDTO;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.ExerciseLabelMapper;
import zercher.be.mapper.ExerciseMapper;
import zercher.be.model.entity.Exercise;
import zercher.be.model.entity.ExerciseLabel;
import zercher.be.model.enums.Language;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Teste unitare suplimentare pentru ExerciseServiceImpl:
 * - getExercise(UUID)
 * - searchExercisesAdmin(Pageable, ExerciseSearchAdminDTO)
 * - searchExercises(ExerciseSearchDTO)
 */
@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplAdditionalTest {

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

    private Exercise ex1;
    private Exercise ex2;
    private ExerciseLabel label1;

    @BeforeEach
    void setUp() {
        // Exerciții de test
        ex1 = new Exercise();
        ex1.setId(UUID.randomUUID());
        ex1.setIdentifier("EX1");

        ex2 = new Exercise();
        ex2.setId(UUID.randomUUID());
        ex2.setIdentifier("EX2");

        // O etichetă asociată lui ex1
        label1 = new ExerciseLabel();
        label1.setId(1L);
        label1.setExercise(ex1);
        label1.setLanguage(Language.en);
        label1.setTitle("Titlu1");
        label1.setDescription("Descriere1");
    }

    // -------------------------------
    // 1. getExercise(UUID id)
    // -------------------------------
    @Test
    void getExercise_ShouldReturnAdminDTO_WhenExists() {
        // Aranjament
        UUID id = ex1.getId();
        when(exerciseRepository.findById(id)).thenReturn(Optional.of(ex1));

        // Mapper-ul principal
        ExerciseViewAdminDTO viewAdminDTO = new ExerciseViewAdminDTO();
        viewAdminDTO.setIdentifier("EX1");
        when(exerciseMapper.entityToViewAdminDTO(ex1)).thenReturn(viewAdminDTO);

        // Repository-ul de etichete returnează o listă cu label1
        when(exerciseLabelRepository.findByExercise(ex1)).thenReturn(List.of(label1));

        // Mapper-ul de etichetă
        ExerciseLabelViewAdminDTO labelAdminDTO = new ExerciseLabelViewAdminDTO();
        labelAdminDTO.setLanguage(Language.en);
        labelAdminDTO.setTitle("Titlu1");
        labelAdminDTO.setDescription("Descriere1");
        when(exerciseLabelMapper.entityToViewAdminDTO(label1)).thenReturn(labelAdminDTO);

        // Acțiune
        ExerciseViewAdminDTO result = exerciseService.getExercise(id);

        // Verificare
        assertThat(result).isSameAs(viewAdminDTO);
        // labelAdminDTO trebuie să fie singurul element din setul de labels
        assertThat(result.getLabels()).containsExactly(labelAdminDTO);

        verify(exerciseRepository, times(1)).findById(id);
        verify(exerciseMapper, times(1)).entityToViewAdminDTO(ex1);
        verify(exerciseLabelRepository, times(1)).findByExercise(ex1);
        verify(exerciseLabelMapper, times(1)).entityToViewAdminDTO(label1);
    }

    @Test
    void getExercise_ShouldThrowException_WhenNotFound() {
        // Aranjament
        UUID fakeId = UUID.randomUUID();
        when(exerciseRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Acțiune + Verificare excepție
        assertThatThrownBy(() -> exerciseService.getExercise(fakeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("exerciseWithIdNotFound");

        verify(exerciseRepository, times(1)).findById(fakeId);
        // Nu vrem apeluri suplimentare către mapper sau repository de etichete
        verifyNoMoreInteractions(exerciseMapper, exerciseLabelRepository, exerciseLabelMapper);
    }

    // -------------------------------
    // 2. searchExercisesAdmin(Pageable, ExerciseSearchAdminDTO)
    // -------------------------------
    @Test
    void searchExercisesAdmin_ShouldReturnPageOfAdminDTOs_WhenExercisesExist() {
        // Aranjament
        ExerciseSearchAdminDTO searchAdminDTO = new ExerciseSearchAdminDTO();
        // Lăsăm identifier și title necompletate (""), deci nu adăugăm filtre la Specification

        Pageable pageable = PageRequest.of(0, 2);
        List<Exercise> exercisesList = List.of(ex1, ex2);
        Page<Exercise> pageExercises = new PageImpl<>(exercisesList, pageable, exercisesList.size());

        // Când repository.findAll este apelat cu orice Specification și exact Pageable, returnează pagina noastră
        when(exerciseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pageExercises);

        // Mokește mapping-ul entitate → DTO admin pentru fiecare exercițiu
        ExerciseViewAdminDTO dto1 = new ExerciseViewAdminDTO();
        dto1.setIdentifier("EX1");
        ExerciseViewAdminDTO dto2 = new ExerciseViewAdminDTO();
        dto2.setIdentifier("EX2");

        when(exerciseMapper.entityToViewAdminDTO(ex1)).thenReturn(dto1);
        when(exerciseMapper.entityToViewAdminDTO(ex2)).thenReturn(dto2);

        // Nu punem etichete (lista goală) pentru ambele exerciții
        when(exerciseLabelRepository.findByExercise(ex1)).thenReturn(Collections.emptyList());
        when(exerciseLabelRepository.findByExercise(ex2)).thenReturn(Collections.emptyList());

        // Acțiune
        Page<ExerciseViewAdminDTO> resultPage = exerciseService.searchExercisesAdmin(pageable, searchAdminDTO);

        // Verificare
        assertThat(resultPage.getContent()).containsExactly(dto1, dto2);
        assertThat(resultPage.getTotalElements()).isEqualTo(exercisesList.size());

        verify(exerciseRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
        verify(exerciseMapper, times(1)).entityToViewAdminDTO(ex1);
        verify(exerciseMapper, times(1)).entityToViewAdminDTO(ex2);
        verify(exerciseLabelRepository, times(1)).findByExercise(ex1);
        verify(exerciseLabelRepository, times(1)).findByExercise(ex2);
    }

    // -------------------------------
    // 3. searchExercises(ExerciseSearchDTO)
    // -------------------------------
    @Test
    void searchExercises_ShouldReturnListOfViewDTOs_WhenExercisesExist() {
        // Aranjament
        ExerciseSearchDTO searchDTO = new ExerciseSearchDTO();
        // Lăsăm contain-e gol și limit = 2
        searchDTO.setContains("");
        searchDTO.setLimit(2);

        List<Exercise> exercisesList = List.of(ex1, ex2);
        Page<Exercise> pageExercises = new PageImpl<>(exercisesList);

        // Când repository.findAll este apelat cu orice Specification și PageRequest.of(0, 2), returnăm pagina noastră
        when(exerciseRepository.findAll(any(Specification.class), eq(PageRequest.of(0, 2))))
                .thenReturn(pageExercises);

        // Mapper entitate → DTO simplu
        ExerciseViewDTO view1 = new ExerciseViewDTO();
        view1.setIdentifier("EX1");
        ExerciseViewDTO view2 = new ExerciseViewDTO();
        view2.setIdentifier("EX2");

        when(exerciseMapper.entityToViewDTO(ex1)).thenReturn(view1);
        when(exerciseMapper.entityToViewDTO(ex2)).thenReturn(view2);

        // Lista de etichete pentru fiecare exercițiu este goală
        when(exerciseLabelRepository.findByExercise(ex1)).thenReturn(Collections.emptyList());
        when(exerciseLabelRepository.findByExercise(ex2)).thenReturn(Collections.emptyList());

        // Acțiune
        List<ExerciseViewDTO> resultList = exerciseService.searchExercises(searchDTO);

        // Verificare
        assertThat(resultList).containsExactly(view1, view2);

        verify(exerciseRepository, times(1))
                .findAll(any(Specification.class), eq(PageRequest.of(0, 2)));
        verify(exerciseMapper, times(1)).entityToViewDTO(ex1);
        verify(exerciseMapper, times(1)).entityToViewDTO(ex2);
        verify(exerciseLabelRepository, times(1)).findByExercise(ex1);
        verify(exerciseLabelRepository, times(1)).findByExercise(ex2);
    }
}
