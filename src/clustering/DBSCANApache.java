package clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

//https://www.demo2s.com/java/apache-commons-dbscanclusterer-tutorial-with-examples.html
public class DBSCANApache {

	public List<DataPoint> points;
	double maxRadius;
	int minPoints;

	public DBSCANApache(double maxRadius, int minPoints) {
		this.points = new ArrayList<DataPoint>();
		this.maxRadius = maxRadius;
		this.minPoints = minPoints;
	}

	public List<ClusterExtension> cluster(ArrayList<DataPoint> p) {
		this.points = p;

		DBSCANClusterer<DataPoint> dbscan = new DBSCANClusterer<DataPoint>(maxRadius, minPoints, new DistanceMeasure() {
			@Override
			public double compute(double[] arg0, double[] arg1) throws DimensionMismatchException {
				// euclidean distance 3d
				double a = Math.pow(arg1[0] - arg0[0], 2);
				double b = Math.pow(arg1[1] - arg0[1], 2);
				double c = Math.pow(arg1[2] - arg0[2], 2);

				return Math.sqrt(a + b + c);
			}
		});

		List<Cluster<DataPoint>> clusterGroups = dbscan.cluster(points);
		List<ClusterExtension> cluster = new ArrayList<ClusterExtension>();
		for (Cluster<DataPoint> c : clusterGroups) {
			// Set the DataPoint with the highest density as representative of the cluster
			// DataPoint representative = getNodeWithHighestDensity(c);
			// cluster.add(new ClusterExtension(representative,c));

			// Get the centroid of a cluster
			cluster.add(new ClusterExtension(getCentroidPosition(c), c));
		}

		// System.out.println("\n############ Number of Cluster: " + cluster.size());
		for (Cluster<DataPoint> c : clusterGroups) {
			// plotCluster(c, false);
		}

		return cluster;
	}

	public void plotCluster(Cluster<DataPoint> c, boolean printPoints) {
		System.out.println("\n############ [Cluster Size: " + c.getPoints().size() + "]");

		if (printPoints) {
			System.out.println("\nPoints:");
			for (int i = 0; i < c.getPoints().size(); i++) {
				System.out.println("(X: " + (((DataPoint) c.getPoints().get(i)).getPoint())[0] + "), (" + "Y: "
						+ (((DataPoint) c.getPoints().get(i)).getPoint())[1] + ")");
			}
		}
	}

	public List<Float> getCentroidPosition(Cluster<DataPoint> c) {
		float centroidX = 0, centroidY = 0, centroidZ = 0;
		List<Float> centroid = new ArrayList<Float>();

		for (DataPoint d : c.getPoints()) {
			centroidX += d.getNode().getX();
			centroidY += d.getNode().getY();
			centroidZ += d.getNode().getZ();
		}
		centroid.add(centroidX / c.getPoints().size());
		centroid.add(centroidY / c.getPoints().size());
		centroid.add(centroidZ / c.getPoints().size());

		return centroid;
	}

	// Algorithm described in
	// https://www.researchgate.net/figure/Median-center-selection-based-on-the-DBSCAN-cluster-results-of-vertex-point_fig6_358838471
	// would be ideal
	// Get DataPoint with the highest density as representative of the cluster
	/*
	 * public DataPoint getNodeWithHighestDensity(Cluster<DataPoint> c) {
	 * for (DataPoint p : c.getPoints()) {
	 * GraphNode[] n = p.getNode().getNeighbors();
	 * System.out.println(n.length);
	 * System.out.println(Arrays.toString(n));
	 * }
	 * return null;
	 * }
	 */
}
