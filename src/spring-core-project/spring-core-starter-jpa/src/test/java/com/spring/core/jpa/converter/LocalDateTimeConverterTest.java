package com.spring.core.jpa.converter;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LocalDateTimeConverterTest {

    private LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @Test
    public void convertToDatabaseColumn() {
        LocalDateTime localDate = LocalDateTime.of(2019,12,1,23,45,12);
        Timestamp result= converter.convertToDatabaseColumn(localDate);
        Assert.assertTrue(result.equals(Timestamp.valueOf(localDate)));
    }

    @Test
    public void convertToEntityAttribute() {
        LocalDateTime expected = LocalDateTime.of(2019,12,1,23,45,12);
        LocalDateTime result= converter.convertToEntityAttribute(Timestamp.valueOf(expected));
        Assert.assertTrue(result.equals(expected));
    }
}