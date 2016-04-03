/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.processmining.log.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author ahmed
 */
public class FieldMapping {
    
    private Map<?, ?> config ; 
    private String mappingFilePath ; 
    private static FieldMapping DATA_MAPPING_INSTANCE = null ; 
    
    
    private FieldMapping(String mapFilePath){
        this.mappingFilePath = mapFilePath ; 
        config = load(this.mappingFilePath) ; 
    }
    
    /*
    
    */
    public static FieldMapping getInstance(String mapFilePath){
        if (DATA_MAPPING_INSTANCE == null){
            DATA_MAPPING_INSTANCE = new FieldMapping(mapFilePath) ;
        }
        return DATA_MAPPING_INSTANCE ;
    }
    
    private Map<?, ?> load(String mapFilePath){
        final Yaml yaml = new Yaml();
        Map<?, ?> conf = null ; 
        Reader reader = null;
        try {
            reader = new FileReader(mapFilePath);
            conf = (Map<?, ?>) yaml.load(reader) ;
        } catch (final FileNotFoundException fnfe) {
            System.err.println("Unfound file: " + fnfe);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (final IOException ioe) {
                    System.err.println("Exception in reading mapping file: " + ioe);
                }
            }
        }
        return conf ; 
    }
    
    public List<String> getCaseIdFields(){
        return (List<String>)config.get(ProcessMetaData.CASE_ID_FIELD_NAME) ; 
    }
    
    public String getEventClassField(){
        return (String) config.get(ProcessMetaData.EVENT_CLASS_FIELD_NAME) ; 
    }
    
    public String getEventStartTimeField(){
        return (String) config.get(ProcessMetaData.EVENT_START_TIME_FIELD_NAME) ; 
    }
    
    public String getEventEndTimeField(){
        return (String) config.get(ProcessMetaData.EVENT_END_TIME_FIELD_NAME) ; 
    }
    
    public String getOriginatorField(){
        return (String) config.get(ProcessMetaData.ORIGINATOR_FIELD_NAME) ; 
    }
}
