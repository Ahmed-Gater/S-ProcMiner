package org.ag.processmining.sna;

import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.Trace;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ahmed.gater on 29/10/2016.
 */
public class HandOverSNBuilder implements SNBuilder, Serializable {
    static final long serialVersionUID = 1L;

    Trace trace ;

    public HandOverSNBuilder(Trace t){
        this.trace = t ;
    }
    @Override
    public SocialNetwork build() {
        SocialNetwork sn = new SocialNetwork();
        List<Originator> collect = trace.getOrderedEvents().values().stream()
                                        .map(x -> x.getOriginator()).collect(Collectors.toList());
        for(int i=0;i<collect.size()-1; i++){
            List<Originator> rel = collect.subList(i, i + 2);
            sn.addRelation(rel.get(0),rel.get(1));
        }
        return sn;
    }

}
