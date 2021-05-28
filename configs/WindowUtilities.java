package configs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import static ui.GUIClient.loadImage;

/**
 * This class is made for configure the status of the screen's resolution and allow resizing
 *
 * @author G7EAS
 */

public class WindowUtilities {

    private static int width;
    private static int height;
    /* The two following parameters are used to calculate the proportion of the old and new resize */
    private static int lastWidth;
    private static int lastheight;
    private static int fullWidth;
    private static int fullHeight;

    public static int getFullWidth() {
        return fullWidth;
    }

    public static void setFullWidth(int fullWidth) {
        WindowUtilities.fullWidth = fullWidth;
    }

    public static int getFullHeight() {
        return fullHeight;
    }

    public static void setFullHeight(int fullHeight) {
        WindowUtilities.fullHeight = fullHeight;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setWidth(int w) {
        width = w;
    }

    public static void setHeight(int h) {
        height = h;
    }

    public static int getLastWidth() {
        return lastWidth;
    }

    public static int getLastheight() {
        return lastheight;
    }

    public static void setLastWidth(int lastWidth) {
        WindowUtilities.lastWidth = lastWidth;
    }

    public static void setLastheight(int lastheight) {
        WindowUtilities.lastheight = lastheight;
    }

    /**
     *
     * @param imageToResize The image to resize
     * @return The ImageIcon with the new scales
     */
    public static ImageIcon resizeImage(ImageIcon imageToResize){
        /* Loads the image with its description */
        ImageIcon img = new ImageIcon(loadImage(imageToResize.getDescription()));
        BufferedImage bi = new BufferedImage(
                img.getIconWidth(),
                img.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        img.paintIcon(null, g, 0,0);
        g.dispose();
        /* Calculate the new measures dividing the new measure with the old one */
        Image dimg = bi.getScaledInstance((int) ((double) (img.getIconWidth()) * ((double) (WindowUtilities.getWidth()) / (double) (WindowUtilities.getLastWidth()))), (int) ((double) (img.getIconHeight()) * ((double) (WindowUtilities.getHeight()) / (double) (WindowUtilities.getLastheight()))),
                Image.SCALE_SMOOTH);
        return new ImageIcon(dimg, imageToResize.getDescription());
    }

}
