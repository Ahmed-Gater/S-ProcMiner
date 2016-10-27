package org.ag.processmining.log.summarizer.overview;

import org.ag.processmining.Utils.TimeUtils.TimeUnit;
import org.ag.processmining.log.model.ActivityClass;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Trace;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.util.StatCounter;
import scala.Tuple2;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ahmed.gater on 25/10/2016.
 */

public class ActivityClassOverview implements Serializable {

    private static final long serialVersionUID = 1L;
    Map<ActivityClass, StatCounter> activityClassStats ;

    private ActivityClassOverview(Map<ActivityClass, StatCounter> actClsStats){
        System.out.println("coucou") ;
        this.activityClassStats = actClsStats ;
    }

    public static class ActivityClassOverviewBuilder implements Serializable {

        private static final long serialVersionUID = 1L;
        JavaPairRDD<CaseId, Trace> traces ;

        public ActivityClassOverviewBuilder(JavaPairRDD<CaseId, Trace> traces){
            this.traces = traces ;
        }

        public ActivityClassOverview build() {
            Map<ActivityClass, StatCounter> activityClassStatCounterMap = traces
                    .flatMapToPair(
                            x -> x._2().getOrderedEvents().values().stream()
                                    .map(e -> new Tuple2<ActivityClass, Double>(e.getActivityClass(), e.duration(TimeUnit.MINUTE)))
                                    .collect(Collectors.toList()))
                    .aggregateByKey(new StatCounter(),
                            new Function2<StatCounter, Double, StatCounter>() {
                                @Override
                                public StatCounter call(StatCounter sc, Double d) throws Exception {
                                    return sc.merge(d);
                                }
                            },
                            new Function2<StatCounter, StatCounter, StatCounter>() {
                                @Override
                                public StatCounter call(StatCounter sc1, StatCounter sc2) throws Exception {
                                    return sc1.merge(sc2);
                                }
                            })
                    .collectAsMap();
            return new ActivityClassOverview(activityClassStatCounterMap) ;
        }

    }


}
