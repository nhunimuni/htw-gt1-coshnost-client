import java.util.PriorityQueue;
import lenz.htw.coshnost.net.NetworkClient;
import lenz.htw.coshnost.world.GraphNode;

public class Client {

  public static void main(String[] args) {

    NetworkClient client = new NetworkClient(null, "Teamname", "Juhu, ich habe gewonnen!");

    client.getMyPlayerNumber();

    client.getScore(0);

    client.getBotSpeed(0);    //Konstanten

    float[] botPosition = client.getBotPosition(0, 0);

    float[] botDirection = client.getBotDirection(0); // vermutlich nicht notwendig
    client.changeMoveDirection(0, 0); // vermutlich nicht notwendig

    client.changeMoveDirection(0, 1, 0, 0);

    client.getMostRecentUpdateId(); // wenn verändert, dann neue Daten vom Server da

    GraphNode[] graph = client.getGraph(); // immer Größe 10242
    graph[0].isBlocked(); // ist ein Graben oder nicht
    graph[0].getOwner(); // wem gehört das Feld? (0,1,2 = player number, -1 = leer)
    graph[0].getX(); // Ortsvektor
    float[] pos = graph[0].getPosition();

    GraphNode[] neighbors = graph[0].getNeighbors();    //immer genau 5 oder 6 Nachbarn
  }

  /*
  Bot 0 (einfarbig) hat die höchste Geschwindigkeit, erhöht eigenen Farbanteil
  Bot 1 (gepunktet) ist am langsamsten, setzt eigenen Farbanteil hoch
  Bot 2 (gestreift) kann sich über Gräben bewegen, löscht alle Farben inklusive der eigenen
   */

  public static GraphNode aStar(GraphNode start, GraphNode target) {
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
  }
}