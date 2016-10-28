package org.ag.processmining.log.summarizer.builder;

import org.ag.processmining.log.model.EventSchema;
import org.ag.processmining.log.summarizer.overview.LogSummary;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by ahmed.gater on 25/10/2016.
 */
public class LogSummarizer {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("There is no argument");
            return;
        }

        String sourceFile = args[0];
        String attributeMappingFilePath = args[1];
        String[] event_attributes = {"Case ID", "Activity", "Resource", "Start Timestamp", "Complete Timestamp", "Variant", "Role"};
        EventSchema att_map = new EventSchema(attributeMappingFilePath);
        System.out.println(att_map);
        String applicationName = "Process Mining using Apache Spark";
        String applicationDesc = "Building statistics about the process";

        SparkConf conf = new SparkConf().setAppName(applicationName).setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        LogSummary lss = LogSummary.buildSummary(sc, sourceFile, event_attributes, att_map);

    }
}