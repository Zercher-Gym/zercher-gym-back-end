package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import zercher.be.dto.exercise.ExerciseLabelCreateDTO;
import zercher.be.dto.exercise.ExerciseLabelUpdateDTO;
import zercher.be.dto.exercise.ExerciseLabelViewAdminDTO;
import zercher.be.dto.exercise.ExerciseLabelViewDTO;
import zercher.be.model.entity.ExerciseLabel;

@Mapper(componentModel = "spring")
public interface ExerciseLabelMapper {
    ExerciseLabel createDTOToExerciseLabel(ExerciseLabelCreateDTO exerciseLabelCreateDTO);

    void updateUserFromDTO(ExerciseLabelUpdateDTO updateDTO, @MappingTarget ExerciseLabel exerciseLabel);

    ExerciseLabelViewAdminDTO entityToViewAdminDTO(ExerciseLabel exerciseLabel);

    ExerciseLabelViewDTO entityToViewDTO(ExerciseLabel exerciseLabel);
}
