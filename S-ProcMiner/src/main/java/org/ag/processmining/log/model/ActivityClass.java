package org.ag.processmining.log.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * An activity ia an atomic operation of a process.
 */

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class ActivityClass implements Comparable, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private String name = null;

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ActivityClass)) {
            return false;
        }
        return this.name.equals(((ActivityClass) o).getName());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(Object o) {
        ActivityClass toCompare = (ActivityClass) o;
        return this.toString().compareTo(toCompare.toString());
    }

    @Override
    public Object clone() {
        ActivityClass o = null;
        try {
            o = (ActivityClass) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
