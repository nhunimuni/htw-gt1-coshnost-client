package clustering;

import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
 
public class ClusterExtension {
	// Most dense node
	DataPoint representative;
	List<Number> centroid;
	Cluster<DataPoint> cluster = new Cluster<DataPoint>();
	
	public ClusterExtension(DataPoint representative, Cluster<DataPoint> cluster) {
		this.representative = representative;
		this.cluster = cluster;
	}
	
	public ClusterExtension(List<Number> centroid, Cluster<DataPoint> cluster) {
		this.centroid = centroid;
		this.cluster = cluster;
	}
	
	public DataPoint getRepresentative() {
		return representative;
	}
	
	public List<Number> getCentroid() {
		return centroid;
	}
	
	public Cluster<DataPoint> getCluster() {
		return cluster;
	}
}
