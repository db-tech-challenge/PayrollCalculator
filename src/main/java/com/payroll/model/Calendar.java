package com.payroll.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Calendar record.
 * Contains detailed calendar data for each day.
 */
public record Calendar(
    int year,
    int month,
    int day,
    DayOfWeek dayOfWeek,
    boolean isHoliday
) {
    /**
     * Creates a Calendar instance from the provided data.
     */
    public static Calendar of(int year, int month, int day, String dayOfWeekStr,
                              String isHolidayStr) {
        DayOfWeek dayOfWeek = switch (dayOfWeekStr.toUpperCase()) {
            case "MON" -> DayOfWeek.MONDAY;
            case "TUE" -> DayOfWeek.TUESDAY;
            case "WED" -> DayOfWeek.WEDNESDAY;
            case "THU" -> DayOfWeek.THURSDAY;
            case "FRI" -> DayOfWeek.FRIDAY;
            case "SAT" -> DayOfWeek.SATURDAY;
            case "SUN" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Invalid day of week: " + dayOfWeekStr);
        };


        boolean isHoliday = "Y".equalsIgnoreCase(isHolidayStr);
        return new Calendar(year, month, day, dayOfWeek, isHoliday);
    }

    /**
     * Gets the LocalDate representation of this calendar day.
     *
     * @return The LocalDate
     */
    public LocalDate toLocalDate() {
        return LocalDate.of(year, month, day);
    }

    /**
     * Checks if this calendar day is a working day (not a holiday and not a weekend).
     *
     * @return true if it's a working day, false otherwise
     */
    public boolean isWorkingDay() {
        return !isHoliday && dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    @Override
    public String toString() {
        return String.format("Calendar{%04d-%02d-%02d, %s, holiday=%b}",
            year, month, day, dayOfWeek, isHoliday);
    }
}