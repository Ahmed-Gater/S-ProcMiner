package org.ag.processmining.log.model;

import java.io.Serializable;
import java.util.TreeMap;
import org.joda.time.DateTime;

public class ProcInstance implements Serializable
{
    static final long serialVersionUID = 1L;
    private TreeMap<DateTime,Event> orderedEvents;
  
  public ProcInstance()
  {
    this.orderedEvents = new TreeMap();
  }
  
  public boolean addEvent(Event e)
  {
      return (this.getOrderedEvents().put(e.getStartDate(), e) != null); 
  }
  
  public EventClass getStartEvent(){
      return this.getOrderedEvents().firstEntry().getValue().getEventClass() ; 
  }
  
  public EventClass getEndEvent(){
      return this.getOrderedEvents().lastEntry().getValue().getEventClass() ; 
  }

    /**
     * @return the orderedEvents
     */
    public TreeMap<DateTime,Event> getOrderedEvents() {
        return orderedEvents;
    }

    /**
     * @param orderedEvents the orderedEvents to set
     */
    public void setOrderedEvents(TreeMap<DateTime,Event> orderedEvents) {
        this.orderedEvents = orderedEvents;
    }
  
}
