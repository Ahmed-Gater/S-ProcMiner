package org.ag.processmining.sna;

import org.ag.processmining.log.model.CaseId;
import org.ag.processmining.log.model.Trace;
import org.apache.spark.api.java.JavaPairRDD;

/**
 * Created by ahmed.gater on 29/10/2016.
 */
public abstract class SNBuilder {

    JavaPairRDD<CaseId, Trace> traces ;
    SocialNetwork rawSn=null ;

    public SNBuilder(JavaPairRDD<CaseId, Trace> traces){
        this.traces = traces ;
    }


    abstract public boolean build() ;

    public SocialNetwork getRawSN(){
        if (this.rawSn == null){
            this.build() ;
        }
        return this.rawSn ;

    }

}
