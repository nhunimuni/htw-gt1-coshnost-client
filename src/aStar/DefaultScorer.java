package aStar;

import lenz.htw.coshnost.world.GraphNode;

public class DefaultScorer implements Scorer {

  @Override
  public double computeCost(GraphNode from, GraphNode to) {
    return 1;
  }
}
