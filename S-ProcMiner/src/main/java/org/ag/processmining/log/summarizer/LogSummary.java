  
package org.ag.processmining.log.summarizer;

/**
 *
 * @author ahmed
 */

import java.io.Serializable;
import java.util.Map;
import java.util.TreeSet;
import org.ag.processmining.log.model.AttributeMapping;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Event;
import org.ag.processmining.log.model.EventClass;
import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.ProcInstance;
import static org.ag.processmining.log.summarizer.SparkUtils.END_EVENT_CLASSES;
import static org.ag.processmining.log.summarizer.SparkUtils.EVENT_CLASSES_GETTER;
import static org.ag.processmining.log.summarizer.SparkUtils.EVENT_ORIGINATOR;
import static org.ag.processmining.log.summarizer.SparkUtils.MAP_TO_CASE_ID_PROC_INSTANCE;
import static org.ag.processmining.log.summarizer.SparkUtils.START_EVENT_CLASSES;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

/**
 *
 */
public class LogSummary implements Serializable
{
    static final long serialVersionUID = 1L;
        /**
	 * The name of the logs (e.g. name of the application that generated the logs)
	 */
        private String logName = null ; 
        /**
	 * The description of the logs (e.g. description of the application that generated the logs)
	 */
        private String logDescription = null ;
        /**
	 * The time frame of the process instances in the log
	 */
	private TimeFrame processTimeFrame = null;
        /*
        The total number of events of the log
        */
	private long numberOfEvents = 0;
	/**
	 * The total number of process instances contained in a log.
	 */
	private long numberOfProcessInstances = 0;
	
	/**
	 * Alphabetically ordered set containing as strings the log's event classes
	 */
	private TreeSet<EventClass> eventClasses = null;
	
	/**
	 * Mapping from event classes that start a process instance to the
	 * number of process instances actually start a process instance
	 */
	private Map<EventClass, Long> startingLogEvents = null;
	/**
	 * Mapping from event classes that end a process instance to the
	 * number of process instances actually end a process instance
	 */
	private Map<EventClass, Long> endingLogEvents = null;
	
        /**
	 * Mapping from event classes to the number of processes they occured in 
	 */
	private Map<EventClass, Long> eventClassOccurences = null;
        
        /**
	 * Log originators alphabitically ordered by their name
	 */
	private TreeSet<Originator> originators = null;
	
        /**
	 * Mapping from originator to the event classes they execute 
	 */
        private Map<Originator, Long> originatorOccurences = null;
	private Map<Originator, Map<EventClass,Long>> mapOriginatorsToEventClasses = null;
	
	
	/**
	 * Creates a new log summary.
	 * @param logName of the summarized log.
	 * @param logDescription Description of the summarized log.
	 */
	public LogSummary(String logName, String logDescription) {
		this.logName = logName ;
                this.logDescription = logDescription ;
	}

