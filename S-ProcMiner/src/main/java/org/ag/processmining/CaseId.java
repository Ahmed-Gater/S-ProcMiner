package org.ag.processmining;

import java.io.Serializable;
import java.util.Map;

public class CaseId implements Serializable
{
    static final long serialVersionUID = 1L;

    Map<String, Object> case_id_fields;
  
  public CaseId(Map<String, Object> case_id_flds)
  {
    this.case_id_fields = case_id_flds;
  }
}
