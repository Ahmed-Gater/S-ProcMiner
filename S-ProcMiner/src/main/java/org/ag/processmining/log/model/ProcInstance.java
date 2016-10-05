package org.ag.processmining.log.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class ProcInstance implements Serializable {
    static final long serialVersionUID = 1L;
    private CaseId id = null;
    private TreeMap<DateTime, Event> orderedEvents;

    public ProcInstance() {
        this.orderedEvents = new TreeMap( );
    }

    public boolean addEvent(Event e) {
        if (this.id == null) {
            this.id = e.getCaseId( );
        }
        return (orderedEvents.put(e.getStartDate( ), e) != null);
    }

    public Event getStartEvent() {
        return orderedEvents.firstEntry( ).getValue( );
    }

    public Event getEndEvent() {
        return orderedEvents.lastEntry( ).getValue( );
    }


    /*
    Get the case duration
     */
    public Long getDuration(TimeUnit tu) {

        if (this.orderedEvents.lastKey( ) != null && this.orderedEvents.firstKey( ) != null) {
            Duration d = new Duration(this.orderedEvents.firstKey( ).getMillis( ), this.orderedEvents.lastKey( ).getMillis( ));
            if (tu.equals(TimeUnit.DAYS)) {
                return d.getStandardDays( );
            } else if (tu.equals(TimeUnit.HOURS)) {
                return d.getStandardHours( );
            } else if (tu.equals(TimeUnit.MINUTES)) {
                return d.getStandardMinutes( );
            } else if (tu.equals(TimeUnit.SECONDS)) {
                return d.getStandardSeconds( );
            }
        }
        return -1L;
    }

    /*
    Get the mean activity duration
    return new Duration(startDate.getMillis( ), endDate.getMillis( ));
     */
    public Long getMeanActivityDuration(TimeUnit tu) {
        Long ts = 0L ;
        for (Map.Entry<DateTime, Event> e : this.orderedEvents.entrySet( )) {
            ts += e.getValue().getDuration(tu) ;
        }
        return ts/this.orderedEvents.size();
    }


    public int getSize() {
        return this.orderedEvents.size( );
    }

    public DateTime getStartTS() {
        return this.orderedEvents.firstEntry( ).getValue( ).getStartDate( );
    }

    public DateTime getEndTS() {
        return this.orderedEvents.lastEntry( ).getValue( ).getEndDate( );
    }

    public Set<DateTime> getActiveDays() {
        DateTime lastDate = this.orderedEvents.lastEntry( ).getValue( ).getEndDate( );
        DateTime lastDateRef = new DateTime(lastDate.getYear( ), lastDate.getMonthOfYear( ), lastDate.getDayOfMonth( ), 0, 0);
        DateTime firstDate = this.orderedEvents.firstEntry( ).getValue( ).getStartDate( );
        DateTime firstDateRef = new DateTime(firstDate.getYear( ), firstDate.getMonthOfYear( ), firstDate.getDayOfMonth( ), 0, 0);
        Set<DateTime> t = new HashSet<>( );
        while (firstDateRef.compareTo(lastDateRef) <= 0) {
            t.add(firstDateRef);
            firstDateRef = firstDateRef.plusDays(1);
        }
        return t;
    }

    public List<String> getWaitingTimes(){
        for(Map.Entry<DateTime,Event> e: this.orderedEvents.entrySet()){

        }
        return null ;
    }
}
