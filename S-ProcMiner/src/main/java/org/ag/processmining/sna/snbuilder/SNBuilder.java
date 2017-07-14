package org.ag.processmining.sna.snbuilder;

import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Trace;
import org.ag.processmining.sna.socialnetwork.SocialNetwork;
import org.apache.spark.api.java.JavaPairRDD;

/**
 * Created by ahmed.gater on 29/10/2016.
 */
public abstract class SNBuilder {

    JavaPairRDD<CaseId, Trace> traces ;

    public SNBuilder(JavaPairRDD<CaseId, Trace> traces){
        this.traces = traces ;
    }


    abstract public SocialNetwork build() ;



}
