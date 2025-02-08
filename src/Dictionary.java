import java.io.*;
import java.util.*;

class Dictionary {
    private List<String> words;
    private Set<String> wordSet; // Pour des recherches plus rapides

    public Dictionary(String filename) {
        words = new ArrayList<>();
        wordSet = new HashSet<>();
        loadWords(filename);
    }

    // Charger les mots à partir du fichier texte
    private void loadWords(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toUpperCase();
                if (!word.isEmpty()) {
                    words.add(word);
                    wordSet.add(word);  // Ajoute également au Set pour une recherche rapide
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du dictionnaire : " + e.getMessage());
        }
    }

    public List<String> getWords() {
        return words;
    }

    public boolean isWordValid(String word) {
        return wordSet.contains(word.toUpperCase());
    }

    public int getMaxWordLength() {
        return words.stream().mapToInt(String::length).max().orElse(5); // Taille minimale 5x5
    }
}
