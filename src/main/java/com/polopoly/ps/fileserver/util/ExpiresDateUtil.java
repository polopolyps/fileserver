package com.polopoly.ps.fileserver.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sun.jersey.core.header.HttpDateFormat;

/**
 * Utility class to generate expires dates
 * for HTTP headers
 */

public class ExpiresDateUtil
{
    private static final SimpleDateFormat DATE_FORMAT = HttpDateFormat.getPreferedDateFormat();

    /**
     * Gets an best practice infinite expires date
     * @return A date one year from now
     */
    public static String getInfinateExpiresDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        String date = DATE_FORMAT.format(calendar.getTime());
        return date;
    }
}