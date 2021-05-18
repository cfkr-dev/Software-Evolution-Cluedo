package game;

/**
 * This class represents a runtime exception in game.
 * 
 * @author G7EAS
 *
 */
@SuppressWarnings("serial")
public class GameError extends RuntimeException {

    /**
     * Construct an error.
     * 
     * @param errMsg
     *            --- the error message string
     */
    public GameError(String errMsg) {
        super(errMsg);
    }

}
