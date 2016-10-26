/*
 * @author Ahmed Gater (ahmed.gater@gmail.com)
 */
package org.ag.processmining.Utils;

import org.joda.time.*;

import java.io.Serializable;

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

    public static int duration(DateTime start, DateTime end, TimeUnit tu) {
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

}
