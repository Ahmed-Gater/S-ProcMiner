package org.ag.processmining.log.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;
import java.util.TreeMap;

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

     */
    public Long getDuration() {
        if (this.orderedEvents.lastKey( ) != null && this.orderedEvents.firstKey( ) != null) {
            Duration d = new Duration(this.orderedEvents.firstKey( ).getMillis( ), this.orderedEvents.lastKey( ).getMillis( ));
            return d.getStandardSeconds( );
        }
        return -1L;
    }

    public int getSize(){
        return this.orderedEvents.size() ;
    }

    public DateTime getStartTS(){
        return this.orderedEvents.firstEntry().getValue().getStartDate() ;
    }

    public DateTime getEndTS(){
        return this.orderedEvents.lastEntry().getValue().getEndDate() ;
    }
}
