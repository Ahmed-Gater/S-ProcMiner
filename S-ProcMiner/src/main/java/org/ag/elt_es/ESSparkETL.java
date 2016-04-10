package org.ag.elt_es;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;



public class ESSparkETL {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("There is no argument");
            //return  ;
        }
        String sourceFile = "D:/ProcessMiningJavaCode/processming/process_data_set.txt";
        
        final String[] es_hosts = {"http://192.168.1.69:9201"};
        final String applicationName = "Loading HDFS data and pushing them to ES";
        final int bulkSize = 30;
        //final String index_field = "" ; 
        final String type_field = "h_idaction";
        final boolean drop_meta = true;
        final String index = "slpi";

        //final String id_field = "" ; 
        final String[] time_fields = {"h_dateentree"};
        final String[] event_attributes = {"a_ref_activitee", "h_create_date", "h_dateentree", "h_date_execution",
            "h_codecorbeille", "h_codestatut", "h_creator", "h_domaine", "h_idaction",
            "frigo", "qs", "app_premium", "lien_referentiel_aq", "a_canalfrom", "a_canalto",
            "a_code_apporteur", "a_codecorbeille", "a_domaine", "a_servicepremium", "a_typologie",
            "h_commentaire"};
        
        SparkConf conf = new SparkConf().setAppName(applicationName).setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> RDDSrc = sc.textFile(sourceFile);

        RDDSrc.foreachPartition(new VoidFunction<Iterator<String>>() {
            @Override
            public void call(Iterator<String> it) throws Exception {
                ESBulkLoader sesb = new ESBulkLoader(es_hosts);
                sesb.SyncCSVIntegration(it, time_fields, event_attributes, bulkSize, index, "type");
            }
        });

        System.out.println("Number of entries: " + RDDSrc.count());

    }
}
