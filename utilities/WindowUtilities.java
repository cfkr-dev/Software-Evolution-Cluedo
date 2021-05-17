package utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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

}
