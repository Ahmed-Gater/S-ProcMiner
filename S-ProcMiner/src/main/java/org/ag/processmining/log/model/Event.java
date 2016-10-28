package org.ag.processmining.log.model;

import lombok.Getter;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.ag.processmining.Utils.TimeUtils;
import org.ag.processmining.Utils.TimeUtils.TimeUnit;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by ahmed.gater on 26/10/2016.
 */

@Getter
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    private CaseId caseId;
    private ActivityClass activityClass;
    private DateTime start ;
    private DateTime end ;
    private Originator originator;
    private Map<String, String> data;
    private String toto ;

    private Event(EventBuilder builder) {
        this.caseId = builder.caseId;
        this.activityClass = builder.activityClass ;
        this.start = builder.start ;
        this.end = builder.end ;
        this.originator = builder.originator ;
        this.data = builder.data ;
    }


    public double duration(TimeUnit tu) {
        System.out.println(TimeUtils.duration(start,end,tu)) ;
        return TimeUtils.duration(start,end,tu)  ;
    }

    public static class EventBuilder {
        private CaseId caseId;
        private ActivityClass activityClass;
        private Originator originator;
        private DateTime start ;
        private DateTime end ;
        private Map<String, String> data;

        public EventBuilder(String eventAsString, char fieldDelimiter,String[] header) throws IOException {
            data = ((CSVRecord) CSVParser.parse(eventAsString, CSVFormat.DEFAULT.withHeader(header)
                    .withDelimiter(fieldDelimiter))
                    .getRecords()
                    .get(0))
                    .toMap();
        }

        public EventBuilder caseId(List<String> caseIdFields){
            this.caseId = new CaseId();
            for (String fld : caseIdFields) {
                caseId.addField(fld, data.get(fld));
            }
            for (String fld : caseIdFields) {
                this.data.remove(fld);
            }
            return this ;
        }

        public EventBuilder activityClass(String activityClassFieldName){
            this.activityClass = new ActivityClass(data.get(activityClassFieldName));
            this.data.remove(activityClassFieldName) ;
            return this ;
        }

        public EventBuilder start(String startField){
            this.start = DateFormatExtractor.buildDateTime(data.get(startField));
            this.data.remove(startField) ;
            return this ;
        }

        public EventBuilder end(String endField){
            this.end = DateFormatExtractor.buildDateTime(data.get(endField));
            this.data.remove(endField) ;
            return this ;
        }

        public EventBuilder originator(String originatorField){
            this.originator = new Originator(data.get(originatorField));
            this.data.remove(originatorField) ;
            return this ;
        }
        public Event build() {
            return new Event(this);
        }
    }
}

