package com.ems.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Centralized date-time utility.
 *
 * Design Rules:
 * - Database storage type: Instant (UTC)
 * - UI input: LocalDateTime in user zone
 * - UI output: LocalDateTime formatted in user zone
 * - No implicit system timezone assumptions
 */
public final class DateTimeUtil {

    private DateTimeUtil() {}

    // Explicitly define application zone for console app
    private static final ZoneId APPLICATION_ZONE = ZoneId.systemDefault();

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
    	    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
    	    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),
    	    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
    	    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    	    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
    	    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    	);

    /* ===================== CURRENT TIME ===================== */

    public static Instant nowUtc() {
        return Instant.now();
    }

    /* ===================== STORAGE CONVERSION ===================== */

    // Convert user local time to UTC Instant for DB storage
    public static Instant toUtcInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(APPLICATION_ZONE).toInstant();
    }

    // Convert DB timestamp to Instant
    public static Instant fromTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    // Convert Instant from DB to LocalDateTime for display
    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, APPLICATION_ZONE);
    }
    
    // Convert Instant from DB to TimeStamp
    public static Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    /* ===================== FORMATTING ===================== */

    public static String formatForDisplay(Instant instant) {
        if (instant == null) return null;
        LocalDateTime local = toLocalDateTime(instant);
        return local.format(DISPLAY_FORMAT);
    }

    /* ===================== PARSING ===================== */

    public static LocalDateTime parseLocalDateTime(String input) {
    	if (input == null || input.trim().isEmpty()) return null;

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    public static LocalDate parseLocalDate(String input) {
        if (input == null || input.trim().isEmpty()) return null;

        List<DateTimeFormatter> dateFormatters = Arrays.asList(
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException ignored) {}
        }

        return null;
    }
}