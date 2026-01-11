package com.userservice.expmbff.utils;

import com.userservice.expmbff.entity.UserEntity;
import com.userservice.expmbff.entity.enums.UserLimit;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

public class AppUtility {

    public static Integer MASKING_DEFAULT_VISIBLE_CHARS = 3;
    /**
     * Masks the data after a specified number of visible characters
     * @param data The string to be masked
     * @param visibleChars Number of characters to keep visible from the start
     * @param maskChar The character to use for masking (default '*')
     * @return Masked string
     */
    public static String maskDataAfter(String data, int visibleChars, char maskChar) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        if (visibleChars <= 0) {
            return String.valueOf(maskChar).repeat(data.length());
        }

        if (visibleChars >= data.length()) {
            return data;
        }

        String visiblePart = data.substring(0, visibleChars);

        return visiblePart + "****";
    }

    /**
     * Masks the data after a specified number of visible characters (uses '*' as default mask)
     * @param data The string to be masked
     * @param visibleChars Number of characters to keep visible from the start
     * @return Masked string
     */
    public static String maskDataAfter(UserEntity user, String data, int visibleChars) {
        return data;
        /*if(user.getPrivilege().equalsIgnoreCase(UserLimit.ADMIN.name())) {
            //no masking for admin users
            return data;
        } else {
            return maskDataAfter(data, visibleChars, '*');
        }*/
    }

    /**
     * Converts month and year to a timestamp (milliseconds since epoch)
     * Returns the timestamp for the first day of the given month at midnight UTC
     * @param month The month (1-12)
     * @param year The year (e.g., 2025)
     * @return Timestamp in milliseconds since epoch
     */
    public static Long getTimestampForMonth(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        return firstDayOfMonth.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    /**
     * Converts month and year strings to a timestamp (milliseconds since epoch)
     * Returns the timestamp for the first day of the given month at midnight UTC
     * @param month The month as string (e.g., "01" or "1")
     * @param year The year as string (e.g., "2025")
     * @return Timestamp in milliseconds since epoch
     */
    public static Long getTimestampForMonth(String month, String year) {
        int monthInt = Integer.parseInt(month);
        int yearInt = Integer.parseInt(year);
        return getTimestampForMonth(monthInt, yearInt);
    }
}
