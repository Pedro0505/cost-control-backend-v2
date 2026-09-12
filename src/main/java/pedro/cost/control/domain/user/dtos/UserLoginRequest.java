package pedro.cost.control.domain.user.dtos;

public record UserLoginRequest(
        String email,
        String password
) {
}