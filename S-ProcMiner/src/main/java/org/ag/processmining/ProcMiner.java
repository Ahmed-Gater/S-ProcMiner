package org.ag.processmining;
import java.io.PrintStream;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class ProcMiner
{
  public static void main(String[] args)
  {
    String sourceFile = "file://D:/ProcessMiningJavaCode/processming/process_data_set.txt";
    SparkConf conf = new SparkConf().setAppName("Workshop").setMaster("local[*]");
    JavaSparkContext sc = new JavaSparkContext(conf);
    
    JavaRDD<String> RDDSrc = sc.textFile(sourceFile);
    JavaPairRDD<CaseId, Event> mapToPair = RDDSrc.mapToPair(new PairFunction()
    {
        public Tuple2<CaseId, Event> call(String t)
        throws Exception
      {
        Event e = new Event(t);
        return new Tuple2(e.caseId, e);
      }

        @Override
        public Tuple2 call(Object t) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    });
    
    JavaRDD<String> Res = mapToPair.map(new Function()
    {
        @Override
        public Object call(Object t1) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    });
    Res.count();
  }
}
