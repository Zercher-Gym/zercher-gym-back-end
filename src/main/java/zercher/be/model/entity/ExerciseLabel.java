package zercher.be.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.model.enums.Language;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercise_labels")
public class ExerciseLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String description;
}
