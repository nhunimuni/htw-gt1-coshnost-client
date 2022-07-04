/*package clustering;

import java.util.ArrayList;
 
public class Cluster {
	
	public ArrayList<DataPoint> points;
	public DataPoint centroid;
	public int id;
	
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<DataPoint>();
		this.centroid = null;
	}
 
	public ArrayList<DataPoint> getPoints() {
		return points;
	}
	
	public void addPoint(DataPoint point) {
		points.add(point);
		point.setCluster(id);
	}
 
	public void setPoints(ArrayList<DataPoint> points) {
		this.points = points;
	}
 
	public DataPoint getCentroid() {
		return centroid;
	}
 
	public void setCentroid(DataPoint centroid) {
		this.centroid = centroid;
	}
 
	public int getId() {
		return id;
	}
	
	public void clear() {
		points.clear();
	}
	
	public void plotCluster() {
		System.out.println("[Cluster: " + id+"]");
		System.out.println("[Centroid: " + centroid + "]");
		System.out.println("[Points: \n");
		for(DataPoint p : this.points) {
			System.out.println(p);
		}
		System.out.println("]");
	}
 
}*/
