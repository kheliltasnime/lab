import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameGUI extends JFrame implements KeyListener {
    private Grid grid;
    private Player player;
    private JLabel[][] labels;
    private JLabel wordLabel;

    public GameGUI(Grid grid, Player player) {
        this.grid = grid;
        this.player = player;

        setTitle("Labyrinthe de Mots");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(grid.getSize(), grid.getSize()));
        labels = new JLabel[grid.getSize()][grid.getSize()];

        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                labels[i][j] = new JLabel("" + grid.getNode(i, j).letter, SwingConstants.CENTER);
                labels[i][j].setOpaque(true);
                labels[i][j].setPreferredSize(new Dimension(50, 50));
                labels[i][j].setFont(new Font("Arial", Font.BOLD, 18));
                labels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));

                if (grid.getNode(i, j).isWall) {
                    labels[i][j].setBackground(Color.DARK_GRAY); // Mur
                } else {
                    labels[i][j].setBackground(Color.WHITE); // Case libre
                }

                gridPanel.add(labels[i][j]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        wordLabel = new JLabel("Mot en cours: ", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(wordLabel, BorderLayout.SOUTH);

        updateGrid();
        addKeyListener(this);
        setFocusable(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateGrid() {
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                if (player.getX() == i && player.getY() == j) {
                    labels[i][j].setBackground(Color.BLUE); // Joueur
                    labels[i][j].setForeground(Color.WHITE);
                } else if (grid.getStart().x == i && grid.getStart().y == j) {
                    labels[i][j].setBackground(Color.GREEN); // Départ
                } else if (grid.getEnd().x == i && grid.getEnd().y == j) {
                    labels[i][j].setBackground(Color.RED); // Arrivée
                } else if (!grid.getNode(i, j).isWall) {
                    labels[i][j].setBackground(Color.WHITE); // Case normale
                    labels[i][j].setForeground(Color.BLACK);
                }
            }
        }
        wordLabel.setText("Mot en cours: " + player.getCurrentWord());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int newX = player.getX();
        int newY = player.getY();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: newX--; break;
            case KeyEvent.VK_DOWN: newX++; break;
            case KeyEvent.VK_LEFT: newY--; break;
            case KeyEvent.VK_RIGHT: newY++; break;
            case KeyEvent.VK_Q: newX--; newY--; break; // Haut-Gauche
            case KeyEvent.VK_E: newX--; newY++; break; // Haut-Droite
            case KeyEvent.VK_A: newX++; newY--; break; // Bas-Gauche
            case KeyEvent.VK_D: newX++; newY++; break; // Bas-Droite
        }

        player.move(newX, newY);
        updateGrid();

        if (player.hasWon()) {
            JOptionPane.showMessageDialog(this, "🎉 Félicitations ! Vous avez gagné !");
            System.exit(0);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
