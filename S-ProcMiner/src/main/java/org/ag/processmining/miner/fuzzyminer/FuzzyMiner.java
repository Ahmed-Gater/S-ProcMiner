package org.ag.processmining.miner.fuzzyminer;

import org.ag.processmining.log.model.AttributeMapping;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Event;
import org.ag.processmining.log.model.ProcInstance;
import org.ag.processmining.log.summarizer.overview.LogSummary;
import org.ag.processmining.log.summarizer.utils.SparkUtils.MapToCaseIdEvent;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import static org.ag.processmining.log.summarizer.utils.SparkUtils.MAP_TO_CASE_ID_PROC_INSTANCE;

public class FuzzyMiner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("There is no argument");
            return;
        }

        String sourceFile = args[0]; //"D:/ProcessMiningJavaCode/processming/process_data_set.txt";
        String attributeMappingFilePath = args[1];
        String[] event_attributes = {"a_ref_activitee", "h_create_date", "h_dateentree", "h_date_execution",
                "h_codecorbeille", "h_codestatut", "h_creator", "h_domaine", "h_idaction",
                "frigo", "qs", "app_premium", "lien_referentiel_aq", "a_canalfrom", "a_canalto",
                "a_code_apporteur", "a_codecorbeille", "a_domaine", "a_servicepremium", "a_typologie",
                "h_commentaire"};

        AttributeMapping att_map = new AttributeMapping(attributeMappingFilePath);
        String applicationName = "Process Mining using Apache Spark";
        String applicationDesc = "Building statistics about the process";
        LogSummary ls = new LogSummary(applicationName, applicationDesc);
        SparkConf conf = new SparkConf( ).setAppName(applicationName).setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> RDDSrc = sc.textFile(sourceFile);


        // Building Summary data
        JavaPairRDD<CaseId, Event> CASE_ID_EVENT_MAP = RDDSrc.mapToPair(new MapToCaseIdEvent(att_map, event_attributes));
        JavaPairRDD<CaseId, ProcInstance> CASE_ID_PROC_INSTANCE = CASE_ID_EVENT_MAP.groupByKey( ).mapToPair(MAP_TO_CASE_ID_PROC_INSTANCE);

        long count = CASE_ID_PROC_INSTANCE.count( );
        System.out.println(count);


    }
}
