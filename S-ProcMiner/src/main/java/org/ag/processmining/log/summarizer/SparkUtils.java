package org.ag.processmining.log.summarizer;

import org.ag.processmining.log.model.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Iterator;

/**
 * @author ahmed
 */
public final class SparkUtils {

    public static final PairFunction<Tuple2<CaseId, Iterable<Event>>, CaseId, ProcInstance> MAP_TO_CASE_ID_PROC_INSTANCE = new PairFunction<Tuple2<CaseId, Iterable<Event>>, CaseId, ProcInstance>( ) {
        @Override
        public Tuple2<CaseId, ProcInstance> call(Tuple2<CaseId, Iterable<Event>> t) throws Exception {
            Iterator<Event> it = t._2( ).iterator( );
            ProcInstance procInstance = new ProcInstance( );
            System.out.println(t._1().toString()) ;
            while (it.hasNext( )) {
                procInstance.addEvent(it.next());
            }
            return new Tuple2(t._1( ), procInstance);
        }
    };
    public static final Function<Tuple2<CaseId, Event>, EventClass> EVENT_CLASSES_GETTER = new Function<Tuple2<CaseId, Event>, EventClass>( ) {
        @Override
        public EventClass call(Tuple2<CaseId, Event> tuple) throws Exception {
            return tuple._2( ).getEventClass( );
        }
    };
    public static final Function<Tuple2<CaseId, ProcInstance>, EventClass> START_EVENT_CLASSES = new Function<Tuple2<CaseId, ProcInstance>, EventClass>( ) {
        @Override
        public EventClass call(Tuple2<CaseId, ProcInstance> tuple) throws Exception {
            return tuple._2( ).getStartEvent().getEventClass();
        }
    };
    public static final Function<Tuple2<CaseId, ProcInstance>, EventClass> END_EVENT_CLASSES = new Function<Tuple2<CaseId, ProcInstance>, EventClass>( ) {
        @Override
        public EventClass call(Tuple2<CaseId, ProcInstance> tuple) throws Exception {
            return tuple._2( ).getEndEvent( ).getEventClass();
        }
    };
    public static final Function<Tuple2<CaseId, Event>, Originator> EVENT_ORIGINATOR = new Function<Tuple2<CaseId, Event>, Originator>( ) {
        @Override
        public Originator call(Tuple2<CaseId, Event> tuple) throws Exception {
            return tuple._2( ).getOriginator( );
        }
    };
    public static final Function<Tuple2<CaseId, Event>, Originator> ORIGINATOR_EVENT = new Function<Tuple2<CaseId, Event>, Originator>( ) {
        @Override
        public Originator call(Tuple2<CaseId, Event> tuple) throws Exception {
            return tuple._2( ).getOriginator( );
        }
    };
    public static final PairFunction<Tuple2<CaseId, Event>, Tuple2<Originator, EventClass>, Long> ORIGINATOR_EVENT_CLASS_OCCURENCES = new PairFunction<Tuple2<CaseId, Event>, Tuple2<Originator, EventClass>, Long>( ) {
        @Override
        public Tuple2<Tuple2<Originator, EventClass>, Long> call(Tuple2<CaseId, Event> t) throws Exception {
            Tuple2<Originator, EventClass> org_eventcls = new Tuple2<>(t._2( ).getOriginator( ), t._2( ).getEventClass( ));
            Tuple2<Tuple2<Originator, EventClass>, Long> r = new Tuple2(org_eventcls, 1L);
            return r;
        }
    };

    public static class MapToCaseIdEvent implements PairFunction<String, CaseId, Event> {

        AttributeMapping att_map;
        String[] event_attributes;

        public MapToCaseIdEvent(AttributeMapping att_m, String[] event_atts) {
            att_map = att_m;
            event_attributes = event_atts;
        }

        @Override
        public Tuple2<CaseId, Event> call(String t) throws Exception {
            Event e = new Event(t, att_map, event_attributes);
            return new Tuple2(e.getCaseId( ), e);
        }
    }

}
