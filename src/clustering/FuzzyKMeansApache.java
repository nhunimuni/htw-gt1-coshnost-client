package clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;

// https://www.demo2s.com/java/apache-commons-fuzzykmeansclusterer-fuzzykmeansclusterer-fin-cbqf.html
// https://en.wikipedia.org/wiki/Determining_the_number_of_clusters_in_a_data_set
public class FuzzyKMeansApache {

	public List<DataPoint> points;
	int clusterSize;
	double fuzziness;

	public FuzzyKMeansApache(int clusterSize, double fuzziness) {
		this.points = new ArrayList<DataPoint>();
		this.clusterSize = clusterSize;
		this.fuzziness = fuzziness;
	}

	public List<CentroidCluster<DataPoint>> cluster(ArrayList<DataPoint> p) {
		this.points = p;

		FuzzyKMeansClusterer<DataPoint> fuzzyKMeansClusterer = new FuzzyKMeansClusterer<>(this.clusterSize, fuzziness);
		List<CentroidCluster<DataPoint>> cluster = fuzzyKMeansClusterer.cluster(points);

		System.out.println("\n############ Number of Cluster: " + clusterSize);
		for (CentroidCluster<DataPoint> c : cluster) {
			plotCluster(c, false);
		}

		return cluster;
	}

	public void plotCluster(CentroidCluster<DataPoint> c, boolean printPoints) {
		System.out.println("\n############ [Cluster Size: " + c.getPoints().size() + "]");

		if (printPoints) {
			System.out.println("\nPoints:");
			for (int i = 0; i < c.getPoints().size(); i++) {
				System.out.println("(X: " + (((DataPoint) c.getPoints().get(i)).getPoint())[0] + "), (" + "Y: "
						+ (((DataPoint) c.getPoints().get(i)).getPoint())[1] + ")");
			}
		}
	}
}
