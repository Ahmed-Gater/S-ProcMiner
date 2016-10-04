package org.ag.processmining.log.model;

import lombok.Getter;
import lombok.Setter;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.ag.processmining.log.summarizer.TimeFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final char FIELDS_DELIMITER = ';';
    AttributeMapping att_map;
    String[] event_attributes;

    private CaseId caseId;
    private EventClass eventClass;
    private Originator originator;
    private TimeFrame timeFrame;
    private Map<String, String> data;


    public Event(String event_as_string, AttributeMapping att_map, String[] event_attributes) throws IOException {
        this.att_map = att_map;
        this.event_attributes = event_attributes;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(event_attributes).withDelimiter(FIELDS_DELIMITER);
        data = ((CSVRecord) CSVParser.parse(event_as_string, csvFileFormat).getRecords( ).get(0)).toMap( );

        // Event Case id
        caseId = new CaseId( );
        for (String case_id_field : att_map.getCaseIdFields( )) {
            caseId.addAttribute(case_id_field, data.get(case_id_field));
            data.remove(case_id_field);
        }

        // Event Time frame

        DateTime start_timestamp = DateFormatExtractor.buildDateTime(data.get(att_map.getEventStartTimeField( )));
        DateTime end_timestamp = DateFormatExtractor.buildDateTime(data.get(att_map.getEventEndTimeField( )));
        data.remove(att_map.getEventStartTimeField( ));
        data.remove(att_map.getEventEndTimeField( ));
        this.timeFrame = new TimeFrame(start_timestamp, end_timestamp);

        // Event class
        eventClass = new EventClass(data.get(att_map.getEventClassField( )));
        data.remove(att_map.getEventClassField( ));

        // Event Originator
        originator = new Originator(data.get(att_map.getOriginatorField( )));
        data.remove(att_map.getOriginatorField( ));
    }

    /**
     * @return the caseId
     */
    public CaseId getCaseId() {
        return caseId;
    }

    public EventClass getEventClass() {
        return this.eventClass;
    }

    public DateTime getStartDate() {
        return this.timeFrame.getStartDate( );
    }

    public DateTime getEndDate() {
        return this.timeFrame.getEndDate( );
    }

    public Duration getDuration() {
        return this.timeFrame.getDuration( );
    }

    public Originator getOriginator() {
        return this.originator;
    }

    public String toString() {
        return data.toString( );
    }

}

  