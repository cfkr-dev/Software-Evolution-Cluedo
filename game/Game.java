package game;

import java.util.*;
import card.Location;
import card.Weapon;
import configs.Configs;
import tile.Entrance;
import tile.Position;
import tile.Room;
import tile.RoomTile;
import tile.Tile;
import view.BoardCanvas;
import view.token.WeaponToken;
import card.Card;
import card.Character;

/**
 * This class represents a running Cluedo game.
 *
 * @author G7EAS
 *
 */
public class Game {
    /**
     * the solution created at beginning
     */
    private Suggestion solution;

    /**
     * the game board
     */
    private final Board board;

    /**
     * number of players
     */
    private int numPlayers;

    /**
     * number of dices
     */
    private final int numDices;

    /**
     * all six players (including dummy tokens) as a list
     */
    private final List<Player> players;

    /**
     * after cards are evenly dealt, all remaining cards are in this list.
     */
    private List<Card> remainingCards;

    /**
     * a random number generator
     */
    private static final Random RAN = new Random();

    /**
     * all six weapon tokens as a static final array
     */
    private final WeaponToken[] weaponTokens;

    /**
     * this map keep a record of who knows what card (that is not involved in crime)
     */
    private final Map<Character, Set<Card>> knownCards;

    /**
     * which character is currently acting
     */
    private Character currentPlayer;

    /**
     * who is the winner
     */
    private Character winner;

    /**
     * a StringBuilder to manipulate strings
     */
    private static final StringBuilder BOARD_STRING = new StringBuilder();

    /**
     * a helper boolean for the Easy mode
     */
    private boolean isEasyMode = false;

    private Configs configurations = Configs.getConfiguration();

    /**
     * Construct the game.
     * 
     * @param numPlayers
     *            --- how many (human controlled) players are playing.
     * @param numDices
     *            --- how many dices are used in this game
     */
    public Game(int numPlayers, int numDices) {
        if (numPlayers < configurations.getMinPlayer() || numPlayers > configurations.getMaxPlayer()) {
            throw new GameError("Invalid number of players");
        }
        else {
            board = new Board();
            players = new ArrayList<>(Character.getNumberOfCharacters());
            this.numPlayers = numPlayers;
            this.numDices = numDices;
            winner = null;

            // initialise known cards, now they are all empty
            // Add characters on the board
            knownCards = new HashMap<>();
            for (int i = 0; i < Character.getNumberOfCharacters(); i++) {
                knownCards.put(Character.get(i), new HashSet<>());
                players.add((new Player(Character.get(i), board.getStartPosition(Character.get(i)), false)));
            }

            // last, put six weapons in random rooms
            weaponTokens = createWeaponTokens();
        }
    }

    /**
     * This method create and randomly put six weapons in random rooms.
     */
    private WeaponToken[] createWeaponTokens() {

        // nine rooms
        List<Location> roomList = new ArrayList<>();
        for (int i = 0; i < Location.getNumberOfLocations(); i++) {
            roomList.add(Location.get(i));
        }
        // six weapon tokens
        WeaponToken[] weaponTokens = new WeaponToken[Weapon.getNumberOfWeapons()];

        for (int i = 0; i < Weapon.getNumberOfWeapons(); i++){
            Weapon w = Weapon.get(i);
            int roomNo = RAN.nextInt(roomList.size());
            RoomTile roomTile = board.getAvailableRoomTile(roomList.remove(roomNo));
            roomTile.setHoldingToken(true);
            WeaponToken weaponToken = new WeaponToken(BoardCanvas.WEAPON_TOKEN_IMG[w.ordinal()], w, roomTile);
            weaponTokens[w.ordinal()] = weaponToken;
        }
        return weaponTokens;
    }

