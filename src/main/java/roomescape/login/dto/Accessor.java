package roomescape.login.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record Accessor(@NotNull @Positive Long id) {

}
