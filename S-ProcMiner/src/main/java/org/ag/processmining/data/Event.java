package org.ag.processmining.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Event
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private char FIELDS_DELIMITER = ';';
  private CaseId caseId;
  private DateTime start_timestamp;
  private DateTime end_timestamp;
  private CSVRecord event_as_csv_record;
  private String[] CSV_HEADER = { "a_ref_activitee", "h_create_date", "h_dateentree", "h_date_execution", "h_codecorbeille", "h_codestatut", "h_creator", "h_domaine", "h_idaction", "frigo", "qs", "app_premium", "lien_referentiel_aq", "a_canalfrom", "a_canalto", "a_code_apporteur", "a_codecorbeille", "a_domaine", "a_servicepremium", "a_typologie", "h_commentaire" };
  private String[] case_id_fields = { "a_ref_activitee" };
  private String start_time_field = "h_dateentree";
  private String end_time_field = "h_date_execution";
  private String event_class_field = "h_codecorbeille" ; 
  private String event_originator_field = "h_creator" ; 
  
  
  public Event(String event_as_string)
    throws IOException
  {
    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(this.CSV_HEADER).withDelimiter(this.FIELDS_DELIMITER);
    this.event_as_csv_record = ((CSVRecord)CSVParser.parse(event_as_string, csvFileFormat).getRecords().get(0));
    Map<String, String> case_id_field_map = new HashMap();
    for (String case_id_field : this.case_id_fields) {
      case_id_field_map.put(case_id_field, this.event_as_csv_record.get(case_id_field));
    }
    this.caseId = new CaseId(case_id_field_map);
    
    this.start_timestamp = DateFormatExtractor.buildDateTime(this.event_as_csv_record.get(this.start_time_field)) ;//DateTime.parse(this.event_as_csv_record.get(this.start_time_field), this.DATE_FORMAT);
    
    this.end_timestamp = DateFormatExtractor.buildDateTime(this.event_as_csv_record.get(this.end_time_field)) ;//DateTime.parse(this.event_as_csv_record.get(this.end_time_field), this.DATE_FORMAT);
  }
  
  public Duration getDuration()
  {
    if (this.getEnd_timestamp() != null) {
      return new Duration(this.getStart_timestamp().getMillis(), this.getEnd_timestamp().getMillis());
    }
    return Duration.ZERO;
  }
  
  public Object get(String key)
  {
    return this.getEvent_as_csv_record().get(key);
  }
  
  public String getEventClass(){
      return get(getEvent_class_field()).toString() ; 
  }
  
  public String getOriginator(){
      return get(getEvent_originator_field()).toString() ; 
  }

    /**
     * @return the FIELDS_DELIMITER
     */
    public char getFIELDS_DELIMITER() {
        return FIELDS_DELIMITER;
    }

    /**
     * @param FIELDS_DELIMITER the FIELDS_DELIMITER to set
     */
    public void setFIELDS_DELIMITER(char FIELDS_DELIMITER) {
        this.FIELDS_DELIMITER = FIELDS_DELIMITER;
    }

    /**
     * @return the caseId
     */
    public CaseId getCaseId() {
        return caseId;
    }

    /**
     * @param caseId the caseId to set
     */
    public void setCaseId(CaseId caseId) {
        this.caseId = caseId;
    }

    /**
     * @return the start_timestamp
     */
    public DateTime getStart_timestamp() {
        return start_timestamp;
    }

    /**
     * @param start_timestamp the start_timestamp to set
     */
    public void setStart_timestamp(DateTime start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    /**
     * @return the end_timestamp
     */
    public DateTime getEnd_timestamp() {
        return end_timestamp;
    }

    /**
     * @param end_timestamp the end_timestamp to set
     */
    public void setEnd_timestamp(DateTime end_timestamp) {
        this.end_timestamp = end_timestamp;
    }

    /**
     * @return the event_as_csv_record
     */
    public CSVRecord getEvent_as_csv_record() {
        return event_as_csv_record;
    }

    /**
     * @param event_as_csv_record the event_as_csv_record to set
     */
    public void setEvent_as_csv_record(CSVRecord event_as_csv_record) {
        this.event_as_csv_record = event_as_csv_record;
    }

    /**
     * @return the CSV_HEADER
     */
    public String[] getCSV_HEADER() {
        return CSV_HEADER;
    }

    /**
     * @param CSV_HEADER the CSV_HEADER to set
     */
    public void setCSV_HEADER(String[] CSV_HEADER) {
        this.CSV_HEADER = CSV_HEADER;
    }

    /**
     * @return the case_id_fields
     */
    public String[] getCase_id_fields() {
        return case_id_fields;
    }

    /**
     * @param case_id_fields the case_id_fields to set
     */
    public void setCase_id_fields(String[] case_id_fields) {
        this.case_id_fields = case_id_fields;
    }

    /**
     * @return the start_time_field
     */
    public String getStart_time_field() {
        return start_time_field;
    }

    /**
     * @param start_time_field the start_time_field to set
     */
    public void setStart_time_field(String start_time_field) {
        this.start_time_field = start_time_field;
    }

    /**
     * @return the end_time_field
     */
    public String getEnd_time_field() {
        return end_time_field;
    }

    /**
     * @param end_time_field the end_time_field to set
     */
    public void setEnd_time_field(String end_time_field) {
        this.end_time_field = end_time_field;
    }

    /**
     * @return the event_class_field
     */
    public String getEvent_class_field() {
        return event_class_field;
    }

    /**
     * @param event_class_field the event_class_field to set
     */
    public void setEvent_class_field(String event_class_field) {
        this.event_class_field = event_class_field;
    }

    /**
     * @return the event_originator_field
     */
    public String getEvent_originator_field() {
        return event_originator_field;
    }

    /**
     * @param event_originator_field the event_originator_field to set
     */
    public void setEvent_originator_field(String event_originator_field) {
        this.event_originator_field = event_originator_field;
    }
}
