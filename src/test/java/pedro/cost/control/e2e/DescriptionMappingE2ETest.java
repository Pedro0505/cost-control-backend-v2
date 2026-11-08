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
import pedro.cost.control.domain.creditcard.repositories.DescriptionMappingRepository;
import pedro.cost.control.helpers.BaseE2ETest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DescriptionMappingE2ETest extends BaseE2ETest {
    private static final String ENDPOINT = "/api/v2/description-mappings";

    @Autowired
    private DescriptionMappingRepository descriptionMappingRepository;

    @Autowired
    private CreditCardExpenseRepository creditCardExpenseRepository;

    private TestUser userA;
    private TestUser userB;

    @BeforeAll
    void createUsers() {
        userA = authHelper.createUser("mappingUserA", "mappingusera@test.com");
        userB = authHelper.createUser("mappingUserB", "mappinguserb@test.com");
    }

    @BeforeEach
    void cleanDatabase() {
        creditCardExpenseRepository.deleteAll();
        descriptionMappingRepository.deleteAll();
    }

    private Long create(TestUser user, String body) {
        return authenticatedRequest(user)
                .contentType("application/json")
                .body(body)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private Long create(TestUser user, String pattern, String value) {
        return create(user, """
                { "pattern": "%s", "value": "%s" }
                """.formatted(pattern, value));
    }

    private List<Integer> idsInOrder(TestUser user) {
        return authenticatedRequest(user)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("id", Integer.class);
    }

    @Test
    void shouldAppendToEndWhenPositionIsNotInformed() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");
        Long c = create(userA, "c", "C");

        authenticatedRequest(userA)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .body("id", contains(a.intValue(), b.intValue(), c.intValue()))
                .body("position", contains(1, 2, 3));
    }

    @Test
    void shouldInsertAtPositionShiftingTheOthersDown() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");
        Long first = create(userA, """
                { "pattern": "x", "value": "X", "position": 1 }
                """);
        Long middle = create(userA, """
                { "pattern": "y", "value": "Y", "position": 3 }
                """);

        assertThat(idsInOrder(userA)).containsExactly(
                first.intValue(), a.intValue(), middle.intValue(), b.intValue()
        );
    }

    @Test
    void shouldMoveMappingWhenUpdatingPosition() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");
        Long c = create(userA, "c", "C");

        authenticatedRequest(userA)
                .contentType("application/json")
                .body("""
                        { "pattern": "c2", "value": "C2", "position": 1 }
                        """)
                .when()
                .put(ENDPOINT + "/" + c)
                .then()
                .statusCode(200)
                .body("position", equalTo(1))
                .body("pattern", equalTo("c2"))
                .body("value", equalTo("C2"));

        assertThat(idsInOrder(userA)).containsExactly(c.intValue(), a.intValue(), b.intValue());
    }

    @Test
    void shouldKeepPositionWhenUpdatingWithoutPosition() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");

        authenticatedRequest(userA)
                .contentType("application/json")
                .body("""
                        { "pattern": "a2", "value": "A2" }
                        """)
                .when()
                .put(ENDPOINT + "/" + a)
                .then()
                .statusCode(200)
                .body("position", equalTo(1));

        assertThat(idsInOrder(userA)).containsExactly(a.intValue(), b.intValue());
    }

    @Test
    void shouldCompactPositionsAfterDelete() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");
        Long c = create(userA, "c", "C");

        authenticatedRequest(userA)
                .when()
                .delete(ENDPOINT + "/" + a)
                .then()
                .statusCode(204);

        authenticatedRequest(userA)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .body("id", contains(b.intValue(), c.intValue()))
                .body("position", contains(1, 2));
    }

    @Test
    void shouldReorderWithFullListOfIds() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");
        Long c = create(userA, "c", "C");

        authenticatedRequest(userA)
                .contentType("application/json")
                .body("{ \"ids\": [%d, %d, %d] }".formatted(c, a, b))
                .when()
                .put(ENDPOINT + "/order")
                .then()
                .statusCode(200)
                .body("id", contains(c.intValue(), a.intValue(), b.intValue()))
                .body("position", contains(1, 2, 3));
    }

    @Test
    void shouldRejectPartialOrDuplicatedReorder() {
        Long a = create(userA, "a", "A");
        Long b = create(userA, "b", "B");

        authenticatedRequest(userA)
                .contentType("application/json")
                .body("{ \"ids\": [%d] }".formatted(b))
                .when()
                .put(ENDPOINT + "/order")
                .then()
                .statusCode(400);

        authenticatedRequest(userA)
                .contentType("application/json")
                .body("{ \"ids\": [%d, %d, %d] }".formatted(b, a, a))
                .when()
                .put(ENDPOINT + "/order")
                .then()
                .statusCode(400);

        assertThat(idsInOrder(userA)).containsExactly(a.intValue(), b.intValue());
    }

    @Test
    void shouldRejectInvalidInput() {
        create(userA, "a", "A");

        authenticatedRequest(userA).contentType("application/json")
                .body("{ \"pattern\": \"(abc\", \"value\": \"X\" }")
                .when().post(ENDPOINT).then().statusCode(400);

        authenticatedRequest(userA).contentType("application/json")
                .body("{ \"pattern\": \"abc\" }")
                .when().post(ENDPOINT).then().statusCode(400);

        authenticatedRequest(userA).contentType("application/json")
                .body("{ \"pattern\": \"abc\", \"value\": \"X\", \"position\": 3 }")
                .when().post(ENDPOINT).then().statusCode(400);

        authenticatedRequest(userA).contentType("application/json")
                .body("{ \"pattern\": \"abc\", \"value\": \"X\", \"position\": 0 }")
                .when().post(ENDPOINT).then().statusCode(400);
    }

    @Test
    void shouldIsolateMappingsByUser() {
        Long a = create(userA, "a", "A");
        create(userB, "b", "B");

        assertThat(idsInOrder(userA)).containsExactly(a.intValue());

        authenticatedRequest(userB).when().get(ENDPOINT + "/" + a).then().statusCode(404);
        authenticatedRequest(userB).when().delete(ENDPOINT + "/" + a).then().statusCode(404);
        authenticatedRequest(userB).contentType("application/json")
                .body("{ \"pattern\": \"z\", \"value\": \"Z\" }")
                .when().put(ENDPOINT + "/" + a).then().statusCode(404);

        authenticatedRequest(userB).when().get(ENDPOINT).then().body("position", contains(1));
    }

    @Test
    void shouldLoadDefaultsOnlyOnceAndKeepOrder() {
        authenticatedRequest(userA)
                .when()
                .post(ENDPOINT + "/defaults")
                .then()
                .statusCode(201)
                .body("[0].type", equalTo("IGNORE"))
                .body("[0].position", equalTo(1));

        authenticatedRequest(userA)
                .when()
                .post(ENDPOINT + "/defaults")
                .then()
                .statusCode(409);

        authenticatedRequest(userA)
                .queryParam("description", "IFOOD CLUB")
                .when()
                .get(ENDPOINT + "/preview")
                .then()
                .statusCode(200)
                .body("normalizedDescription", equalTo("Assinaturas"));
    }

    @Test
    void previewShouldShowWhichPositionMatched() {
        create(userA, "ifood\\\\s*club", "Assinaturas");
        Long ifood = create(userA, "ifood", "Delivery");

        authenticatedRequest(userA)
                .queryParam("description", "IFOOD *LANCHES")
                .when()
                .get(ENDPOINT + "/preview")
                .then()
                .statusCode(200)
                .body("normalizedDescription", equalTo("Delivery"))
                .body("matchedMappingId", equalTo(ifood.intValue()))
                .body("matchedPosition", equalTo(2))
                .body("ignored", equalTo(false));

        authenticatedRequest(userA)
                .queryParam("description", "Posto Shell")
                .when()
                .get(ENDPOINT + "/preview")
                .then()
                .statusCode(200)
                .body("normalizedDescription", equalTo("Outros"))
                .body("matchedMappingId", nullValue());
    }

    @Test
    void reprocessingShouldApplyCurrentMappingsAndOrder() {
        Long club = create(userA, "ifood\\\\s*club", "Assinaturas");
        Long ifood = create(userA, "ifood", "Delivery");

        creditCardExpenseRepository.save(expense("IFOOD CLUB", userA));
        creditCardExpenseRepository.save(expense("Estorno compra", userA));

        reprocess(userA);
        assertThat(normalizedDescriptionsOf(userA)).containsExactlyInAnyOrder("Assinaturas", "Outros");

        authenticatedRequest(userA)
                .contentType("application/json")
                .body("{ \"ids\": [%d, %d] }".formatted(ifood, club))
                .when()
                .put(ENDPOINT + "/order")
                .then()
                .statusCode(200);

        create(userA, """
                { "pattern": "\\\\bestorno\\\\b", "type": "IGNORE", "position": 1 }
                """);

        reprocess(userA);
        assertThat(normalizedDescriptionsOf(userA)).containsExactlyInAnyOrder("Delivery", null);

        authenticatedRequest(userA)
                .queryParam("invoiceYear", 2026)
                .queryParam("invoiceMonth", 11)
                .when()
                .get("/api/v2/credit-card-expenses")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].normalizedDescription", equalTo("Delivery"));
    }

    private void reprocess(TestUser user) {
        authenticatedRequest(user)
                .when()
                .put("/api/v2/credit-card-expenses/reprocessing-descriptions")
                .then()
                .statusCode(200);
    }

    private List<String> normalizedDescriptionsOf(TestUser user) {
        return creditCardExpenseRepository.findAllByUserId(user.user().getId()).stream()
                .map(CreditCardExpense::getNormalizedDescription)
                .toList();
    }

    private CreditCardExpense expense(String rawDescription, TestUser user) {
        CreditCardExpense expense = new CreditCardExpense();
        expense.setRawDescription(rawDescription);
        expense.setNormalizedDescription("Outros");
        expense.setAmount(BigDecimal.TEN);
        expense.setInvoiceReferenceYear(2026);
        expense.setInvoiceReferenceMonth(11);
        expense.setExpenseDate(LocalDate.of(2026, 11, 1));
        expense.setInstallment(false);
        expense.setUser(user.user());
        return expense;
    }
}
