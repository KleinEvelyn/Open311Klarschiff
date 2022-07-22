package com.iu.open311_klarschiff;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateUtils {

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return "";
        }

        return localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.GERMAN));
    }
}