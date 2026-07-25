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

    // BUG #14 (MEDIUM): Uses system default timezone for date conversions
    // Should use UTC for all internal timestamps and convert to user's
    // timezone only at the presentation layer.
    // System default timezone varies by server location, causing inconsistent
    // order timestamps for users in different regions.

    public Date convertToDate(LocalDateTime localDateTime) {
        // BUG: Uses system default timezone
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime convertToLocalDateTime(Date date) {
        // BUG: Uses system default timezone
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // BUG: Uses system default timezone for ZonedDateTime
        ZonedDateTime zoned = dateTime.atZone(ZoneId.systemDefault());
        return zoned.format(formatter);
    }

    // Correct method exists but is never used by the rest of the application
    public String formatDateTimeUTC(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime zoned = dateTime.atZone(ZoneId.of("UTC"));
        return zoned.format(formatter);
    }
}
