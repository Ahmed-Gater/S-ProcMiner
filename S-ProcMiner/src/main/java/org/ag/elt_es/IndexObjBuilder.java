/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.elt_es;

import io.searchbox.core.Index;
import java.util.Map;
import java.util.Optional;
import org.ag.processmining.Utils.DateFormatExtractor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author ahmed
 */
public class IndexObjBuilder {
    
    /*
        * 
        */
        public static Index buildIndexFromCSVEntry(String csv_str, String[] time_fields, String[] event_attributes, String index, String type, Optional<String> id_field) {
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
        public static Index buildIndexFromCSVEntry(String csv_str, String[] event_attributes, String index_field, String type_field, Optional<String> id_field, boolean drop_meta) {
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
        public static Index buildIndexFromJSONObject(JSONObject jsonObj, String index_field, String type_field, Optional<String> id_field, boolean drop_meta) {
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
        public static Index buildIndexFromJSONObject(JSONObject jsonObj, String index, String type, Optional<String> id) {
            if (id.isPresent()){
                return new Index.Builder(jsonObj).index(index).type(type).id(id.get()).build();
            }
            else {
                return new Index.Builder(jsonObj).index(index).type(type).build();
            }
        }
        
        
}
