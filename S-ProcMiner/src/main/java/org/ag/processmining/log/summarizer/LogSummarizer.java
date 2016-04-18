package org.ag.processmining.log.summarizer;

import org.ag.processmining.log.model.AttributeMapping;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;


public class LogSummarizer {

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
        System.out.println(att_map);
        String applicationName = "Process Mining using Apache Spark";
        String applicationDesc = "Building statistics about the process";

        SparkConf conf = new SparkConf().setAppName(applicationName).setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        LogSummary lss = LogSummary.buildSummary(sc, applicationName, applicationDesc, sourceFile, event_attributes, att_map);
        lss.print();
    }
}
