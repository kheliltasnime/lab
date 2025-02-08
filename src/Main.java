import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Charger le dictionnaire depuis le fichier
        Dictionary dictionary = new Dictionary("src/dictionnaire.txt");
        List<String> wordsList = dictionary.getWords();

        if (wordsList.isEmpty()) {
            System.out.println("Le dictionnaire est vide !");
            return;
        }

        // Convertir la liste en un Set pour une recherche rapide
        Set<String> wordsSet = new HashSet<>(wordsList);
        Grid grid = new Grid(wordsList); // Initialisation de la grille avec les mots du dictionnaire

        System.out.println("Affichage détaillé de la grille :");
        for (int i = 0; i < grid.getSize(); i++) { // Utilisation de getSize() pour obtenir la taille
            for (int j = 0; j < grid.getSize(); j++) {
                System.out.println("Case [" + i + "][" + j + "] : " + grid.getNode(i, j).letter);
            }
        }

        Scanner scanner = new Scanner(System.in);
        int startX, startY, endX, endY;
        grid.displayGrid();
        System.out.println("Voulez-vous choisir les cases de départ et d'arrivée ? (O/N)");
        String choix = scanner.next().toUpperCase();

        if (choix.equals("O")) {
            // Sélection manuelle
            while (true) {
                System.out.println("Choisissez votre case de départ (format: x y) : ");
                startX = scanner.nextInt();
                startY = scanner.nextInt();

                if (!grid.isValid(startX, startY) || grid.getNode(startX, startY).isWall) {
                    System.out.println("❌ Position invalide ! Veuillez choisir une case libre.");
                    continue;
                }

                System.out.println("Choisissez votre case d'arrivée (format: x y) : ");
                endX = scanner.nextInt();
                endY = scanner.nextInt();

                if (!grid.isValid(endX, endY) || grid.getNode(endX, endY).isWall) {
                    System.out.println("❌ Position invalide ! Veuillez choisir une case libre.");
                    continue;
                }

                // Vérifier s'il existe un chemin entre les deux cases
                grid.setStartAndEnd(startX, startY, endX, endY);

                if (grid.isPathExists()) {
                    break; // ✅ Chemin valide, on sort de la boucle
                } else {
                    System.out.println("❌ Aucun chemin possible entre ces deux cases ! Recommencez.");
                }
            }
        } else {
            // Sélection aléatoire
            Random random = new Random();
            while (true) {
                startX = random.nextInt(grid.getSize());
                startY = random.nextInt(grid.getSize());
                endX = random.nextInt(grid.getSize());
                endY = random.nextInt(grid.getSize());

                if (grid.isValid(startX, startY) && grid.isValid(endX, endY) &&
                        !grid.getNode(startX, startY).isWall && !grid.getNode(endX, endY).isWall) {

                    grid.setStartAndEnd(startX, startY, endX, endY);

                    if (grid.isPathExists()) {
                        System.out.println("Départ choisi aléatoirement : (" + startX + ", " + startY + ")");
                        System.out.println("Arrivée choisie aléatoirement : (" + endX + ", " + endY + ")");
                        break;
                    }
                }
            }
        }

        // Initialisation du joueur
        Player player = new Player(grid, wordsSet, startX, startY);
        SwingUtilities.invokeLater(() -> {
            GameGUI gameGUI = new GameGUI(grid, player);
            gameGUI.setVisible(true);
        });

        System.out.println("\nBienvenue dans le Labyrinthe de Mots !");
        System.out.println("Votre position de départ est en (" + startX + ", " + startY + ") - Lettre: " + grid.getStart().letter);
        System.out.println("Votre objectif est d'atteindre la sortie en (" + endX + ", " + endY + ") - Lettre: " + grid.getEnd().letter);
        System.out.println("Formez des mots valides pour progresser dans le labyrinthe !\n");

        // Boucle principale du jeu
        while (true) {
            System.out.println("Déplacez-vous :");
            System.out.println("(Z = Haut, Q = Gauche, S = Bas, D = Droite," +
                    " A = Haut-Gauche, E = Haut-Droite," +
                    " W = Bas-Gauche, C = Bas-Droite, U = Annuler, X = Quitter)");

            String input = scanner.next().toUpperCase();

            int newX = player.getX();
            int newY = player.getY();

            switch (input) {
                case "Z": newX--; break; // Haut
                case "Q": newY--; break; // Gauche
                case "S": newX++; break; // Bas
                case "D": newY++; break; // Droite
                case "A": newX--; newY--; break; // Haut-Gauche
                case "E": newX--; newY++; break; // Haut-Droite
                case "W": newX++; newY--; break; // Bas-Gauche
                case "C": newX++; newY++; break; // Bas-Droite
                case "U":
                    player.undo();
                    System.out.println("Annulation du dernier déplacement !");
                    continue;
                case "X":
                    System.out.println("Fin du jeu !");
                    scanner.close();
                    return;
                default:
                    System.out.println("Commande invalide !");
                    continue;
            }

            player.move(newX, newY);
           // player.displayPath();
            // Afficher la lettre prise et le mot actuel
            System.out.println("Lettre prise : " + grid.getNode(player.getX(), player.getY()).letter);
            System.out.println("Mot actuel formé : " + player.getCurrentWord());
            if (player.hasWon()) {
                System.out.println("🎉 Félicitations ! Vous avez atteint la sortie avec un mot valide !");

                System.out.println("Mots formés par le joueur : " + String.join(", ", player.getWordsFormed()));

                player.displayWordsFormed();
                break;
            }
        }

        scanner.close();
    }
}
