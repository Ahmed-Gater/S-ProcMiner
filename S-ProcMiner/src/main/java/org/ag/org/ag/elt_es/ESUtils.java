/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.org.ag.elt_es;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.jettison.json.JSONObject;
import org.ag.processmining.Utils.DateFormatExtractor ; 
/**
 *
 * @author ahmed
 */
public class ESUtils {

    public final static class SyncESBulkLoader {

        JestClient client;
        String defaultIndex = null ;
        public SyncESBulkLoader(String[] es_hosts) {
            JestClientFactory factory = new JestClientFactory();
            factory.setHttpClientConfig(new HttpClientConfig.Builder(Arrays.asList(es_hosts))
                    .multiThreaded(true)
                    .build());
            client = factory.getObject();
        }
        
        /*
        Sending JSON having index and type as attribue ()
        */
        public void send(Iterator<String> it, String[] time_fields, String[] event_attributes, int bulkSize, String index, String type) {
            List<Index> bulk_buffer = new ArrayList<>();
            Random r = new Random() ; 
            while (it.hasNext()) {
                Optional<String> op = Optional.empty() ;
                Index idx_json = buildIndex(it.next(),time_fields, event_attributes, index, type,op);
                if (idx_json != null) {
                    bulk_buffer.add(idx_json);
                } else {
                    /* HANDLING BUILDING ERRORS */
                    System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL") ; 
                }

                if (bulk_buffer.size() == bulkSize) {
                    Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build() ;
                    try {
                        BulkResult blk_result = client.execute(bulk); 
                        handleFailedItems(blk_result) ; 
                    } catch (IOException ex) {
                        Logger.getLogger(ESUtils.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Salut les erreurs") ; 
                    }
                    bulk_buffer = new ArrayList<>();
                }
            }
            if (!bulk_buffer.isEmpty()) {
                    Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build() ;
                    try {
                        BulkResult blk_result = client.execute(bulk); 
                        handleFailedItems(blk_result) ; 
                    } catch (IOException ex) {
                        Logger.getLogger(ESUtils.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Salut les erreurs") ; 
                    }
            }
            Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build() ;
            client.executeAsync(bulk,new JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    handleFailedItems((BulkResult) result) ; 
                }
                @Override
                public void failed(Exception ex) {
                    
                }
            });
        }
        
        /*
        * 
        */
        private Index buildIndex(String csv_str, String[] time_fields, String[] event_attributes, String index, String type, Optional<String> id_field) {
            try {
                CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(event_attributes).withDelimiter(';');
                Map<String, String> csv_as_map = ((CSVRecord) CSVParser.parse(csv_str, csvFileFormat).getRecords().get(0)).toMap();
                for(String time_field: time_fields){
                    csv_as_map.put(time_field,DateFormatExtractor.buildDateTime(csv_as_map.get(time_field)).toString()) ; 
                }
                if (id_field.isPresent()){
                    return new Index.Builder(csv_as_map).index(index).type(type).id(id_field.get()).build();
                }
                else{
                    return new Index.Builder(csv_as_map).index(index).type(type).build();
                }
            } catch (Exception e) {
                return null;
            }
        }

        /*
        * 
         */
        private Index buildIndex(String csv_str, String[] event_attributes, String index_field, String type_field, Optional<String> id_field, boolean drop_meta) {
            try {
                CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(event_attributes).withDelimiter(';');
                Map<String, String> csv_as_map = ((CSVRecord) CSVParser.parse(csv_str, csvFileFormat).getRecords().get(0)).toMap();
                String index = csv_as_map.get(index_field) ; 
                String type = csv_as_map.get(type_field);
                String id = id_field.isPresent()? csv_as_map.get(id_field.get()):null;
                if (drop_meta) {
                    csv_as_map.remove(index_field) ; 
                    csv_as_map.remove(type_field);
                    if (id_field.isPresent()){
                        csv_as_map.remove(id_field.get());
                    }
                }
                if (id_field.isPresent()){
                    return new Index.Builder(csv_as_map).index(index).type(type).id(id).build();
                }
                else{
                    return new Index.Builder(csv_as_map).index(index).type(type).build();
                }
            } catch (Exception e) {
                return null;
            }
        }
        
       /*
        
        */
        private Index buildIndex(JSONObject jsonObj, String index_field, String type_field, Optional<String> id_field, boolean drop_meta) {
            try {
                String index = jsonObj.getString(index_field);
                String type = jsonObj.getString(type_field);
                String id = id_field.isPresent() ? jsonObj.getString(id_field.get()):null;
                if (drop_meta) {
                    jsonObj.remove(index_field);
                    jsonObj.remove(type_field);
                    if (id_field.isPresent()){
                        jsonObj.remove(id_field.get());
                    }
                }
                if (id != null){
                    return new Index.Builder(jsonObj).index(index).type(type).id(id).build(); 
                }
                else{
                    return new Index.Builder(jsonObj).index(index).type(type).build();
                }
            } catch (Exception e) {
                return null;
            }
        }
        /*
        Building Index object from a json object
        */
        private Index buildIndex(JSONObject jsonObj, String index, String type, Optional<String> id) {
            if (id.isPresent()){
                return new Index.Builder(jsonObj).index(index).type(type).id(id.get()).build();
            }
            else {
                return new Index.Builder(jsonObj).index(index).type(type).build();
            }
        }
        
        private void handleFailedItems(BulkResult result){
            System.out.println(result.getFailedItems().size()) ; 
        }
               
    }
}
