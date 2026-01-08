package pedro.cost.control.domain.contract.enums;

import lombok.Getter;

@Getter
public enum ContractType {
    CLT,
    PJ;

    public static ContractType getByName(String name) {
        for (ContractType contractType: ContractType.values()) {
            if (contractType.name().equals(name)) {
                return contractType;
            }
        }

        throw new IllegalArgumentException("Tipo de contrato inválido");
    }
}
