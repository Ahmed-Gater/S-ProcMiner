package org.ag.processmining.log.model;


import java.io.Serializable;

/**
 * An activity ia an atomic operation of a process.
 */

public class Originator implements Comparable, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    protected String originatorName = null;


    public Originator(String name) {
        originatorName = name;
    }

    public String geActivityName() {
        return originatorName;
    }

    public void copy(Originator e) {
        this.originatorName = e.geActivityName( );
    }


    public boolean equals(String otheractivityname) {
        return this.originatorName.equals(otheractivityname);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Originator)) {
            return false;
        }
        return this.originatorName.equals(((Originator) o).geActivityName( ));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.originatorName.hashCode( );
        return result;
    }

    @Override
    public String toString() {
        return this.originatorName;
    }

    @Override
    public int compareTo(Object o) {
        Originator toCompare = (Originator) o;
        return this.toString( ).compareTo(toCompare.toString( ));
    }

    @Override
    public Object clone() {
        Originator o = null;
        try {
            o = (Originator) super.clone( );
        } catch (CloneNotSupportedException e) {
            e.printStackTrace( );
        }
        return o;
    }
}