    /**
     * This method randomly choose one character, one room, and one weapon to create a
     * solution, then shuffles all remaining cards, and deal them evenly to all players.
     */
    public void creatSolution() {
        remainingCards = new ArrayList<>();

        // let's get all Character cards first
        List<Character> characterCards = new ArrayList<>();
        for (int i = 0; i < Character.getNumberOfCharacters(); i++) {
            characterCards.add(Character.get(i));
        }
        // randomly choose one as the murderer
        Character solCharacter = characterCards.remove(RAN.nextInt(characterCards.size()));
        // then put the rest character cards in the card pile
        remainingCards.addAll(characterCards);

        // then let's get all Location cards
        List<Location> locationCards = new ArrayList<>();
        for (int i = 0; i < Location.getNumberOfLocations(); i++) {
            locationCards.add(Location.get(i));
        }
        // randomly choose one as the crime scene
        Location solLocation = locationCards.remove(RAN.nextInt(locationCards.size()));
        // then put the rest location cards in the card pile
        remainingCards.addAll(locationCards);

        // then let's get all Weapon cards
        List<Weapon> weaponCards = new ArrayList<>();
        for (int i = 0; i < Weapon.getNumberOfWeapons(); i++) {
            weaponCards.add(Weapon.get(i));
        }
        // randomly choose one as the murder weapon
        Weapon solWeapon = weaponCards.remove(RAN.nextInt(weaponCards.size()));
        // then put the rest location cards in the card pile
        remainingCards.addAll(weaponCards);

        // now we have a solution
        solution = new Suggestion(solCharacter, solWeapon, solLocation);
    }

    /**
     * Set the given player as human controlled, give it a name.
     * 
     * @param playerChoice
     *            --- the character chosen by a player
     * @param name
     *            --- the customised name
     */
    public void joinPlayer(Character playerChoice, String name) {
        players.get(playerChoice.ordinal()).setPlaying(true);
        players.get(playerChoice.ordinal()).setName(name);
    }

    /**
     * Kick the given player out, so he / she cannot play any more.
     * 
     * @param character
     *            --- the character to be kicked out
     */
    public void kickPlayerOut(Character character) {
        players.get(character.ordinal()).setPlaying(false);
        numPlayers--;
        if (numPlayers == 1) {
            //search the winner player
            for (Player player : players) {
                if (player.isPlaying()) {
                    setWinner(player.getToken());
                }
            }
        }
    }

    /**
     * This method deals cards evenly to players. Note that this method should be called
     * after the solution is created.
     */
    public void dealCard() {
        if (remainingCards == null) {
            throw new GameError("The solution should be created before dealing cards.");
        }
        else {
            // deal cards randomly and evenly to all players
            while (remainingCards.size() >= numPlayers) {
                Collections.shuffle(remainingCards);
                for (Player player : players) {
                    if (player.isPlaying()) {
                        player.drawACard(remainingCards.remove(RAN.nextInt(remainingCards.size())));
                    }
                }
            }

            // let each player know what card he has, and what card remains undealt
            for (Player player : players) {
                knownCards.get(player.getToken()).addAll(player.getCards());
                knownCards.get(player.getToken()).addAll(remainingCards);
            }
        }
    }

    /**
     * This method sets who the first character is to move.
     */
    public void decideWhoMoveFirst() {
        currentPlayer = Character.Miss_Scarlet;
        while (!getPlayerByCharacter(currentPlayer).isPlaying()) {
            // if this character is kicked out or not controlled by a player, skip him
            currentPlayer = currentPlayer.nextCharacter();
        }
    }

    /**
     * let current player end turn.
     */
    public void currentPlayerEndTurn() {
        currentPlayer = currentPlayer.nextCharacter();
        while (!getPlayerByCharacter(currentPlayer).isPlaying()) {
            // if this character is kicked out or not controlled by a player, skip him
            currentPlayer = currentPlayer.nextCharacter();
        }
    }

    /**
     * Move a character to the given position.
     * 
     * @param character
     *            --- the character to be moved
     * @param position
     *            --- where to move
     */
    public void movePlayer(Character character, Position position) {
        board.movePlayer(getPlayerByCharacter(character), position);
    }

