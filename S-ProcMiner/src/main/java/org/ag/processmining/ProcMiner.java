package org.ag.processmining;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import static org.ag.processmining.SparkUtils.MAP_TO_CASE_ID_EVENT ;
import static org.ag.processmining.SparkUtils.MAP_TO_CASE_ID_PROC_INSTANCE ; 

public class ProcMiner
{
  public static void main(String[] args)
  {
    //String sourceFile = "file://D:/ProcessMiningJavaCode/processming/process_data_set.txt";
      String sourceFile = "D:/ProcessMiningJavaCode/processming/process_data_set.txt";
    SparkConf conf = new SparkConf().setAppName("Workshop").setMaster("local[*]");
    JavaSparkContext sc = new JavaSparkContext(conf);
    
    
    JavaRDD<String> RDDSrc = sc.textFile(sourceFile);
    // Building 
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
      
  }
}
