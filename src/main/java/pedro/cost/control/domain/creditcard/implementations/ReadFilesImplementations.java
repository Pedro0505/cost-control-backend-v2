package pedro.cost.control.domain.creditcard.implementations;

import org.springframework.web.multipart.MultipartFile;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;

import java.util.List;

public interface ReadFilesImplementations {
    List<CostFileDiscriminationContext> read(MultipartFile content);
    String getName();
}
