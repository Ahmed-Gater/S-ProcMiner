package org.ag.processmining.sna;

import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.Trace;
import org.apache.spark.api.java.JavaPairRDD;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ahmed.gater on 29/10/2016.
 */

public class HandOverSNBuilder extends SNBuilder implements Serializable {
    static final long serialVersionUID = 1L;

    public HandOverSNBuilder(JavaPairRDD<CaseId, Trace> traces){
        super(traces) ;
    }

    @Override
    public boolean build() {
        try{
            this.rawSn = traces.map(x -> buildTraceSocialNetwork(x._2()))
                    .reduce((x, y) -> x.merge(y));
            return true ;
        }
        catch(Exception e){
            return false ;
        }
    }

    private SocialNetwork buildTraceSocialNetwork(Trace trace){
        SocialNetwork sn = new SocialNetwork();
        List<Originator> collect = trace.getOrderedEvents().values().stream()
                .map(x -> x.getOriginator())
                .collect(Collectors.toList());
        for(int i=0;i<collect.size()-1; i++){
            List<Originator> rel = collect.subList(i, i + 2);
            sn.addRelation(rel.get(0),rel.get(1));
        }
        return sn;
    }

}
