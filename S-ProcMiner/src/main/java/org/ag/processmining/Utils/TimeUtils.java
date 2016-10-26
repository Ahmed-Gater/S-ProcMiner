/*
 * @author Ahmed Gater (ahmed.gater@gmail.com)
 */
package org.ag.processmining.Utils;

import org.joda.time.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ahmed
 */
public final class TimeUtils implements Serializable {
    static final long serialVersionUID = 1L;

    public static enum TimeUnit {
        MONTH,
        WEEK,
        DAY,
        HOUR,
        MINUTE,
        SECOND
    }
    private TimeUtils() {

    }

    public static double duration(DateTime start, DateTime end, TimeUnit tu) {
        if (start == null || end == null) {
            return -1 ;
        }
        switch (tu) {
            case MONTH:
                return Months.monthsBetween(start,end).getMonths() ;
            case WEEK:
                Weeks.weeksBetween(start, end).getWeeks();
            case DAY:
                return Days.daysBetween(start,end).getDays() ;
            case HOUR:
                return Hours.hoursBetween(start, end).getHours();
            case MINUTE:
                return Minutes.minutesBetween(start,end).getMinutes() ;
            case SECOND:
                Seconds.secondsBetween(start,end).getSeconds() ;
            default:
                return Days.daysBetween(start,end).getDays() ;
        }
    }

    public static List<DateTime> daysBetween(DateTime start, DateTime end){
        DateTime startRef = new DateTime(start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), 0, 0);
        DateTime endRef = new DateTime(end.getYear(), end.getMonthOfYear(), end.getDayOfMonth(), 0, 0);
        List<DateTime> days = new ArrayList<>();
        while (startRef.compareTo(endRef) <= 0) {
            days.add(startRef);
            startRef = startRef.plusDays(1);
        }
        return days ;
    }

}