    /**
     * Move a weapon into the given room tile.
     * 
     * @param weapon
     *            --- the character to be moved
     * @param roomTile
     *            --- which room to move into, and on which tile is this token put
     */
    public void moveWeapon(Weapon weapon, RoomTile roomTile) {
        for (WeaponToken wt : weaponTokens) {
            if (wt.getWeapon().equals(weapon)) {
                board.moveWeapon(wt, roomTile);
                break;
            }
        }
    }

    /**
     * Check whether the given character has the given card in hand.
     * 
     * @param character
     *            --- the character
     * @param card
     *            --- the card
     * @return --- true if he has; false is he has not
     */
    public boolean playerHasCard(Character character, Card card) {
        return getPlayerByCharacter(character).hasCard(card);
    }

    /**
     * This method moves the suspect and weapon in the given suggestion into the mentioned
     * location.
     * 
     * @param suggestion
     *            --- the suggestion
     */
    public void moveTokensInvolvedInSuggestion(Suggestion suggestion) {
        moveWeapon(suggestion.weapon, board.getAvailableRoomTile(suggestion.location));
        movePlayer(suggestion.character, configurations.getRoom(suggestion.location.ordinal()));
    }

    /**
     * This method examines the given suggestion, let other players try to refute it, and
     * returns a String that represents other player's "voice" in turn, which is either
     * he/she can refute this suggestion with one card, or he/she can't.
     * 
     * @param suggestion
     *            --- the suggestion
     * @return --- a string for text output, represents whether or not another player can
     *         refute the given suggestion.
     */
    public String refuteSuggestion(Suggestion suggestion) {

        // what cards are known to current player?
        Set<Card> knownCardsForCurrentPlayer = knownCards.get(currentPlayer);

        StringBuilder rejectMsg = new StringBuilder();
        List<Card> cardsInSuggetion = suggestion.asList();
        // shuffle so that it randomly reject the first refutable card
        Collections.shuffle(cardsInSuggetion);

        outer: for (Player p : players) {
            // as long as this player has drawn cards, he can attempt to reject;
            if (p.getToken() != currentPlayer && !p.getCards().isEmpty()) {
                for (Card card : cardsInSuggetion) {
                    if (p.getCards().contains(card)) {
                        rejectMsg.append(p.getToken().toString()).append(" rejects your suggestion with card: ").append(card.toString()).append("\n");
                        // update current player's known cards
                        knownCardsForCurrentPlayer.add(card);
                        continue outer; // only refute one card
                    }
                }
                rejectMsg.append(p.getToken().toString()).append(" cannot reject your suggestion.\n");
            }
        }

        return rejectMsg.toString();
    }

