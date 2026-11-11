package pedro.cost.control.e2e;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pedro.cost.control.builders.dto.TestUser;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;
import pedro.cost.control.domain.creditcard.repositories.CreditCardExpenseRepository;
import pedro.cost.control.helpers.BaseE2ETest;

import io.restassured.path.json.JsonPath;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreditCardExpensePercentageE2ETest extends BaseE2ETest {
    private static final String ENDPOINT = "/api/v2/credit-card-expenses";

    @Autowired
    private CreditCardExpenseRepository creditCardExpenseRepository;

    private TestUser userA;
    private TestUser userB;

    @BeforeAll
    void createUsers() {
        userA = authHelper.createUser("percentUserA", "percentusera@test.com");
        userB = authHelper.createUser("percentUserB", "percentuserb@test.com");
    }

    @BeforeEach
    void cleanDatabase() {
        creditCardExpenseRepository.deleteAll();
    }

    @Test
    void percentageShouldOnlyConsiderTheAuthenticatedUserExpenses() {
        save(userA, "Delivery", "75.00", false);
        save(userA, "Amazon", "25.00", false);
        save(userB, "Delivery", "900.00", false);

        JsonPath json = authenticatedRequest(userA)
                .queryParam("month", 11)
                .queryParam("year", 2026)
                .when()
                .get(ENDPOINT + "/percentage")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("normalizedDescription", containsInAnyOrder("Delivery", "Amazon"))
                .extract()
                .jsonPath();

        assertThat(json.getDouble("find { it.normalizedDescription == 'Delivery' }.percentage")).isCloseTo(75.0, within(0.01));
        assertThat(json.getDouble("find { it.normalizedDescription == 'Amazon' }.percentage")).isCloseTo(25.0, within(0.01));
    }

    @Test
    void percentagesShouldIgnoreExpensesWithoutNormalizedDescription() {
        save(userA, "Delivery", "50.00", true);
        save(userA, "Amazon", "50.00", false);
        save(userA, null, "100.00", false);

        JsonPath percentages = authenticatedRequest(userA)
                .queryParam("month", 11)
                .queryParam("year", 2026)
                .when()
                .get(ENDPOINT + "/percentage")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("normalizedDescription", containsInAnyOrder("Delivery", "Amazon"))
                .extract()
                .jsonPath();

        assertThat(percentages.getDouble("[0].percentage")).isCloseTo(50.0, within(0.01));
        assertThat(percentages.getDouble("[1].percentage")).isCloseTo(50.0, within(0.01));

        JsonPath installment = authenticatedRequest(userA)
                .queryParam("month", 11)
                .queryParam("year", 2026)
                .when()
                .get(ENDPOINT + "/installment-percentage")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        assertThat(installment.getDouble("installmentPercentage")).isCloseTo(50.0, within(0.01));
        assertThat(installment.getDouble("nonInstallmentPercentage")).isCloseTo(50.0, within(0.01));
    }

    private void save(TestUser user, String normalizedDescription, String amount, boolean installment) {
        CreditCardExpense expense = new CreditCardExpense();
        expense.setRawDescription("raw " + normalizedDescription);
        expense.setNormalizedDescription(normalizedDescription);
        expense.setAmount(new BigDecimal(amount));
        expense.setInvoiceReferenceYear(2026);
        expense.setInvoiceReferenceMonth(11);
        expense.setExpenseDate(LocalDate.of(2026, 11, 1));
        expense.setInstallment(installment);
        expense.setUser(user.user());
        creditCardExpenseRepository.save(expense);
    }
}
