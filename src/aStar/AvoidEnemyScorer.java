package aStar;

import java.util.Arrays;
import lenz.htw.coshnost.world.GraphNode;

public class AvoidEnemyScorer implements Scorer {

  @Override
  public double computeCost(GraphNode from, GraphNode to, int myNumber) {
    int[] players = new int[]{0, 1, 2};
    players = Arrays.stream(players).filter(player -> player != myNumber).toArray();
    double cost = 0;
    cost += to.ownership[players[0]];
    cost += to.ownership[players[1]];
    return cost;
  }
}
