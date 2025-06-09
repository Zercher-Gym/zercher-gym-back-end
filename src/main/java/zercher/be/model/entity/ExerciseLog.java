package zercher.be.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercise_logs")
public class ExerciseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "workout_log_id")
    private WorkoutLog workoutLog;

    @ManyToOne
    @JoinColumn(name = "custom_workout_exercise_id")
    private CustomWorkoutExercise customWorkoutExercise;

    @ManyToOne
    @JoinColumn(name = "workout_exercise_id")
    private WorkoutExercise workoutExercise;

    @Column(name = "details")
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}
