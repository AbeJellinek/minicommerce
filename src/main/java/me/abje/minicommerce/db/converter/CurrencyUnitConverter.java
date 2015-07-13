package me.abje.minicommerce.db.converter;

import org.joda.money.CurrencyUnit;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyUnitConverter implements AttributeConverter<CurrencyUnit, String> {
    @Override
    public String convertToDatabaseColumn(CurrencyUnit attribute) {
        return attribute.getCurrencyCode();
    }

    @Override
    public CurrencyUnit convertToEntityAttribute(String dbData) {
        return CurrencyUnit.of(dbData);
    }
}
