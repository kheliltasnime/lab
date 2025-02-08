import java.util.*;

class Player {
    private Grid grid;
    private Set<String> validWords;
    private int x, y;
    private Set<String> wordsFormed;  // Liste pour les mots formés
    private List<String> pathHistory;  // Liste pour le parcours
    private StringBuilder currentWord;
    private Set<Node> usedNodes;// Suivi des cases utilisées

    public Player(Grid grid, Set<String> validWords, int startX, int startY) {
        this.grid = grid;
        this.validWords = validWords;
        this.x = startX;
        this.y = startY;
        this.wordsFormed = new HashSet<>(); // Utilisation d'un Set pour éviter les doublons

        this.pathHistory = new ArrayList<>();
        this.pathHistory.add("(" + x + ", " + y + ")"); // Ajouter la position de départ au parcours
        this.currentWord = new StringBuilder();
        this.usedNodes = new HashSet<>();
        // 📌 Ajouter la position de départ et la lettre de départ
        this.pathHistory.add("(" + x + ", " + y + ")");
        Node startNode = grid.getNode(startX, startY);
        this.currentWord.append(startNode.letter);  // ✅ Ajout immédiat de la lettre de départ
        this.usedNodes.add(startNode);  // ✅ Marquer la case comme utilisée

        System.out.println("🔹 Début du jeu : Lettre initiale prise - " + currentWord);
    }


    private boolean isValidMove(int newX, int newY) {
        if (newX < 0 || newX >= grid.getSize() || newY < 0 || newY >= grid.getSize()) {
            return false;
        }
        if (grid.getNode(newX, newY).isWall) {
            return false;
        }
        return true;
    }

    public void move(int newX, int newY) {
        if (grid.isValid(newX, newY) && !grid.getNode(newX, newY).isWall) {

            Node node = grid.getNode(newX, newY);
            x = newX;
            y = newY;
            pathHistory.add("(" + x + ", " + y + ")");

            // Ajouter la lettre du nouveau noeud à currentWord
            currentWord.append(node.letter);

            // Ajouter le noeud utilisé à usedNodes
            usedNodes.add(node);

            // Vérifier les mots formés après chaque déplacement
            checkWordsInPath();

            // Soumettre le mot formé si c'est un mot valide
            submitWord();
        }
    }

    public void submitWord() {
        String word = currentWord.toString();
        if (validWords.contains(word)) {
            wordsFormed.add(word);
            System.out.println("Mot valide trouvé : " + word);
            freeUsedNodes();
            currentWord.setLength(0);
            displayWordsFormed(); // Afficher les mots formés après la soumission
        } else {
            System.out.println("Mot invalide : " + word);
        }
    }

    public void undo() {
        if (pathHistory.size() > 1) {
            // Annuler le dernier mouvement
            pathHistory.remove(pathHistory.size() - 1); // Enlever le dernier élément du parcours
            String lastPosition = pathHistory.get(pathHistory.size() - 1);
            String[] coords = lastPosition.substring(1, lastPosition.length() - 1).split(", ");
            x = Integer.parseInt(coords[0]);
            y = Integer.parseInt(coords[1]);

            // Enlever la dernière lettre du mot formé
            if (currentWord.length() > 0) {
                currentWord.deleteCharAt(currentWord.length() - 1);
            }

            Node lastNode = grid.getNode(x, y);
            usedNodes.remove(lastNode);
        }
    }


    public boolean hasWon() {
        if (x == grid.getEnd().x && y == grid.getEnd().y) {
            verifyShortestPath();  // Vérification du chemin le plus court
            return true;
        }
        return false;
    }


    public String getCurrentWord() {
        return currentWord.toString();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    } // Méthode pour afficher le parcours
    // Méthode pour afficher le parcours

    public void displayPath() {
        System.out.println("Parcours du joueur : " + String.join(" -> ", pathHistory));
    }
    public void displayWordsFormed() {
        System.out.println("Mots formés : " + wordsFormed);
    }

    public Set<String> getWordsFormed() {
        return wordsFormed;
    }

    private void freeUsedNodes() {
        for (Node node : usedNodes) {
            grid.resetNode(node.x, node.y);
        }
        usedNodes.clear();
    }



    // Nouvelle méthode pour vérifier les mots formés dans le chemin parcouru
    public void checkWordsInPath() {
        Set<String> foundWords = new HashSet<>();

        // On parcourt la chaîne et on génère toutes les sous-chaînes possibles
        for (int i = 0; i < currentWord.length(); i++) {
            for (int j = i + 1; j <= currentWord.length(); j++) {
                String subWord = currentWord.substring(i, j);
                if (validWords.contains(subWord)) {
                    foundWords.add(subWord);  // Ajouter le mot valide trouvé
                }
            }
        }

        // Ajouter les mots valides à la liste des mots formés
        if (!foundWords.isEmpty()) {
            wordsFormed.addAll(foundWords);
            System.out.println("Mots valides trouvés dans le chemin : " + foundWords);
        } else {
            System.out.println("Aucun mot valide trouvé dans le chemin.");
        }
    }
    public List<Node> findShortestPath() {
        int size = grid.getSize();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Map<Node, Node> cameFrom = new HashMap<>();
        Node start = grid.getNode(grid.getStart().x, grid.getStart().y);
        Node end = grid.getEnd();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid.getNode(i, j).distance = Integer.MAX_VALUE;
            }
        }
        start.distance = 0;
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current == end) break;

            for (Node neighbor : grid.getNeighbors(current.x, current.y)) {
                int newDist = current.distance + 1;
                if (newDist < neighbor.distance) {
                    neighbor.distance = newDist;
                    pq.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        Node step = end;
        while (cameFrom.containsKey(step)) {
            path.add(step);
            step = cameFrom.get(step);
        }
        Collections.reverse(path);
        return path;
    }

    public void verifyShortestPath() {
        List<Node> shortestPath = findShortestPath();  // Calcul du chemin le plus court
        int shortestPathLength = shortestPath.size();
        int playerPathLength = pathHistory.size() - 1;  // Ne pas compter la case de départ

        System.out.println("\n🏁 Le joueur a atteint la case d'arrivée !");
        System.out.println("📏 Longueur du plus court chemin : " + shortestPathLength);
        System.out.println("📌 Longueur du chemin parcouru par le joueur : " + playerPathLength);

        // Vérification si les chemins sont identiques
        if (playerPathLength == shortestPathLength) {
            boolean isSamePath = true;
            for (int i = 0; i < shortestPath.size(); i++) {
                try {
                    // Extraire les coordonnées x et y de pathHistory
                    String[] coords = pathHistory.get(i).split(", ");
                    int x = Integer.parseInt(coords[0].trim());  // X de la coordonnée
                    int y = Integer.parseInt(coords[1].trim());  // Y de la coordonnée

                    // Obtenir le node à partir des coordonnées
                    Node playerNode = grid.getNode(x, y);

                    if (!playerNode.equals(shortestPath.get(i))) {
                        isSamePath = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Erreur de format pour la case " + pathHistory.get(i) + ". Format attendu : x, y.");
                    isSamePath = false;
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Erreur dans les coordonnées de pathHistory, vérifiez le format.");
                    isSamePath = false;
                    break;
                }
            }
            if (isSamePath) {
                System.out.println("✅ Bravo ! Tu as emprunté le plus court chemin !");
            } else {
                System.out.println("❌ Tu aurais pu trouver un chemin plus court !");
            }
        } else {
            System.out.println("❌ Tu aurais pu trouver un chemin plus court !");
        }
    }


}
