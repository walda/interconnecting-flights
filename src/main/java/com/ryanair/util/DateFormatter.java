package com.ryanair.util;


import com.ryanair.exception.InvalidDateException;
import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@UtilityClass
public class DateFormatter {

    public static final TimeZone defaultTimeZone = TimeZone.getTimeZone("UTC");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public static Date parse(String date) {
        simpleDateFormat.setTimeZone(defaultTimeZone);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateException(e.getMessage());
        }
    }

    public static Date parse(String date, String exceptionMessage) {
        simpleDateFormat.setTimeZone(defaultTimeZone);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateException(exceptionMessage);
        }
    }

}
