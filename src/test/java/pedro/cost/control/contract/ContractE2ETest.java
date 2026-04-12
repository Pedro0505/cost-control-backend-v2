package pedro.cost.control.contract;

import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import pedro.cost.control.config.errorHandler.ErrorResponse;
import pedro.cost.control.contract.mocks.EmploymentContractCltTestBuilder;
import pedro.cost.control.contract.mocks.EmploymentContractPjTestBuilder;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractClt;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractCltRepository;
import pedro.cost.control.domain.contract.repositories.EmploymentContractPjRepository;
import pedro.cost.control.domain.contract.repositories.EmploymentContractRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContractE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private EmploymentContractRepository employmentContractRepository;

    @Autowired
    private EmploymentContractPjRepository employmentContractPjRepository;

    @Autowired
    private EmploymentContractCltRepository employmentContractCltRepository;

    @BeforeAll
    void setup() {
        RestAssured.port = port;
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
                .withContractInitDate(LocalDate.of(2025, 1, 1))
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

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/pj")
                .then().statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    void shouldCreateContractPjWithEndDateExpectSuccess() {
        EmploymentContractPj expectedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractEndDate(LocalDate.of(2025, 12, 1))
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 50,
            "contractInitDate": "2025-01-01",
            "contractEndDate": "2025-12-01"
        }
        """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/pj")
                .then().statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedContract);
    }

    @Test
    void shouldCreateContractPjIfHasOpenedContractMustBeClosedAndCreateNew() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                        .withHourlyRate(BigDecimal.valueOf(50.00))
                        .withContractInitDate(LocalDate.of(2025, 1, 1))
                        .withContractEndDate(null)
                        .withContractType("PJ")
                        .build();

        employmentContractPjRepository.save(existingContract);

        EmploymentContractPj expectedClosedContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractEndDate(LocalDate.of(2025, 12, 31))
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedNewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(52.00))
                .withContractInitDate(LocalDate.of(2026, 1, 1))
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

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/pj")
                .then().statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedClosedContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedNewContract);
    }

    @Test
    void shouldCreateContractPjWithInitDateBeforeOfAnExitsContractMustBeCreateNewAndNotCloseTheOther() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        employmentContractPjRepository.save(existingContract);

        EmploymentContractPj expectedExistentContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        EmploymentContractPj expectedNewContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(40.00))
                .withContractInitDate(LocalDate.of(2024, 1, 1))
                .withContractEndDate(LocalDate.of(2024, 12, 31))
                .withContractType("PJ")
                .build();

        String body = """
        {
            "hourlyRate": 40,
            "contractInitDate": "2024-01-01",
            "contractEndDate": "2024-12-31"
        }
        """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/pj")
                .then().statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(2);
        assertThat(employmentContractPjs).hasSize(2);


        assertThat(employmentContractPjs.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedExistentContract);

        assertThat(employmentContractPjs.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedNewContract);
    }

    @Test
    void shouldNotAllowCreatingPjContractWhenAnotherIsOpenedInSamePeriod() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractEndDate(null)
                .withContractType("PJ")
                .build();

        employmentContractPjRepository.save(existingContract);

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2025-01-02",
            "contractEndDate": ""
        }
        """;

        ErrorResponse endpointReturn = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/pj")
                .then().statusCode(409).extract().as(ErrorResponse.class);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);
        assertThat(endpointReturn.getMessage()).isEqualTo("Já existe um contrato PJ ativo para essa data");
    }

    @Test
    void shouldNotAllowCreatingPjContractWhenAnotherClosedInSamePeriod() {
        EmploymentContractPj existingContract = EmploymentContractPjTestBuilder.builder()
                .withHourlyRate(BigDecimal.valueOf(50.00))
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractEndDate(LocalDate.of(2025, 12, 31))
                .withContractType("PJ")
                .build();

        employmentContractPjRepository.save(existingContract);

        String body = """
        {
            "hourlyRate": 52,
            "contractInitDate": "2025-12-31",
            "contractEndDate": ""
        }
        """;

        ErrorResponse endpointReturn = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/pj")
                .then().statusCode(409).extract().as(ErrorResponse.class);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractPj> employmentContractPjs = employmentContractPjRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractPjs).hasSize(1);
        assertThat(endpointReturn.getMessage()).isEqualTo("Já existe um contrato PJ ativo para essa data");
    }

    @Test
    void shouldCreateContractCltWithOutEndDateExpectSuccess() {
        EmploymentContractClt expectedContractClt = EmploymentContractCltTestBuilder.builder()
                .withContractInitDate(LocalDate.of(2025, 1, 1))
                .withContractType("CLT")
                .withContractEndDate(null)
                .withGrossSalary(BigDecimal.valueOf(3000.00))
                .withNetSalary(BigDecimal.valueOf(2800.00))
                .build();

        String body = """
        {
            "grossSalary": 3000,
            "netSalary": 2800,
            "contractInitDate": "2025-01-01",
            "contractEndDate": ""
        }
        """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v2/contracts/employment/clt")
                .then().statusCode(201);

        List<EmploymentContract> employmentContracts = employmentContractRepository.findAll();
        List<EmploymentContractClt> employmentContractClt = employmentContractCltRepository.findAll();

        assertThat(employmentContracts).hasSize(1);
        assertThat(employmentContractClt).hasSize(1);


        assertThat(employmentContractClt.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedContractClt);
    }

    @Test
    void shouldReturnFirstPageWithCorrectPaginationMetadata() {
        for (int i = 0; i <= 10; i++) {
            employmentContractPjRepository.save(
                    EmploymentContractPjTestBuilder.builder()
                            .withHourlyRate(BigDecimal.valueOf(50).add(BigDecimal.valueOf(i)))
                            .withContractInitDate(LocalDate.of(2025, 1, 1).plusYears(i))
                            .withContractEndDate(LocalDate.of(2025, 12, 31).plusYears(i))
                            .withContractType("PJ")
                            .build()
            );
        }

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/v2/contracts")
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
                            .withContractInitDate(LocalDate.of(2025, 1, 1).plusYears(i))
                            .withContractEndDate(LocalDate.of(2025, 12, 31).plusYears(i))
                            .withContractType("PJ")
                            .build()
            );
        }

        given()
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("sort", "hourlyRate,asc")
                .when()
                .get("/api/v2/contracts")
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
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/v2/contracts")
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
                        .withContractInitDate(LocalDate.of(2025, 1, 1))
                        .withContractEndDate(LocalDate.of(2025, 12, 31))
                        .withContractType("PJ")
                        .build()
        );

        employmentContractCltRepository.save(
                EmploymentContractCltTestBuilder.builder()
                        .withGrossSalary(BigDecimal.valueOf(5000.))
                        .withNetSalary(BigDecimal.valueOf(4000))
                        .withContractInitDate(LocalDate.of(2026, 1, 1))
                        .withContractEndDate(null)
                        .withContractType("CLT")
                        .build()
        );

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/v2/contracts")
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
}