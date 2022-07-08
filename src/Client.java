import aStar.DefaultScorer;
import aStar.RouteFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import clustering.ClusterExtension;
import clustering.DBSCANApache;
import clustering.DataFilter;
import clustering.DataPoint;
import java.util.Optional;
import lenz.htw.coshnost.net.NetworkClient;
import lenz.htw.coshnost.world.GraphNode;
import util.Tuple;

public class Client {

  public static void main(String[] args) {

    // Start Server: java -Djava.library.path=lib/native -jar coshnost.jar

    NetworkClient client = new NetworkClient(null, "Teamname", "Juhu, ich habe gewonnen!");

    int myNumber = client.getMyPlayerNumber();

    RouteFinder routeFinder = new RouteFinder(new DefaultScorer());

    double radius = 0.05;
    DBSCANApache DBSCAN = new DBSCANApache(radius, 5);
    /*
     * DBSCANApache DBSCAN1 = new DBSCANApache(radius, 5);
     * DBSCANApache DBSCAN2 = new DBSCANApache(radius, 5);
     * DBSCANApache DBSCAN3 = new DBSCANApache(radius, 5);
     */
    DBSCANApache PITHOLES = new DBSCANApache(0.09, 5);
    DataFilter FILTER = new DataFilter();
    long recentId = -1;
    while (client.isGameRunning()) {
      long currentUpdateId = client.getMostRecentUpdateId();

      if (currentUpdateId != recentId) {
        recentId = currentUpdateId;
        GraphNode[] graph = client.getGraph();
        ArrayList<ArrayList<DataPoint>> allFilteredDataTypes = new ArrayList<>();
        Tuple targetForBot0 = new Tuple();

        if (recentId % 50 == 0) {
          allFilteredDataTypes = FILTER.getAllTypeOfNodes(myNumber, graph);

          System.out.println("route" + targetForBot0.route.toString());
          if (targetForBot0.cluster != null) {
            System.out.println("centroid" + targetForBot0.cluster.getCentroid().toString());
          }
        }

        if (recentId > 0 && allFilteredDataTypes.size() > 0 && targetForBot0.cluster == null) {
          float[] bot0 = client.getBotPosition(myNumber, 0);
          GraphNode bot0Node = findGraphNodeByPosition(graph, bot0);
          if (bot0.length > 0) {
            System.out.println("bot0: " + Arrays.toString(bot0));
          }
          List<ClusterExtension> emptyClusters = DBSCAN.cluster(allFilteredDataTypes.get(3));
          if (bot0Node != null && emptyClusters.size() > 0) {
            System.out.println("I'm in! " + bot0Node);
            emptyClusters.forEach(cluster -> {
              List<Float> centroidList = cluster.getCentroid();
              float[] centroidArray = new float[centroidList.size()];
              // System.out.println("Centroid Array: " + Arrays.toString(centroidArray));
              for (int n = 0; n < centroidList.size(); n++) {
                centroidArray[n] = centroidList.get(n);
              }
              GraphNode center = findGraphNodeByPosition(graph, centroidArray);
              System.out.println("Center: " + Arrays.toString(center.getPosition()));
              List<GraphNode> route;
              if (center != null) {
                route = routeFinder.findRoute(bot0Node, center);
                if (targetForBot0.route.size() == 0 || route.size() < targetForBot0.route.size()) {
                  targetForBot0.cluster = cluster;
                  targetForBot0.route = route;
                }
              }
            });
          }
        }

        if (targetForBot0.route.size() > 0) {
          GraphNode nextNode = targetForBot0.route.get(0);
          client.changeMoveDirection(0, nextNode.x, nextNode.y, nextNode.z);
          targetForBot0.route.remove(0);
        }
      }
    }
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
