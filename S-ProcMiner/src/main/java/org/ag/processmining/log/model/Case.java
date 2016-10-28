/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.processmining.log.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 *
 * @author youssef.hissou
 */
@Getter
@Setter
@AllArgsConstructor
public class Case {
    
    public CaseId caseid;
    public int nbEvents;
    public DateTime started;
    public DateTime finished;

    
    
}
