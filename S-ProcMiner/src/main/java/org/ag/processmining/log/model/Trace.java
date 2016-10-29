package org.ag.processmining.log.model;

import lombok.Getter;
import lombok.Setter;
import org.ag.processmining.Utils.TimeUtils;
import org.ag.processmining.Utils.TimeUtils.TimeUnit;
import org.joda.time.DateTime;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Trace implements Serializable {
    static final long serialVersionUID = 1L;
    private CaseId id = null;
    private TreeMap<DateTime, Event> orderedEvents = null;


    public Trace(CaseId id) {
        this.orderedEvents = new TreeMap();
        this.id = id;
    }

    public Trace addEvent(Event e) {
        if (this.id == null) {
            this.id = e.getCaseId();
        }
        orderedEvents.put(e.getStart(), e);
        return this;
    }

    public double duration(TimeUnit tu) {
        return TimeUtils.duration((DateTime) this.orderedEvents.firstKey(), (DateTime) this.orderedEvents.lastKey(), tu);
    }

    public DoubleSummaryStatistics getEventDurationStats(TimeUnit tu) {
        return this.orderedEvents
                .values().stream()
                .mapToDouble(event -> event.duration(tu)).summaryStatistics();

    }

    public double getActiveTime(TimeUnit tu) {
        return getEventDurationStats(tu).getSum();
    }

    public int getWaitingTime(TimeUnit tu) {
        Event previous = null;
        int waitingTime = 0;
        for (Map.Entry<DateTime, Event> e : this.orderedEvents.entrySet()) {
            if (previous != null) {
                waitingTime += TimeUtils.duration(previous.getEnd(), e.getValue().getStart(), tu);
            }
            previous = e.getValue();
        }
        return waitingTime;
    }

    public int size() {
        return this.orderedEvents.size();
    }

    public Set<DateTime> getActiveDays() {
        return new HashSet<DateTime>(TimeUtils.daysBetween(this.orderedEvents.firstEntry().getValue().getStart(), this.orderedEvents.lastEntry().getValue().getEnd()));
    }

    public Event getStartEvent() {
        return orderedEvents.firstEntry().getValue();
    }

    public Event getEndEvent() {
        return orderedEvents.lastEntry().getValue();
    }

    public DateTime getStartTS() {
        return this.orderedEvents.firstEntry().getValue().getStart();
    }

    public DateTime getEndTS() {
        return this.orderedEvents.lastEntry().getValue().getEnd();
    }

    public Trace merge(Trace y) {
        if (!y.getId().equals(this.getId())) {
            return null;
        }
        Trace t = new Trace(this.getId());
        t.orderedEvents.putAll(this.getOrderedEvents());
        t.orderedEvents.putAll(y.getOrderedEvents());
        return t;
    }

    public List<Tuple2<ActivityClass,Originator>> activityClassOriginator(){
        return new ArrayList(
                this.getOrderedEvents().values().stream()
                        .map(x -> new Tuple2<ActivityClass,Originator>(x.getActivityClass(),x.getOriginator()))
                        .collect(Collectors.toSet()));
    }


    public Case buildCase() {
        return new Case(this.id, this.size(), this.getStartTS(), this.getEndTS());
    }

    /*
    Get a list of generators sorted as they appear in the trace
     */
    public List<Originator> getOriginators(){
        List<Originator> o = new ArrayList<>() ;
        getOrderedEvents().entrySet().stream().forEach(x -> o.add(x.getValue().getOriginator()));
        return o ;
    }


}
