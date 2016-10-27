package org.ag.processmining.log.summarizer.utils;

import org.ag.processmining.log.model.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

/**
 * @author ahmed
 */
public final class SparkUtils {

 public static final Function<Tuple2<CaseId, Event>, ActivityClass> EVENT_CLASSES_GETTER = new Function<Tuple2<CaseId, Event>, ActivityClass>() {
        @Override
        public ActivityClass call(Tuple2<CaseId, Event> tuple) throws Exception {
            return tuple._2().getActivityClass();
        }
    };
    public static final Function<Tuple2<CaseId, Trace>, ActivityClass> START_EVENT_CLASSES = new Function<Tuple2<CaseId, Trace>, ActivityClass>() {
        @Override
        public ActivityClass call(Tuple2<CaseId, Trace> tuple) throws Exception {
            return tuple._2().getStartEvent().getActivityClass();
        }
    };
    public static final Function<Tuple2<CaseId, Trace>, ActivityClass> END_EVENT_CLASSES = new Function<Tuple2<CaseId, Trace>, ActivityClass>() {
        @Override
        public ActivityClass call(Tuple2<CaseId, Trace> tuple) throws Exception {
            return tuple._2().getEndEvent().getActivityClass();
        }
    };
    public static final Function<Tuple2<CaseId, Event>, Originator> EVENT_ORIGINATOR = new Function<Tuple2<CaseId, Event>, Originator>() {
        @Override
        public Originator call(Tuple2<CaseId, Event> tuple) throws Exception {
            return tuple._2().getOriginator();
        }
    };
    public static final Function<Tuple2<CaseId, Event>, Originator> ORIGINATOR_EVENT = new Function<Tuple2<CaseId, Event>, Originator>() {
        @Override
        public Originator call(Tuple2<CaseId, Event> tuple) throws Exception {
            return tuple._2().getOriginator();
        }
    };
    public static final PairFunction<Tuple2<CaseId, Event>, Tuple2<Originator, ActivityClass>, Long> ORIGINATOR_EVENT_CLASS_OCCURENCES = new PairFunction<Tuple2<CaseId, Event>, Tuple2<Originator, ActivityClass>, Long>() {
        @Override
        public Tuple2<Tuple2<Originator, ActivityClass>, Long> call(Tuple2<CaseId, Event> t) throws Exception {
            Tuple2<Originator, ActivityClass> org_eventcls = new Tuple2<>(t._2().getOriginator(), t._2().getActivityClass());
            Tuple2<Tuple2<Originator, ActivityClass>, Long> r = new Tuple2(org_eventcls, 1L);
            return r;
        }
    };

    public static void getVoid() {

    }

    public static class MapToCaseIdEvent implements PairFunction<String, CaseId, Event> {

        EventSchema att_map;
        String[] event_attributes;

        public MapToCaseIdEvent(EventSchema att_m, String[] event_atts) {
            att_map = att_m;
            event_attributes = event_atts;
        }

        @Override
        public Tuple2<CaseId, Event> call(String t) throws Exception {
            //EventBuilder(String eventAsString, char fieldDelimiter,String[] header) ;
            //Event e = new Event(t, att_map, event_attributes);
            //return new Tuple2(e.getCaseId(), e);
            return null ;
        }
    }
}
