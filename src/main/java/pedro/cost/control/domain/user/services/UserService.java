package pedro.cost.control.domain.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.ConflictException;
import pedro.cost.control.domain.user.dtos.AuthResponse;
import pedro.cost.control.domain.user.dtos.UserLoginRequest;
import pedro.cost.control.domain.user.dtos.UserMeResponse;
import pedro.cost.control.domain.user.dtos.UserRegisterRequest;
import pedro.cost.control.domain.user.entities.User;
import pedro.cost.control.domain.user.repositories.UserRepository;
import pedro.cost.control.security.CustomUserDetails;
import pedro.cost.control.security.JwtService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public void register(UserRegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email já cadastrado");
        }

        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(UserLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }

    public UserMeResponse getMe(User user) {
        return new UserMeResponse(user.getEmail(), user.getUsername());
    }
}
