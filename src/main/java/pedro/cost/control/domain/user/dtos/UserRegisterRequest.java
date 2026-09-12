package pedro.cost.control.domain.user.dtos;

public record UserRegisterRequest(
        String email,
        String username,
        String password
) {
}