package org.ag.processmining.log.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.ag.processmining.log.summarizer.TimeFrame;
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
  
  public Event(String event_as_string,AttributeMapping fMap,String[] event_attributes) throws IOException
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

  