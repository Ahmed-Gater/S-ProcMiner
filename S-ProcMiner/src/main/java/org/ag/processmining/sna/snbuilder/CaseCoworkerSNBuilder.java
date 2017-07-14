package org.ag.processmining.sna.snbuilder;

import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Trace;
import org.ag.processmining.sna.socialnetwork.CaseCoworkerSocialNetwork;
import org.apache.spark.api.java.JavaPairRDD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by ahmed.gater on 29/10/2016.
 */

public class CaseCoworkerSNBuilder<Originator> extends SNBuilder implements Serializable {
    static final long serialVersionUID = 1L;



    public CaseCoworkerSNBuilder(JavaPairRDD<CaseId, Trace> traces){
        super(traces) ;
    }
    @Override
    public CaseCoworkerSocialNetwork build() {
        try{
            return traces.map(x -> buildTraceSocialNetwork(x._2())).reduce((x, y) -> x.merge(y));
        }
        catch(Exception e){
            return null ;
        }
    }

    private CaseCoworkerSocialNetwork buildTraceSocialNetwork(Trace trace){
        CaseCoworkerSocialNetwork localSn = new CaseCoworkerSocialNetwork();
        ArrayList<Originator> originators =  new ArrayList(
                trace.getOrderedEvents().values().stream()
                        .map(x -> x.getOriginator())
                        .collect(Collectors.toSet()));

        for (int i=0; i<originators.size()-1; i++){
            for(int j=i+1;j<originators.size();j++){
                localSn.addRelation(originators.get(i),originators.get(j));
            }
        }
        return localSn ;
    }
}
