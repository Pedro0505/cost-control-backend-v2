package pedro.cost.control.domain.creditcard.emuns;

/**
 * O que acontece quando a regex de um mapeamento casa com a descrição.
 */
public enum DescriptionMappingType {
    /** A descrição passa a ser o {@code value} do mapeamento. */
    CLASSIFY,
    /** O lançamento é descartado (ex.: estornos, pagamentos recebidos). */
    IGNORE
}
