package pedro.cost.control.domain.creditcard.emuns;

import lombok.Getter;

@Getter
public enum DescriptionNameEnum {
    ASSINATURAS("Assinaturas"),
    TRANSPORTE("Transporte"),
    DELIVERY("Delivery"),
    AMAZON("Amazon"),
    STEAM("Steam"),
    MERCADOLIVRE("Mercado Livre"),
    ALIMENTACAO("Alimentação"),
    COMPRAS_ONLINE("Compras Online"),
    SAUDE("Saúde");

    private final String value;

    DescriptionNameEnum(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
