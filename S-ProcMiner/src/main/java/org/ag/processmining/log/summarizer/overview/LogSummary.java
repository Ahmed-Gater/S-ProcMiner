package org.ag.processmining.log.summarizer.overview;

/**
 * @author ahmed
 */

import org.ag.processmining.log.model.*;
import org.ag.processmining.log.summarizer.utils.SparkUtils;
import org.ag.processmining.Utils.TimeUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.util.StatCounter;
import org.joda.time.DateTime;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

import static org.ag.processmining.log.summarizer.utils.SparkUtils.EVENT_CLASSES_GETTER;
import static org.ag.processmining.log.summarizer.utils.SparkUtils.MAP_TO_CASE_ID_PROC_INSTANCE;

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

    public static LogSummary buildSummary(JavaSparkContext sc, String appliName, String appliDesc, String sourceFile, String[] event_attributes, AttributeMapping att_map) {

        LogSummary ls = new LogSummary(appliName, appliDesc);
        JavaRDD<String> RDDSrc = sc.textFile(sourceFile);

        // Building Summary data
        JavaPairRDD<CaseId, EventOld> CASE_ID_EVENT_MAP = RDDSrc.mapToPair(new SparkUtils.MapToCaseIdEvent(att_map, event_attributes));
        JavaPairRDD<CaseId, Trace> CASE_ID_PROC_INSTANCE = CASE_ID_EVENT_MAP.groupByKey().mapToPair(MAP_TO_CASE_ID_PROC_INSTANCE);


        /**
         *******************************************************************************
         ***************************** Process Level Stats *****************************
         *******************************************************************************
         */


        // Number of process instance
        ls.setNumberOfProcessInstances(CASE_ID_PROC_INSTANCE.count());

        // Number of events
        ls.setNumberOfEvents(RDDSrc.count());

        // EventOld classes
        ls.setEventClassOccurences(CASE_ID_EVENT_MAP.map(EVENT_CLASSES_GETTER).countByValue());

        // Mean, Max, Min, Std duration of cases
        ls.caseDurationStats = CASE_ID_PROC_INSTANCE.mapToDouble(new DoubleFunction<Tuple2<CaseId, Trace>>() {
            @Override
            public double call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getDuration(TimeUnit.DAYS);
            }
        }).stats();
        // Mean, Max, Min and Std of case size (number of events)
        ls.caseSizeStats = CASE_ID_PROC_INSTANCE.mapToDouble(new DoubleFunction<Tuple2<CaseId, Trace>>() {
            @Override
            public double call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getSize();
            }
        }).stats();

        // First and Last event date
        DateTime logStartDate = CASE_ID_PROC_INSTANCE.map(new Function<Tuple2<CaseId, Trace>, DateTime>() {
            @Override
            public DateTime call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getStartTS();
            }
        }).min(new DateTimeComparator());

        DateTime logEndDate = CASE_ID_PROC_INSTANCE.map(new Function<Tuple2<CaseId, Trace>, DateTime>() {
            @Override
            public DateTime call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getEndTS();
            }
        }).max(new DateTimeComparator());

        System.out.println("Start process: " + logStartDate);
        System.out.println("End process: " + logEndDate);
        ls.processTimeUtils = new TimeUtils(logStartDate, logEndDate);

        // Events over time
        ls.eventsOverTime = CASE_ID_EVENT_MAP.map(new Function<Tuple2<CaseId, EventOld>, DateTime>() {
            @Override
            public DateTime call(Tuple2<CaseId, EventOld> t) throws Exception {
                DateTime dt = t._2().getStartDate();
                return new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 0, 0);
            }
        }).countByValue();

        // Active cases over time
        ls.activeCasesOverTime = CASE_ID_PROC_INSTANCE.flatMap(new FlatMapFunction<Tuple2<CaseId, Trace>, DateTime>() {
            @Override
            public Iterable<DateTime> call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getActiveDays();
            }
        }).countByValue();

        // Histogram of events by case
        ls.caseSizeDistribution = CASE_ID_PROC_INSTANCE.map(new Function<Tuple2<CaseId, Trace>, Integer>() {
            @Override
            public Integer call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getSize();
            }
        }).countByValue();

        // Histogram of duration
        ls.caseDurationHistogram = CASE_ID_PROC_INSTANCE.map(new Function<Tuple2<CaseId, Trace>, Long>() {
            @Override
            public Long call(Tuple2<CaseId, Trace> t) throws Exception {
                return t._2().getDuration(TimeUnit.DAYS);
            }
        }).countByValue();


        /*
        Mean Activity duration (est la moyenne des durees des activites)
         */
        ls.caseActivityMeanDurationHistogram = CASE_ID_PROC_INSTANCE.map(new Function<Tuple2<CaseId, Trace>, Long>() {
            Long timeBucket = 1L;

            @Override
            public Long call(Tuple2<CaseId, Trace> t) throws Exception {
                return (t._2().getMeanActivityDuration(TimeUnit.HOURS) / timeBucket) * timeBucket;
            }
        }).countByValue();

        ls.caseWaitingTimeHistogram = CASE_ID_PROC_INSTANCE.map(new Function<Tuple2<CaseId, Trace>, Long>() {
            Long timeBucket = 1L;

            @Override
            public Long call(Tuple2<CaseId, Trace> t) throws Exception {
                return (t._2().getWaitingTimes(TimeUnit.DAYS) / timeBucket) * timeBucket;
            }
        }).countByValue();


        /**
         ********************************************************************************
         ***************************** Activity Level Stats *****************************
         ********************************************************************************
         */
        /*
        Activities occurence stats
         */

        ls.eventClassOccurences = CASE_ID_EVENT_MAP.map(new Function<Tuple2<CaseId, EventOld>, ActivityClass>() {
            @Override
            public ActivityClass call(Tuple2<CaseId, EventOld> t) throws Exception {
                return t._2().getActivityClass();
            }
        }).countByValue();

        StatCounter ssc = new StatCounter();

        //ls.numberOfEventClassess = ls.eventClassOccurences.keySet().size() ;

        /*
        Process Instance waiting time (global waiting time)
         */

        /*
        // Start event class occurences

        Map<EventClass, Long> start_event_class_occurences = CASE_ID_PROC_INSTANCE.map(START_EVENT_CLASSES).countByValue( );
        ls.setStartingLogEvents(start_event_class_occurences);

        // End event class occurences
        Map<EventClass, Long> end_event_class_occurences = CASE_ID_PROC_INSTANCE.map(END_EVENT_CLASSES).countByValue( );
        ls.setEndingLogEvents(end_event_class_occurences);

        // Originator occurences
        Map<Originator, Long> event_originator_occurences = CASE_ID_EVENT_MAP.map(EVENT_ORIGINATOR).countByValue( );
        ls.setOriginatorOccurences(event_originator_occurences);

        // Originator-EventClass occurences
        Map<Tuple2<Originator, EventClass>, Object> mapOriginatorsToEventClassesOccs = CASE_ID_EVENT_MAP.mapToPair(ORIGINATOR_EVENT_CLASS_OCCURENCES).countByKey( );
        ls.setMapOriginatorsEventClassesOccurences(mapOriginatorsToEventClassesOccs);

        // Case Statistics

        */
        ls.print();
        return ls;
    }


    /**
     * @return the logName
     */
    public String getLogName() {
        return logName;
    }

    /**
     * @param logName the logName to set
     */
    public void setLogName(String logName) {
        this.logName = logName;
    }

    /**
     * @return the logDescription
     */
    public String getLogDescription() {
        return logDescription;
    }

    /**
     * @param logDescription the logDescription to set
     */
    public void setLogDescription(String logDescription) {
        this.logDescription = logDescription;
    }

    /**
     * @return the processTimeFrame
     */
    public TimeUtils getProcessTimeFrame() {
        return processTimeUtils;
    }

    /**
     * @param processTimeUtils the processTimeFrame to set
     */
    public void setProcessTimeFrame(TimeUtils processTimeUtils) {
        this.processTimeUtils = processTimeUtils;
    }

    /**
     * @return the numberOfEvents
     */
    public long getNumberOfEvents() {
        return numberOfEvents;
    }

    /**
     * @param numberOfEvents the numberOfEvents to set
     */
    public void setNumberOfEvents(long numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    /**
     * @return the numberOfProcessInstances
     */
    public long getNumberOfProcessInstances() {
        return numberOfProcessInstances;
    }

    /**
     * @param numberOfProcessInstances the numberOfProcessInstances to set
     */
    public void setNumberOfProcessInstances(long numberOfProcessInstances) {
        this.numberOfProcessInstances = numberOfProcessInstances;
    }

    /**
     * @return the eventClasses
     */
    public TreeSet<ActivityClass> getEventClasses() {
        if (this.activityClasses == null) {
            this.activityClasses = new TreeSet<>(eventClassOccurences.keySet());
        }
        return this.activityClasses;
    }

    /**
     * @param activityClasses the eventClasses to set
     */
    public void setEventClasses(TreeSet<ActivityClass> activityClasses) {
        this.activityClasses = activityClasses;
    }

    /**
     * @return the startingLogEvents
     */
    public Map<ActivityClass, Long> getStartingLogEvents() {
        return startingLogEvents;
    }

    /**
     * @param startingLogEvents the startingLogEvents to set
     */
    public void setStartingLogEvents(Map<ActivityClass, Long> startingLogEvents) {
        this.startingLogEvents = startingLogEvents;
    }

    /**
     * @return the endingLogEvents
     */
    public Map<ActivityClass, Long> getEndingLogEvents() {
        return endingLogEvents;
    }

    /**
     * @param endingLogEvents the endingLogEvents to set
     */
    public void setEndingLogEvents(Map<ActivityClass, Long> endingLogEvents) {
        this.endingLogEvents = endingLogEvents;
    }

    /**
     * @return the mapEventClassToProcessOccurences
     */
    public Map<ActivityClass, Long> getEventClassesOccurences() {
        return eventClassOccurences;
    }

    /**
     * @param eventClassOccurences
     */
    public void setEventClassOccurences(Map<ActivityClass, Long> eventClassOccurences) {
        this.eventClassOccurences = eventClassOccurences;
    }

    /**
     * @return the originators
     */
    public TreeSet<Originator> getOriginators() {
        return originators;
    }

    /**
     * @param originators the originators to set
     */
    public void setOriginators(TreeSet<Originator> originators) {
        this.originators = originators;
    }

    /**
     * @param originator add an originator
     */
    public void addOriginator(Originator originator) {
        this.originators.add(originator);
    }

    /**
     * @return the occurences of originators ok
     */
    public Map<Originator, Long> getOriginatorOccurences() {
        return this.originatorOccurences;
    }

    /*
    * set the occurences of originators
     */
    public void setOriginatorOccurences(Map<Originator, Long> orgOcc) {
        this.originatorOccurences = orgOcc;
        this.originators = new TreeSet<>(orgOcc.keySet());
    }

    /**
     * @return the mapOriginatorsToEventClasses
     */
    public Map<Tuple2<Originator, ActivityClass>, Long> getMapOriginatorsEventClassesOccurences() {
        return mapOriginatorEventClassOccurences;
    }

    /**
     * @param mapOriginatorsToEventClasses the mapOriginatorsToEventClasses to
     *                                     set
     */
    public void setMapOriginatorsEventClassesOccurences(Map<Tuple2<Originator, ActivityClass>, Object> mapOriginatorsToEventClasses) {
        this.mapOriginatorEventClassOccurences = new HashMap<>();
        for (Map.Entry<Tuple2<Originator, ActivityClass>, Object> e : mapOriginatorsToEventClasses.entrySet()) {
            this.mapOriginatorEventClassOccurences.put(e.getKey(), (Long) e.getValue());
        }
    }

    public void print() {
        System.out.println("Application name: " + this.logName);
        System.out.println("Application description: " + this.logDescription);
        System.out.println("Number of events: " + this.numberOfEvents);
        System.out.println("Number of cases: " + this.numberOfProcessInstances);
        System.out.println("Number of activities: " + this.getEventClasses().size());
        System.out.println("Case duration: (min," + this.caseDurationStats.min() +
                "), (max, " + this.caseDurationStats.max() +
                "), (mean, " + this.caseDurationStats.mean() +
                "), (std, " + this.caseDurationStats.stdev() + ")"
        );

        System.out.println("Case size: (min," + this.caseSizeStats.min() +
                "), (max, " + this.caseSizeStats.max() +
                "), (mean, " + this.caseSizeStats.mean() +
                "), (std, " + this.caseSizeStats.stdev() + ")"
        );

        System.out.println("Log Time Frame (start ts,end ts): (" + this.getProcessTimeFrame().getStartDate() + " , " + this.getProcessTimeFrame().getEndDate() + ")");

        System.out.println("Histogram of Events over time");
        for (Map.Entry<DateTime, Long> e : this.eventsOverTime.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }

        System.out.println("Number of active cases over time");
        for (Map.Entry<DateTime, Long> e : this.activeCasesOverTime.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }

        System.out.println("Distribution of case size");
        for (Map.Entry<Integer, Long> e : this.caseSizeDistribution.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }

        System.out.println("Case duration histogram");
        for (Map.Entry<Long, Long> e : this.caseDurationHistogram.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }

        System.out.println("Case activity mean duration histogram");
        for (Map.Entry<Long, Long> e : caseActivityMeanDurationHistogram.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }

        System.out.println("Case waiting time histogram");
        for (Map.Entry<Long, Long> e : caseWaitingTimeHistogram.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }


        System.out.println("EventOld class occurences");
        for (Map.Entry<ActivityClass, Long> e : eventClassOccurences.entrySet()) {
            System.out.println(e.getKey() + "--> " + e.getValue());
        }

        Long minEventClassOcuurences = eventClassOccurences.values().stream().min((a, b) -> a > b ? 0 : 1).get();
        Long maxEventClassOccurences = eventClassOccurences.values().stream().max((a, b) -> a > b ? 0 : 1).get();
        Long numberEventClass = eventClassOccurences.values().stream().count();
        long l = eventClassOccurences.values().stream().reduce(new BinaryOperator<Long>() {
            @Override
            public Long apply(Long a, Long b) {
                return a + b;
            }
        }).get() / numberEventClass;

        Long eventclassoccurences = eventClassOccurences.values().stream().reduce((a, b) -> a + b).get() / numberEventClass;


        /*
        System.out.println("EventOld class occurences:");
        System.out.println(this.eventClassOccurences);
        System.out.println("Start EventOld class occurences");
        System.out.println(this.startingLogEvents);
        System.out.println("End EventOld class occurences");
        System.out.println(this.endingLogEvents);
        System.out.println("Number of originators: " + this.originators.size( ));
        System.out.println("Orignator occurences");

        for (Map.Entry<Originator, Long> e : getOriginatorOccurences( ).entrySet( )) {
            System.out.println(e.getKey( ) + "," + e.getValue( ));
        }
        System.out.println("Occurences by (Originator and event class)");
        for (Map.Entry<Tuple2<Originator, EventClass>, Long> e : getMapOriginatorsEventClassesOccurences( ).entrySet( )) {
            System.out.println(e.getKey( )._1( ) + "," + e.getKey( )._2( ) + "," + e.getValue( ));
        }
        */
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
