package aStar;

import lenz.htw.coshnost.world.GraphNode;

class RouteNode implements Comparable<RouteNode> {

  private final GraphNode current;
  private GraphNode previous;
  private double routeScore;
  private double estimatedScore;

  RouteNode(GraphNode current) {
    this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  RouteNode(GraphNode current, GraphNode previous, double routeScore, double estimatedScore) {
    this.current = current;
    this.previous = previous;
    this.routeScore = routeScore;
    this.estimatedScore = estimatedScore;
  }

  @Override
  public int compareTo(RouteNode other) {
    if (this.estimatedScore > other.estimatedScore) {
      return 1;
    } else if (this.estimatedScore < other.estimatedScore) {
      return -1;
    } else {
      return 0;
    }
  }

  public GraphNode getCurrent() {
    return current;
  }

  public void setPrevious(GraphNode previous) {
    this.previous = previous;
  }

  public GraphNode getPrevious() {
    return previous;
  }

  public void setRouteScore(double routeScore) {
    this.routeScore = routeScore;
  }

  public double getRouteScore() {
    return routeScore;
  }

  public void setEstimatedScore(double estimatedScore) {
    this.estimatedScore = estimatedScore;
  }

  public double getEstimatedScore() {
    return estimatedScore;
  }
}
