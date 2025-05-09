package zercher.be.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VerificationTokenType {
    CONFIRM_EMAIL("confirmEmail"),
    RESET_PASSWORD("resetPassword"),
    ;

    private final String name;
}
