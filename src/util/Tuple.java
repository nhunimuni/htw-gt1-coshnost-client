package util;

import clustering.ClusterExtension;
import java.util.ArrayList;
import java.util.List;
import lenz.htw.coshnost.world.GraphNode;

public class Tuple {
  public ClusterExtension cluster;
  public List<GraphNode> route;
  public Tuple(ClusterExtension cluster, List<GraphNode> route) {
    this.cluster = cluster;
    this.route = route;
  }

  public Tuple() {
    this.route = new ArrayList<>();
  }
}
