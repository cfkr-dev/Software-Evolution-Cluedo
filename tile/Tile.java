package tile;

import configs.Configs;
import game.GameError;

/**
 * This class represents a single tile on Cluedo game board (i.e. tiles that are out of
 * rooms).
 * 
 * @author G7EAS
 * 
 */
public class Tile extends Position {

    /**
     * the coordinates of this tile
     */
    public final int x, y;

    private Configs configurations = Configs.getConfiguration();

    /**
     * Takes the coordinates and construct a Tile. Coordinate are checked against board's
     * width and height.
     * 
     * @param x
     *            --- horizontal coordinate
     * @param y
     *            --- vertical coordinate
     */
    public Tile(int x, int y) {
        // check
        int width = configurations.getBoardWidth();
        int height = configurations.getBoardHeight();
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            throw new GameError("Invalid Coordinates");
        }

        this.x = x;
        this.y = y;
    }

    @Override
    public String optionString(Position destination) {
        if (destination instanceof Tile) {
            Tile destinationTile = (Tile) destination;
            if (destinationTile.x - this.x == 1) {
                return "Move east.";
            } else if (destinationTile.x - this.x == -1) {
                return "Move west.";
            } else if (destinationTile.y - this.y == 1) {
                return "Move south.";
            } else if (destinationTile.y - this.y == -1) {
                return "Move north.";
            } else {
                throw new GameError("Shouldn't move more than one tile once");
            }
        } else {
            throw new GameError(
                    "Shouldn't move from a tile to a room. Use \"Entrance\" tile");
        }
    }

    @Override
    public String toString() {
        return "[" + x + " , " + y + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        else {
            Tile other = (Tile) obj;
            return ((x == other.x) && (y == other.y));
        }
    }

}
