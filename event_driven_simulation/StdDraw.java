package event_driven_simulation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class StdDraw implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    public static final Color BLACK;
    public static final Color BLUE;
    public static final Color CYAN;
    public static final Color DARK_GRAY;
    public static final Color GRAY;
    public static final Color GREEN;
    public static final Color LIGHT_GRAY;
    public static final Color MAGENTA;
    public static final Color ORANGE;
    public static final Color PINK;
    public static final Color RED;
    public static final Color WHITE;
    public static final Color YELLOW;
    public static final Color BOOK_BLUE;
    public static final Color BOOK_LIGHT_BLUE;
    public static final Color BOOK_RED;
    public static final Color PRINCETON_ORANGE;
    private static final Color DEFAULT_PEN_COLOR;
    private static final Color DEFAULT_CLEAR_COLOR;
    private static Color penColor;
    private static final int DEFAULT_SIZE = 512;
    private static int width;
    private static int height;
    private static final double DEFAULT_PEN_RADIUS = 0.002D;
    private static double penRadius;
    private static boolean defer;
    private static final double BORDER = 0.0D;
    private static final double DEFAULT_XMIN = 0.0D;
    private static final double DEFAULT_XMAX = 1.0D;
    private static final double DEFAULT_YMIN = 0.0D;
    private static final double DEFAULT_YMAX = 1.0D;
    private static double xmin;
    private static double ymin;
    private static double xmax;
    private static double ymax;
    private static Object mouseLock;
    private static Object keyLock;
    private static final Font DEFAULT_FONT;
    private static Font font;
    private static BufferedImage offscreenImage;
    private static BufferedImage onscreenImage;
    private static Graphics2D offscreen;
    private static Graphics2D onscreen;
    private static StdDraw std;
    private static JFrame frame;
    private static boolean isMousePressed;
    private static double mouseX;
    private static double mouseY;
    private static LinkedList<Character> keysTyped;
    private static TreeSet<Integer> keysDown;

    private StdDraw() {
    }

    public static void setCanvasSize() {
        setCanvasSize(512, 512);
    }

    public static void setCanvasSize(int canvasWidth, int canvasHeight) {
        if (canvasWidth > 0 && canvasHeight > 0) {
            width = canvasWidth;
            height = canvasHeight;
            init();
        } else {
            throw new IllegalArgumentException("width and height must be positive");
        }
    }

    private static void init() {
        if (frame != null) {
            frame.setVisible(false);
        }

        frame = new JFrame();
        offscreenImage = new BufferedImage(width, height, 2);
        onscreenImage = new BufferedImage(width, height, 2);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();
        clear();
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        draw.addMouseListener(std);
        draw.addMouseMotionListener(std);
        frame.setContentPane(draw);
        frame.addKeyListener(std);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(3);
        frame.setTitle("Standard Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save...   ");
        menuItem1.addActionListener(std);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(83, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menuItem1);
        return menuBar;
    }

    public static void setXscale() {
        setXscale(0.0D, 1.0D);
    }

    public static void setYscale() {
        setYscale(0.0D, 1.0D);
    }

    public static void setScale() {
        setXscale();
        setYscale();
    }

    public static void setXscale(double min, double max) {
        double size = max - min;
        if (size == 0.0D) {
            throw new IllegalArgumentException("the min and max are the same");
        } else {
            synchronized(mouseLock) {
                xmin = min - 0.0D * size;
                xmax = max + 0.0D * size;
            }
        }
    }

    public static void setYscale(double min, double max) {
        double size = max - min;
        if (size == 0.0D) {
            throw new IllegalArgumentException("the min and max are the same");
        } else {
            synchronized(mouseLock) {
                ymin = min - 0.0D * size;
                ymax = max + 0.0D * size;
            }
        }
    }

    public static void setScale(double min, double max) {
        double size = max - min;
        if (size == 0.0D) {
            throw new IllegalArgumentException("the min and max are the same");
        } else {
            synchronized(mouseLock) {
                xmin = min - 0.0D * size;
                xmax = max + 0.0D * size;
                ymin = min - 0.0D * size;
                ymax = max + 0.0D * size;
            }
        }
    }

    private static double scaleX(double x) {
        return (double)width * (x - xmin) / (xmax - xmin);
    }

    private static double scaleY(double y) {
        return (double)height * (ymax - y) / (ymax - ymin);
    }

    private static double factorX(double w) {
        return w * (double)width / Math.abs(xmax - xmin);
    }

    private static double factorY(double h) {
        return h * (double)height / Math.abs(ymax - ymin);
    }

    private static double userX(double x) {
        return xmin + x * (xmax - xmin) / (double)width;
    }

    private static double userY(double y) {
        return ymax - y * (ymax - ymin) / (double)height;
    }

    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    public static void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        draw();
    }

    public static double getPenRadius() {
        return penRadius;
    }

    public static void setPenRadius() {
        setPenRadius(0.002D);
    }

    public static void setPenRadius(double radius) {
        if (radius < 0.0D) {
            throw new IllegalArgumentException("pen radius must be nonnegative");
        } else {
            penRadius = radius;
            float scaledPenRadius = (float)(radius * 512.0D);
            BasicStroke stroke = new BasicStroke(scaledPenRadius, 1, 1);
            offscreen.setStroke(stroke);
        }
    }

    public static Color getPenColor() {
        return penColor;
    }

    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    public static void setPenColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException();
        } else {
            penColor = color;
            offscreen.setColor(penColor);
        }
    }

    public static void setPenColor(int red, int green, int blue) {
        if (red >= 0 && red < 256) {
            if (green >= 0 && green < 256) {
                if (blue >= 0 && blue < 256) {
                    setPenColor(new Color(red, green, blue));
                } else {
                    throw new IllegalArgumentException("amount of blue must be between 0 and 255");
                }
            } else {
                throw new IllegalArgumentException("amount of green must be between 0 and 255");
            }
        } else {
            throw new IllegalArgumentException("amount of red must be between 0 and 255");
        }
    }

    public static Font getFont() {
        return font;
    }

    public static void setFont() {
        setFont(DEFAULT_FONT);
    }

    public static void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException();
        } else {
            StdDraw.font = font;
        }
    }

    public static void line(double x0, double y0, double x1, double y1) {
        offscreen.draw(new Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        draw();
    }

    private static void pixel(double x, double y) {
        offscreen.fillRect((int)Math.round(scaleX(x)), (int)Math.round(scaleY(y)), 1, 1);
    }

    public static void point(double x, double y) {
        double xs = scaleX(x);
        double ys = scaleY(y);
        double r = penRadius;
        float scaledPenRadius = (float)(r * 512.0D);
        if (scaledPenRadius <= 1.0F) {
            pixel(x, y);
        } else {
            offscreen.fill(new java.awt.geom.Ellipse2D.Double(xs - (double)(scaledPenRadius / 2.0F), ys - (double)(scaledPenRadius / 2.0F), (double)scaledPenRadius, (double)scaledPenRadius));
        }

        draw();
    }

    public static void circle(double x, double y, double radius) {
        if (radius < 0.0D) {
            throw new IllegalArgumentException("radius must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * radius);
            double hs = factorY(2.0D * radius);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.draw(new java.awt.geom.Ellipse2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void filledCircle(double x, double y, double radius) {
        if (radius < 0.0D) {
            throw new IllegalArgumentException("radius must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * radius);
            double hs = factorY(2.0D * radius);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.fill(new java.awt.geom.Ellipse2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        if (semiMajorAxis < 0.0D) {
            throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
        } else if (semiMinorAxis < 0.0D) {
            throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * semiMajorAxis);
            double hs = factorY(2.0D * semiMinorAxis);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.draw(new java.awt.geom.Ellipse2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void filledEllipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        if (semiMajorAxis < 0.0D) {
            throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
        } else if (semiMinorAxis < 0.0D) {
            throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * semiMajorAxis);
            double hs = factorY(2.0D * semiMinorAxis);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.fill(new java.awt.geom.Ellipse2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void arc(double x, double y, double radius, double angle1, double angle2) {
        if (radius < 0.0D) {
            throw new IllegalArgumentException("arc radius must be nonnegative");
        } else {
            while(angle2 < angle1) {
                angle2 += 360.0D;
            }

            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * radius);
            double hs = factorY(2.0D * radius);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.draw(new java.awt.geom.Arc2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs, angle1, angle2 - angle1, 0));
            }

            draw();
        }
    }

    public static void square(double x, double y, double halfLength) {
        if (halfLength < 0.0D) {
            throw new IllegalArgumentException("half length must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * halfLength);
            double hs = factorY(2.0D * halfLength);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.draw(new java.awt.geom.Rectangle2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void filledSquare(double x, double y, double halfLength) {
        if (halfLength < 0.0D) {
            throw new IllegalArgumentException("half length must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * halfLength);
            double hs = factorY(2.0D * halfLength);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.fill(new java.awt.geom.Rectangle2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void rectangle(double x, double y, double halfWidth, double halfHeight) {
        if (halfWidth < 0.0D) {
            throw new IllegalArgumentException("half width must be nonnegative");
        } else if (halfHeight < 0.0D) {
            throw new IllegalArgumentException("half height must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * halfWidth);
            double hs = factorY(2.0D * halfHeight);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.draw(new java.awt.geom.Rectangle2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void filledRectangle(double x, double y, double halfWidth, double halfHeight) {
        if (halfWidth < 0.0D) {
            throw new IllegalArgumentException("half width must be nonnegative");
        } else if (halfHeight < 0.0D) {
            throw new IllegalArgumentException("half height must be nonnegative");
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(2.0D * halfWidth);
            double hs = factorY(2.0D * halfHeight);
            if (ws <= 1.0D && hs <= 1.0D) {
                pixel(x, y);
            } else {
                offscreen.fill(new java.awt.geom.Rectangle2D.Double(xs - ws / 2.0D, ys - hs / 2.0D, ws, hs));
            }

            draw();
        }
    }

    public static void polygon(double[] x, double[] y) {
        if (x == null) {
            throw new IllegalArgumentException("x-coordinate array is null");
        } else if (y == null) {
            throw new IllegalArgumentException("y-coordinate array is null");
        } else {
            int n1 = x.length;
            int n2 = y.length;
            if (n1 != n2) {
                throw new IllegalArgumentException("arrays must be of the same length");
            } else {
                int n = n1;
                if (n1 != 0) {
                    GeneralPath path = new GeneralPath();
                    path.moveTo((float)scaleX(x[0]), (float)scaleY(y[0]));

                    for(int i = 0; i < n; ++i) {
                        path.lineTo((float)scaleX(x[i]), (float)scaleY(y[i]));
                    }

                    path.closePath();
                    offscreen.draw(path);
                    draw();
                }
            }
        }
    }

    public static void filledPolygon(double[] x, double[] y) {
        if (x == null) {
            throw new IllegalArgumentException("x-coordinate array is null");
        } else if (y == null) {
            throw new IllegalArgumentException("y-coordinate array is null");
        } else {
            int n1 = x.length;
            int n2 = y.length;
            if (n1 != n2) {
                throw new IllegalArgumentException("arrays must be of the same length");
            } else {
                int n = n1;
                if (n1 != 0) {
                    GeneralPath path = new GeneralPath();
                    path.moveTo((float)scaleX(x[0]), (float)scaleY(y[0]));

                    for(int i = 0; i < n; ++i) {
                        path.lineTo((float)scaleX(x[i]), (float)scaleY(y[i]));
                    }

                    path.closePath();
                    offscreen.fill(path);
                    draw();
                }
            }
        }
    }

    private static Image getImage(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException();
        } else {
            ImageIcon icon = new ImageIcon(filename);
            URL url;
            if (icon == null || icon.getImageLoadStatus() != 8) {
                try {
                    url = new URL(filename);
                    icon = new ImageIcon(url);
                } catch (MalformedURLException var3) {
                }
            }

            if (icon == null || icon.getImageLoadStatus() != 8) {
                url = StdDraw.class.getResource(filename);
                if (url != null) {
                    icon = new ImageIcon(url);
                }
            }

            if (icon == null || icon.getImageLoadStatus() != 8) {
                url = StdDraw.class.getResource("/" + filename);
                if (url == null) {
                    throw new IllegalArgumentException("image " + filename + " not found");
                }

                icon = new ImageIcon(url);
            }

            return icon.getImage();
        }
    }

    public static void picture(double x, double y, String filename) {
        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth((ImageObserver)null);
        int hs = image.getHeight((ImageObserver)null);
        if (ws >= 0 && hs >= 0) {
            offscreen.drawImage(image, (int)Math.round(xs - (double)ws / 2.0D), (int)Math.round(ys - (double)hs / 2.0D), (ImageObserver)null);
            draw();
        } else {
            throw new IllegalArgumentException("image " + filename + " is corrupt");
        }
    }

    public static void picture(double x, double y, String filename, double degrees) {
        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth((ImageObserver)null);
        int hs = image.getHeight((ImageObserver)null);
        if (ws >= 0 && hs >= 0) {
            offscreen.rotate(Math.toRadians(-degrees), xs, ys);
            offscreen.drawImage(image, (int)Math.round(xs - (double)ws / 2.0D), (int)Math.round(ys - (double)hs / 2.0D), (ImageObserver)null);
            offscreen.rotate(Math.toRadians(degrees), xs, ys);
            draw();
        } else {
            throw new IllegalArgumentException("image " + filename + " is corrupt");
        }
    }

    public static void picture(double x, double y, String filename, double scaledWidth, double scaledHeight) {
        Image image = getImage(filename);
        if (scaledWidth < 0.0D) {
            throw new IllegalArgumentException("width  is negative: " + scaledWidth);
        } else if (scaledHeight < 0.0D) {
            throw new IllegalArgumentException("height is negative: " + scaledHeight);
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(scaledWidth);
            double hs = factorY(scaledHeight);
            if (ws >= 0.0D && hs >= 0.0D) {
                if (ws <= 1.0D && hs <= 1.0D) {
                    pixel(x, y);
                } else {
                    offscreen.drawImage(image, (int)Math.round(xs - ws / 2.0D), (int)Math.round(ys - hs / 2.0D), (int)Math.round(ws), (int)Math.round(hs), (ImageObserver)null);
                }

                draw();
            } else {
                throw new IllegalArgumentException("image " + filename + " is corrupt");
            }
        }
    }

    public static void picture(double x, double y, String filename, double scaledWidth, double scaledHeight, double degrees) {
        if (scaledWidth < 0.0D) {
            throw new IllegalArgumentException("width is negative: " + scaledWidth);
        } else if (scaledHeight < 0.0D) {
            throw new IllegalArgumentException("height is negative: " + scaledHeight);
        } else {
            Image image = getImage(filename);
            double xs = scaleX(x);
            double ys = scaleY(y);
            double ws = factorX(scaledWidth);
            double hs = factorY(scaledHeight);
            if (ws >= 0.0D && hs >= 0.0D) {
                if (ws <= 1.0D && hs <= 1.0D) {
                    pixel(x, y);
                }

                offscreen.rotate(Math.toRadians(-degrees), xs, ys);
                offscreen.drawImage(image, (int)Math.round(xs - ws / 2.0D), (int)Math.round(ys - hs / 2.0D), (int)Math.round(ws), (int)Math.round(hs), (ImageObserver)null);
                offscreen.rotate(Math.toRadians(degrees), xs, ys);
                draw();
            } else {
                throw new IllegalArgumentException("image " + filename + " is corrupt");
            }
        }
    }

    public static void text(double x, double y, String text) {
        if (text == null) {
            throw new IllegalArgumentException();
        } else {
            offscreen.setFont(font);
            FontMetrics metrics = offscreen.getFontMetrics();
            double xs = scaleX(x);
            double ys = scaleY(y);
            int ws = metrics.stringWidth(text);
            int hs = metrics.getDescent();
            offscreen.drawString(text, (float)(xs - (double)ws / 2.0D), (float)(ys + (double)hs));
            draw();
        }
    }

    public static void text(double x, double y, String text, double degrees) {
        if (text == null) {
            throw new IllegalArgumentException();
        } else {
            double xs = scaleX(x);
            double ys = scaleY(y);
            offscreen.rotate(Math.toRadians(-degrees), xs, ys);
            text(x, y, text);
            offscreen.rotate(Math.toRadians(degrees), xs, ys);
        }
    }

    public static void textLeft(double x, double y, String text) {
        if (text == null) {
            throw new IllegalArgumentException();
        } else {
            offscreen.setFont(font);
            FontMetrics metrics = offscreen.getFontMetrics();
            double xs = scaleX(x);
            double ys = scaleY(y);
            int hs = metrics.getDescent();
            offscreen.drawString(text, (float)xs, (float)(ys + (double)hs));
            draw();
        }
    }

    public static void textRight(double x, double y, String text) {
        if (text == null) {
            throw new IllegalArgumentException();
        } else {
            offscreen.setFont(font);
            FontMetrics metrics = offscreen.getFontMetrics();
            double xs = scaleX(x);
            double ys = scaleY(y);
            int ws = metrics.stringWidth(text);
            int hs = metrics.getDescent();
            offscreen.drawString(text, (float)(xs - (double)ws), (float)(ys + (double)hs));
            draw();
        }
    }

    /** @deprecated */
    @Deprecated
    public static void show(int t) {
        show();
        pause(t);
        enableDoubleBuffering();
    }

    public static void pause(int t) {
        try {
            Thread.sleep((long)t);
        } catch (InterruptedException var2) {
            System.out.println("Error sleeping");
        }

    }

    public static void show() {
        onscreen.drawImage(offscreenImage, 0, 0, (ImageObserver)null);
        frame.repaint();
    }

    private static void draw() {
        if (!defer) {
            show();
        }

    }

    public static void enableDoubleBuffering() {
        defer = true;
    }

    public static void disableDoubleBuffering() {
        defer = false;
    }

    public static void save(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException();
        } else {
            File file = new File(filename);
            String suffix = filename.substring(filename.lastIndexOf(46) + 1);
            if ("png".equalsIgnoreCase(suffix)) {
                try {
                    ImageIO.write(onscreenImage, suffix, file);
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            } else if ("jpg".equalsIgnoreCase(suffix)) {
                WritableRaster raster = onscreenImage.getRaster();
                WritableRaster newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[]{0, 1, 2});
                DirectColorModel cm = (DirectColorModel)onscreenImage.getColorModel();
                DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(), cm.getRedMask(), cm.getGreenMask(), cm.getBlueMask());
                BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false, (Hashtable)null);

                try {
                    ImageIO.write(rgbBuffer, suffix, file);
                } catch (IOException var9) {
                    var9.printStackTrace();
                }
            } else {
                System.out.println("Invalid image file type: " + suffix);
            }

        }
    }

    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(frame, "Use a .png or .jpg extension", 1);
        chooser.setVisible(true);
        String filename = chooser.getFile();
        if (filename != null) {
            save(chooser.getDirectory() + File.separator + chooser.getFile());
        }

    }

    public static boolean isMousePressed() {
        synchronized(mouseLock) {
            return isMousePressed;
        }
    }

    /** @deprecated */
    @Deprecated
    public static boolean mousePressed() {
        synchronized(mouseLock) {
            return isMousePressed;
        }
    }

    public static double mouseX() {
        synchronized(mouseLock) {
            return mouseX;
        }
    }

    public static double mouseY() {
        synchronized(mouseLock) {
            return mouseY;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        synchronized(mouseLock) {
            mouseX = userX((double)e.getX());
            mouseY = userY((double)e.getY());
            isMousePressed = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        synchronized(mouseLock) {
            isMousePressed = false;
        }
    }

    public void mouseDragged(MouseEvent e) {
        synchronized(mouseLock) {
            mouseX = userX((double)e.getX());
            mouseY = userY((double)e.getY());
        }
    }

    public void mouseMoved(MouseEvent e) {
        synchronized(mouseLock) {
            mouseX = userX((double)e.getX());
            mouseY = userY((double)e.getY());
        }
    }

    public static boolean hasNextKeyTyped() {
        synchronized(keyLock) {
            return !keysTyped.isEmpty();
        }
    }

    public static char nextKeyTyped() {
        synchronized(keyLock) {
            if (keysTyped.isEmpty()) {
                throw new NoSuchElementException("your program has already processed all keystrokes");
            } else {
                return (Character)keysTyped.remove(keysTyped.size() - 1);
            }
        }
    }

    public static boolean isKeyPressed(int keycode) {
        synchronized(keyLock) {
            return keysDown.contains(keycode);
        }
    }

    public void keyTyped(KeyEvent e) {
        synchronized(keyLock) {
            keysTyped.addFirst(e.getKeyChar());
        }
    }

    public void keyPressed(KeyEvent e) {
        synchronized(keyLock) {
            keysDown.add(e.getKeyCode());
        }
    }

    public void keyReleased(KeyEvent e) {
        synchronized(keyLock) {
            keysDown.remove(e.getKeyCode());
        }
    }

    public static void main(String[] args) {
        square(0.2D, 0.8D, 0.1D);
        filledSquare(0.8D, 0.8D, 0.2D);
        circle(0.8D, 0.2D, 0.2D);
        setPenColor(BOOK_RED);
        setPenRadius(0.02D);
        arc(0.8D, 0.2D, 0.1D, 200.0D, 45.0D);
        setPenRadius();
        setPenColor(BOOK_BLUE);
        double[] x = new double[]{0.1D, 0.2D, 0.3D, 0.2D};
        double[] y = new double[]{0.2D, 0.3D, 0.2D, 0.1D};
        filledPolygon(x, y);
        setPenColor(BLACK);
        text(0.2D, 0.5D, "black text");
        setPenColor(WHITE);
        text(0.8D, 0.8D, "white text");
    }

    static {
        BLACK = Color.BLACK;
        BLUE = Color.BLUE;
        CYAN = Color.CYAN;
        DARK_GRAY = Color.DARK_GRAY;
        GRAY = Color.GRAY;
        GREEN = Color.GREEN;
        LIGHT_GRAY = Color.LIGHT_GRAY;
        MAGENTA = Color.MAGENTA;
        ORANGE = Color.ORANGE;
        PINK = Color.PINK;
        RED = Color.RED;
        WHITE = Color.WHITE;
        YELLOW = Color.YELLOW;
        BOOK_BLUE = new Color(9, 90, 166);
        BOOK_LIGHT_BLUE = new Color(103, 198, 243);
        BOOK_RED = new Color(150, 35, 31);
        PRINCETON_ORANGE = new Color(245, 128, 37);
        DEFAULT_PEN_COLOR = BLACK;
        DEFAULT_CLEAR_COLOR = WHITE;
        width = 512;
        height = 512;
        defer = false;
        mouseLock = new Object();
        keyLock = new Object();
        DEFAULT_FONT = new Font("SansSerif", 0, 16);
        std = new StdDraw();
        isMousePressed = false;
        mouseX = 0.0D;
        mouseY = 0.0D;
        keysTyped = new LinkedList();
        keysDown = new TreeSet();
        init();
    }
}