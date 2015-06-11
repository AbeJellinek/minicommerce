package me.abje.minicommerce.db.converter;

import me.abje.minicommerce.config.MinicommerceConfig;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

@Converter(autoApply = true)
@Component
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {
    private static MinicommerceConfig config;

    @Override
    public BigDecimal convertToDatabaseColumn(Money attribute) {
        return attribute.getAmount();
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        return Money.of(config.getCurrency(), dbData);
    }

    /**
     * Sets the static configuration.
     * There has to be a better way to do this...
     *
     * @param config The new config.
     */
    @Autowired
    public void setConfig(MinicommerceConfig config) {
        MoneyConverter.config = config;
    }
}
