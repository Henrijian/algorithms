/******************************************************************************
 *  Compilation:  javac PictureDump.java
 *  Execution:    java PictureDump width height < file
 *  Dependencies: BinaryStdIn.java Picture.java
 *
 *  Reads in a binary file and writes out the bits as w-by-h picture,
 *  with the 1 bits in black and the 0 bits in white.
 *
 *  % more abra.txt
 *  ABRACADABRA!
 *
 *  % java PictureDump 16 6 < abra.txt
 *
 ******************************************************************************/
package strings.data_compression;

import javax.swing.*;
import java.awt.*;

/**
 *  The {@code PictureDump} class provides a client for displaying the contents
 *  of a binary file as a black-and-white picture.
 */
public class PictureDump {
    private PictureDump() {
        // Do not instantiate.
    }

    /**
     * Reads in a sequence of bytes from standard input and draws
     * them to standard drawing output as a width-by-height picture,
     * using black for 1 and white for 0 (and red for any leftover
     * pixels).
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        int width = Integer.parseInt(argv[0]);
        int height = Integer.parseInt(argv[1]);
        PictureDumpFrame frame = new PictureDumpFrame("Picture Dump", width, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (!BinaryStdIn.isEmpty()) {
                    boolean bit = BinaryStdIn.readBoolean();
                    frame.addBit(bit, row, col);
                }
            }
        }
        frame.setVisible(true);
    }
}
