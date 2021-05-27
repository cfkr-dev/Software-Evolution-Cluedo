package configs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import static ui.GUIClient.loadImage;

public class WindowUtilities {
    private static int width;
    private static int lastWidth;
    private static int height;
    private static int lastheight;

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

    public static ImageIcon resizeImage(ImageIcon imageToResize){
        ImageIcon img = new ImageIcon(loadImage(imageToResize.getDescription()));
        BufferedImage bi = new BufferedImage(
                img.getIconWidth(),
                img.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        img.paintIcon(null, g, 0,0);
        g.dispose();
        Image dimg = bi.getScaledInstance((int) ((double) (img.getIconWidth()) * ((double) (WindowUtilities.getWidth()) / (double) (WindowUtilities.getLastWidth()))), (int) ((double) (img.getIconHeight()) * ((double) (WindowUtilities.getHeight()) / (double) (WindowUtilities.getLastheight()))),
                Image.SCALE_SMOOTH);
        return new ImageIcon(dimg, imageToResize.getDescription());
    }

}
