package item.card;

import javax.swing.*;
import java.io.Serializable;

/**
 * This interface represents a item.card in Cluedo game. It could be a Character item.card, a Room
 * (Location) item.card, or a weapon item.card.
 * 
 * @author G7EAS
 *
 */
public interface Card extends Serializable {

    /**
     * This is an alternative version of toString() method which returns a single char
     * String to print a symbol on text-based graphical board.
     * 
     * @return --- a single char String to print a symbol on text-based graphical board.
     */
     char toStringOnBoard();
     void resizeImage(Card card);
     JLabel addCard(Card card);

}
