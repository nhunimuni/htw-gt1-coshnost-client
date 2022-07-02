import java.util.ArrayList;

import clustering.ClusteringThread;
import clustering.DBSCAN;
import clustering.DBSCANApache;
import clustering.DataPoint;
import lenz.htw.coshnost.net.NetworkClient;
import lenz.htw.coshnost.world.GraphNode;

public class Client {

  public static void main(String[] args) {
	  
	//Start Server: java -Djava.library.path=lib/native -jar coshnost.jar

    NetworkClient client = new NetworkClient(null, "Teamname", "Juhu, ich habe gewonnen!");

    int myNumber = client.getMyPlayerNumber();
    //client.getScore(0);
    //client.getBotSpeed(0);    //Konstanten
    
    //float[] botDirection = client.getBotDirection(0); // vermutlich nicht notwendig
    //client.changeMoveDirection(0, 0); // vermutlich nicht notwendig 
    //System.out.println(recentUpdate);
    //System.out.println(myNumber);

    GraphNode[] graph = client.getGraph(); // immer Größe 10242
    /*System.out.println(graph[0].isBlocked()); // ist ein Graben oder nicht
    System.out.println(graph[0].getOwner()); // wem gehört das Feld? (0,1,2 = player number, -1 = leer)
    System.out.println(graph[0].getX());*/ // Ortsvektor
    float[] pos = graph[0].getPosition();

    GraphNode[] neighbors = graph[0].getNeighbors();    //immer genau 5 oder 6 Nachbarn
 
    DBSCANApache DBSCAN = new DBSCANApache(1, 5);
    DBSCANApache DBSCANBlank = new DBSCANApache(1, 5);
    long recentId = -1;
    while (client.isGameRunning()) {
    	long currentUpdateId = client.getMostRecentUpdateId();
    	client.changeMoveDirection(0, (float) -0.5276859402656555, (float) -0.5276859402656555, (float) 0.0);
    	client.changeMoveDirection(1, (float) -0.5276859402656555, (float) -0.5276859402656555, (float) 0.0);
    	client.changeMoveDirection(2, (float) -0.5276859402656555, (float) -0.5276859402656555, (float) 0.0);
    	
    	if (currentUpdateId != recentId) {
    		recentId = currentUpdateId;
    		GraphNode[] netGraph = client.getGraph();
    		//getNumberOfFreeFields(netGraph);
    		
    		if (recentId == 10) {
    			
    			// CLUSTERING OF THE OCCUPPIED NODES OF THE OTHER PLAYERS
        		ArrayList<DataPoint> p = new ArrayList<DataPoint>();
        		for (GraphNode node: netGraph) {
        			if (node.getOwner() != -1 && node.getOwner() != myNumber) {
        				p.add(new DataPoint(node));
        			};		
        		}
        		//DBSCAN.cluster(p);
        		
        		// BLANK NODES
        		ArrayList<DataPoint> p2 = new ArrayList<DataPoint>();
        		for (GraphNode node: netGraph) {
        			if (node.getOwner() == -1) {
        				p2.add(new DataPoint(node));
        			};		
        		}
        		DBSCANBlank.cluster(p2);
        		
    		}
    	}
    }  
    
  }
  
  public static int getNumberOfFreeFields(GraphNode[] netGraph) {
  	int free = 0;
  	for (GraphNode node : netGraph) {
  		if (node.getOwner() == -1)
  			free++;
  		}
  	System.out.println("UPDATED GRAPH: free: " + free);
  	return free;
  }

  /*
  Bot 0 (einfarbig) hat die höchste Geschwindigkeit, erhöht eigenen Farbanteil
  Bot 1 (gepunktet) ist am langsamsten, setzt eigenen Farbanteil hoch
  Bot 2 (gestreift) kann sich über Gräben bewegen, löscht alle Farben inklusive der eigenen
   */

  /*public static GraphNode aStar(GraphNode start, GraphNode target) {
    PriorityQueue<GraphNode> closedList = new PriorityQueue<>();
    PriorityQueue<GraphNode> openList = new PriorityQueue<>();

    openList.add(start);

    while (!openList.isEmpty()) {
      GraphNode n = openList.peek();
      if (n == target) {
        return n;
      }

      for (GraphNode node : n.getNeighbors()) {
        // TODO
        // Bot 0 -> sucht nach leeren Feldern (je mehr auf einem Haufen, desto besser)
        // Bot 1 -> sucht nach leeren & von Gegnern gefüllten Feldern (je näher, desto besser)
        // Bot 2 -> sucht nach von Gegnern gefüllten Feldern (je mehr auf einem Haufen, desto besser)
      }

      openList.remove(n);
      closedList.add(n);
    }
    return null;
  }*/
}