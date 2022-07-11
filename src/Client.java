import aStar.AvoidEnemyScorer;
import aStar.ConfrontEnemyScorer;
import aStar.RouteFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import clustering.ClusterExtension;
import clustering.DBSCANApache;
import clustering.DataFilter;
import clustering.DataPoint;
import lenz.htw.coshnost.net.NetworkClient;
import lenz.htw.coshnost.world.GraphNode;
import util.Tuple;

public class Client {
  // Start Server: java -Djava.library.path=lib/native -jar coshnost.jar

  public static void main(String[] args) {
    NetworkClient client = new NetworkClient(null, "Minu", "uwu");
    int myNumber = client.getMyPlayerNumber();
    System.out.println(myNumber);
    RouteFinder avoidingRouteFinder = new RouteFinder(new AvoidEnemyScorer());
    RouteFinder confrontingRouteFinder = new RouteFinder(new ConfrontEnemyScorer());

    double radius = 0.05;
    DBSCANApache DBSCAN = new DBSCANApache(radius, 5);
    DataFilter FILTER = new DataFilter();
    long recentId = -1;

    ArrayList<ArrayList<DataPoint>> allFilteredDataTypes0 = new ArrayList<>();
    ArrayList<ArrayList<DataPoint>> allFilteredDataTypes1 = new ArrayList<>();
    Tuple targetForBot0 = new Tuple(); // One colored - fast
    Tuple targetForBot1 = new Tuple(); // Dotted - strong but slow
    Tuple targetForBot2 = new Tuple(); // Striped - Deletion

    while (client.isGameRunning()) {
      long currentUpdateId = client.getMostRecentUpdateId();
      if (currentUpdateId != recentId) {
        recentId = currentUpdateId;
        GraphNode[] graph = client.getGraph();

        if (recentId % 80 == 1) {
          allFilteredDataTypes0 = FILTER.getAllTypeOfNodes(myNumber, graph);
        }
        if (recentId % 120 == 1) {
          allFilteredDataTypes1 = FILTER.getAllTypeOfNodes(myNumber, graph);
        }

        if (recentId > 0 && allFilteredDataTypes0.size() > 0 && allFilteredDataTypes1.size() > 0) {
          GraphNode bot0Node = findGraphNodeByPosition(graph, client.getBotPosition(myNumber, 0));
          GraphNode bot1Node = findGraphNodeByPosition(graph, client.getBotPosition(myNumber, 1));
          GraphNode bot2Node = findGraphNodeByPosition(graph, client.getBotPosition(myNumber, 2));
          List<ClusterExtension> occupiedClusters = DBSCAN.cluster(allFilteredDataTypes0.get(2));
          List<ClusterExtension> emptyClusters = DBSCAN.cluster(allFilteredDataTypes0.get(3));

          List<ClusterExtension> opponentClusters1 = DBSCAN.cluster(allFilteredDataTypes1.get(1));

          // BOT 0
          // Mainly empty cluster but should also target occupied cluster!
          if (bot0Node != null && targetForBot0.route.size() == 0) {
            List<ClusterExtension> joinedCluster = Stream.concat(occupiedClusters.stream(), emptyClusters.stream())
                .toList();
            Tuple plannedMove = planMove(myNumber, graph, joinedCluster, avoidingRouteFinder, bot0Node, targetForBot0);
            targetForBot0.cluster = plannedMove.cluster;
            targetForBot0.route = plannedMove.route;
          }

          // BOT 1 - Strong but slow
          // opponnent cluster
          if (bot1Node != null && targetForBot1.route.size() == 0) {
            Tuple plannedMove = planMove(myNumber, graph, opponentClusters1, confrontingRouteFinder, bot1Node,
                targetForBot1);
            targetForBot1.cluster = plannedMove.cluster;
            targetForBot1.route = plannedMove.route;
          }

          // BOT 2 - Deletion
          // Follows enemy positions
          if (bot2Node != null && targetForBot2.route.size() == 0) {
            int[] players = new int[] { 0, 1, 2 };
            players = Arrays.stream(players).filter(player -> player != myNumber).toArray();
            int enemy1Score = client.getScore(players[0]);
            int enemy2Score = client.getScore(players[1]);
            int winningEnemy = players[0];
            if (enemy1Score < enemy2Score) {
              winningEnemy = players[1];
            }
            if (recentId % 50 == 1) {
              float[] fastBot = client.getBotPosition(winningEnemy, 0);
              targetForBot2.route = confrontingRouteFinder.findRoute(myNumber, bot2Node,
                  findGraphNodeByPosition(graph, fastBot), false);
            }
          }
        }

        if (targetForBot0.route.size() > 0) {
          setMove(targetForBot0, client, 0);
        }

        if (targetForBot1.route.size() > 0) {
          setMove(targetForBot1, client, 1);
        }

        if (targetForBot2.route.size() > 0) {
          setMove(targetForBot2, client, 2);
        }
      }
    }
  }

