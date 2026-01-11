package pedro.cost.control.domain.creditcard.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.implementations.ReadFilesImplementations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {
    private final Map<String, ReadFilesImplementations> readers = new HashMap<>();
    private final CostFileClusterService costFileClusterService;

    public FileService(List<ReadFilesImplementations> readersImplementations, CostFileClusterService costFileClusterService) {
        this.costFileClusterService = costFileClusterService;
        for (ReadFilesImplementations e : readersImplementations) {
            readers.put(e.getName(), e);
        }
    }

    public void uploadFileContent(MultipartFile file) {
        ReadFilesImplementations fileReader = readers.get(file.getContentType());

        List<CostFileDiscriminationContext> fileContent = fileReader.read(file);

        Map<String, List<CostFileDiscriminationContext>> expensesByEnterprise = costFileClusterService.groupExpensesByEnterprise(fileContent);

        return;
    }
}
