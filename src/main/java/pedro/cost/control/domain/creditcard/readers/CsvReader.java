package pedro.cost.control.domain.creditcard.readers;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pedro.cost.control.config.exceptions.BadRequestException;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.emuns.ContentType;
import pedro.cost.control.domain.creditcard.implementations.ReadFilesImplementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Component
public class CsvReader implements ReadFilesImplementations {
    @Override
    public List<CostFileDiscriminationContext> read(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            int headerPosition = 1;

            Stream<CostFileDiscriminationContext> costFileDiscriminationStream = reader
                    .lines()
                    .skip(headerPosition)
                    .map(e -> parseCsvString(e, ","));

            return costFileDiscriminationStream.toList();
        } catch (IOException e) {
            throw new BadRequestException("Erro ao ler arquivo");
        }
    }

    @Override
    public String getName() {
        return ContentType.CSV.get();
    }

    public CostFileDiscriminationContext parseCsvString(String line, String separator) {
        String[] parts = line.split(separator);

        LocalDate dateColumn = LocalDate.parse(parts[0].trim());
        String descriptionColumn = parts[1].trim();
        Double amountColumn = Double.valueOf(parts[2].trim());

        CostFileDiscriminationContext dto = new CostFileDiscriminationContext();
        dto.setDate(dateColumn);
        dto.setDescription(descriptionColumn);
        dto.setAmount(amountColumn);

        return dto;
    }
}
