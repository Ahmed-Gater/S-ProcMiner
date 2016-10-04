/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.processmining.log.model;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author ahmed
 */
public class AttributeMapping implements Serializable {

    private static final long serialVersionUID = 1L;
    private static AttributeMapping DATA_MAPPING_INSTANCE = null;
    private Map<?, ?> att_mapping;
    private String mappingFilePath;


    public AttributeMapping(String mapFilePath) {
        this.mappingFilePath = mapFilePath;
        att_mapping = load(this.mappingFilePath);
    }

    private Map<?, ?> load(String mapFilePath) {
        final Yaml yaml = new Yaml( );
        Map<?, ?> conf = null;
        Reader reader = null;
        try {
            reader = new FileReader(mapFilePath);
            conf = (Map<?, ?>) yaml.load(reader);
        } catch (final FileNotFoundException fnfe) {
            System.err.println("Unfound file: " + fnfe);
        } finally {
            if (null != reader) {
                try {
                    reader.close( );
                } catch (final IOException ioe) {
                    System.err.println("Exception in reading mapping file: " + ioe);
                }
            }
        }
        System.out.println(conf);
        return conf;
    }

    public List<String> getCaseIdFields() {

        return (List<String>) att_mapping.get(ProcessMetaData.CASE_ID_FIELD_NAME);
    }

    public String getEventClassField() {
        return (String) att_mapping.get(ProcessMetaData.EVENT_CLASS_FIELD_NAME);
    }

    public String getEventStartTimeField() {
        return (String) att_mapping.get(ProcessMetaData.EVENT_START_TIME_FIELD_NAME);
    }

    public String getEventEndTimeField() {
        return (String) att_mapping.get(ProcessMetaData.EVENT_END_TIME_FIELD_NAME);
    }

    public String getOriginatorField() {
        return (String) att_mapping.get(ProcessMetaData.ORIGINATOR_FIELD_NAME);
    }

    @Override
    public String toString() {
        return "CaseId field: " + this.getCaseIdFields( ) +
                "\n Event class field: " + this.getEventClassField( ) +
                "\n Start Time stamps field: " + this.getEventStartTimeField( ) +
                "\n End time stamps field: " + this.getEventEndTimeField( );
    }
}
