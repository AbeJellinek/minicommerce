package me.abje.minicommerce.db.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.util.Date;

@Converter(autoApply = true)
public class InstantConverter implements AttributeConverter<Instant, Date> {

    @Override
    public Date convertToDatabaseColumn(Instant instant) {
        return Date.from(instant);
    }

    @Override
    public Instant convertToEntityAttribute(Date value) {
        return value.toInstant();
    }
}
