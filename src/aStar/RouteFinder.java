package aStar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import lenz.htw.coshnost.world.GraphNode;

public class RouteFinder {

  private final Scorer scorer;

  public RouteFinder(Scorer scorer) {
    this.scorer = scorer;
  }

  public List<GraphNode> findRoute(GraphNode from, GraphNode to) {
    Queue<RouteNode> openSet = new PriorityQueue<>();
    Map<GraphNode, RouteNode> allNodes = new HashMap<>();

    RouteNode start = new RouteNode(from, null, 0d, scorer.computeCost(from, to));
    openSet.add(start);
    allNodes.put(from, start);

    while (!openSet.isEmpty()) {
      RouteNode next = openSet.poll();
      // if current is target, return full route
      if (next.getCurrent().equals(to)) {
        List<GraphNode> route = new ArrayList<>();
        RouteNode current = next;
        while (current != null) {
          route.add(0, current.getCurrent());
          current = allNodes.get(current.getPrevious());
        }
        return route;
      }

      // otherwise continue from connections of current
      Arrays.stream(next.getCurrent().getNeighbors()).forEach(connection -> {
        RouteNode nextNode = allNodes.getOrDefault(connection, new RouteNode(connection));
        allNodes.put(connection, nextNode);

        double newScore = next.getRouteScore() + scorer.computeCost(next.getCurrent(), connection);
        if (newScore < nextNode.getRouteScore()) {
          nextNode.setPrevious(next.getCurrent());
          nextNode.setRouteScore(newScore);
          nextNode.setEstimatedScore(newScore + scorer.computeCost(connection, to));
          openSet.add(nextNode);
        }
      });
    }

    throw new IllegalStateException("No route found");
  }
}
