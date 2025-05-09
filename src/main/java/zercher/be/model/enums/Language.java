package zercher.be.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    RO("ro"),
    EN("en");

    private final String code;
}
