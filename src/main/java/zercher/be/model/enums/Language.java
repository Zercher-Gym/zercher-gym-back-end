package zercher.be.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    ro("ro"),
    en("en");

    private final String code;
}
