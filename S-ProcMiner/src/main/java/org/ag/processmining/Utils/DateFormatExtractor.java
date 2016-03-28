/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.processmining.Utils;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFormatExtractor {
    
 private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
    put("^\\d{8}$", "yyyyMMdd");
    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
    put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
    put("^\\d{12}$", "yyyyMMddHHmm");
    put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
    put("^\\d{14}$", "yyyyMMddHHmmss");
    put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
}};

/**
 * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
 * format is unknown. You can simply extend DateUtil with more formats if needed.
 * @param dateString The date string to determine the SimpleDateFormat pattern for.
 * @return The matching SimpleDateFormat pattern, or null if format is unknown.
 * @see SimpleDateFormat
 */
private static String determineDateFormat(String dateString) {
    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
        if (dateString.toLowerCase().matches(regexp)) {
            return DATE_FORMAT_REGEXPS.get(regexp);
        }
    }
    return null; // Unknown format.
}

public static DateTime buildDateTime(String dateAsString){
    return DateTimeFormat.forPattern(determineDateFormat(dateAsString))
                         .parseDateTime(dateAsString) ;
}
}