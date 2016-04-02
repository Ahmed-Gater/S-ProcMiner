package org.ag.processmining.log.model;


import java.io.Serializable;

/**
 * An activity ia an atomic operation of a process.
 */

public class Activity implements Comparable, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	protected String activityName = null;
	

	public Activity(String name) {
		activityName = name;
	}

	public String geActivityName() {
		return activityName;
	}

	public void copy(Activity e) {
		this.activityName = e.geActivityName();
	}

	
        public boolean equals(String otheractivityname) {
		return this.activityName.equals(otheractivityname) ;
	}

	
        @Override
	public boolean equals(Object o) {
            if (o == null || !(o instanceof Activity)) {
                return false;
            }
            return this.activityName.equals(((Activity) o).geActivityName()) ;
	}
	
	@Override
        public int hashCode() {
            int result = 17;
            result = 37 * result + this.activityName.hashCode();
            return result;
	}

	@Override
        public String toString() {
		return this.activityName ;
	}

	@Override
        public int compareTo(Object o) {
		Activity toCompare = (Activity) o;
		return this.toString().compareTo(toCompare.toString());
	}

	@Override
	public Object clone() {
            Activity o = null;
            try {
                o = (Activity)super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return o;
	}
}
