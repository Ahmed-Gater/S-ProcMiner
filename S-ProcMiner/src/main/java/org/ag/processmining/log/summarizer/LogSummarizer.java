package org.ag.processmining.log.summarizer;

import java.util.Map;
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
                                 "h_commentaire" }; 
    
    AttributeMapping att_map = new AttributeMapping(attributeMappingFilePath) ;  
    SparkConf conf = new SparkConf().setAppName("Workshop").setMaster("local[*]");
    JavaSparkContext sc = new JavaSparkContext(conf);
    JavaRDD<String> RDDSrc = sc.textFile(sourceFile);
    

    // Building Summary data
    MapToCaseIdEvent MAP_TO_CASE_ID_EVENT = new MapToCaseIdEvent(att_map,event_attributes) ; 
    JavaPairRDD<CaseId, Event> CASE_ID_EVENT_MAP = RDDSrc.mapToPair(MAP_TO_CASE_ID_EVENT);
    JavaPairRDD<CaseId, ProcInstance> CASE_ID_PROC_INSTANCE =  CASE_ID_EVENT_MAP.groupByKey().mapToPair(MAP_TO_CASE_ID_PROC_INSTANCE) ;
     
    /***************************************************************************
       **************************************************************************/
      // Number of process instance
      
      long number_process_instances = CASE_ID_PROC_INSTANCE.count();
      System.out.println("Number of process instances: " + number_process_instances) ;
      // Number of events
      long number_events = RDDSrc.count() ;
      System.out.println("Number of events: " + number_events) ;
      // Event classes
      Map<EventClass, Long> event_class_occurences = CASE_ID_EVENT_MAP.map(EVENT_CLASSES_GETTER).countByValue();
      System.out.println(event_class_occurences) ;
      // Start event class occurences
      Map<EventClass, Long> start_event_class_occurences = CASE_ID_PROC_INSTANCE.map(START_EVENT_CLASSES).countByValue();
      System.out.println("Start Event class occurences") ;
      System.out.println(start_event_class_occurences);
      // End event class occurences
      Map<EventClass, Long> end_event_class_occurences = CASE_ID_PROC_INSTANCE.map(END_EVENT_CLASSES).countByValue();
      System.out.println("End Event class occurences") ;
      System.out.println(end_event_class_occurences);
      // Originators
      Map<Originator, Long> event_originator_occurences = CASE_ID_EVENT_MAP.map(EVENT_ORIGINATOR).countByValue();
      System.out.println("Number of originators: " + event_originator_occurences.keySet().size()) ; 
      System.out.println("Event orignator occurences") ;
      System.out.println(event_originator_occurences) ;
      

  }
}
