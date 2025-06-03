package zercher.be.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnitType {
    GROUP("GROUP"),
    DISTANCE("DISTANCE"),
    TIME("TIME");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}