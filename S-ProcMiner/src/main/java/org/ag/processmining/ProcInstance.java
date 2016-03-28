package org.ag.processming;

import java.util.Iterator;
import java.util.PriorityQueue;
import org.ag.processmining.Event;

public class ProcInstance
{
  PriorityQueue<Event> orderedEvents;
  
  public ProcInstance()
  {
    this.orderedEvents = new PriorityQueue();
  }
  
  public boolean addEvent(Event e)
  {
    return this.orderedEvents.add(e);
  }
  
  public Iterator<Event> iterator()
  {
    return this.orderedEvents.iterator();
  }
}