    /**
     * This method checks the accusation. If it is correct, current player wins; if wrong,
     * current player is out.
     * 
     * @param accusation
     *            --- the accusation
     * @return --- true if correct; false if not.
     */
    public boolean checkAccusation(Suggestion accusation) {
        if (solution.equals(accusation)) {
            // win!!
            setWinner(currentPlayer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Let the player roll dices.
     *
     * @return --- an array of integer, whose length is the number of dice, and each
     *         number is the rolled number of individual dice. Here we use 0 to 5 to
     *         represents 1 - 6 (for simplicity when calling graphical update)
     */
    public int[] rollDice() {
        // e.g. two dices can roll out 2 - 12;
        int[] roll = new int[numDices];
        for (int i = 0; i < numDices; i++) {
            roll[i] = RAN.nextInt(6);
        }

        return roll;
    }

    /**
     * This method checks the given character's position, and returns all possible
     * positions to move to. The positions in the list returned will be of a certain
     * order, which is: north tile -> east tile -> south tile -> west tile -> room if
     * standing at an entrance -> exits (entrances) if in a room -> room if via the secret
     * passage in current room. Any position that cannot be accessible will not be added
     * in this list. In particular, a tile on which has another player standing will not
     * be added in.
     *
     * This ensured order is to make the option menu more predictable.
     * 
     * @param character
     *            --- the player
     * @return --- a list of positions that are all movable.
     */
    public List<Position> getMovablePositions(Character character) {

        Player player = getPlayerByCharacter(character);

        List<Position> movablePos = new ArrayList<>();


        // if there are tiles in four directions
        movablePos.add(board.lookNorth(player));
        movablePos.add(board.lookEast(player));
        movablePos.add(board.lookSouth(player));
        movablePos.add(board.lookWest(player));

        // if the player is standing at an entrance to a room
        movablePos.add(board.atEntranceTo(player));

        // if the player is in a room, get the exits
        List<Entrance> entrances = board.lookForExit(player);
        if (entrances != null && !entrances.isEmpty()) {
            movablePos.addAll(entrances);
        }

        // if the player is in a room, and there is a secret passage
        movablePos.add(board.lookForSecPas(player));

        //elimination of non-viable options
        movablePos.removeAll(Collections.singleton(null));

        // check if any other player standing there, then it's not an option
        for (Player existingPlayer : players) {
            movablePos.removeIf(nextPos -> nextPos instanceof Tile && nextPos.equals(existingPlayer.getPosition()));
        }
        return movablePos;
    }

    /**
     * Whether game has a winner (i.e. game end)
     * 
     * @return --- true if game is still running, there is no winner yet; false if not.
     */
    public boolean isGameRunning() {
        return winner == null;
    }

    /**
     * Is the game run on Easy mode?
     * 
     * @return --- true if the game run on Easy mode, or false if not.
     */
    public boolean isEasyMode() {
        return isEasyMode;
    }

    /**
     * Set the game to easy mode (so that the game will remember clues for player
     *
     */
    public void setEasyMode(boolean isEasyMode) {
        this.isEasyMode = isEasyMode;
    }

    /**
     * Get the solution
     * 
     * @return --- the solution
     */
    public Suggestion getSolution() {
        return solution;
    }

    /**
     * Get the winner.
     * 
     * @return --- winner
     */
    public Character getWinner() {
        return winner;
    }

    /**
     * Set a player as winner.
     * 
     * @param character
     *            --- the player
     */
    public void setWinner(Character character) {
        winner = character;
    }

    /**
     * Get the game board
     * 
     * @return --- the game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Get the player who needs to move.
     * 
     * @return --- the current player
     */
    public Character getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * A helper method to get the corresponding Player of given Character.
     * 
     * @param character
     *            --- the given character
     * @return --- the corresponding Player of given Character
     */
    public Player getPlayerByCharacter(Character character) {
        return players.get(character.ordinal());
    }

    /**
     * Get all players (including dummy token not controlled by human).
     * 
     * @return --- all players (including dummy token not controlled by human) as a list
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Get all playable characters, i.e. those who hasn't been chosen by any player yet.
     * 
     * @return --- all playable characters as a list
     */
    public List<Character> getPlayableCharacters() {
        List<Character> playableCharacters = new ArrayList<>();

        for (Player p : players) {
            if (!p.isPlaying()) {
                // only those who hasn't been chosen
                playableCharacters.add(p.getToken());
            }
        }

        return playableCharacters;
    }

    /**
     * Get the player's position.
     * 
     * @param character
     *            --- the player
     * @return --- the player's position
     */
    public Position getPlayerPosition(Character character) {
        return getPlayerByCharacter(character).getPosition();
    }

    /**
     * get the start position of given character.
     * 
     * @param character
     *            --- the character
     * @return --- the start position of this character
     */
    public Tile getStartPosition(Character character) {
        return board.getStartPosition(character);
    }

    /**
     * Get the remaining cards as a list. Note that the returned list could be empty if
     * all cards are dealt.
     * 
     * @return --- the remaining cards as a list
     */
    public List<Card> getRemainingCards() {
        return remainingCards;
    }

    /**
     * This method gets all cards that is known as not involved in crime.
     * 
     * @return --- all cards that is known as not involved in crime.
     */
    public Set<Card> getKnownCards() {
        return knownCards.get(currentPlayer);
    }

    /**
     * Get how many steps left for the player to move.
     * 
     * @param character
     *            --- the player
     * @return --- how many steps left for the player to move.
     */
    public int getRemainingSteps(Character character) {
        return getPlayerByCharacter(character).getRemainingSteps();
    }

    /**
     * Set how many steps left for the player to move.
     * 
     * @param character
     *            --- the player
     * @param remainingSteps
     *            --- how many steps left for the player to move.
     */
    public void setRemainingSteps(Character character, int remainingSteps) {
        getPlayerByCharacter(character).setRemainingSteps(remainingSteps);
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
        return board.getAvailableRoomTile(location);
    }

    /**
     * Get all weapon tokens as a list
     * 
     * @return --- all weapon tokens
     */
    public WeaponToken[] getWeaponTokens() {
        return weaponTokens;
    }

    /**
     * This method returns the ASCII, text-based game board as a String. On this board,
     * players' position, weapon's position are updated.
     * 
     * @return --- the text-based game board to print in console
     */
    public String getBoardString() {

        // first clear the StringBuilder
        BOARD_STRING.delete(0, BOARD_STRING.length());

        BOARD_STRING.append("=======Game Board=======\n");

        int width = configurations.getBoardWidth() + 1;

        // get the canvas first
        char[] boardChars = configurations.getUiStringB().toCharArray();

        // draw players by replacing his character on his position
        for (Player p : players) {
            Position pos = p.getPosition();
            if (pos instanceof Tile) {
                // normal tile
                Tile tile = (Tile) pos;
                int index = tile.x + tile.y * width;
                boardChars[index] = p.getToken().toStringOnBoard();
            } else if (pos instanceof Room) {
                // inside a room
                Room room = (Room) pos;
                RoomTile roomTile = getAvailableRoomTile(room.getRoom());
                int index = roomTile.getX() + roomTile.getY() * width;
                boardChars[index] = p.getToken().toStringOnBoard();
            }
        }

        // draw the weapon tokens by replacing its character on his position
        for (WeaponToken w : weaponTokens) {
            RoomTile roomTile = w.getRoomTile();
            int index = roomTile.getX() + roomTile.getY() * width;
            boardChars[index] = w.getWeapon().toStringOnBoard();
        }

        BOARD_STRING.append(boardChars);
        BOARD_STRING.append("========================\n");

        // put remaining cards after the ASCII board
        if (!remainingCards.isEmpty()) {
            BOARD_STRING.append("[Remaining cards]:\n");
            for (Card c : remainingCards) {
                BOARD_STRING.append(c.toString());
                BOARD_STRING.append("\n");
            }
        }

        // shows what cards are in the current player's hand
        Player player = getPlayerByCharacter(currentPlayer);

        BOARD_STRING.append("[Cards in hand]:\n");
        for (Card c : player.getCards()) {
            BOARD_STRING.append(c.toString());
            BOARD_STRING.append("\n");
        }

        BOARD_STRING.append("[Salary]: ");
        BOARD_STRING.append(player.getSalary().getCoins());
        BOARD_STRING.append("\n");

        BOARD_STRING.append("========================\n");
        BOARD_STRING.append("Type \"help\" for help\n");
        BOARD_STRING.append("Type \"coins help\" for see your salary\n");
        return BOARD_STRING.toString();
    }

    /**
     *If the player who wants to use coins has sufficient salary to pay for the specific operation, the cost of the operation is subtracted.
     *
     * @param currentPlayer --- player who takes the action to spend the money on his turn
     * @param tax --- coin tax for performing the special action
     * @return --- if it was possible to carry out the operation and subtract the cost
     */
    public boolean hasSalaryPlayer(Character currentPlayer, int tax) {
        Player player = getPlayerByCharacter(currentPlayer);
        if (player.feasibleOperation(tax)) {
           player.setSalary(tax);
           return true;
        }
        else {
            return false;
        }
    }
}
