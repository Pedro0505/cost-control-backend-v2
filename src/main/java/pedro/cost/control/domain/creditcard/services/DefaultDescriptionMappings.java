package pedro.cost.control.domain.creditcard.services;

import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;

import java.util.List;

import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.ALIMENTACAO;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.AMAZON;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.ASSINATURAS;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.COMPRAS_ONLINE;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.DELIVERY;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.MERCADOLIVRE;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.SAUDE;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.STEAM;
import static pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum.TRANSPORTE;

final class DefaultDescriptionMappings {

    record DefaultMapping(String pattern, DescriptionMappingType type, String value) {
        static DefaultMapping ignore(String pattern) {
            return new DefaultMapping(pattern, DescriptionMappingType.IGNORE, null);
        }

        static DefaultMapping classify(String pattern, String value) {
            return new DefaultMapping(pattern, DescriptionMappingType.CLASSIFY, value);
        }
    }

    static final List<DefaultMapping> ALL = List.of(
            DefaultMapping.ignore("(?i)\\bpagamento(s)?\\s*recebido\\b"),
            DefaultMapping.ignore("(?i)\\bestorno\\b"),

            DefaultMapping.classify("(?i).*amazon\\s*prime.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*prime\\s*aluguel.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*amazon\\s+ad\\s+free.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*dl\\*google.*(youtub|google|one|play).*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*google\\s*(one|youtube|play).*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*hbomax.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*microsoft.*(subscription|meses).*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*match\\s*fit.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*ifood\\s*club.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i)^.*\\*\\s*melimais.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*vivo.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*ebanx.*|.*xsolla.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i).*crunchyroll.*", ASSINATURAS.getValue()),
            DefaultMapping.classify("(?i)^dl\\s*\\*\\s*google\\s+chess\\b.*", ASSINATURAS.getValue()),

            DefaultMapping.classify("(?i).*uber.*trip.*", TRANSPORTE.getValue()),
            DefaultMapping.classify("(?i).*\\buber\\b.*", TRANSPORTE.getValue()),
            DefaultMapping.classify("(?i).*99app.*", TRANSPORTE.getValue()),
            DefaultMapping.classify("(?i).*\\bdl\\s*\\*\\s*99\\s+ride\\b.*", TRANSPORTE.getValue()),
            DefaultMapping.classify("(?i).*uberrides.*", TRANSPORTE.getValue()),

            DefaultMapping.classify("(?i)^ifd\\*.*", DELIVERY.getValue()),
            DefaultMapping.classify("(?i).*ifood.*", DELIVERY.getValue()),
            DefaultMapping.classify("(?i)^ifd\\s+\\*.*", DELIVERY.getValue()),
            DefaultMapping.classify("(?i).*99food.*", DELIVERY.getValue()),

            DefaultMapping.classify("(?i).*amazon.*", AMAZON.getValue()),

            DefaultMapping.classify("(?i).*steam.*", STEAM.getValue()),

            DefaultMapping.classify("(?i).*mercadolivre.*", MERCADOLIVRE.getValue()),
            DefaultMapping.classify("(?i).*mercadopago.*", MERCADOLIVRE.getValue()),
            DefaultMapping.classify("(?i)^mp", MERCADOLIVRE.getValue()),

            DefaultMapping.classify("(?i).*padaria.*", ALIMENTACAO.getValue()),

            DefaultMapping.classify("(?i).*shopee.*", COMPRAS_ONLINE.getValue()),
            DefaultMapping.classify("(?i).*magazineluiza.*|.*magalu.*|.*magazinelu.*", COMPRAS_ONLINE.getValue()),
            DefaultMapping.classify("(?i).*kabum.*", COMPRAS_ONLINE.getValue()),
            DefaultMapping.classify("(?i).*nike.*", COMPRAS_ONLINE.getValue()),
            DefaultMapping.classify("(?i).*centauro.*", COMPRAS_ONLINE.getValue()),
            DefaultMapping.classify("(?i).*ferreira\\s*costa.*", COMPRAS_ONLINE.getValue()),
            DefaultMapping.classify("(?i).*nuuvem.*", COMPRAS_ONLINE.getValue()),

            DefaultMapping.classify(
                    "(?i).*farmaci.*|.*pague\\s*menos.*|.*raiadrogasil.*|.*pharma.*|.*fcia.*|.*raia\\sdrogasil.*",
                    SAUDE.getValue()
            )
    );

    private DefaultDescriptionMappings() {
    }
}
