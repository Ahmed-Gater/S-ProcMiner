package org.ag.processmining;

import java.io.Serializable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TreeMap;
import org.joda.time.DateTime;

public class ProcInstance implements Serializable
{
    static final long serialVersionUID = 1L;
  TreeMap<DateTime,Event> orderedEvents;
  
  public ProcInstance()
  {
    this.orderedEvents = new TreeMap();
  }
  
  public boolean addEvent(Event e)
  {
      return (this.orderedEvents.put(e.start_timestamp, e) != null); 
  }
  
  
}
