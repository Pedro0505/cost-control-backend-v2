package pedro.cost.control.domain.creditcard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;
import pedro.cost.control.domain.creditcard.entities.DescriptionMapping;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DescriptionNormalizerTest {

    private static DescriptionMapping rule(long id, int position, String pattern, String value) {
        return DescriptionMapping.builder()
                .id(id).position(position).pattern(pattern)
                .type(DescriptionMappingType.CLASSIFY).value(value)
                .build();
    }

    private static DescriptionMapping ignore(long id, int position, String pattern) {
        return DescriptionMapping.builder()
                .id(id).position(position).pattern(pattern)
                .type(DescriptionMappingType.IGNORE)
                .build();
    }

    @Test
    void firstMatchingRuleByPositionWins() {
        DescriptionMapping club = rule(1, 1, "ifood\\s*club", "Assinaturas");
        DescriptionMapping ifood = rule(2, 2, "ifood", "Delivery");

        assertEquals("Assinaturas", DescriptionNormalizer.of(List.of(club, ifood)).normalize("IFOOD CLUB"));

        club.setPosition(2);
        ifood.setPosition(1);
        assertEquals("Delivery", DescriptionNormalizer.of(List.of(club, ifood)).normalize("IFOOD CLUB"));
    }

    @Test
    void orderComesFromPositionNotFromListOrder() {
        DescriptionMapping club = rule(1, 1, "ifood\\s*club", "Assinaturas");
        DescriptionMapping ifood = rule(2, 2, "ifood", "Delivery");

        assertEquals("Assinaturas", DescriptionNormalizer.of(List.of(ifood, club)).normalize("IFOOD CLUB"));
    }

    @Test
    void ignoreRuleReturnsNullAndRespectsOrder() {
        DescriptionMapping estorno = ignore(1, 1, "\\bestorno\\b");
        DescriptionMapping amazon = rule(2, 2, "amazon", "Amazon");

        assertNull(DescriptionNormalizer.of(List.of(estorno, amazon)).normalize("Estorno Amazon"));

        estorno.setPosition(2);
        amazon.setPosition(1);
        assertEquals("Amazon", DescriptionNormalizer.of(List.of(estorno, amazon)).normalize("Estorno Amazon"));
    }

    @Test
    void fallsBackToOutros() {
        DescriptionNormalizer normalizer = DescriptionNormalizer.of(List.of(rule(1, 1, "amazon", "Amazon")));

        assertEquals("Outros", normalizer.normalize("Posto Shell"));
        assertEquals("Outros", normalizer.normalize(null));
        assertEquals("Outros", DescriptionNormalizer.of(List.of()).normalize("Amazon"));
    }

    @Test
    void isCaseInsensitiveAndAccentInsensitiveOnBothSides() {
        DescriptionNormalizer normalizer = DescriptionNormalizer.of(List.of(rule(1, 1, "farmácia", "Saúde")));

        assertEquals("Saúde", normalizer.normalize("FARMACIA SAO JOAO"));
        assertEquals("Saúde", normalizer.normalize("Farmácia São João"));
    }

    @Test
    void stripsInstallmentSuffixBeforeMatching() {
        DescriptionNormalizer normalizer = DescriptionNormalizer.of(List.of(rule(1, 1, "kabum$", "Compras Online")));

        assertEquals("Compras Online", normalizer.normalize("KABUM - Parcela 2/10"));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = "NULL", value = {
            "Pagamento recebido|NULL",
            "Estorno Uber|NULL",
            "Amazon Prime Canais|Assinaturas",
            "AMAZON MARKETPLACE|Amazon",
            "IFOOD CLUB|Assinaturas",
            "IFD*RESTAURANTE|Delivery",
            "Ifood *Lanches|Delivery",
            "Uber *Trip|Transporte",
            "DL*GOOGLE YouTube|Assinaturas",
            "Steamgames.com|Steam",
            "MP *LOJA|Mercado Livre",
            "Padaria Pão Quente|Alimentação",
            "Magalu - Parcela 3/10|Compras Online",
            "Drogasil Raia Drogasil|Saúde",
            "Pague Menos 123|Saúde",
            "Centro de Atividades|Outros"
    })
    void defaultsMatchPreviousHardcodedBehavior(String raw, String expected) {
        List<DescriptionMapping> mappings = new ArrayList<>();
        for (int i = 0; i < DefaultDescriptionMappings.ALL.size(); i++) {
            DefaultDescriptionMappings.DefaultMapping d = DefaultDescriptionMappings.ALL.get(i);
            mappings.add(DescriptionMapping.builder()
                    .id((long) i + 1).position(i + 1)
                    .pattern(d.pattern()).type(d.type()).value(d.value())
                    .build());
        }

        assertEquals(expected, DescriptionNormalizer.of(mappings).normalize(raw));
    }
}
