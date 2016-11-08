package org.ag.processmining.sna.socialnetwork;

import java.io.Serializable;

/**
 * Created by ahmed.gater on 29/10/2016.
 */

public class ActivityCoworkerSocialNetwork<Originator> extends SocialNetwork<Originator> implements Serializable {
    static final long serialVersionUID = 1L;


    public ActivityCoworkerSocialNetwork() {
        super() ;
    }

    public ActivityCoworkerSocialNetwork merge(ActivityCoworkerSocialNetwork<Originator> sn1) {
       return (ActivityCoworkerSocialNetwork) super.merge(sn1) ;
    }

    public void met(){

    }

}
