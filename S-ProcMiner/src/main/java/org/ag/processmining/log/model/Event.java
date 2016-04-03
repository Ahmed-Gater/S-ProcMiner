package org.ag.processmining.log.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.ag.processmining.logsummarizer.TimeFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Event implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static final char FIELDS_DELIMITER = ';' ;
  
  private CaseId caseId;
  private EventClass eventClass ; 
  private Originator originator ;
  private TimeFrame timeFrame ; 
  private Map<String, String> data ; 
  
  public Event(String event_as_string,FieldMapping fMap,String[] event_attributes) throws IOException
  {
    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(event_attributes).withDelimiter(FIELDS_DELIMITER);
    data = ((CSVRecord)CSVParser.parse(event_as_string, csvFileFormat).getRecords().get(0)).toMap();
    
    // Case id
    caseId = new CaseId();
    for (String case_id_field : fMap.getCaseIdFields()) {
        caseId.addAttribute(case_id_field, data.get(case_id_field)); 
        data.remove(case_id_field); 
    }
    
    // Time frame 
    DateTime start_timestamp = DateFormatExtractor.buildDateTime(data.get(fMap.getEventStartTimeField())) ;
    DateTime end_timestamp = DateFormatExtractor.buildDateTime(data.get(fMap.getEventEndTimeField())) ;
    data.remove(fMap.getEventStartTimeField()) ; 
    data.remove(fMap.getEventEndTimeField()) ; 
    this.timeFrame = new TimeFrame(start_timestamp,end_timestamp) ; 
    
    // Event class
    eventClass = new EventClass(data.get(fMap.getEventClassField())) ;
    data.remove(fMap.getEventClassField()) ; 
    
    // Originator
    originator = new Originator(fMap.getOriginatorField()) ; 
    data.remove(fMap.getOriginatorField()) ; 
  }
  
   /**
     * @return the caseId
     */
    public CaseId getCaseId() {
        return caseId;
    }
    
    public EventClass getEventClass(){
      return this.eventClass ; 
  }
    
    public DateTime getStartDate(){
        return this.timeFrame.getStartDate() ; 
    }
    
    public DateTime getEndDate(){
        return this.timeFrame.getEndDate() ; 
    }
  
    public Duration getDuration()
    {
        return this.timeFrame.getDuration() ; 
    }
  
    public Originator getOriginator(){
        return this.originator ; 
    }

}


  //private CSVRecord event_as_csv_record;
  
  //private String[] CSV_HEADER = { "a_ref_activitee", "h_create_date", "h_dateentree", "h_date_execution", "h_codecorbeille", "h_codestatut", "h_creator", "h_domaine", "h_idaction", "frigo", "qs", "app_premium", "lien_referentiel_aq", "a_canalfrom", "a_canalto", "a_code_apporteur", "a_codecorbeille", "a_domaine", "a_servicepremium", "a_typologie", "h_commentaire" };
  //private String[] case_id_fields = { "a_ref_activitee" };
  //private String start_time_field = "h_dateentree";
  //private String end_time_field = "h_date_execution";
  //private String event_class_field = "h_codecorbeille" ; 
  //private String event_originator_field = "h_creator" ; 
  
  /*
  public Event(String event_as_string) throws IOException
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
  */
  