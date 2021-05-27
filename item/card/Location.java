package item.card;

import game.GameError;
import configs.WindowUtilities;
import view.PlayerPanelCanvas;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This enum class represents a Room item.card in Cluedo game. There are nine rooms,
 * Kitchen, Ball room, Conservatory, Billard Room, Library, Study, Hall, Lounge, and Dining Room.<br>
 *
 * Note that this class is also used to symbolically represent the Room position in game.
 * 
 * @author G7EAS
 *
 */
public enum Location implements Card, Serializable {

    Kitchen("Kitchen", ' '),
    Ball_Room("Ball Room", ' '),
    Conservatory("Conservatory", ' '),
    Billard_Room("Billard Room", ' '),
    Library("Library", ' '),
    Study("Study", ' '),
    Hall("Hall", ' '),
    Lounge("Lounge", ' '),
    Dining_Room("Dining Room", ' ');

    private static final ArrayList<Location> LOCATIONS = new ArrayList<>();

    private final String name;
    private final char symbolOnBoard;

    static {
        LOCATIONS.addAll(Arrays.asList(values()));
    }

    Location(String location, char symbol) {
        this.name = location;
        this.symbolOnBoard = symbol;
    }


    @Override
    public String toString() {
        return get(ordinal()).name;
    }

    @Override
    public char toStringOnBoard() {
        char s = ' ';
        if (ordinal() < LOCATIONS.size())
            s = LOCATIONS.get(ordinal()).symbolOnBoard;
        return s;
    }

    @Override
    public void resizeImage(Card card) {
        Location loc = (Location) card;
        PlayerPanelCanvas.LOCATION_IMG[loc.ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.LOCATION_IMG[loc.ordinal()]);
        PlayerPanelCanvas.LOCATION_LABELS[loc.ordinal()].setIcon(PlayerPanelCanvas.LOCATION_IMG[loc.ordinal()]);
    }

    @Override
    public JLabel addCard(Card card) {
        Location loc  = (Location) card;
        return PlayerPanelCanvas.LOCATION_LABELS[loc.ordinal()];
    }

    /**
     * Get the location whose ordinal is index.
     * 
     * @param index
     *            --- the index (ordinal)
     * @return --- the location at the given index (ordinal)
     */
    public static Location get(int index) {
        try {
            return LOCATIONS.get(index);
        } catch (GameError e) {
            throw new GameError("Invalid index.");
        }
    }


    /**
     * Get the number of locations.
     *
     * @return --- the array of locations's size
     */
    public static int getNumberOfLocations() {
        return LOCATIONS.size();
    }

}
