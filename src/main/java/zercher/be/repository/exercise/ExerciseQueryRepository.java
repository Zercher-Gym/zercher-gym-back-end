package zercher.be.repository.exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zercher.be.dto.exercise.ExerciseSearchAdminDTO;
import zercher.be.dto.exercise.ExerciseSearchDTO;
import zercher.be.dto.exercise.ExerciseViewAdminDTO;
import zercher.be.dto.exercise.ExerciseViewDTO;

import java.util.List;
import java.util.UUID;

public interface ExerciseQueryRepository {
    ExerciseViewAdminDTO getView(UUID id);

    Page<ExerciseViewAdminDTO> getSearchAdmin(Pageable pageable, ExerciseSearchAdminDTO searchAdminDTO);

    List<ExerciseViewDTO> getSearch(ExerciseSearchDTO searchDTO);
}
