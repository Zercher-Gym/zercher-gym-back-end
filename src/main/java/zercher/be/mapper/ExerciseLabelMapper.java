package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import zercher.be.dto.exerciselabel.ExerciseLabelCreateDTO;
import zercher.be.dto.exerciselabel.ExerciseLabelUpdateDTO;
import zercher.be.model.entity.ExerciseLabel;

@Mapper(componentModel = "spring")
public interface ExerciseLabelMapper {
    ExerciseLabel createDTOToExerciseLabel(ExerciseLabelCreateDTO exerciseLabelCreateDTO);

    void updateUserFromDTO(ExerciseLabelUpdateDTO updateDTO, @MappingTarget ExerciseLabel exerciseLabel);
}
