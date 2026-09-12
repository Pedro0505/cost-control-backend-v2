package pedro.cost.control.e2e;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pedro.cost.control.builders.dto.TestUser;
import pedro.cost.control.config.errorHandler.ErrorResponse;
import pedro.cost.control.builders.entities.EmploymentContractCltTestBuilder;
import pedro.cost.control.builders.entities.EmploymentContractPjTestBuilder;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractClt;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractCltRepository;
import pedro.cost.control.domain.contract.repositories.EmploymentContractPjRepository;
import pedro.cost.control.domain.contract.repositories.EmploymentContractRepository;
import pedro.cost.control.helpers.BaseE2ETest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContractE2ETest extends BaseE2ETest {
    private static final String PJ_ENDPOINT = "/api/v2/contracts/employment/pj";
    private static final String CLT_ENDPOINT = "/api/v2/contracts/employment/clt";
    private static final String CONTRACTS_ENDPOINT = "/api/v2/contracts";

    @Autowired
    private EmploymentContractRepository employmentContractRepository;

    @Autowired
    private EmploymentContractPjRepository employmentContractPjRepository;

    @Autowired
    private EmploymentContractCltRepository employmentContractCltRepository;

    private TestUser userA;
    private TestUser userB;

    @BeforeAll
    void createUsers() {
        userA = authHelper.createUser("userA", "usera@test.com");
        userB = authHelper.createUser("userB", "userb@test.com");
    }

    @BeforeEach
    void cleanDatabase() {
        employmentContractPjRepository.deleteAll();
        employmentContractCltRepository.deleteAll();
        employmentContractRepository.deleteAll();
    }

    @Test
    void shouldCreateContractPjWithOutEndDateExpectSuccess() {
        EmploymentContractPj expected = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractType("PJ")
                .withContractEndDate(null)
                .build();

        String body = """
            {
                "hourlyRate": 50,
                "contractInitDate": "2025-01-01",
                "contractEndDate": ""
            }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();

        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);

        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expected);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldCreateContractPjWithEndDateExpectSuccess() {
        EmploymentContractPj expectedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 1))
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 50,
            "contractInitDate": "2025-01-01",
            "contractEndDate": "2025-12-01"
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldCreateContractPjIfHasOpenedContractMustBeClosedAndCreateNew() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                        .withHourlyRate(BigDecimal.valueOf(50.00))
                        .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                        .withContractEndDate(null)
                        .withContractType("PJ")
                        .withUser(userA)
                        .build();

        employmentContractPjRepository.save(existingContract);

        EmploymentContractPj expectedClosedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedNewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(52.00))
                .withContractInitDate(LocalDate.of(2026, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2026-01-01",
            "contractEndDate": ""
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedClosedContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedNewContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userA.user().getId());
        assertThat(employmentContractPjs.get(1).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldCreateContractPjIfHasOpenedContractOfAnotherUserHasNotBeClosedJustOpenNewContract() {
        EmploymentContractPj userBExistingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .withUser(userB)
                .build();

        employmentContractPjRepository.save(userBExistingContract);

        EmploymentContractPj expectedUserBOpenedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedNewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(52.00))
                .withContractInitDate(LocalDate.of(2026, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2026-01-01",
            "contractEndDate": ""
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserBOpenedContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedNewContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userB.user().getId());
        assertThat(employmentContractPjs.get(1).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldCreateContractPjWithInitDateBeforeOfAnExitsContractMustBeCreateNewAndNotCloseTheOther() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .withUser(userA)
                .build();

        employmentContractPjRepository.save(existingContract);

        EmploymentContractPj expectedExistentContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedNewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(40.00))
                .withContractInitDate(LocalDate.of(2024, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2024, Month.DECEMBER, 31))
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 40,
            "contractInitDate": "2024-01-01",
            "contractEndDate": "2024-12-31"
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedExistentContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedNewContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userA.user().getId());
        assertThat(employmentContractPjs.get(1).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldCreateContractPjWithInitDateBeforeOfAnExitsContractFromAnotherUserMustBeCreateNewAndNotCloseTheOther() {
        EmploymentContractPj userBExistingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .withUser(userB)
                .build();

        employmentContractPjRepository.save(userBExistingContract);

        EmploymentContractPj expectedUserBOpenedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedUserANewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(40.00))
                .withContractInitDate(LocalDate.of(2024, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2024, Month.DECEMBER, 31))
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 40,
            "contractInitDate": "2024-01-01",
            "contractEndDate": "2024-12-31"
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserBOpenedContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserANewContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userB.user().getId());
        assertThat(employmentContractPjs.get(1).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldNotAllowCreatingPjContractWhenAnotherIsOpenedInSamePeriod() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .withUser(userA)
                .build();

        employmentContractPjRepository.save(existingContract);

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2025-01-02",
            "contractEndDate": ""
        }
        """;

        ErrorResponse endpointReturn = authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(409)
                .extract()
                .as(ErrorResponse.class);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);
        assertThat(endpointReturn.getMessage()).isEqualTo("Já existe um contrato PJ ativo para essa data");
    }

    @Test
    void shouldAllowCreatingPjContractWhenAnotherIsOpenedInSamePeriodFromAnotherUser() {
        EmploymentContractPj userBExistingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .withUser(userB)
                .build();

        employmentContractPjRepository.save(userBExistingContract);

        EmploymentContractPj expectedUserBOpenedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedUserANewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 50.00,
            "contractInitDate": "2025-01-01",
            "contractEndDate": ""
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserBOpenedContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserANewContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userB.user().getId());
        assertThat(employmentContractPjs.get(1).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldNotAllowCreatingPjContractWhenAnotherClosedInSamePeriod() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
                .withContractType("PJ")
                .withUser(userA)
                .build();

        employmentContractPjRepository.save(existingContract);

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2025-12-31",
            "contractEndDate": ""
        }
        """;

        ErrorResponse endpointReturn = authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(409)
                .extract()
                .as(ErrorResponse.class);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);
        assertThat(endpointReturn.getMessage()).isEqualTo("Já existe um contrato PJ ativo para essa data");
    }

    @Test
    void shouldAllowCreatingPjContractWhenAnotherClosedInSamePeriodFromAnotherUser() {
        EmploymentContractPj userBExistingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
                .withContractType("PJ")
                .withUser(userB)
                .build();

        employmentContractPjRepository.save(userBExistingContract);

        EmploymentContractPj expectedUserBOpenedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedUserANewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(52.00))
                .withContractInitDate(LocalDate.of(2025, Month.DECEMBER, 31))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2025-12-31",
            "contractEndDate": ""
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(PJ_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserBOpenedContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedUserANewContract);

        assertThat(employmentContractPjs.get(0).getUser().getId()).isEqualTo(userB.user().getId());
        assertThat(employmentContractPjs.get(1).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldCreateContractCltWithOutEndDateExpectSuccess() {
        EmploymentContractClt expectedContractClt = EmploymentContractCltTestBuilder.builder()
                .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .withContractType("CLT")
                .withContractEndDate(null)
                .withGrossSalary(BigDecimal.valueOf(3000.00))
                .withNetSalary(BigDecimal.valueOf(2800.00))
                .withUser(userA)
                .build();

        String body = """
        {
            "grossSalary": 3000,
            "netSalary": 2800,
            "contractInitDate": "2025-01-01",
            "contractEndDate": ""
        }
        """;

        authenticatedRequest(userA)
                .contentType("application/json")
                .body(body)
                .when()
                .post(CLT_ENDPOINT)
                .then()
                .statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractClt> employmentContractClt = employmentContractCltRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractClt).hasSize(1);


        assertThat(employmentContractClt.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id", "user")
                .isEqualTo(expectedContractClt);

        assertThat(employmentContractClt.get(0).getUser().getId()).isEqualTo(userA.user().getId());
    }

    @Test
    void shouldReturnFirstPageWithCorrectPaginationMetadata() {
        for (int i = 0; i <= 10; i++) {
            employmentContractPjRepository.save(
                    EmploymentContractPjTestBuilder.builder()
                            .withHourlyRate(BigDecimal.valueOf(50).add(BigDecimal.valueOf(i)))
                            .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1).plusYears(i))
                            .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31).plusYears(i))
                            .withContractType("PJ")
                            .withUser(userA)
                            .build()
            );
        }

        authenticatedRequest(userA)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(CONTRACTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(10))
                .body("pageable.pageNumber", equalTo(0))
                .body("pageable.pageSize", equalTo(10))
                .body("totalElements", equalTo(11))
                .body("totalPages", equalTo(2))
                .body("first", equalTo(true))
                .body("last", equalTo(false))
                .body("content[0].hourlyRate", equalTo(60.0F))
                .body("content[1].hourlyRate", equalTo(59.0F))
                .body("content.contractType", everyItem(equalTo("PJ")));
    }

    @Test
    void shouldReturnSecondPageWithRemainingElements() {
        for (int i = 0; i <= 10; i++) {
            employmentContractPjRepository.save(
                    EmploymentContractPjTestBuilder.builder()
                            .withHourlyRate(BigDecimal.valueOf(50).add(BigDecimal.valueOf(i)))
                            .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1).plusYears(i))
                            .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31).plusYears(i))
                            .withContractType("PJ")
                            .withUser(userA)
                            .build()
            );
        }

        authenticatedRequest(userA)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("sort", "hourlyRate,asc")
                .when()
                .get(CONTRACTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("pageable.pageNumber", equalTo(1))
                .body("pageable.pageSize", equalTo(10))
                .body("totalElements", equalTo(11))
                .body("totalPages", equalTo(2))
                .body("first", equalTo(false))
                .body("last", equalTo(true))
                .body("content[0].hourlyRate", equalTo(50.0F));
    }

    @Test
    void shouldReturnEmptyPageWhenNoContractsExist() {
        authenticatedRequest(userA)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(CONTRACTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(0))
                .body("totalElements", equalTo(0))
                .body("totalPages", equalTo(0))
                .body("first", equalTo(true))
                .body("last", equalTo(true));
    }

    @Test
    void shouldReturnMixedContractTypesCorrectly() {
        employmentContractPjRepository.save(
                EmploymentContractPjTestBuilder.builder()
                        .withHourlyRate(BigDecimal.valueOf(100))
                        .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1))
                        .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
                        .withContractType("PJ")
                        .withUser(userA)
                        .build()
        );

        employmentContractCltRepository.save(
                EmploymentContractCltTestBuilder.builder()
                        .withGrossSalary(BigDecimal.valueOf(5000.))
                        .withNetSalary(BigDecimal.valueOf(4000))
                        .withContractInitDate(LocalDate.of(2026, Month.JANUARY, 1))
                        .withContractEndDate(null)
                        .withContractType("CLT")
                        .withUser(userA)
                        .build()
        );

        authenticatedRequest(userA)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(CONTRACTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("content[0].contractType", equalTo("CLT"))
                .body("content[0].grossSalary", equalTo(5000.0F))
                .body("content[0].netSalary", equalTo(4000.0F))
                .body("content[0].hourlyRate", nullValue())
                .body("content[1].contractType", equalTo("PJ"))
                .body("content[1].hourlyRate", equalTo(100.0F))
                .body("content[1].grossSalary", nullValue())
                .body("content[1].netSalary", nullValue());
    }

    @Test
    void shouldReturnOnlyLoggedUsersContractsWhenHasContractsFromAnotherUserCreated() {
        for (int i = 0; i < 5; i++) {
            employmentContractPjRepository.save(
                    EmploymentContractPjTestBuilder.builder()
                            .withHourlyRate(BigDecimal.valueOf(50).add(BigDecimal.valueOf(i)))
                            .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1).plusYears(i))
                            .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31).plusYears(i))
                            .withContractType("PJ")
                            .withUser(userA)
                            .build()
            );
        }

        for (int i = 0; i < 5; i++) {
            employmentContractPjRepository.save(
                    EmploymentContractPjTestBuilder.builder()
                            .withHourlyRate(BigDecimal.valueOf(56).add(BigDecimal.valueOf(i)))
                            .withContractInitDate(LocalDate.of(2025, Month.JANUARY, 1).plusYears(i))
                            .withContractEndDate(LocalDate.of(2025, Month.DECEMBER, 31).plusYears(i))
                            .withContractType("PJ")
                            .withUser(userB)
                            .build()
            );
        }

        authenticatedRequest(userA)
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get(CONTRACTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(5))
                .body("pageable.pageNumber", equalTo(0))
                .body("pageable.pageSize", equalTo(5))
                .body("totalElements", equalTo(5))
                .body("totalPages", equalTo(1))
                .body("first", equalTo(true))
                .body("last", equalTo(true))
                .body("content[0].hourlyRate", equalTo(54.0F))
                .body("content[1].hourlyRate", equalTo(53.0F))
                .body("content.contractType", everyItem(equalTo("PJ")));

        authenticatedRequest(userB)
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get(CONTRACTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(5))
                .body("pageable.pageNumber", equalTo(0))
                .body("pageable.pageSize", equalTo(5))
                .body("totalElements", equalTo(5))
                .body("totalPages", equalTo(1))
                .body("first", equalTo(true))
                .body("last", equalTo(true))
                .body("content[0].hourlyRate", equalTo(60.0F))
                .body("content[1].hourlyRate", equalTo(59.0F))
                .body("content.contractType", everyItem(equalTo("PJ")));
    }
}