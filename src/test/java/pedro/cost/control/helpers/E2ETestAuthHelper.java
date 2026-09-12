package pedro.cost.control.helpers;

import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pedro.cost.control.builders.dto.TestUser;
import pedro.cost.control.builders.entities.UserTestBuilder;
import pedro.cost.control.domain.user.entities.User;
import pedro.cost.control.domain.user.repositories.UserRepository;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@Component
public class E2ETestAuthHelper {

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TestUser createUser() {
        return createUser(
                UUID.randomUUID().toString(),
                UUID.randomUUID() + "@test.com"
        );
    }

    public TestUser createUser(
            String username,
            String email
    ) {
        User user = UserTestBuilder.builder()
                .withUsername(username)
                .withEmail(email)
                .withPassword(passwordEncoder.encode(DEFAULT_PASSWORD))
                .build();

        user = userRepository.save(user);

        String token = login(email, DEFAULT_PASSWORD);

        return new TestUser(user, token);
    }

    private String login(String email, String password) {
        return given()
                .contentType("application/json")
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/api/v2/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public RequestSpecification authenticatedRequest(TestUser testUser) {
        return given()
                .header(
                        "Authorization",
                        "Bearer " + testUser.token()
                );
    }
}
