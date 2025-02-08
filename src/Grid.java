import java.util.*;

class Grid {
    private int size;
    private Node[][] grid;
    private List<String> dictionary;
    private boolean[][] visited;
    private Node start, end;
    private Set<Node> randomFilledCells;

   // private Node[][] nodes;
    public Grid(List<String> dictionary) {
        this.dictionary = dictionary;
        this.size = Math.max(dictionary.stream().mapToInt(String::length).max().orElse(5), 5);
        this.grid = new Node[size][size];
        this.visited = new boolean[size][size];
        this.randomFilledCells = new HashSet<>();
        generateGrid();
        generateMazeDFS(0, 0);
        addWalls();

    }
    public void resetNode(int x, int y) {
        if (isValid(x, y)) {
            grid[x][y].setAvailable(true); // Marque la case comme disponible
        }
    }

    private void generateGrid() {
        Random random = new Random();
        List<String> placedWords = new ArrayList<>();

        // Remplir la grille avec des espaces vides
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Node(' ', i, j);

            }
        }

        // Placer des mots du dictionnaire
        for (String word : dictionary) {
            if (placedWords.size() >= size) break;

            int wordLen = word.length();
            int startX = random.nextInt(size);
            int startY = random.nextInt(size);
            int direction = random.nextInt(3);

            if (canPlaceWord(startX, startY, word, direction)) {
                placeWord(startX, startY, word, direction);
                placedWords.add(word);
            }
        }

        // Remplir les cases vides avec des lettres aléatoires du dictionnaire
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].letter == ' ') {
                    String word = dictionary.get(random.nextInt(dictionary.size()));
                    grid[i][j].letter = word.charAt(random.nextInt(word.length()));
                    randomFilledCells.add(grid[i][j]);
                }
            }
        }
    }

    private boolean canPlaceWord(int x, int y, String word, int direction) {
        int wordLen = word.length();
        if (direction == 0 && y + wordLen > size) return false;
        if (direction == 1 && x + wordLen > size) return false;
        if (direction == 2 && (x + wordLen > size || y + wordLen > size)) return false;

        for (int i = 0; i < wordLen; i++) {
            int nx = x, ny = y;
            if (direction == 0) ny += i;
            else if (direction == 1) nx += i;
            else { nx += i; ny += i; }

            if (grid[nx][ny].letter != ' ') {
                return false;
            }
        }
        return true;
    }

    private void placeWord(int x, int y, String word, int direction) {
        for (int i = 0; i < word.length(); i++) {
            grid[x][y].letter = word.charAt(i);
            if (direction == 0) y++;
            else if (direction == 1) x++;
            else { x++; y++; }
        }
    }

    public void setStartAndEnd(int startX, int startY, int endX, int endY) {
        if (isValid(startX, startY) && isValid(endX, endY) && !grid[startX][startY].isWall && !grid[endX][endY].isWall) {
            this.start = grid[startX][startY];
            this.end = grid[endX][endY];
            ensurePathExists();
        } else {
            throw new IllegalArgumentException("Les positions de départ et de fin doivent être valides et non sur un mur.");
        }
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private void generateMazeDFS(int x, int y) {
        visited[x][y] = true;
        List<int[]> directions = Arrays.asList(new int[]{-1, 0}, new int[]{1, 0}, new int[]{0, -1}, new int[]{0, 1});
        Collections.shuffle(directions);

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (isValid(nx, ny) && !visited[nx][ny]) {
                grid[x][y].addNeighbor(grid[nx][ny]);
                grid[nx][ny].addNeighbor(grid[x][y]);
                generateMazeDFS(nx, ny);
            }
        }
    }
    private void addWalls() {
        Random random = new Random();
        for (Node node : randomFilledCells) { // Ne modifier que les cases remplies aléatoirement
            if (random.nextDouble() < 0.1) { // 10% de chance de devenir un mur
                node.isWall = true;
                node.neighbors.clear();
            }
        }
    }

    private void ensurePathExists() {
        while (!isPathExists()) {
            generateMazeDFS(0, 0);
            addWalls();
        }
    }

    public boolean isPathExists() {
        boolean[][] visited = new boolean[size][size];
        return dfsCheck(start.x, start.y, visited);
    }

    private boolean dfsCheck(int x, int y, boolean[][] visited) {
        if (x == end.x && y == end.y) return true;
        if (!isValid(x, y) || visited[x][y]) return false;

        visited[x][y] = true;
        if (dfsCheck(x + 1, y, visited) || dfsCheck(x - 1, y, visited) || dfsCheck(x, y + 1, visited) || dfsCheck(x, y - 1, visited)) {
            return true;
        }

        return false;
    }

    // Nouvelle méthode getNode pour récupérer un Node spécifique
    public Node getNode(int x, int y) {
        if (isValid(x, y)) {
            return grid[x][y];
        } else {
            throw new IllegalArgumentException("Position invalide dans la grille");
        }
    }

    // Méthode pour afficher la grille
    public void displayGrid() {
        System.out.println("Affichage détaillé de la grille :");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print("[" + grid[i][j].letter + "] ");
            }
            System.out.println();  // Passe à la ligne suivante après chaque ligne de la grille
        }
    }


    public int getSize() {
        return size;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public Set<Node> getNeighbors(int x, int y) {
        Set<Node> neighbors = new HashSet<>();
        if (isValid(x - 1, y)) neighbors.add(grid[x - 1][y]); // Gauche
        if (isValid(x + 1, y)) neighbors.add(grid[x + 1][y]); // Droite
        if (isValid(x, y - 1)) neighbors.add(grid[x][y - 1]); // Haut
        if (isValid(x, y + 1)) neighbors.add(grid[x][y + 1]); // Bas
        return neighbors;
    }



}
