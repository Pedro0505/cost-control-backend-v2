package pedro.cost.control.domain.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pedro.cost.control.domain.user.dtos.AuthResponse;
import pedro.cost.control.domain.user.dtos.UserLoginRequest;
import pedro.cost.control.domain.user.dtos.UserMeResponse;
import pedro.cost.control.domain.user.dtos.UserRegisterRequest;
import pedro.cost.control.domain.user.services.UserService;
import pedro.cost.control.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(security = {})
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegisterRequest request) {
        userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Operation(security = {})
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getMe(userDetails.getUser()));
    }
}
