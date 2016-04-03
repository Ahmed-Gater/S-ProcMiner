/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.processmining.log.model;

import java.io.Serializable;

/**
 *
 * @author ahmed
 */
public class ProcessMetaData implements Serializable {

	private static final long serialVersionUID = 1L;
    
    public final static String CASE_ID_FIELD_NAME= "CASE_ID_FIELD_NAME" ; 
    public final static String EVENT_CLASS_FIELD_NAME = "EVENT_CLASS_FIELD_NAME" ; 
    public final static String EVENT_START_TIME_FIELD_NAME = "EVENT_START_TIME_FIELD_NAME" ;  
    public final static String EVENT_END_TIME_FIELD_NAME = "EVENT_END_TIME_FIELD_NAME" ; 
    public final static String ORIGINATOR_FIELD_NAME = "ORIGINATOR_FIELD_NAME" ; 
}
