package org.ag.processmining.sna;

import org.ag.processmining.log.model.ActivityClass;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.Trace;
import org.apache.commons.collections.map.HashedMap;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ahmed.gater on 29/10/2016.
 */
public class ActivityCoworkerSNBuilder implements SNBuilder, Serializable {
    static final long serialVersionUID = 1L;

    JavaPairRDD<CaseId, Trace> traces ;

    public ActivityCoworkerSNBuilder(JavaPairRDD<CaseId, Trace> traces){
        this.traces = traces ;
    }
    @Override
    public SocialNetwork build() {
        Map<Tuple2<ActivityClass, Originator>, Long> k = traces.flatMapToPair(x -> x._2().activityClassOriginator())
                                                                .countByValue();
        Map<ActivityClass,List<Tuple2<Originator,Long>>> res = new HashedMap() ;
        k.keySet().stream().forEach(x->{
            if (!res.containsKey(x._1())){
                res.put(x._1(),new ArrayList<Tuple2<Originator, Long>>()) ;
            }
            res.get(x._1()).add(new Tuple2<Originator, Long>(x._2(),k.get(x)));
        });

        // Building the social network
        SocialNetwork<Originator> sn = new SocialNetwork<>() ;
        res.values().stream().forEach(x->{
            for (int i=0; i<x.size()-1; i++){
                for(int j=i+1;j<x.size();j++){
                    sn.addRelation(x.get(i)._1(),x.get(j)._1(),Math.min(x.get(i)._2(),x.get(j)._2()));
                }
            }
        });
        return sn ;
    }
}
