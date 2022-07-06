package clustering;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.coshnost.world.GraphNode;

public class DataFilter {
	
	/**
	 * Get our own nodes and nodes which we own by a few percentages.
	 * Used by the deletion bot (2).
	 * @return
	 */
	public ArrayList<DataPoint> getPlayersNodes(int playerNr, GraphNode[] worldGraph) {
		ArrayList<DataPoint> p = new ArrayList<DataPoint>();
		for (GraphNode node: worldGraph) {
			if (node.getOwner() != -1) {
				if (node.getOwner() == playerNr) {
					p.add(new DataPoint(node));
				} else if (node.ownership[playerNr] <= 0.5 && node.ownership[playerNr] >= 0.1) {
					p.add(new DataPoint(node));
				}
			};		
		}
		return p;
	}
	
	/**
	 * Get nodes owned only by the opponents.
	 * @return
	 */
	public ArrayList<DataPoint> getOpponentsNodes(int playerNr, GraphNode[] worldGraph) {
		ArrayList<DataPoint> p = new ArrayList<DataPoint>();
		for (GraphNode node: worldGraph) {
			if (node.getOwner() != -1 && node.getOwner() != playerNr) {
				p.add(new DataPoint(node));
			};		
		}
		return p;
	}
	
	/**
	 * Get nodes owned by the opponents and nodes which we own by a few percentages.
	 * @return
	 */
	public ArrayList<DataPoint> getOccupiedNodes(int playerNr, GraphNode[] worldGraph) {
		ArrayList<DataPoint> p = new ArrayList<DataPoint>();
		for (GraphNode node: worldGraph) {
			if (node.getOwner() != -1) {
				if (node.getOwner() != playerNr && node.ownership[playerNr] <= 0.5 && node.ownership[playerNr] >= 0.1) {
					p.add(new DataPoint(node));
				} else {
					p.add(new DataPoint(node));
				}
			};		
		}
		System.out.println(p.size());
		return p;
	}
	
	/**
	 * Get blank nodes.
	 * @return
	 */
	public ArrayList<DataPoint> getBlankNodes(GraphNode[] worldGraph) {
		ArrayList<DataPoint> p = new ArrayList<DataPoint>();
		for (GraphNode node: worldGraph) {
			if (node.getOwner() == -1) {
				p.add(new DataPoint(node));
			};		
		}
		return p;
	}
	
	/**
	 * Get pitholes.
	 * @return
	 */
	public ArrayList<DataPoint> getPitholeNodes(GraphNode[] worldGraph) {
		ArrayList<DataPoint> p = new ArrayList<DataPoint>();
		for (GraphNode node: worldGraph) {
			if (node.blocked) {
				p.add(new DataPoint(node));
			};		
		}
		return p;
	}
	
	/**
	 * Get an array consisting of all the type of nodes.
	 * - Index 0: Our players occupied nodes
	 * - Index 1: Solely occupied nodes of the opponents all together
	 * - Index 2: Occupied nodes of the opponents and nodes we own by a few percentages
	 * - Index 3: Blank nodes
	 * - Index 4: Pithole nodes
	 * @return ArrayList<ArrayList<DataPoint>>
	 */
	public ArrayList<ArrayList<DataPoint>> getAllTypeOfNodes(int playerNr, GraphNode[] worldGraph) {
		ArrayList<DataPoint> playersP = new ArrayList<DataPoint>();
		ArrayList<DataPoint> opponentP = new ArrayList<DataPoint>();
		ArrayList<DataPoint> occupiedP = new ArrayList<DataPoint>();
		ArrayList<DataPoint> blankP = new ArrayList<DataPoint>();
		ArrayList<DataPoint> pitholeP = new ArrayList<DataPoint>();
		
		for (GraphNode node: worldGraph) {
			if (node.getOwner() != -1) {
				if (!node.blocked) {
					// 0
					if (node.getOwner() == playerNr) {
						playersP.add(new DataPoint(node));
					} else if (node.ownership[playerNr] <= 0.5 && node.ownership[playerNr] >= 0.1) {
						playersP.add(new DataPoint(node));
					}
					else {
						// 1
						opponentP.add(new DataPoint(node));
					}	
					
					// 2
					if (node.getOwner() != playerNr) {
						occupiedP.add(new DataPoint(node));
					} else if (node.ownership[playerNr] <= 0.5 && node.ownership[playerNr] >= 0.1) {
						occupiedP.add(new DataPoint(node));
					}	
				}
			} else {
				// 3
				blankP.add(new DataPoint(node));
			};	
			
			// 4
			if (node.blocked) {
				pitholeP.add(new DataPoint(node));
			};
		}
		
		return new ArrayList<ArrayList<DataPoint>>(List.of(playersP, opponentP, occupiedP, blankP, pitholeP));
	}
}
