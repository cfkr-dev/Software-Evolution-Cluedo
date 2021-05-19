package game;

import card.Character;
import card.Location;
import configs.Configs;
import tile.*;
import view.token.WeaponToken;

import java.util.List;

/**
 * This class represents a Cluedo game board.
 *
 * @author G7EAS
 *
 */
public class Board {

    /**
     * board is created as a 2D array of positions
     */
    private final Position[][] board;

    /**
     * six starting tiles for each character
     */
    private final Tile[] startPositions;

    private Configs configurations = Configs.getConfiguration();

    /**
     * Construct a board.
     */
    public Board() {
        configurations.DimensionCounter();

        startPositions = new Tile[Character.values().length];

        String boardString = configurations.getBoardStringB();

        int height = configurations.getBoardHeight();
        int width = configurations.getBoardWidth();

        board = new Position[height][width];
        int index = 0; // index to track chars
        int x, y; // coordinates

        while (index < boardString.length()) {
            x = index % (width + 1);
            y = index / (width + 1);


            // skip the '\n' character
            if (x == width) {
                index++;
            }
            else {
                //Represent anything of the board
                char logicSymbolBoard = boardString.charAt(index);

                // ' ' (space) represents walls and unenterable tiles
                if (logicSymbolBoard == ' ') {
                    board[y][x] = null;
                }

                // walkable tiles, tiles that are out of all rooms
                if (logicSymbolBoard == '0') {
                    board[y][x] = new Tile(x, y);
                }

                // If the tile corresponds to a room
                if (logicSymbolBoard >= '1' && logicSymbolBoard <= '9') {
                    board[y][x] = new RoomTile(configurations.getRoom(java.lang.Character.getNumericValue(logicSymbolBoard) - 1), x, y);
                }

                /*
                 * ';', '<', '=', '>', '?', '%' represents six starting tiles
                 * for player tokens. ';' indicates Scarlet's start position, '<' for Mustard,
                 * and so on.
                 */
                if (logicSymbolBoard >= ';' && logicSymbolBoard <= '@') {
                    Tile starPositionCharacter = new Tile(x, y);
                    board[y][x] = starPositionCharacter;
                    int auxiliar = logicSymbolBoard;
                    startPositions[auxiliar - 59] = starPositionCharacter;
                }

                /*
                 * 'a' - 'i' represents entrance to each room, 'a' is entrance to room '1',
                 * 'b' to room '2', and so on.
                 */
                if (logicSymbolBoard >= 'a' && logicSymbolBoard <= 'i') {
                    int auxiliar = logicSymbolBoard;
                    Entrance entrance = new Entrance(x, y, configurations.getRoom(auxiliar - 97));
                    board[y][x] = entrance;
                    configurations.getRoom(auxiliar - 97).addEntrances(entrance);
                }
            }
            index++;
        }
    }

    /**
     * Get the Position at given coordinate
     *
     * @param x
     *            --- the horizontal coordinate
     * @param y
     *            --- the vertical coordinate
     * @return --- the Position at given coordinate
     */
    public Position getPosition(int x, int y) {
        return board[y][x];
    }

    /**
     * get the start position of given character.
     *
     * @param character
     *            --- the character
     * @return --- the start position of this character
     */
    public Tile getStartPosition(Character character) {
        return startPositions[character.ordinal()];
    }

