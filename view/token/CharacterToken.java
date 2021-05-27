package view.token;

import javax.swing.ImageIcon;
import item.card.Character;

/**
 * This class represents a character token on board. It remembers where to draw tokens
 * (outside-room positions are remembered by game.Player class). Also, for GUI mode, each
 * character token has a custom tooltip to show a better-looking tooltip in no-brainer
 * mode.
 * 
 * @author G7EAS
 *
 */

public class CharacterToken extends AbstractToken {

    /**
     * the character
     */
    private final Character character;

    /**
     * Construct a character token on board.
     * 
     * @param img
     *            --- the image used to draw this weapon token on board
     */
    public CharacterToken(ImageIcon img, Character character) {
        super(img, null);
        this.character = character;
    }

    /**
     * which character is it?
     * 
     * @return --- which character
     */
    public Character getCharacter() {
        return character;
    }

}
