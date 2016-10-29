package org.ag.processmining.sna;

import org.ag.processmining.log.model.Originator;
import org.ag.processmining.log.model.Trace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by ahmed.gater on 29/10/2016.
 */
public class CaseCoworkerSNBuilder implements SNBuilder, Serializable {
    static final long serialVersionUID = 1L;

    Trace trace ;

    public CaseCoworkerSNBuilder(Trace t){
        this.trace = t ;
    }
    @Override
    public SocialNetwork build() {
        SocialNetwork sn = new SocialNetwork();
        ArrayList<Originator> originators =  new ArrayList(
                trace.getOrderedEvents().values().stream()
                                        .map(x -> x.getOriginator())
                                        .collect(Collectors.toSet()));

        for (int i=0; i<originators.size()-1; i++){
            for(int j=i;j<originators.size();j++){
                sn.addRelation(originators.get(i),originators.get(j));
            }
        }
        return sn;
    }
}
