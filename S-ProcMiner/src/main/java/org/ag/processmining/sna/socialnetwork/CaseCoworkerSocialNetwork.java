package org.ag.processmining.sna.socialnetwork;

import java.io.Serializable;

/**
 * Created by ahmed.gater on 29/10/2016.
 */

public class CaseCoworkerSocialNetwork<Originator> extends SocialNetwork<Originator> implements Serializable {
    static final long serialVersionUID = 1L;


    public CaseCoworkerSocialNetwork() {
        super() ;
    }

    public CaseCoworkerSocialNetwork merge(CaseCoworkerSocialNetwork<Originator> sn1) {
       return (CaseCoworkerSocialNetwork) super.merge(sn1) ;
    }

    public void met(){

    }

}
