package net.ulfheim;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Michael Driscoll on 3/5/17.
 *
 * Given an image, create an oval portrait with drop-shadow.
 */
public class Cameo {

    private BufferedImage image;
    private int xCenter;
    private double xAxisSq;
    private int yCenter;
    private double yAxisSq;

    public Cameo(String filename) throws IOException {
        image = ImageIO.read(new File(filename));
        xCenter = image.getWidth()/2;
        yCenter = image.getHeight()/2;
        double xAxis = image.getWidth() * 0.25;
        double yAxis = image.getHeight() * 0.4;
        xAxisSq = xAxis * xAxis;
        yAxisSq = yAxis * yAxis;
    }

    private double ellipseVal(int x, int y) {
        return (x-xCenter)*(x-xCenter)/xAxisSq + (y-yCenter)*(y-yCenter)/yAxisSq;
    }

    private boolean isInEllipse(int x, int y) {
        return ellipseVal(x, y) <= 1.0;
    }

    public void maskEllipse() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (!isInEllipse(x, y)) {
                    image.setRGB(x, y, 0);
                }
            }
        }
    }

    public static double shadowSpread = 0.88;

    public void addDropShadow() {
        double v, level;
        int mask;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                v = ellipseVal(x, y);
                if (v < 1 && v > shadowSpread) {
                    level = (1-v) / (1-shadowSpread);
                    mask = (int) (255 * level);
                    if (mask < 0) {
                        mask = 0;
                    }
                    image.setRGB(x, y, mask << 24 | 0x7f7f7f);
                }
            }
        }
    }

    public void writePng(String outfile) throws IOException {
        File output = new File(outfile);
        ImageIO.write(image, "png", output);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java net.ulfheim.Cameo infile.png outfile.png");
            System.exit(1);
        }

        Cameo cameo = null;
        try {
            cameo = new Cameo(args[0]);
        } catch (IOException e) {
            System.err.println("Error opening " + args[0] + ": " + e);
            System.exit(1);
        }
        cameo.maskEllipse();
        cameo.addDropShadow();
        try {
            cameo.writePng(args[1]);
        } catch (IOException e) {
            System.err.println("Error writing " + args[1] + ": " + e);
            System.exit(1);
        }
    }
}
