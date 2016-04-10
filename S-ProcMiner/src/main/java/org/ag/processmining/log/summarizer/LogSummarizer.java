package org.ag.processmining.log.summarizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import org.ag.processmining.log.model.AttributeMapping;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Event;
import org.ag.processmining.log.model.EventClass;
import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.ProcInstance;
import static org.ag.processmining.log.summarizer.SparkUtils.* ; 
import org.ag.processmining.log.summarizer.SparkUtils.MapToCaseIdEvent;
import org.apache.spark.SparkConf ;
import org.apache.spark.api.java.JavaPairRDD ;
import org.apache.spark.api.java.JavaRDD ;
import org.apache.spark.api.java.JavaSparkContext ;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class LogSummarizer
{
  public static void main(String[] args)
  {
    if (args.length == 0){
        System.out.println("There is no argument") ; 
        return  ;
    }
    
    String sourceFile = args[0] ; //"D:/ProcessMiningJavaCode/processming/process_data_set.txt";
    String attributeMappingFilePath = args[1] ; 
    String[] event_attributes = {"a_ref_activitee", "h_create_date", "h_dateentree", "h_date_execution", 
                                 "h_codecorbeille", "h_codestatut", "h_creator", "h_domaine", "h_idaction", 
                                 "frigo", "qs", "app_premium", "lien_referentiel_aq", "a_canalfrom", "a_canalto", 
                                 "a_code_apporteur", "a_codecorbeille", "a_domaine", "a_servicepremium", "a_typologie", 
                                 "h_commentaire" } ; 
    
    AttributeMapping att_map = new AttributeMapping(attributeMappingFilePath) ;  
    String applicationName = "Process Mining using Apache Spark" ; 
    String applicationDesc = "Building statistics about the process" ; 
    LogSummary ls = new LogSummary(applicationName,applicationDesc) ; 
    SparkConf conf = new SparkConf().setAppName(applicationName).setMaster("local[*]");
    JavaSparkContext sc = new JavaSparkContext(conf);
    JavaRDD<String> RDDSrc = sc.textFile(sourceFile);
    

    // Building Summary data
    JavaPairRDD<CaseId, Event> CASE_ID_EVENT_MAP = RDDSrc.mapToPair(new MapToCaseIdEvent(att_map,event_attributes));
    JavaPairRDD<CaseId, ProcInstance> CASE_ID_PROC_INSTANCE =  CASE_ID_EVENT_MAP.groupByKey().mapToPair(MAP_TO_CASE_ID_PROC_INSTANCE) ;
     
    /***************************************************************************
       **************************************************************************/
      // Number of process instance
      
      long number_process_instances = CASE_ID_PROC_INSTANCE.count();
      ls.setNumberOfProcessInstances(number_process_instances); 
      
      // Number of events
      long number_events = RDDSrc.count() ;
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
      JavaPairRDD<Originator, EventClass> mapToPair = CASE_ID_EVENT_MAP.mapToPair(new PairFunction<Tuple2<CaseId,Event>,Originator,EventClass>(){
          @Override
          public Tuple2<Originator, EventClass> call(Tuple2<CaseId, Event> t) throws Exception {
              return new Tuple2<>(t._2().getOriginator(), t._2().getEventClass()) ;
          }
      }); 
      
      JavaPairRDD<Originator, Iterable<EventClass>> groupByKey = mapToPair.groupByKey(); 
      groupByKey.saveAsTextFile("/user/cloudera/test.txt");
      // Printing the summary
      ls.print();
      

  }
}
