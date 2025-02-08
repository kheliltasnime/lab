import java.util.HashSet;
import java.util.Set;

class Node implements Comparable<Node>{
    char letter;
    int x, y;
    boolean isWall;
    boolean isAvailable;
    Set<Node> neighbors; // Utilisation d'un Set pour éviter les doublons
    int distance; // Ajout pour Dijkstra

    public Node(char letter, int x, int y) {
        this.letter = letter;
        this.x = x;
        this.y = y;
        this.isWall = false;
        this.isAvailable = true;
        this.neighbors = new HashSet<>();
        this.distance = Integer.MAX_VALUE;
    }

    public void addNeighbor(Node neighbor) {
        if (!this.isWall && !neighbor.isWall) { // Ne pas connecter les murs
            neighbors.add(neighbor);
        }
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    @Override
    public String toString() {
        return isWall ? "#" : Character.toString(letter);
    }

    public int compareTo(Node other) { // SUPPRESSION de @Override
        return Integer.compare(this.distance, other.distance);
    }

}
