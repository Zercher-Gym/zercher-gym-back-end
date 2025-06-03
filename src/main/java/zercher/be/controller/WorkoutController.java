package zercher.be.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Workout")
@RequiredArgsConstructor
@RequestMapping("/api/workout")
@SecurityRequirement(name = "Authentication")
public class WorkoutController {
    // create new workout

    // edit existing workout

    // view all workouts (admin)

    // search workouts

    // delete existing unit
}