  /**
   * @param graph
   * @param clusters
   * @param routeFinder
   * @param botStartNode
   * @param botTargetNode
   * @return
   */
  public static Tuple planMove(int myNumber, GraphNode[] graph, List<ClusterExtension> clusters,
      RouteFinder routeFinder,
      GraphNode botStartNode, Tuple botTargetNode) {
    Tuple targetNode = new Tuple();
    clusters.forEach(cluster -> {
      List<Float> centroidList = cluster.getCentroid();
      float[] centroidArray = new float[centroidList.size()];
      for (int n = 0; n < centroidList.size(); n++) {
        centroidArray[n] = centroidList.get(n);
      }
      GraphNode center = findGraphNodeByPosition(graph, centroidArray);
      List<GraphNode> route;
      route = routeFinder.findRoute(myNumber, botStartNode, center, false);

      // Find cluster nearest to our bot
      if (botTargetNode.route.size() == 0 || route.size() < botTargetNode.route.size()) {
        targetNode.cluster = cluster;
        targetNode.route = route;
      }
    });

    return targetNode;
  }

  /**
   * @param graph
   * @param clusters
   * @param routeFinder
   * @param botStartNode
   * @param botTargetNode
   * @return
   */
  public static GraphNode getClosestCentroid(GraphNode[] graph, List<ClusterExtension> clusters,
      GraphNode botStartNode) {
    float[] centerPos = new float[3];
    double currentClosestCentroid = Double.POSITIVE_INFINITY;
    for (int i = 0; i < clusters.size(); i++) {
      List<Float> centroidList = clusters.get(i).getCentroid();
      float[] centroidArray = new float[centroidList.size()];
      for (int n = 0; n < centroidList.size(); n++) {
        centroidArray[n] = centroidList.get(n);
      }
      double d = distance(botStartNode.getPosition(), centroidArray);
      if (Math.min(currentClosestCentroid, d) == d) {
        centerPos = centroidArray;
        currentClosestCentroid = d;
      }
    }
    GraphNode center = findGraphNodeByPosition(graph, centerPos);
    return center;
  }

  /**
   * Set move for bot.
   */
  public static void setMove(Tuple target, NetworkClient client, int botNr) {
    GraphNode nextNode = target.route.get(0);
    if (nextNode.isBlocked()) {
      System.out.println("BLOCKED");
      GraphNode[] n = nextNode.getNeighbors();
      GraphNode nextUnblockedNode = null;
      boolean loopStop = true;
      int counter = 0;
      while (loopStop) {
        if (!n[counter].isBlocked()) {
          nextUnblockedNode = n[counter];
          loopStop = false;
        }
      }
      System.out.println(nextUnblockedNode.toString());
      client.changeMoveDirection(botNr, nextUnblockedNode.x, nextUnblockedNode.y, nextUnblockedNode.z);
    } else {
      client.changeMoveDirection(botNr, nextNode.x, nextNode.y, nextNode.z);
    }
    target.route.remove(0);
  }

  /**
   * Find a matching GraphNode on the graph net by the position
   *
   * @param graph
   * @param position
   * @return
   */
  public static GraphNode findGraphNodeByPosition(GraphNode[] graph, float[] position) {
    return Arrays.stream(graph).filter(a -> !a.blocked)
        .min(Comparator.comparingDouble(a -> distance(position, a.getPosition()))).orElse(null);
  }

  /**
   * Euclidean Distance 3D
   *
   * @param arg0
   * @param arg1
   * @return
   */
  private static double distance(float[] arg0, float[] arg1) {
    double a = Math.pow(arg1[0] - arg0[0], 2);
    double b = Math.pow(arg1[1] - arg0[1], 2);
    double c = Math.pow(arg1[2] - arg0[2], 2);
    return Math.sqrt(a + b + c);
  }
}
