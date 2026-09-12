package pedro.cost.control.helpers;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import pedro.cost.control.builders.dto.TestUser;

public abstract class BaseE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    protected E2ETestAuthHelper authHelper;

    @BeforeAll
    void setupRestAssured() {
        RestAssured.port = port;
    }

    protected RequestSpecification authenticatedRequest(TestUser testUser) {
        return authHelper.authenticatedRequest(testUser);
    }
}