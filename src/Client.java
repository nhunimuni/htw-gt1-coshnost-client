import aStar.DefaultScorer;
import aStar.RouteFinder;
import java.util.ArrayList;
import java.util.Arrays;
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

    double radius = 0.04;
    DBSCANApache DBSCAN = new DBSCANApache(radius, 5);
    /*DBSCANApache DBSCAN1 = new DBSCANApache(radius, 5);
    DBSCANApache DBSCAN2 = new DBSCANApache(radius, 5);
    DBSCANApache DBSCAN3 = new DBSCANApache(radius, 5);*/
    DBSCANApache PITHOLES = new DBSCANApache(0.09, 5);
    // FuzzyKMeansApache FuzzyKMeans = new FuzzyKMeansApache(1, 2);
    DataFilter FILTER = new DataFilter();
    long recentId = -1;
    while (client.isGameRunning()) {
      long currentUpdateId = client.getMostRecentUpdateId();
      client.changeMoveDirection(0, (float) 0.9703449321829755, (float) 0.026010560276715652, (float) 0.6594838045145336);
      client.changeMoveDirection(1, (float) -0.36492302185959286, (float) 0.4430104684498575, (float) -0.3259028345346451);
      client.changeMoveDirection(2, (float) 0.0, (float) -0.5254689455032349, (float) -0.8059597046751725);

      if (currentUpdateId != recentId) {
        recentId = currentUpdateId;
        GraphNode[] graph = client.getGraph();

        ArrayList<ArrayList<DataPoint>> allFilteredDataTypes = new ArrayList<>();
        final Tuple targetForBot0 = new Tuple();

        if (recentId % 100 == 1) {
          /*
           * ArrayList<DataPoint> playersP = FILTER.getPlayersNodes(myNumber, netGraph);
           * ArrayList<DataPoint> opponentP = FILTER.getOpponentsNodes(myNumber,
           * netGraph);
           * ArrayList<DataPoint> occupiedP = FILTER.getOccupiedNodes(myNumber, netGraph);
           * ArrayList<DataPoint> blankP = FILTER.getBlankNodes(netGraph);
           */
          allFilteredDataTypes = FILTER.getAllTypeOfNodes(myNumber, graph);

          // System.out.println("\nMy Number: " + myNumber + "\n");
          // --------- DBSCAN- Measure execution time - start
          long startTime = System.nanoTime();
          List<ClusterExtension> dbscanCluster = DBSCAN.cluster(allFilteredDataTypes.get(0));
          if (dbscanCluster.size() > 0) {
            // System.out.println("Centroid of Cluster 0: " + dbscanCluster.get(0).getCentroid());
          }
          //List<ClusterExtension> dbscanCluster1 = DBSCAN.cluster(allFilteredDataTypes.get(1));
          //List<ClusterExtension> dbscanCluster2 = DBSCAN.cluster(allFilteredDataTypes.get(2));
          //<ClusterExtension> dbscanCluster3 = DBSCAN.cluster(allFilteredDataTypes.get(3));
          //<ClusterExtension> dbscanCluster4 = PITHOLES.cluster(allFilteredDataTypes.get(4));

          // List<Cluster<DataPoint>> dbscanCluster2 = DBSCANBlank.cluster(p2);
          long endTime = System.nanoTime();
          long duration = (endTime - startTime);
          // System.out.println("DBSCAN Elapsed Time in nano seconds: " + duration);
          // --------- DBSCAN- Measure execution time - end

          System.out.println("route" + targetForBot0.route.toString());
          if (targetForBot0.cluster != null) {
            System.out.println("centroid" + targetForBot0.cluster.getCentroid().toString());
          }
        }

        if (recentId > 0 && allFilteredDataTypes.size() > 0 && targetForBot0.cluster == null) {
          float[] bot0 = client.getBotPosition(myNumber, 0);
          Optional<GraphNode> bot0Node = findGraphNodeByPosition(graph, bot0);
          if (bot0.length > 0) {
            System.out.println("bot0" + Arrays.toString(bot0));
            System.out.println("bot0 node" + bot0Node);
          }
          List<ClusterExtension> emptyClusters = DBSCAN.cluster(allFilteredDataTypes.get(3));
          if (bot0Node.isPresent() && emptyClusters.size() > 0) {
            System.out.println("i'm in!" +  bot0Node);
            emptyClusters.forEach(cluster -> {
              List<Number> centroidList = cluster.getCentroid();
              float[] centroidArray = new float[]{centroidList.get(0).floatValue(), centroidList.get(1).floatValue(), centroidList.get(2).floatValue()};
              Optional<GraphNode> center = findGraphNodeByPosition(graph, centroidArray);
              List<GraphNode> route;
              if (center.isPresent()) {
                route = routeFinder.findRoute(bot0Node.get(), center.get());
                if (targetForBot0.route.size() == 0 || route.size() < targetForBot0.route.size()) {
                  targetForBot0.cluster = cluster;
                  targetForBot0.route = route;
                }
              }
            });
          }

          // --------- FuzzyKMeans - Measure execution time - start
          /*
           * long startTime2 = System.nanoTime();
           * List<CentroidCluster<DataPoint>> fuzzyKMansCluster =
           * FuzzyKMeans.cluster(allFilteredDataTypes.get(1));
           * for (CentroidCluster<DataPoint> c : fuzzyKMansCluster) {
           * System.out.println("FuzzyK Center of Cluster " + c.toString() + ": " +
           * Arrays.toString(c.getCenter().getPoint()));
           * }
           * long endTime2 = System.nanoTime();
           * long duration2 = (endTime2 - startTime2);
           * System.out.println("FuzzyK Elapsed Time in nano seconds: " + duration2);
           * //--------- FuzzyKMeans - Measure execution time - end
           */
        }

        if (targetForBot0.route.size() > 0) {
          GraphNode nextNode = targetForBot0.route.get(0);
          client.changeMoveDirection(0, nextNode.x, nextNode.y, nextNode.z);
          targetForBot0.route.remove(0);
        }
      }
    }
  }

  public static Optional<GraphNode> findGraphNodeByPosition(GraphNode[] graph, float[] position) {
    return Arrays.stream(graph).filter(node -> {
      return node.getX() == position[0] && node.getY() == position[1] && node.getZ() == position[2];
    }).findFirst();
  }
}
