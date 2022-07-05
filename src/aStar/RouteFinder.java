package aStar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class RouteFinder<T extends GraphNode> {

  private final Graph<T> graph;
  private final Scorer<T> nextNodeScorer;
  private final Scorer<T> targetScorer;

  RouteFinder(Graph<T> graph, Scorer<T> nextNodeScorer, Scorer<T> targetScorer) {
    this.graph = graph;
    this.nextNodeScorer = nextNodeScorer;
    this.targetScorer = targetScorer;
  }

  public List<T> findRoute(T from, T to) {
    Queue<RouteNode<T>> openSet = new PriorityQueue<>();
    Map<T, RouteNode<T>> allNodes = new HashMap<>();

    RouteNode<T> start = new RouteNode<>(from, null, 0d, targetScorer.computeCost(from, to));
    openSet.add(start);
    allNodes.put(from, start);

    while (!openSet.isEmpty()) {
      RouteNode<T> next = openSet.poll();
      // if current is target, return full route
      if (next.getCurrent().equals(to)) {
        List<T> route = new ArrayList<>();
        RouteNode<T> current = next;
        while (current != null) {
          route.add(0, current.getCurrent());
          current = allNodes.get(current.getPrevious());
        }
        return route;
      }

      // otherwise continue from connections of current
      graph.getConnections(next.getCurrent()).forEach(connection -> {
        RouteNode<T> nextNode = allNodes.getOrDefault(connection, new RouteNode<>(connection));
        allNodes.put(connection, nextNode);

        double newScore = next.getRouteScore() + nextNodeScorer.computeCost(next.getCurrent(), connection);
        if (newScore < nextNode.getRouteScore()) {
          nextNode.setPrevious(next.getCurrent());
          nextNode.setRouteScore(newScore);
          nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to));
          openSet.add(nextNode);
        }
      });
    }

    throw new IllegalStateException("No route found");
  }
}
