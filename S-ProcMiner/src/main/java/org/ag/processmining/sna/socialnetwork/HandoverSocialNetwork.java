package org.ag.processmining.sna.socialnetwork;

import java.io.Serializable;

/**
 * Created by ahmed.gater on 29/10/2016.
 */

public class HandoverSocialNetwork<Originator> extends SocialNetwork<Originator> implements Serializable {
    static final long serialVersionUID = 1L;


    public HandoverSocialNetwork() {
        super() ;
    }

    public HandoverSocialNetwork merge(HandoverSocialNetwork<Originator> sn1) {
       return (HandoverSocialNetwork) super.merge(sn1) ;
    }

    public void met(){

    }

}
