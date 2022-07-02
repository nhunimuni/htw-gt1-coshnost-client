package clustering;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class DBSCANApache {
	
	public List<DataPoint> points;
	double maxRadius;
	int minPoints;
	
	public DBSCANApache(double maxRadius, int minPoints) {
		this.points = new ArrayList<DataPoint>();
		this.maxRadius = maxRadius;
		this.minPoints = minPoints;
	}
	
	public List<Cluster<DataPoint>> cluster(ArrayList<DataPoint> p) {
		this.points = p;
		//System.out.println(p.get(10).getPoint()[0]);
		
		DBSCANClusterer<DataPoint> dbscan = new DBSCANClusterer<DataPoint>(maxRadius, minPoints, new DistanceMeasure() {
			@Override
			public double compute(double[] arg0, double[] arg1) throws DimensionMismatchException {
				double ac = Math.abs(arg1[1] - arg0[1]);
			    double cb = Math.abs(arg1[0] - arg1[0]);
			        
			    return Math.hypot(ac, cb);
			}
        });
        List<Cluster<DataPoint>> cluster = dbscan.cluster(points);   

        System.out.println("\n############ Number of Cluster: " + cluster.size());
        for (Cluster<?> c : cluster) {
        	plotCluster(c, false);
        }
        
        return cluster;
	}
	
	public void plotCluster(Cluster c, boolean printPoints) {
		System.out.println("\n############ [Cluster Size: " + c.getPoints().size()+ "]");
		
		if(printPoints) {
			System.out.println("\nPoints:");
			for (int i = 0; i < c.getPoints().size(); i++) {
				System.out.println("(X: " + (((DataPoint) c.getPoints().get(i)).getPoint())[0] + "), (" + "Y: " +(((DataPoint) c.getPoints().get(i)).getPoint())[1] + ")");
			}
		}
	}
 

    /*public static void main(String[] args) throws Exception {

        //List<DataPoint> points = new ArrayList<DataPoint>();
        //for (int i = 0; i < 100; i++)
        //    points.add(new DoublePoint(new double[] { 5 + Math.random(), 5 + Math.random() }));
        //for (int i = 0; i < 100; i++)
        //    points.add(new DoublePoint(new double[] { 15 + Math.random(), 15 + Math.random() }));

        //DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(1, 5);
        DBSCANClusterer<DataPoint> dbscan = new DBSCANClusterer<DataPoint>(1, 5, new DistanceMeasure() {
			@Override
			public double compute(double[] arg0, double[] arg1) throws DimensionMismatchException {
				double ac = Math.abs(arg1[1] - arg0[1]);
			    double cb = Math.abs(arg1[0] - arg1[0]);
			        
			    return Math.hypot(ac, cb);
			}
        });
        List<Cluster> cluster = dbscan.cluster(points);

        System.out.println(cluster.size());
        for (Cluster c : cluster) {
            System.out.println(c.getPoints().get(0));
        }
    }*/
}