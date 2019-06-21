package com.spring.core.jpa.converter;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class OffsetDateTimeConverterTest {
    private OffsetDateTimeConverter converter = new OffsetDateTimeConverter();

    @Test
    public void convertToDatabaseColumn() {
        LocalDateTime localDate = LocalDateTime.of(2019,12,1,23,45,12);
        Timestamp result= converter.convertToDatabaseColumn(OffsetDateTime.of(localDate, ZoneOffset.UTC));
        Assert.assertTrue(result.equals(Timestamp.valueOf(localDate)));
    }

    @Test
    public void convertToEntityAttribute() {
        LocalDateTime localDate = LocalDateTime.of(2019,12,1,23,45,12);
        OffsetDateTime result= converter.convertToEntityAttribute(Timestamp.valueOf(localDate));
        Assert.assertEquals(result, OffsetDateTime.of(localDate, ZoneOffset.UTC));
    }
}