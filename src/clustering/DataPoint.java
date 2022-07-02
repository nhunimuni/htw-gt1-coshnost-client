package clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

import lenz.htw.coshnost.world.GraphNode;

public class DataPoint implements Clusterable{
	private GraphNode node;
	
	public DataPoint(GraphNode node) {
		this.node = node;
	}
	
	public GraphNode getNode(){
		return node;
	}

	@Override
	public double[] getPoint() {
		double[] p = new double[]{node.getX(), node.getY()};
		return p;
	};
}
