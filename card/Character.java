package card;

import game.GameError;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * This enum class represents a character card in Cluedo game. There are six characters,
 * Miss Scarlet, Colonel Mustard, Mrs White, The Reverend Green, Mrs Peacock, and
 * Professor Plum. Each of them has a unique starting position on board.<br>
 * <br>
 * Note that this class is also used to symbolically represent the player in game.
 * 
 * @author Hector
 * 
 */
public enum Character implements Card {

    Miss_Scarlet("Miss Scarlet", 'S'), Colonel_Mustard("Colonel Mustard", 'M'), Mrs_White("Mrs White", 'W'), The_Reverend_Green("The Reverend Green", 'G'), Mrs_Peacock("Mrs Peacock", 'C'), Professor_Plum("Professor Plum", 'P');

    public static ArrayList<Character> BY_LABEL = new ArrayList<>();

    private String label;
    private char character;

    Character(String label, char character) {
        this.label = label;
        this.character = character;
    }

    static {
        BY_LABEL.addAll(Arrays.asList(values()));
    }

    @Override
    public String toString() {
        return get(ordinal()).label;
    }

    /**
     * This method returns the next character in turn. When current character ends turn,
     * It's useful for the game to know who the next acting character is.
     * 
     * @return --- the next character in turn.
     */
    public Character nextCharacter() {
        return get(ordinal()+ 1 % values().length);
    }

    @Override
    public char toStringOnBoard() {
        char s = ' ';
        if (ordinal() < 6)
        s = BY_LABEL.get(ordinal()).character;
        return s;
    }

    /**
     * Get the character whose ordinal is index.
     * 
     * @param index
     *            --- the index (ordinal)
     * @return --- the character at the given index (ordinal)
     */
    public static Character get(int index) {
        try {
            return BY_LABEL.get(index);
        }catch (GameError e){
            throw new GameError("Invalid index.");
        }
    }
}
