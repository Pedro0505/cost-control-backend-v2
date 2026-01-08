package pedro.cost.control.enums;

import lombok.Getter;


@Getter
public enum MonthEnum {
    JANEIRO(1, "Janeiro"),
    FEVEREIRO(2, "Fevereiro"),
    MARCO(3, "Março"),
    ABRIL(4, "Abril"),
    MAIO(5, "Maio"),
    JUNHO(6, "Junho"),
    JULHO(7, "Julho"),
    AGOSTO(8, "Agosto"),
    SETEMBRO(9, "Setembro"),
    OUTUBRO(10, "Outubro"),
    NOVEMBRO(11, "Novembro"),
    DEZEMBRO(12, "Dezembro");

    private final Integer id;
    private final String monthName;

    MonthEnum(int numero, String monthName) {
        this.id = numero;
        this.monthName = monthName;
    }

    public static MonthEnum getMonthById(int id) {
        for (MonthEnum mes : MonthEnum.values()) {
            if (mes.getId() == id) {
                return mes;
            }
        }
        throw new IllegalArgumentException("Número de mês inválido: " + id);
    }
}
