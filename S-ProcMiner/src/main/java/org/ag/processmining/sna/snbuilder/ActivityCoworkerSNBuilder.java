package org.ag.processmining.sna.snbuilder;

import org.ag.processmining.log.model.ActivityClass;
import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.Trace;
import org.ag.processmining.sna.socialnetwork.ActivityCoworkerSocialNetwork;
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


public class ActivityCoworkerSNBuilder extends SNBuilder implements Serializable {
    static final long serialVersionUID = 1L;

    public ActivityCoworkerSNBuilder(JavaPairRDD<CaseId, Trace> traces){
        super(traces) ;
    }

    @Override
    public ActivityCoworkerSocialNetwork build() {
        try{
            Map<Tuple2<ActivityClass, org.ag.processmining.log.model.Originator>, Long> k = traces.flatMapToPair(x -> x._2().activityClassOriginator())
                    .countByValue();
            Map<ActivityClass,List<Tuple2<Originator,Long>>> res = new HashedMap() ;
            k.keySet().stream().forEach(x->{
                if (!res.containsKey(x._1())){
                    res.put(x._1(),new ArrayList<Tuple2<Originator, Long>>()) ;
                }
                res.get(x._1()).add(new Tuple2<Originator, Long>(x._2(),k.get(x)));
            });

            // Building the social network
            ActivityCoworkerSocialNetwork rawSn = new ActivityCoworkerSocialNetwork<>() ;
            res.values().stream().forEach(x->{
                for (int i=0; i<x.size()-1; i++){
                    for(int j=i+1;j<x.size();j++){
                        rawSn.addRelation(x.get(i)._1(),x.get(j)._1(),Math.min(x.get(i)._2(),x.get(j)._2()));
                    }
                }
            });
            return rawSn ;
        }
        catch(Exception e){
           return null ;
        }
    }


}
