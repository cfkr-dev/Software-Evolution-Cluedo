package card;

import game.GameError;
import utilities.WindowUtilities;
import view.PlayerPanelCanvas;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This enum class represents a Weapon card in Cluedo game. There are six weapons,
 * Candlestick, Dagger, Lead Pipe, Revolver, Rope, and Spanner.<br>
 *
 * Note that this class is also used to symbolically represent the weapon token in game.
 * 
 * @author G7EAS
 *
 */
public enum Weapon implements Card {

    Candlestick("Candlestick", 'c'),
    Dagger("Dagger", 'd'),
    Lead_Pipe("Lead Pipe", 'p'),
    Revolver("Revolver", 'g'),
    Rope("Rope", 'r'),
    Spanner("Spanner", 's');

    public static ArrayList<Weapon> WEAPONS = new ArrayList<>();

    private final String name;
    private final char symbolOnBoard;

    static {
        WEAPONS.addAll(Arrays.asList(values()));
    }

    Weapon(String weapon, char symbol) {
        this.name = weapon;
        this.symbolOnBoard = symbol;
    }

    @Override
    public String toString() {
        return get(ordinal()).name;
    }

    @Override
    public char toStringOnBoard() {
        char s = ' ';
        if (ordinal() < WEAPONS.size())
            s = WEAPONS.get(ordinal()).symbolOnBoard;
        return s;
    }

    @Override
    public void resizeImage(Card card) {
        Weapon loc = (Weapon) card;
        PlayerPanelCanvas.WEAPON_IMG[loc.ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.WEAPON_IMG[loc.ordinal()]);
        PlayerPanelCanvas.WEAPON_LABELS[loc.ordinal()].setIcon(PlayerPanelCanvas.WEAPON_IMG[loc.ordinal()]);
    }

    /**
     * Get the weapon whose ordinal is index.
     * 
     * @param index
     *            --- the index (ordinal)
     * @return --- the weapon at the given index (ordinal)
     */
    public static Weapon get(int index) {
        try {
            return WEAPONS.get(index);
        } catch (GameError e) {
            throw new GameError("Invalid index.");
        }
    }

    public static int getNumberOfWeapons() {
        return WEAPONS.size();
    }

}
