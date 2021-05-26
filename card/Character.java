package card;

import game.GameError;
import utilities.WindowUtilities;
import view.PlayerPanelCanvas;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This enum class represents a character card in Cluedo game. There are six characters,
 * Miss Scarlet, Colonel Mustard, Mrs White, The Reverend Green, Mrs Peacock, and Professor Plum.
 * Each of them has a unique starting position on board.<br>
 *
 * Note that this class is also used to symbolically represent the player in game.
 *
 * @author G7EAS
 */
public enum Character implements Card, Serializable {

    Miss_Scarlet("Miss Scarlet", 'S'),
    Colonel_Mustard("Colonel Mustard", 'M'),
    Mrs_White("Mrs White", 'W'),
    The_Reverend_Green("The Reverend Green", 'G'),
    Mrs_Peacock("Mrs Peacock", 'C'),
    Professor_Plum("Professor Plum", 'P');

    public static ArrayList<Character> CHARACTERS = new ArrayList<>();

    private final String name;
    private final char symbolOnBoard;


    Character(String character, char symbol) {
        this.name = character;
        this.symbolOnBoard = symbol;
    }

    static {
        CHARACTERS.addAll(Arrays.asList(values()));
    }

    @Override
    public String toString() {
        return get(ordinal()).name;
    }

    @Override
    public char toStringOnBoard() {
        char s = ' ';
        if (ordinal() < CHARACTERS.size())
            s = CHARACTERS.get(ordinal()).symbolOnBoard;
        return s;
    }

    @Override
    public void resizeImage(Card card) {
        Character ch = (Character) card;
        PlayerPanelCanvas.CHARACTER_IMG[ch.ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.CHARACTER_IMG[ch.ordinal()]);
        PlayerPanelCanvas.CHARACTER_LABELS[ch.ordinal()].setIcon(PlayerPanelCanvas.CHARACTER_IMG[ch.ordinal()]);
    }

    /**
     * This method returns the next character in turn. When current character ends turn,
     * It's useful for the game to know who the next acting character is.
     *
     * @return --- the next character in turn.
     */
    public Character nextCharacter() {
        return get((ordinal() + 1) % CHARACTERS.size());
    }

    /**
     * Get the character whose ordinal is index.
     *
     * @param index --- the index (ordinal)
     * @return --- the character at the given index (ordinal)
     */
    public static Character get(int index) {
        try {
            return CHARACTERS.get(index);
        } catch (GameError e) {
            throw new GameError("Invalid index.");
        }
    }

    public static int getNumberOfCharacters() {
        return CHARACTERS.size();
    }

    @Override
    public JLabel addCard(Card card) {
        Character ch = (Character) card;
        return PlayerPanelCanvas.CHARACTER_LABELS[ch.ordinal()];
    }
}