	/**
	 * Creates a new, empty and initialized lightweight
	 * log summary instance.
	 */
	public LogSummary() {
		this("", "");
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
    public TimeFrame getProcessTimeFrame() {
        return processTimeFrame;
    }

    /**
     * @param processTimeFrame the processTimeFrame to set
     */
    public void setProcessTimeFrame(TimeFrame processTimeFrame) {
        this.processTimeFrame = processTimeFrame;
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
    public TreeSet<EventClass> getEventClasses() {
        return eventClasses;
    }

    /**
     * @param eventClasses the eventClasses to set
     */
    public void setEventClasses(TreeSet<EventClass> eventClasses) {
        this.eventClasses = eventClasses;
    }

    /**
     * @return the startingLogEvents
     */
    public Map<EventClass, Long> getStartingLogEvents() {
        return startingLogEvents;
    }

    /**
     * @param startingLogEvents the startingLogEvents to set
     */
    public void setStartingLogEvents(Map<EventClass, Long> startingLogEvents) {
        this.startingLogEvents = startingLogEvents;
    }

    /**
     * @return the endingLogEvents
     */
    public Map<EventClass, Long> getEndingLogEvents() {
        return endingLogEvents;
    }

    /**
     * @param endingLogEvents the endingLogEvents to set
     */
    public void setEndingLogEvents(Map<EventClass, Long> endingLogEvents) {
        this.endingLogEvents = endingLogEvents;
    }

    /**
     * @return the mapEventClassToProcessOccurences
     */
    public Map<EventClass, Long> getEventClassesOccurences() {
        return eventClassOccurences;
    }

    /**
     * @param mapEventClassToProcessOccurences the mapEventClassToProcessOccurences to set
     */
    public void setEventClassOccurences(Map<EventClass, Long> eventClassOccurences) {
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
    public void addOriginator(Originator originator){
        this.originators.add(originator) ; 
    }
    

    /*
    * set the occurences of originators
    */
    public void setOriginatorOccurences(Map<Originator, Long> orgOcc){
        this.originatorOccurences = orgOcc ;
        this.originators = new TreeSet<>(orgOcc.keySet()) ; 
    }
    
    /**
     * @return the occurences of originators
     * ok
     */
    public Map<Originator, Long> getOriginatorOccurences(){
        return this.originatorOccurences ; 
    }
    
    /**
     * @return the mapOriginatorsToEventClasses
     */
    public Map<Originator, Map<EventClass,Long>> getMapOriginatorsToEventClasses() {
        return mapOriginatorsToEventClasses;
    }
    /**
     * @param mapOriginatorsToEventClasses the mapOriginatorsToEventClasses to set
     */
    public void setMapOriginatorsToEventClasses(Map<Originator, Map<EventClass,Long>> mapOriginatorsToEventClasses) {
        this.mapOriginatorsToEventClasses = mapOriginatorsToEventClasses;
    }
    
    public void print(){
        System.out.println("Application name: " +  this.logName) ; 
        System.out.println("Application description: " + this.logDescription) ; 
        System.out.println("Number of process instances: " + this.numberOfProcessInstances) ;
        System.out.println("Number of events: " + this.numberOfEvents) ;
        System.out.println("Event class occurences:") ;
        System.out.println(this.eventClassOccurences) ;
        System.out.println("Start Event class occurences") ;
        System.out.println(this.startingLogEvents);
        System.out.println("End Event class occurences") ;
        System.out.println(this.endingLogEvents);
        System.out.println("Number of originators: " + this.originators.size()) ; 
        System.out.println("Orignator occurences") ;
        System.out.println(this.eventClassOccurences) ;
    }
    
    public static LogSummary buildSummary(JavaSparkContext sc, String appliName, String appliDesc , String sourceFile, String[] event_attributes, AttributeMapping att_map) {
        
        LogSummary ls = new LogSummary(appliName, appliDesc);
        JavaRDD<String> RDDSrc = sc.textFile(sourceFile);
        // Building Summary data
        JavaPairRDD<CaseId, Event> CASE_ID_EVENT_MAP = RDDSrc.mapToPair(new SparkUtils.MapToCaseIdEvent(att_map, event_attributes));
        JavaPairRDD<CaseId, ProcInstance> CASE_ID_PROC_INSTANCE = CASE_ID_EVENT_MAP.groupByKey().mapToPair(MAP_TO_CASE_ID_PROC_INSTANCE);

        /**
         * *************************************************************************
         *************************************************************************
         */
        // Number of process instance
        long number_process_instances = CASE_ID_PROC_INSTANCE.count();
        ls.setNumberOfProcessInstances(number_process_instances);

        // Number of events
        long number_events = RDDSrc.count();
        ls.setNumberOfEvents(number_events);
        // Event classes
        Map<EventClass, Long> event_class_occurences = CASE_ID_EVENT_MAP.map(EVENT_CLASSES_GETTER).countByValue();
        ls.setEventClassOccurences(event_class_occurences);

        // Start event class occurences
        Map<EventClass, Long> start_event_class_occurences = CASE_ID_PROC_INSTANCE.map(START_EVENT_CLASSES).countByValue();
        ls.setStartingLogEvents(start_event_class_occurences);

        // End event class occurences
        Map<EventClass, Long> end_event_class_occurences = CASE_ID_PROC_INSTANCE.map(END_EVENT_CLASSES).countByValue();
        ls.setEndingLogEvents(end_event_class_occurences);

        // Originator occurences
        Map<Originator, Long> event_originator_occurences = CASE_ID_EVENT_MAP.map(EVENT_ORIGINATOR).countByValue();
        ls.setOriginatorOccurences(event_originator_occurences);

        // Originator-EventClass occurences
        JavaPairRDD<Originator, EventClass> mapToPair = CASE_ID_EVENT_MAP.mapToPair(new PairFunction<Tuple2<CaseId, Event>, Originator, EventClass>() {
            @Override
            public Tuple2<Originator, EventClass> call(Tuple2<CaseId, Event> t) throws Exception {
                return new Tuple2<>(t._2().getOriginator(), t._2().getEventClass());
            }
        });

        return ls;
    }
}
