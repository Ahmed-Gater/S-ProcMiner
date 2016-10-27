package org.ag.processmining.log.model;

import lombok.*;

import java.io.Serializable;
import java.util.TreeMap;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Getter
@Setter
public class CaseId implements Serializable {
    static final long serialVersionUID = 1L;
    private TreeMap<String, String> fields = null;


    public void addField(String name, String value) {
        if (fields ==null) fields = new TreeMap<>();
        this.fields.put(name, value);
    }

    public String getField(String fld){
        return fields.containsKey(fld) ? fields.get(fld) : null ;
    }

    /**
     * @return the case_id_fields
     */
    public TreeMap<String, String> getFields() {
        return fields;
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        for (String field : this.getFields().navigableKeySet()) {
            sb = sb.append(this.getFields().get(field));
        }
        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) ||
                (!(obj instanceof CaseId)) ||
                (((CaseId) obj).fields.size() != this.fields.size())) {
            return false;
        }

        for (String fld : getFields().navigableKeySet())
            if (!this.getField(fld).equalsIgnoreCase(((CaseId) obj).getField(fld))) return false ;
        return true;
    }

    @Override
    public String toString() {
        String s = "";
        for (String str : this.fields.keySet()) {
            s = s.concat(this.fields.get(str));
        }
        return s;
    }
}
