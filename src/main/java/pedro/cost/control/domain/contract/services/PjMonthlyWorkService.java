package pedro.cost.control.domain.contract.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.domain.contract.dtos.PjMonthlyContractOutputDTO;
import pedro.cost.control.domain.contract.dtos.PjMonthlyWorkInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;
import pedro.cost.control.domain.contract.repositories.PjMonthlyWorkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PjMonthlyWorkService {
    private final PjMonthlyWorkRepository pjMonthlyWorkRepository;
    private final EmploymentContractPjService employmentContractPjService;

    public void save(PjMonthlyWork pjMonthlyWork) {
        pjMonthlyWorkRepository.save(pjMonthlyWork);
    }

    @Transactional
    public void createPjMonthlyWork(PjMonthlyWorkInputCreateDTO pjMonthlyWorkInputCreate) {
        Integer referenceYear = pjMonthlyWorkInputCreate.getReferenceYear();
        Integer referenceMonth = pjMonthlyWorkInputCreate.getReferenceMonth();

        EmploymentContract employmentContract  = employmentContractPjService.getEmploymentContractByYearAndMonth(
                referenceYear, referenceMonth
        );

        PjMonthlyWork pjMonthlyWork = createPjMonthlyWorkObject(
                referenceYear, referenceMonth, pjMonthlyWorkInputCreate.getBusinessDays(), employmentContract
        );

        save(pjMonthlyWork);
    }

    private PjMonthlyWork createPjMonthlyWorkObject(
            Integer referenceYear, Integer referenceMonth, Integer businessDays, EmploymentContract employmentContract
    ) {
        PjMonthlyWork pjMonthlyWork = new PjMonthlyWork();

        pjMonthlyWork.setReferenceMonth(referenceMonth);
        pjMonthlyWork.setReferenceYear(referenceYear);
        pjMonthlyWork.setBusinessDays(businessDays);
        pjMonthlyWork.setEmploymentContract(employmentContract);

        return pjMonthlyWork;
    }

    public LegacyPageResponse<PjMonthlyContractOutputDTO> getAllPjMonthlyWithContract(PageRequest pageable) {
        Page<PjMonthlyContractOutputDTO> pageResult = pjMonthlyWorkRepository.getAllPjMonthlyWithContractPageable(pageable);

        return new LegacyPageResponse<>(
                pageResult.getContent(),
                pageResult.getPageable(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.isLast(),
                pageResult.isFirst(),
                pageResult.getSize(),
                pageResult.getNumber(),
                pageResult.getSort(),
                pageResult.getNumberOfElements(),
                pageResult.isEmpty()
        );
    }


    public List<PjMonthlyContractOutputDTO> getAllPjMonthlyWithContractFiltered(Integer year) {
        return pjMonthlyWorkRepository.getAllPjMonthlyWithContractFiltered(year);
    }
}
