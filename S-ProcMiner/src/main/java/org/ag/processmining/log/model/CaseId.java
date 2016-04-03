package org.ag.processmining.log.model;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class CaseId implements Serializable
{
    static final long serialVersionUID = 1L;
    private TreeMap<String, String> case_id_attributes;
  
  public CaseId(Map<String, String> case_id_flds)
  {
    this.case_id_attributes = new TreeMap<>(case_id_flds);
  }
  
  public CaseId(){
      this.case_id_attributes = new TreeMap<>() ;
  }
  public void addAttribute(String name, String value){
      this.case_id_attributes.put(name, value) ; 
  }
  
  
  @Override
  public int hashCode(){
      StringBuilder sb = new StringBuilder() ; 
      for (String field: this.getCase_id_fields().navigableKeySet()){
          sb = sb.append(this.getCase_id_fields().get(field)) ; 
      }
      return sb.toString().hashCode() ; 
  }
  
  @Override
  public boolean equals(Object obj){
      if (obj == null){
          return false ;
      }
      
      if (!(obj instanceof CaseId)){
          return false ;
      }
      
      for (String field: this.getCase_id_fields().navigableKeySet()){
          if (!this.case_id_attributes.get(field).equalsIgnoreCase(((CaseId)obj).case_id_attributes.get(field))){
              return false ; 
          }
      }
      return true ;
  }

    /**
     * @return the case_id_fields
     */
    public TreeMap<String, String> getCase_id_fields() {
        return case_id_attributes;
    }
}
