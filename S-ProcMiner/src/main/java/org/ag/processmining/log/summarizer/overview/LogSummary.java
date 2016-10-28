package org.ag.processmining.log.summarizer.overview;

/**
 * @author ahmed
 */
import org.ag.processmining.Utils.TimeUtils;
import org.ag.processmining.log.model.*;
import org.ag.processmining.log.model.Event.EventBuilder;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.StatCounter;
import org.joda.time.DateTime;
import scala.Tuple2;
import org.ag.processmining.log.summarizer.overview.ActivityClassOverview.ActivityClassOverviewBuilder ;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

//import static org.ag.processmining.log.summarizer.utils.SparkUtils.MAP_TO_CASE_ID_PROC_INSTANCE;

/**
 *
 */
public class LogSummary implements Serializable {

    static final long serialVersionUID = 1L;
    Map<Tuple2<Originator, ActivityClass>, Long> mapOriginatorEventClassOccurences;
    /*
    Histogram of Events over time
     */
    Map<DateTime, Long> eventsOverTime;
    /*
    Active cases over time
     */
    Map<DateTime, Long> activeCasesOverTime;
    /*
    Histogram of events by cases (Histogram showing the distribution of case sizes (number of events)
     */
    Map<Integer, Long> caseSizeDistribution;
    /*
    Case duration histogram
     */
    Map<Long, Long> caseDurationHistogram;
    /*
    Mean Activity duration histogram
     */
    Map<Long, Long> caseActivityMeanDurationHistogram;
    /*
    Case waiting time histogram
     */
    Map<Long, Long> caseWaitingTimeHistogram;
    /*
    Occurences of each event class
     */
    Map<ActivityClass, Long> eventClassOccurences;
    String toot;
    /**
     * The name of the logs (e.g. name of the application that generated the
     * logs)
     */
    private String logName = null;
    /**
     * The description of the logs (e.g. description of the application that
     * generated the logs)
     */
    private String logDescription = null;
    /**
     * The time frame of the process instances in the log
     */
    private TimeUtils processTimeUtils = null;
    /*
        The total number of events of the log
     */
    private long numberOfEvents = 0;
    /**
     * The total number of process instances contained in a log.
     */
    private long numberOfProcessInstances = 0;
    /*
    Case duration stats
     */
    private StatCounter caseDurationStats;
    /*
    Case size stats
     */
    private StatCounter caseSizeStats;
    private StatCounter numberOfEventClassess;
    /**
     * Mapping from event classes that start a process instance to the number of
     * process instances actually start a process instance
     */
    private Map<ActivityClass, Long> startingLogEvents = null;
    /**
     * Mapping from event classes that end a process instance to the number of
     * process instances actually end a process instance
     */
    private Map<ActivityClass, Long> endingLogEvents = null;


    /**
     * Log originators alphabitically ordered by their name
     */
    private TreeSet<Originator> originators = null;
    /**
     * Mapping from originator to the event classes they execute
     */
    private Map<Originator, Long> originatorOccurences = null;


    private TreeSet<ActivityClass> activityClasses;

    /**
     * Creates a new log summary.
     *
     * @param logName        of the summarized log.
     * @param logDescription Description of the summarized log.
     */
    public LogSummary(String logName, String logDescription) {
        this.logName = logName;
        this.logDescription = logDescription;
    }

    /**
     * Creates a new, empty and initialized lightweight log summary instance.
     */
    public LogSummary() {
        this("", "");
    }

    public static LogSummary buildSummary(JavaSparkContext sc, String sourceFile, String[] logHeader, EventSchema eSchema) {

        LogSummary ls = new LogSummary();
        JavaRDD<String> rawLogRDD = sc.textFile(sourceFile);
        JavaPairRDD<CaseId, Event> events = buildEvents(rawLogRDD,logHeader, eSchema) ;
        JavaPairRDD<CaseId, Trace> traces = buildTraces(events);
        System.out.println(traces.count()) ;
        ActivityClassOverview build = new ActivityClassOverviewBuilder(traces).build();
        Map<ActivityClass, StatCounter> activityClassStats = build.activityClassStats;
        System.out.println(activityClassStats) ;
            
        //System.out.println(CASE_ID_PROC_INSTANCE.count());
        return null;
    }

    public static JavaPairRDD<CaseId, Trace> buildTraces(JavaPairRDD<CaseId, Event> events){
        return events
                .mapToPair(x -> new Tuple2<>(x._1(), new Trace(x._1()).addEvent(x._2())))
                .reduceByKey((x, y) -> x.merge(y));
    }

    public static JavaPairRDD<CaseId, Event> buildEvents(JavaRDD<String> rawLogRDD,String[] logHeader, EventSchema eSchema){
        return rawLogRDD.mapToPair(x -> {
            Event e = new EventBuilder(x, ';', logHeader)
                    .caseId(eSchema.getCaseIdFields())
                    .activityClass(eSchema.getEventClassField())
                    .originator(eSchema.getOriginatorField())
                    .start(eSchema.getEventStartTimeField())
                    .end(eSchema.getEventEndTimeField())
                    .build();

            return new Tuple2(e.getCaseId(), e) ;
        }) ;
    }


    private static class DoubleComparator implements Comparator<Double>, Serializable {
        @Override
        public int compare(Double o1, Double o2) {
            return o1.compareTo(o2);
        }
    }

    private static class DateTimeComparator implements Comparator<DateTime>, Serializable {
        @Override
        public int compare(DateTime o1, DateTime o2) {
            return o1.compareTo(o2);
        }
    }
}
