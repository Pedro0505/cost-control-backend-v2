package pedro.cost.control.domain.creditcard.readers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pedro.cost.control.config.exceptions.BadRequestException;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.emuns.ContentType;
import pedro.cost.control.domain.creditcard.implementations.ReadFilesImplementations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("text/csv")
public class CsvReader implements ReadFilesImplementations {
    @Override
    public List<CostFileDiscriminationContext> read(MultipartFile file) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
        ) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get();

            List<CostFileDiscriminationContext> result = new ArrayList<>();

            for (CSVRecord csvRecord : format.parse(reader)) {

                CostFileDiscriminationContext dto = new CostFileDiscriminationContext();

                dto.setDate(LocalDate.parse(csvRecord.get(0)));
                dto.setRawDescription(csvRecord.get(1));
                String amount = csvRecord.get(2)
                        .replace("\"", "")
                        .replace(" ", "")
                        .replace(".", "")
                        .replace(",", ".");

                dto.setAmount(Double.parseDouble(amount));

                result.add(dto);
            }

            return result;

        } catch (Exception e) {
            throw new BadRequestException("Erro ao ler arquivo");
        }
    }

    @Override
    public String getName() {
        return ContentType.CSV.get();
    }
}