    /**
     * This method finds the next empty spot in a given room to display player or weapon
     * tokens.
     *
     * @param location
     *            --- which room we want to display a token
     * @return --- an empty spot to display a token in the given room, or null if the room
     *         is full (impossible to happen with the default board)
     */
    public RoomTile getAvailableRoomTile(Location location) {
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                if (board[y][x] instanceof RoomTile) {
                    RoomTile roomTile = (RoomTile) board[y][x];
                    if (roomTile.getRoom() == location && !roomTile.isHoldingToken()) {
                        return roomTile;
                    }
                }
            }
        }
        return null;
    }

    public Boolean ThereIsNoConstraints(Position playerPos, int index) {
        // Player can only look north out of rooms
        if (playerPos instanceof Room) {
            return false;
        }

        Tile playerTile = (Tile) playerPos;
        Boolean[] Constraints= new Boolean[4];

        // boundary check


        Constraints[0]= playerTile.y - 1 < 0; //NorthBoundaryCheck
        Constraints[1]= playerTile.y + 1 > configurations.getBoardHeight() - 1; //SouthBoundaryCheck
        Constraints[2]= playerTile.x + 1 > configurations.getBoardWidth() - 1; //EastBoundaryCheck
        Constraints[3]= playerTile.x - 1 < 0; //WestBoundaryCheck

        if (Constraints[index]) {
            return false;
        }

        // this method should not return a Room

        switch (index) {
            case 0:
                Constraints[0] = board[playerTile.y - 1][playerTile.x] instanceof Room; //NorthInRoom
                break;
            case 1:
                Constraints[1] = board[playerTile.y + 1][playerTile.x] instanceof Room; //SouthInRoom
                break;
            case 2:
                Constraints[2] = board[playerTile.y][playerTile.x + 1] instanceof Room; //EastInRoom
                break;
            case 3:
                Constraints[3] = board[playerTile.y][playerTile.x - 1] instanceof Room; //WestInRoom
                break;

        }

        return !Constraints[index];
    }




    /**
     * Let the player look at north, see which *Tile* or *Entrance* is there. Note that
     * the player cannot see a room even if a room is at north. Also note that the player
     * cannot see anything at north if he is standing inside a room.
     *
     * @param player
     *            --- the player
     * @return --- the Tile or Entrance at north, one step away, if there is one. Or null
     *         if there is a wall or nothing. If the player is in a room, this method will
     *         always return null.
     */
    public Tile lookNorth(Player player) {
        Position playerPos = player.getPosition();
        int index= 0;

        // this method should not return a Room
        if (!ThereIsNoConstraints(playerPos, index)) {
            return null;
        }
        // only return a Tile or a Entrance
        Tile playerTile = (Tile) playerPos;
        return (Tile) board[playerTile.y - 1][playerTile.x];
    }

    /**
     * Let the player look at south, see which *Tile* or *Entrance* is there. Note that
     * the player cannot see a room even if a room is at south. Also note that the player
     * cannot see anything at south if he is standing inside a room.
     *
     * @param player
     *            --- the player
     * @return --- the Tile or Entrance at south, one step away, if there is one. Or null
     *         if there is a wall or nothing. If the player is in a room, this method will
     *         always return null.
     */
    public Tile lookSouth(Player player) {
        Position playerPos = player.getPosition();
        int index= 1;

        // this method should not return a Room
        if (!ThereIsNoConstraints(playerPos, index)) {
            return null;
        }
        // only return a Tile or a Entrance
        Tile playerTile = (Tile) playerPos;
        return (Tile) board[playerTile.y + 1][playerTile.x];
    }

    /**
     * Let the player look at east, see which *Tile* or *Entrance* is there. Note that the
     * player cannot see a room even if a room is at east. Also note that the player
     * cannot see anything at east if he is standing inside a room.
     *
     * @param player
     *            --- the player
     * @return --- the Tile or Entrance at east, one step away, if there is one. Or null
     *         if there is a wall or nothing. If the player is in a room, this method will
     *         always return null.
     */
    public Tile lookEast(Player player) {
        Position playerPos = player.getPosition();
        int index= 2;

        // this method should not return a Room
        if (!ThereIsNoConstraints(playerPos, index)) {
            return null;
        }
        // only return a Tile or a Entrance
        Tile playerTile = (Tile) playerPos;
        return (Tile) board[playerTile.y][playerTile.x + 1];
    }

    /**
     * Let the player look at west, see which *Tile* or *Entrance* is there. Note that the
     * player cannot see a room even if a room is at west. Also note that the player
     * cannot see anything at west if he is standing inside a room.
     *
     * @param player
     *            --- the player
     * @return --- the Tile or Entrance at west, one step away, if there is one. Or null
     *         if there is a wall or nothing. If the player is in a room, this method will
     *         always return null.
     */
    public Tile lookWest(Player player) {
        Position playerPos = player.getPosition();
        int index= 3;

        // this method should not return a Room
        if (!ThereIsNoConstraints(playerPos, index)) {
            return null;
        }
        // only return a Tile or a Entrance
        Tile playerTile = (Tile) playerPos;
        return (Tile) board[playerTile.y][playerTile.x - 1];
    }

    /**
     * Let the player look around, see whether he/she is standing at an entrance to a
     * room.
     *
     * @param player
     *            --- the player
     * @return --- the room that can enter within one step, if there is one. Or null if
     *         there isn't. If the player is in a room, this method will always return
     *         null.
     */
    public Room atEntranceTo(Player player) {
        Position playerPos = player.getPosition();
        if (playerPos instanceof Entrance) {
            Entrance entrance = (Entrance) playerPos;
            return entrance.toRoom();
        } else {
            return null;
        }
    }

    /**
     * Let the player look for exits of the room that he / she is standing in. Note that
     * if the player is not standing in a room, this method will always return null.
     *
     * @param player
     *            --- the player
     * @return --- all exits of current room as a list. If the player is not standing in a
     *         room, this method will always return null.
     */
    public List<Entrance> lookForExit(Player player) {
        Position playerPos = player.getPosition();
        if (playerPos instanceof Room) {
            Room room = (Room) playerPos;
            return room.getEntrances();
        } else {
            return null;
        }
    }

    /**
     * Let the player look for secret passage to another room if he / she is standing in a
     * room. Note that if the player is not standing in a room, this method will always
     * return null.
     *
     * @param player
     *            --- the player
     * @return --- the end of the secret passage if there is one in current room. If not,
     *         this method return null. Or, if the player is not standing in a room, this
     *         method also returns null.
     */
    public Room lookForSecPas(Player player) {
        Position playerPos = player.getPosition();
        if (playerPos instanceof Room) {
            Room room = (Room) playerPos;
            if (room.hasSecPas()) {
                return configurations.getRoom(room.getSecPasTo().ordinal());
            }
        }
        return null;
    }

    /**
     * This method set player's position to another given position, which could be any
     * position on board. In other worlds, this method is not always a reasonable move in
     * normal Cluedo game.<br>
     * <br>
     * Note that this method does no sanity checks, so it should be always guarded by
     * calling lookNorth / lookSouth / lookWest / lookEast / atEntrance / lookForExit in
     * advance.
     *
     * @param player
     *            --- the player
     * @param position
     *            --- where to move to
     */
    public void movePlayer(Player player, Position position) {
        player.setPosition(position);
    }

    /**
     * This method moves a weapon token to another room.
     *
     * @param weaponToken
     *            --- the weapon token
     */
    public void moveWeapon(WeaponToken weaponToken, RoomTile roomTile) {
        weaponToken.setRoomTile(roomTile);
    }
}
