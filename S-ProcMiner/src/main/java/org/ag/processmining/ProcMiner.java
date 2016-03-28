package org.ag.processmining;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import static org.ag.processmining.SparkUtils.MAP_TO_CASE_ID_EVENT ;
import static org.ag.processmining.SparkUtils.MAP_TO_CASE_ID_PROC_INSTANCE ; 

public class ProcMiner
{
  public static void main(String[] args)
  {
    String sourceFile = "file://D:/ProcessMiningJavaCode/processming/process_data_set.txt";
    SparkConf conf = new SparkConf().setAppName("Workshop").setMaster("local[*]");
    JavaSparkContext sc = new JavaSparkContext(conf);
    
    JavaRDD<String> RDDSrc = sc.textFile(sourceFile);
    // Building 
    JavaPairRDD<CaseId, Event> CASE_ID_EVENT_MAP = RDDSrc.mapToPair(MAP_TO_CASE_ID_EVENT); 
    JavaPairRDD<CaseId, ProcInstance> CASE_ID_PROC_INSTANCE =  CASE_ID_EVENT_MAP.groupByKey().mapToPair(MAP_TO_CASE_ID_PROC_INSTANCE) ; 
    
  }
}
