package utilities;

public class WindowUtilities {
    private static int width;
    private static int height;

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

    public static int resizeVerticalDimension(int dimension) {
        return (height * dimension) / 830;
    }

    public static int resizeHorizontalDimension(int dimension) {
        return (width) * dimension / 1508;
    }
}
