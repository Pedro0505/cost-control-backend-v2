package pedro.cost.control.domain.contract.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.contract.dtos.CltContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContractClt;
import pedro.cost.control.domain.user.entities.User;

@Service
@RequiredArgsConstructor
public class EmploymentContractCltService {
    public EmploymentContractClt createEmploymentContractCltObject(CltContractInputCreateDTO cltContractInputCreateDTO, User user) {
        EmploymentContractClt employmentContractClt = new EmploymentContractClt();

        employmentContractClt.setInitDate(cltContractInputCreateDTO.getContractInitDate());
        employmentContractClt.setEndDate(cltContractInputCreateDTO.getContractEndDate());
        employmentContractClt.setNetSalary(cltContractInputCreateDTO.getNetSalary());
        employmentContractClt.setGrossSalary(cltContractInputCreateDTO.getGrossSalary());
        employmentContractClt.setUser(user);

        return employmentContractClt;
    }
}
