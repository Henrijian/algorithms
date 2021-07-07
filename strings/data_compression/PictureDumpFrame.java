package strings.data_compression;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class PictureDumpFrame extends JFrame {
    private final int CELL_WIDTH = 50;
    private final int CELL_HEIGHT = 50;
    private final Color TRUE_BIT_COLOR = Color.BLACK;
    private final Color FALSE_BIT_COLOR = Color.WHITE;
    private final Color NONE_BIT_COLOR = Color.RED;
    private int width;
    private int height;
    private HashMap<Integer, Boolean> bits;

    public PictureDumpFrame (String title, int width, int height) {
        super(title);
        this.width = width;
        this.height = height;
        bits = new HashMap<>();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width * CELL_WIDTH, height * CELL_HEIGHT);
        setBackground(NONE_BIT_COLOR);
    }

    public void addBit(boolean bit, int row, int col) {
        int bitIdx = row * width + col;
        bits.put(bitIdx, bit);
    }

    public void paint(Graphics g) {
        super.paint(g);
        paintBits(g);
    }

    public void paintBits(Graphics g) {
        int topBorder = (g.getClipBounds().height - height * CELL_HEIGHT) / 2;
        int leftBorder = (g.getClipBounds().width - width * CELL_WIDTH) / 2;
        for (int bitIdx : bits.keySet()) {
            boolean bit = bits.get(bitIdx);
            if (bit) {
                g.setColor(TRUE_BIT_COLOR);
            } else {
                g.setColor(FALSE_BIT_COLOR);
            }
            int row = bitIdx / width;
            int col = bitIdx % width;
            g.fillRect(col * CELL_WIDTH + leftBorder, row * CELL_HEIGHT + topBorder, CELL_WIDTH, CELL_HEIGHT);
        }
    }
}
