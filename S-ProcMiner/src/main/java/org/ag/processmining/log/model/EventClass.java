package org.ag.processmining.log.model;


import java.io.Serializable;

/**
 * An activity ia an atomic operation of a process.
 */

public class EventClass implements Comparable, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    protected String activityName = null;


    public EventClass(String name) {
        activityName = name;
    }

    public String geActivityName() {
        return activityName;
    }

    public void copy(EventClass e) {
        this.activityName = e.geActivityName( );
    }


    public boolean equals(String otheractivityname) {
        return this.activityName.equals(otheractivityname);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EventClass)) {
            return false;
        }
        return this.activityName.equals(((EventClass) o).geActivityName( ));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.activityName.hashCode( );
        return result;
    }

    @Override
    public String toString() {
        return this.activityName;
    }

    @Override
    public int compareTo(Object o) {
        EventClass toCompare = (EventClass) o;
        return this.toString( ).compareTo(toCompare.toString( ));
    }

    @Override
    public Object clone() {
        EventClass o = null;
        try {
            o = (EventClass) super.clone( );
        } catch (CloneNotSupportedException e) {
            e.printStackTrace( );
        }
        return o;
    }
}
