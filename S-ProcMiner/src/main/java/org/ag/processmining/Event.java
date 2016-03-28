package org.ag.processmining;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Event
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  char FIELDS_DELIMITER = ';';
  CaseId caseId;
  DateTime start_timestamp;
  DateTime end_timestamp;
  CSVRecord event_as_csv_record;
  String[] CSV_HEADER = { "a_ref_activitee", "h_create_date", "h_dateentree", "h_date_execution", "h_codecorbeille", "h_codestatut", "h_creator", "h_domaine", "h_idaction", "frigo", "qs", "app_premium", "lien_referentiel_aq", "a_canalfrom", "a_canalto", "a_code_apporteur", "a_codecorbeille", "a_domaine", "a_servicepremium", "a_typologie", "h_commentaire" };
  String[] case_id_fields = { "a_ref_activitee" };
  String start_time_field = "h_dateentree";
  String end_time_field = "h_date_execution";
 
  
  
  public Event(String event_as_string)
    throws IOException
  {
    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(this.CSV_HEADER).withDelimiter(this.FIELDS_DELIMITER);
    this.event_as_csv_record = ((CSVRecord)CSVParser.parse(event_as_string, csvFileFormat).getRecords().get(0));
    Map<String, Object> case_id_field_map = new HashMap();
    for (String case_id_field : this.case_id_fields) {
      case_id_field_map.put(case_id_field, this.event_as_csv_record.get(case_id_field));
    }
    this.caseId = new CaseId(case_id_field_map);
    
    this.start_timestamp = DateFormatExtractor.buildDateTime(this.event_as_csv_record.get(this.start_time_field)) ;//DateTime.parse(this.event_as_csv_record.get(this.start_time_field), this.DATE_FORMAT);
    
    this.end_timestamp = DateFormatExtractor.buildDateTime(this.event_as_csv_record.get(this.end_time_field)) ;//DateTime.parse(this.event_as_csv_record.get(this.end_time_field), this.DATE_FORMAT);
  }
  
  public Duration getDuration()
  {
    if (this.end_timestamp != null) {
      return new Duration(this.start_timestamp.getMillis(), this.end_timestamp.getMillis());
    }
    return Duration.ZERO;
  }
  
  public Object get(String key)
  {
    return this.event_as_csv_record.get(key);
  }
}
