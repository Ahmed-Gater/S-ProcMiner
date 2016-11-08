package org.ag.processmining.sna.socialnetwork;

import com.google.common.collect.ImmutableList;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.Serializable;

/**
 * Created by ahmed.gater on 29/10/2016.
 */

public class SocialNetwork<T> implements Serializable {
    static final long serialVersionUID = 1L;
    public DefaultDirectedWeightedGraph<T, DefaultWeightedEdge> sGraph;

    public SocialNetwork() {
        this.sGraph = new DefaultDirectedWeightedGraph(DefaultWeightedEdge.class);
    }


    public void addRelation(T src, T dest, double weight) {
        double nWeight = weight;
        if (!this.sGraph.containsEdge(src, dest)) {
            Graphs.addEdgeWithVertices(this.sGraph, src, dest);
            nWeight = weight;
        } else {
            weight = this.sGraph.getEdgeWeight(this.sGraph.getEdge(src, dest)) + weight;
            nWeight = weight + this.sGraph.getEdgeWeight(this.sGraph.getEdge(src, dest));
        }
        this.sGraph.setEdgeWeight(this.sGraph.getEdge(src, dest), nWeight);
    }

    public void addRelation(T src, T dest) {
        addRelation(src, dest, 1.0);
    }


    public SocialNetwork merge(SocialNetwork<T> sn1) {
        SocialNetwork sn = new SocialNetwork<T>();
        new ImmutableList.Builder<DefaultWeightedEdge>()
                .addAll(this.sGraph.edgeSet())
                .build().stream().forEach(e -> sn.addRelation(sGraph.getEdgeSource(e),
                sGraph.getEdgeTarget(e),
                sGraph.getEdgeWeight(e)));
        new ImmutableList.Builder<DefaultWeightedEdge>()
                .addAll(sn1.sGraph.edgeSet())
                .build().stream().forEach(e -> sn.addRelation(sn1.sGraph.getEdgeSource(e),
                sGraph.getEdgeTarget(e),
                sGraph.getEdgeWeight(e)));
        return sn;
    }
}
