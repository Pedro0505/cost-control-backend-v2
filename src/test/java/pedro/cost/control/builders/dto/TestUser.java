package pedro.cost.control.builders.dto;

import pedro.cost.control.domain.user.entities.User;

public record TestUser(
        User user,
        String token
) {
}