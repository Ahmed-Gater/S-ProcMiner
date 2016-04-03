/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.processmining.log.summarizer;

import java.util.Iterator;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Event;
import org.ag.processmining.log.model.ProcInstance;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

/**
 *
 * @author ahmed
 */
public class SparkUtils {
    
    public static final PairFunction<String,CaseId,Event> MAP_TO_CASE_ID_EVENT = new PairFunction<String,CaseId,Event>(){
        @Override
        public Tuple2<CaseId, Event> call(String t) throws Exception {
            Event e = new Event(t);
            return new Tuple2(e.getCaseId(), e);
        }
    } ; 
    
    
    public static final PairFunction<Tuple2<CaseId,Iterable<Event>>,CaseId,ProcInstance> MAP_TO_CASE_ID_PROC_INSTANCE = new PairFunction<Tuple2<CaseId,Iterable<Event>>,CaseId,ProcInstance>(){
          ProcInstance procInstance = new ProcInstance() ;
          @Override
          public Tuple2<CaseId, ProcInstance> call(Tuple2<CaseId, Iterable<Event>> t) throws Exception {
              Iterator<Event> it = t._2().iterator() ;
              while(it.hasNext()){
                  procInstance.addEvent(it.next()) ;
              }
              return new Tuple2(t._1(),procInstance);
          } 
    } ; 
            
    
    public static final Function<Tuple2<CaseId,Event>,String> EVENT_CLASSES_GETTER = new Function<Tuple2<CaseId,Event>,String>(){
          @Override
          public String call(Tuple2<CaseId, Event> tuple) throws Exception {
              return tuple._2().getEventClass() ; 
          }
      } ; 

    public static final Function<Tuple2<CaseId,ProcInstance>,String> START_EVENT_CLASSES = new Function<Tuple2<CaseId,ProcInstance>,String>(){
          @Override
          public String call(Tuple2<CaseId, ProcInstance> tuple) throws Exception {
              return tuple._2().getStartEvent() ; 
          }
      } ; 
    
    public static final Function<Tuple2<CaseId,ProcInstance>,String> END_EVENT_CLASSES = new Function<Tuple2<CaseId,ProcInstance>,String>(){
          @Override
          public String call(Tuple2<CaseId, ProcInstance> tuple) throws Exception {
              return tuple._2().getEndEvent() ; 
          }
      } ; 
    
    public static final Function<Tuple2<CaseId,Event>,String> EVENT_ORIGINATOR = new Function<Tuple2<CaseId,Event>,String>(){
          @Override
          public String call(Tuple2<CaseId, Event> tuple) throws Exception {
              return tuple._2().getOriginator() ; 
          }
      } ; 
}
