public class aStar {

  public static void main(String[] args) {
    Queue<RouteNode> openSet = new PriorityQueue<>();
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

      throw new IllegalStateException("No route found");
    }
  }
}

public interface GraphNode {

  String getId();
}

public class Graph<T extends GraphNode> {

  private final Set<T> nodes;
  private final Map<String, Set<String>> connections;

  public T getNode(String id) {
    return nodes.stream()
        .filter(node -> node.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No node found with ID"));
  }

  public Set<T> getConnections(T node) {
    return connections.get(node.getId()).stream()
        .map(this::getNode)
        .collect(Collectors.toSet());
  }
}

public interface Scorer<T extends GraphNode> {

  double computeCost(T from, T to);
}

class RouteNode<T extends GraphNode> implements Comparable<RouteNode> {

  private final T current;
  private T previous;
  private double routeScore;
  private double estimatedScore;

  RouteNode(T current) {
    this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  RouteNode(T current, T previous, double routeScore, double estimatedScore) {
    this.current = current;
    this.previous = previous;
    this.routeScore = routeScore;
    this.estimatedScore = estimatedScore;
  }
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

public class RouteFinder<T extends GraphNode> {

  private final Graph<T> graph;
  private final Scorer<T> nextNodeScorer;
  private final Scorer<T> targetScorer;

  public List<T> findRoute(T from, T to) {
    throw new IllegalStateException("No route found");
  }
}
