package aStar;

import lenz.htw.coshnost.world.GraphNode;

public interface Scorer {

  double computeCost(GraphNode from, GraphNode to);
}
