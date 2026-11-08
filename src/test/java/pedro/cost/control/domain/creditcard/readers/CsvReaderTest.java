package pedro.cost.control.domain.creditcard.readers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockMultipartFile;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvReaderTest {

    private final CsvReader reader = new CsvReader();

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "114,40|114.40",
            "\"114,40\"|114.40",
            "1.234,56|1234.56",
            "89.90|89.90",
            "1,234.56|1234.56",
            "-12,50|-12.50",
            "100|100.0"
    })
    void parseAmountShouldHandleBothFormats(String raw, double expected) {
        assertEquals(expected, CsvReader.parseAmount(raw), 0.0001);
    }

    @Test
    void readShouldParseNewFormatWithQuotedCommaDecimal() {
        String csv = "date,title,amount\n2026-11-08,Anthropic* Claude Sub,\"114,40\"\n";

        List<CostFileDiscriminationContext> result = reader.read(file(csv));

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 11, 8), result.get(0).getDate());
        assertEquals("Anthropic* Claude Sub", result.get(0).getRawDescription());
        assertEquals(114.40, result.get(0).getAmount(), 0.0001);
    }

    @Test
    void readShouldParseOldFormatWithDotDecimal() {
        String csv = "date,title,amount\n2024-11-07,Centro de Atividades,89.90\n";

        List<CostFileDiscriminationContext> result = reader.read(file(csv));

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2024, 11, 7), result.get(0).getDate());
        assertEquals("Centro de Atividades", result.get(0).getRawDescription());
        assertEquals(89.90, result.get(0).getAmount(), 0.0001);
    }

    private MockMultipartFile file(String content) {
        return new MockMultipartFile("file", "fatura.csv", "text/csv",
                content.getBytes(StandardCharsets.UTF_8));
    }
}
