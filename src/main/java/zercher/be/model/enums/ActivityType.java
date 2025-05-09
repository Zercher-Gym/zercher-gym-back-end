package zercher.be.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityType {
    CARDIO("CARDIO"),
    WEIGHTLIFTING("WEIGHTLIFTING"),
    BODY_WEIGHT("BODY_WEIGHT"),
    CALISTHENICS("CALISTHENICS");

    private final String name;
}
