/*
 * @author Ahmed Gater (ahmed.gater@gmail.com)
 */
package org.ag.processmining.log.summarizer.utils;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;


/**
 * @author ahmed
 */
public class TimeFrame implements Serializable {
    static final long serialVersionUID = 1L;
    private DateTime startDate = null;
    private DateTime endDate = null;

    public TimeFrame() {

    }

    public TimeFrame(DateTime start, DateTime end) {
        this.startDate = start;
        this.endDate = end;
    }

    /**
     * @return the startDate
     */
    public DateTime getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public DateTime getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Duration getDuration() {
        if (this.startDate != null && this.endDate != null) {
            return new Duration(startDate.getMillis( ), endDate.getMillis( ));
        }
        return Duration.ZERO;
    }

}
