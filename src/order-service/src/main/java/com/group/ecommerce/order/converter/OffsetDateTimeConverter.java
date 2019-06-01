package com.group.ecommerce.order.converter;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class OffsetDateTimeConverter implements AttributeConverter <OffsetDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(OffsetDateTime attribute) {
        return attribute != null ? Timestamp.valueOf(attribute
                .atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()) : null;
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(Timestamp dbData) {
        return dbData != null ? OffsetDateTime.ofInstant(Instant
                .ofEpochMilli(dbData.getTime()), ZoneOffset.UTC) : null;
    }

}