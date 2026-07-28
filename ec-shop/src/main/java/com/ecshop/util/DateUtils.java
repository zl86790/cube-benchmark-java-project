package com.ecshop.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Slf4j
public class DateUtils {
    public Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant());
    }

    public LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zoned = dateTime.atZone(ZoneId.systemDefault());
        return zoned.format(formatter);
    }

    public String formatDateTimeUTC(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime zoned = dateTime.atZone(ZoneId.of("UTC"));
        return zoned.format(formatter);
    }
}
