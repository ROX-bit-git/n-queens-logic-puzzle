import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Random;

public class NQueens6RegionGUI extends JFrame {

    private final int N = 8;
    private final int REGIONS = 6;

    private JButton[][] cells = new JButton[N][N];
    private boolean[][] queens = new boolean[N][N];
    private int[][] regionMap = new int[N][N];

    private boolean darkMode = false;

    private final Color[] LIGHT_COLORS = {
            new Color(255, 224, 224),
            new Color(224, 255, 224),
            new Color(224, 224, 255),
            new Color(255, 255, 224),
            new Color(224, 255, 255),
            new Color(243, 224, 255)
    };

    private final Color[] DARK_COLORS = {
            new Color(121, 85, 72),
            new Color(56, 142, 60),
            new Color(48, 63, 159),
            new Color(251, 192, 45),
            new Color(0, 151, 167),
            new Color(156, 39, 176)
    };

    public NQueens6RegionGUI() {
        setTitle("N-Queens – 6 Playable Regions");
        setSize(860, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        generatePlayableRegions();

        JPanel boardPanel = new JPanel(new GridLayout(N, N));
        initializeBoard(boardPanel);

        JPanel controlPanel = new JPanel();
        JButton resetBtn = new JButton("Reset 🔄");
        JButton hintBtn = new JButton("Hint 💡");
        JButton modeBtn = new JButton("Dark Mode 🌙");

        resetBtn.addActionListener(e -> resetBoard());
        hintBtn.addActionListener(e -> showHint());
        modeBtn.addActionListener(e -> toggleMode(modeBtn));

        controlPanel.add(resetBtn);
        controlPanel.add(hintBtn);
        controlPanel.add(modeBtn);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ===================== GUARANTEED PLAYABLE REGION GENERATION =====================
    private void generatePlayableRegions() {
        Random rand = new Random();
        boolean solved;

        do {
            // assign random regions
            for (int r = 0; r < N; r++)
                for (int c = 0; c < N; c++)
                    regionMap[r][c] = rand.nextInt(REGIONS);

            // attempt to place one queen per region
            queens = new boolean[N][N];
            solved = placeQueensPerRegion(0);
        } while (!solved);
    }

    private boolean placeQueensPerRegion(int region) {
        if (region >= REGIONS) return true; // all regions placed

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (regionMap[r][c] == region && isSafe(r, c)) {
                    queens[r][c] = true;
                    if (placeQueensPerRegion(region + 1))
                        return true;
                    queens[r][c] = false;
                }
            }
        }
        return false;
    }

    private void initializeBoard(JPanel boardPanel) {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 32));
                btn.setFocusPainted(false);
                btn.setBorder(new LineBorder(Color.BLACK, 1));

                btn.setBackground(getRegionColor(r, c));

                int row = r, col = c;
                btn.addActionListener(e -> toggleQueen(row, col));

                cells[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        // remove pre-placed queens for player
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                queens[r][c] = false;
    }

    private Color getRegionColor(int r, int c) {
        int region = regionMap[r][c];
        return darkMode ? DARK_COLORS[region] : LIGHT_COLORS[region];
    }

    private void toggleQueen(int row, int col) {
        if (queens[row][col]) {
            queens[row][col] = false;
            cells[row][col].setText("");
            updateBoard();
            return;
        }

        int region = regionMap[row][col];
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                if (queens[r][c] && regionMap[r][c] == region)
                    return;

        queens[row][col] = true;
        cells[row][col].setText("♕");
        updateBoard();
        checkWin();
    }

    private void updateBoard() {
        resetColors();

        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                if (queens[r][c] && !isSafe(r, c))
                    cells[r][c].setBackground(Color.RED);
    }

    private void resetColors() {
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                cells[r][c].setBackground(getRegionColor(r, c));
    }

    private boolean isSafe(int row, int col) {
        for (int i = 0; i < N; i++) {
            if (i != col && queens[row][i]) return false;
            if (i != row && queens[i][col]) return false;
        }

        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--)
            if (queens[r][c]) return false;
        for (int r = row + 1, c = col + 1; r < N && c < N; r++, c++)
            if (queens[r][c]) return false;
        for (int r = row - 1, c = col + 1; r >= 0 && c < N; r--, c++)
            if (queens[r][c]) return false;
        for (int r = row + 1, c = col - 1; r < N && c >= 0; r++, c--)
            if (queens[r][c]) return false;

        return true;
    }

    private void checkWin() {
        int count = 0;
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                if (queens[r][c]) {
                    count++;
                    if (!isSafe(r, c)) return;
                }

        if (count == REGIONS)
            JOptionPane.showMessageDialog(this,
                    "🎉 You solved the 6-Region Queens Puzzle!",
                    "Winner",
                    JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetBoard() {
        queens = new boolean[N][N];
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                cells[r][c].setText("");
        updateBoard();
    }

    private void showHint() {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (!queens[r][c]) {
                    queens[r][c] = true;
                    if (isSafe(r, c)) {
                        cells[r][c].setBackground(new Color(144, 238, 144));
                        queens[r][c] = false;
                        return;
                    }
                    queens[r][c] = false;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "No safe hints available!");
    }

    private void toggleMode(JButton btn) {
        darkMode = !darkMode;
        btn.setText(darkMode ? "Light Mode ☀️" : "Dark Mode 🌙");
        updateBoard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NQueens6RegionGUI::new);
    }
}
