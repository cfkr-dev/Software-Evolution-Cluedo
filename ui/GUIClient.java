package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import item.card.Card;
import item.card.Character;
import item.card.Location;
import item.card.Weapon;
import configs.Configs;
import game.*;
import tile.Position;
import tile.Room;
import tile.RoomTile;
import configs.WindowUtilities;
import view.BoardCanvas;
import view.CustomMenu;
import view.PlayerPanelCanvas;
import view.dialogs.*;
import view.token.CharacterToken;
import view.token.WeaponToken;

/**
 * A GUI client for Cluedo game.
 *
 * @author G7EAS
 */

public class GUIClient extends JFrame {

    private Timer resizingTimer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (WindowUtilities.getWidth() < 1008 || WindowUtilities.getHeight() < 658) {
                resetDimension(new Dimension(1024, 720));
                window.setSize(new Dimension(1024, 720));
                JOptionPane.showOptionDialog(window, "You can't resize under 1024×720", "Alert on resizing", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
            }
            BoardCanvas.refreshScreen();
            playerPanel.refreshScreen();
            playerPanel.resetImages();
            LEFT_PANEL_WIDTH = WindowUtilities.getWidth() / 2;
            RIGHT_PANEL_WIDTH = WindowUtilities.getWidth() / 2;
            HEIGHT = BoardCanvas.BOARD_IMG_HEIGHT;
            boardPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, HEIGHT));
            playerPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, HEIGHT));
            repaint();
        }
    });

    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * the main window
     */
    private JPanel window;

    /**
     * game board on left
     */
    private BoardCanvas boardPanel;

    /**
     * player panel on right
     */
    private PlayerPanelCanvas playerPanel;

    /**
     * The height of main frame
     */
    public static int HEIGHT;

    /**
     * The width of game board (left panel)
     */
    public static int LEFT_PANEL_WIDTH;

    /**
     * Tthe width of game board (right panel)
     */
    public static int RIGHT_PANEL_WIDTH;

    /**
     * The main features of the game
     */
    private final Configs configurations = Configs.getConfiguration();

    // ============= models ===================

    /**
     * the game
     */
    private Game game;

    /**
     * the number of players
     */
    private int numPlayers;

    /**
     * the number of dices
     */
    private int numDices;

    /**
     * Construct a GUI to run Cluedo
     */
    public GUIClient() {
        welcomeScreen();
    }

    /**
     * Initialise the main frame, menuBar, and a welcome screen
     */
    private void welcomeScreen() {

        this.setTitle("Cluedo");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // =================== Let's make menu first ===================
        JMenuBar menuBar = new CustomMenu(this);
        this.setJMenuBar(menuBar);

        // ============ then the welcome screen =====================
        window = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.drawImage(INIT_SCREEN, 0, 0, this.getWidth(), this.getHeight(), null);
            }
        };

        window.setPreferredSize(screenSize);
        getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                WindowUtilities.setFullWidth(GUIClient.super.getWidth());
                WindowUtilities.setFullHeight(GUIClient.super.getHeight());
                WindowUtilities.setWidth(e.getComponent().getWidth());
                WindowUtilities.setHeight(e.getComponent().getHeight());
            }
        });

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.add(window);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Pop up a dialog to setup how many players and how many dices are used in game
     */
    public void setupNumPlayers() {
        new NumberSetupDialog(this, SwingUtilities.windowForComponent(this),
                "Setup Wizard");
    }

    /**
     * Get the game's solution cards
     */
    public Suggestion getSolution() {
        return game.getSolution();
    }

    /**
     * Displays the game solution on screen
     */
    public void displaySolution() {
        new SolutionDialog(this, SwingUtilities.windowForComponent(this), "Game solution");
    }

    /**
     * Pop up a dialog to join players
     */
    public void setupPlayers() {
        new PlayerSetupDialog(this, SwingUtilities.windowForComponent(this),
                "Join Players");
    }

    /**
     * Initialise the game with the given number of players and number of dices
     *
     * @param numPlayers --- how many players
     * @param numDices   --- how many dices are used in game
     */
    public void createNewGame(int numPlayers, int numDices) {
        this.numPlayers = numPlayers;
        this.numDices = numDices;
        game = new Game(numPlayers, numDices);
    }

    /**
     * Set the given player as human controlled, give it a name.
     *
     * @param playerChoice --- the character chosen by a player
     * @param name         --- the customised name
     */
    public void joinPlayer(Character playerChoice, String name) {
        game.joinPlayer(playerChoice, name);
    }

    public void resetDimension(Dimension dimension) {
        this.setSize(dimension);
        super.setSize(dimension);
    }

    /**
     * This method construct the in-game GUI, and let the game begin.
     */
    public void startGame() {

        getContentPane().addComponentListener(new ComponentAdapter() {

            // the game's pannel elements is resized
            @Override
            public void componentResized(ComponentEvent e) {

                if (!resizingTimer.isRunning()) {
                    resizingTimer.restart();
                    resizingTimer.setRepeats(false);
                }
                super.componentResized(e);
                WindowUtilities.setWidth(e.getComponent().getWidth());
                WindowUtilities.setHeight(e.getComponent().getHeight());
                WindowUtilities.setFullWidth(GUIClient.super.getWidth());
                WindowUtilities.setFullHeight(GUIClient.super.getHeight());
                if (WindowUtilities.getWidth() <= 0) {
                    WindowUtilities.setWidth(1);
                }
                if (WindowUtilities.getHeight() <= 0) {
                    WindowUtilities.setHeight(1);
                }
            }
        });

        // first let's finish initialising the game
        game.decideWhoMoveFirst();
        game.creatSolution();
        game.dealCard();

        // remove the welcome screen, and load into the game interface
        this.remove(window);
        window = new JPanel();
        window.setLayout(new BoxLayout(window, BoxLayout.X_AXIS));
        double width = WindowUtilities.getWidth();
        double height = WindowUtilities.getHeight();
        window.setPreferredSize(new Dimension((int) width, (int) height));
        WindowUtilities.setLastWidth(1508);
        WindowUtilities.setWidth((int) width);
        WindowUtilities.setLastheight(830);
        WindowUtilities.setHeight((int) height);
        LEFT_PANEL_WIDTH = WindowUtilities.getWidth() / 2;
        RIGHT_PANEL_WIDTH = WindowUtilities.getWidth() / 2;

        // now make the left panel, which is game board
        boardPanel = new BoardCanvas(this);
        boardPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, HEIGHT));
        boardPanel.setBorder(creatTitledBorder("Board"));

        // now the right panel (player panel)
        playerPanel = new PlayerPanelCanvas(this);
        playerPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, HEIGHT));
        playerPanel.setBorder(creatTitledBorder("Player Panel"));
        playerPanel.setLabelCoins(getCurrentPlayer());

        // now put them together
        window.add(boardPanel);
        window.add(playerPanel);

        // add key bindings
        addKeyBindings(window);

        // enable the no brainer mode on menu
        ((CustomMenu) this.getJMenuBar()).enableEasyModeMenu();

        // last, pack and display
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.add(window);
        this.pack();
        this.validate();
        this.setResizable(true);
        this.setVisible(true);
    }

    /**
     * This method updates game board and player panel display according to the model
     * (game).
     */
    public void update() {
        if (game.isGameRunning()) {
            boardPanel.update();
            playerPanel.update();
        } else {

            // The game in finished, so will display the option panel to start new game, see solution or exit
            configurations.getRecords().add(new GameRecord(game.getSolution(), game.getPlayerByCharacter(game.getWinner()).getName(), game.getPlayerByCharacter(game.getCurrentPlayer()).getCards()));

            String[] options = new String[]{"Okay", "Show solution", "Exit"};

            int choice = JOptionPane.showOptionDialog(window, game.getWinner().toString()
                            + " are the only player left. Congratulations, "
                            + game.getPlayerByCharacter(game.getWinner()).getName()
                            + " are the winner!\n" + "Do you want to play again?", "Game ended",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (choice == 0) {
                // start a new game
                setupNumPlayers();
            } else if (choice == 1) {

                displaySolution();
            } else {
                configurations.Serialize();
                System.exit(0);
            }
        }
    }


    /**
     * Pop up a Game Records dialog.
     */
    public void openGameRecords() {
        new GameRecordDialog(this, SwingUtilities.windowForComponent(this), "Game Records");
    }

    /**
     * Pop up a help dialog.
     */
    public void popUpHelp() {
        new HelpDialog(this, SwingUtilities.windowForComponent(this), "Help");
    }

    /**
     * Pop up a dialog for player to make suggestion.
     */
    public void popUpSuggestion() {
        new SuggestionDialog(this, SwingUtilities.windowForComponent(this),
                "Make a Suggestion", false, game);

        if (getPlayerByCharacter(getCurrentPlayer()).feasibleOperation(2)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Your accusation is wrong.\n"
                            + getCurrentPlayer().toString() + " ("
                            + game.getPlayerByCharacter(getCurrentPlayer()).getName()
                            + " ).\n Do you want to spend 2 coins to make another one?",
                    "You are wrong!", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            // start a new game or quit
            if (choice == JOptionPane.OK_OPTION) {
                game.extractSalaryPlayer(getCurrentPlayer(), 2);
                new SuggestionDialog(this, SwingUtilities.windowForComponent(this),
                        "Make a Suggestion", false, game);
                playerPanel.setLabelCoins(getCurrentPlayer());
            }
        }

    }

    /**
     * Pop up a player to change pannel resolution.
     */
    public void openChangeResolution() {
        new ChangeResolutionDialog(this, SwingUtilities.windowForComponent(this), "Change Resolution");
    }

    /**
     * After the player has made his suggestion, this method evaluate the suggestion, and
     * pop up a option panel to show how other players refuted this suggestion.
     *
     * @param sug --- the suggestion made by player
     */
    public void makeSuggestion(Suggestion sug) {
        // move the involved character and weapon into the involved location
        movePlayer(sug.getCharacter(), configurations.getRoom(sug.getLocation().ordinal()));
        moveWeapon(sug.getWeapon(), getAvailableRoomTile(sug.getLocation()));

        // let's see how others refute it
        String s = game.refuteSuggestion(sug);
        JOptionPane.showMessageDialog(window, s, "Refution from other players",
                JOptionPane.INFORMATION_MESSAGE);

        /*
         * After the player has made a suggestion, he can choose to make an accusation
         * right away
         */
        int choice = JOptionPane.showConfirmDialog(window,
                "Do you want to make an accusation right away?", "Make Accusation?",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                ACCUSE_ICON);

        if (choice == JOptionPane.OK_OPTION) {
            popUpAccusation();
        }
    }

    /**
     * Pop up a dialog for player to make accusation.
     */
    public void popUpAccusation() {
        new SuggestionDialog(this, SwingUtilities.windowForComponent(this),
                "Make an Accusation", true, game);
    }

    /**
     * After the player has made his accusation, this method evaluate the suggestion, and
     * pop up a option panel to show if he is the winner or he loses the game.
     *
     * @param accusation --- the accusation made by player
     */
    public void makeAccusation(Suggestion accusation) {
        // move the involved character and weapon into the involved location
        movePlayer(accusation.getCharacter(), configurations.getRoom(accusation.getLocation().ordinal()));
        moveWeapon(accusation.getWeapon(), getAvailableRoomTile(accusation.getLocation()));

        // let's see if the accusation is right or wrong
        boolean isCorrect = game.checkAccusation(accusation);
        if (isCorrect) {
            int choice = JOptionPane.showConfirmDialog(window,
                    "Your accusation is correct.\nCongratulations, "
                            + getCurrentPlayer().toString() + "("
                            + game.getPlayerByCharacter(getCurrentPlayer()).getName()
                            + ") is the winner!\n Do you want to play again?",
                    "WINNER!", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, CORRECT);

            // start a new game or quit
            if (choice == JOptionPane.OK_OPTION) {
                setupNumPlayers();
            } else {
                System.exit(0);
            }

        } else {
            if (getPlayerByCharacter(getCurrentPlayer()).feasibleOperation(5)) {
                int choice = JOptionPane.showConfirmDialog(window,
                        "Your accusation is wrong.\n"
                                + getCurrentPlayer().toString() + " ("
                                + game.getPlayerByCharacter(getCurrentPlayer()).getName()
                                + " ).\n Do you want to spend 5 coins to keep on the game?",
                        "You are wrong!", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);

                // start a new game or quit
                if (choice == JOptionPane.OK_OPTION) {
                    game.extractSalaryPlayer(getCurrentPlayer(), 5);
                    playerPanel.setLabelCoins(game.getCurrentPlayer());
                } else {
                    kickPlayer();
                }
            } else {
                kickPlayer();
            }
        }
    }

    /**
     * Pop up a dialog for player to notify that it's out of the game.
     */
    private void kickPlayer() {
        game.kickPlayerOut(getCurrentPlayer());
        JOptionPane.showMessageDialog(window,
                "Your accusation is WRONG, you are out!", "Incorrect",
                JOptionPane.ERROR_MESSAGE, INCORRECT);
    }

    /**
     * Let the player roll dices.
     *
     * @return --- an array of integer, whose length is the number of dice, and each
     * number is the rolled number of individual dice. Here we use 0 to 5 to
     * represents 1 - 6 (for simplicity when calling graphical update)
     */
    public int[] rollDice() {
        return game.rollDice();
    }

    /**
     * let current player end turn.
     */
    public void currentPlayerEndTurn() {
        game.currentPlayerEndTurn();
        playerPanel.setLabelCoins(getCurrentPlayer());
    }

    /**
     * Move a character to the given position.
     *
     * @param character --- the character to be moved
     * @param position  --- where to move
     */
    public void movePlayer(Character character, Position position) {
        // move the player
        game.movePlayer(character, position);
        // we move the corresponding character token as well
        CharacterToken[] characterTokens = boardPanel.getCharacterTokens();
        if (position instanceof Room) {
            Room room = (Room) position;
            RoomTile destRoomTile = getAvailableRoomTile(room.getRoom());
            characterTokens[character.ordinal()].setRoomTile(destRoomTile);
        }
    }

    /**
     * Move a weapon to the given room.
     *
     * @param weapon   --- the character to be moved
     * @param roomTile --- which room to move into, and on which tile is this token put
     */
    public void moveWeapon(Weapon weapon, RoomTile roomTile) {
        // move the weapon
        game.moveWeapon(weapon, roomTile);
        // move the corresponding weapon token as well
        WeaponToken[] weaponTokens = game.getWeaponTokens();
        weaponTokens[weapon.ordinal()].setRoomTile(roomTile);
    }

    /**
     * Get the current game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the player's salary
     */
    public void extractSalaryPlayer(int tax) {
        game.extractSalaryPlayer(game.getCurrentPlayer(), tax);
    }

    /**
     * Get the number of players
     *
     * @return --- the number of players (3 to 6 inclusive)
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Get the number of dices
     *
     * @return --- the number of dices (1 to 3 inclusive)
     */
    public int getNumDices() {
        return numDices;
    }

    /**
     * Get the game board
     *
     * @return --- the game board
     */
    public Board getBoard() {
        return game.getBoard();
    }

    /**
     * Get all players (including dummy token not controlled by human).
     *
     * @return --- all players (including dummy token not controlled by human) as a list
     */
    public List<Player> getAllPlayers() {
        return game.getPlayers();
    }

    /**
     * Get the player who needs to move.
     *
     * @return --- the current player
     */
    public Character getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    /**
     * A helper method to get the corresponding Player of given Character.
     *
     * @param character --- the given character
     * @return --- the corresponding Player of given Character
     */
    public Player getPlayerByCharacter(Character character) {
        return game.getPlayerByCharacter(character);
    }

    /**
     * Get all weapon tokens as a list
     *
     * @return --- all weapon tokens
     */
    public WeaponToken[] getWeaponTokens() {
        return game.getWeaponTokens();
    }

    /**
     * Get the remaining cards as a list. Note that the returned list could be empty if
     * all cards are dealt.
     *
     * @return --- the remaining cards as a list
     */
    public List<Card> getRemainingCards() {
        return game.getRemainingCards();
    }

    /**
     * Is the game run on easy mode?
     *
     * @return --- true if the game run on easy mode, or false if not.
     */
    public boolean isEasyMode() {
        return game.isEasyMode();
    }

    /**
     * Set the game to easy mode (so that the game will remember clues for
     * player...cheating).
     *
     * @param isEasyMode --- a flag to turn on or off easy mode
     */
    public void setEasyMode(boolean isEasyMode) {
        game.setEasyMode(isEasyMode);
        update();
    }

    /**
     * Whether game has a winner (i.e. game end)
     *
     * @return --- true if game is still running, there is no winner yet; false if not.
     */
    public boolean isGameRunning() {
        if (game == null) {
            return false;
        } else {
            return game.isGameRunning();
        }
    }

    /**
     * This method finds the next empty spot in a given room to display player or weapon
     * tokens.
     *
     * @param location --- which room we want to display a token
     * @return --- an empty spot to display a token in the given room, or null if the room
     * is full (impossible to happen with the default board)
     */
    public RoomTile getAvailableRoomTile(Location location) {
        return game.getAvailableRoomTile(location);
    }


    /**
     * Get the player's position.
     *
     * @param character --- the player
     * @return --- the player's position
     */
    public Position getPlayerPosition(Character character) {
        return game.getPlayerPosition(character);
    }

    /**
     * This method gets all cards that is known as not involved in crime.
     *
     * @return --- all cards that is known as not involved in crime.
     */
    public Set<Card> getKnownCards() {
        return game.getKnownCards();
    }

    /**
     * Get how many steps left for the player to move.
     *
     * @param character --- the player
     * @return --- how many steps left for the player to move.
     */
    public int getRemainingSteps(Character character) {
        return game.getRemainingSteps(character);
    }

    /**
     * Set how many steps left for the player to move.
     *
     * @param character      --- the player
     * @param remainingSteps --- how many steps left for the player to move.
     */
    public void setRemainingSteps(Character character, int remainingSteps) {
        game.setRemainingSteps(character, remainingSteps);
    }

    /**
     * This method does key bindings:<br>
     * <br>
     * W/up arrow for moving up<br>
     * S/down arrow for moving down<br>
     * A/left arrow for moving left<br>
     * D/right arrow for moving right<br>
     * Q fir entering/exiting room<br>
     * E for taking secret passage<br>
     * Space bar for rolling dice<br>
     * Esc for Ending turn.
     */
    private void addKeyBindings(JPanel jpanel) {
        InputMap inputMap = jpanel.getInputMap();
        ActionMap actionMap = jpanel.getActionMap();

        // add UP / W as short-cut key
        Action moveUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnUp();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke('w'), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke('W'), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        actionMap.put("moveUp", moveUp);

        // add DOWN / S as short-cut key
        Action moveDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnDown();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke('s'), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke('S'), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        actionMap.put("moveDown", moveDown);

        // add LEFT / A as short-cut key
        Action moveLeft = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnLeft();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke('a'), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke('A'), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        actionMap.put("moveLeft", moveLeft);

        // add RIGHT / D as short-cut key
        Action moveRight = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnRight();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke('d'), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke('D'), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        actionMap.put("moveRight", moveRight);

        // add Q as short-cut key for entering/exiting room
        Action enterExit = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnEnterExitRoom();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke('q'), "enterExit");
        inputMap.put(KeyStroke.getKeyStroke('Q'), "enterExit");
        actionMap.put("enterExit", enterExit);

        // add E as short-cut key for taking secret passage
        Action secPas = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnSecretOass();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke('e'), "secPas");
        inputMap.put(KeyStroke.getKeyStroke('E'), "secPas");
        actionMap.put("secPas", secPas);

        // add SPACE as short-cut key for rolling dice
        Action rollDice = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnRollDice();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "rollDice");
        actionMap.put("rollDice", rollDice);

        // add ESC as short-cut key for ending turn
        Action endTurn = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerPanel.tryClickOnEndTurn();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "endTurn");
        actionMap.put("endTurn", endTurn);
    }

    /**
     * A helper method to create a titled border
     *
     * @param string --- the border tile
     * @return --- a titled border
     */
    private TitledBorder creatTitledBorder(String string) {
        return BorderFactory
                .createTitledBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createSoftBevelBorder(BevelBorder.RAISED),
                                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)),
                        string, TitledBorder.CENTER, TitledBorder.TOP);
    }

    /**
     * Main method to start the game.
     *
     * @param args --- who cares it in GUI?
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new GUIClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * A helper method to load image
     *
     * @param filename --- the file name
     * @return --- the Image object of the given file
     */
    public static Image loadImage(String filename) {
        URL imageURL = BoardCanvas.class.getResource(IMAGE_PATH + filename);
        try {
            return ImageIO.read(imageURL);
        } catch (IOException e) {
            throw new GameError("Unable to load image: " + filename);
        }
    }

    /**
     * The new pannel resolution is resized
     */
    public void changeResolution(Dimension dimension) {
        this.setSize(dimension);
    }

    /**
     * This String specifies the path for loadImage() to look for images.
     */
    private static final String IMAGE_PATH = "resources/";
    /**
     * The image displayed on welcome screen
     */
    public static final Image INIT_SCREEN = loadImage("Initial_Screen.png");
    /**
     * An icon used to pop a dialog to tell players something is *Correct*
     */
    public static final ImageIcon CORRECT = new ImageIcon(loadImage("Icon_Correct.png"));
    /**
     * An icon used to pop a dialog to tell players something is *Incorrect*
     */
    public static final ImageIcon INCORRECT = new ImageIcon(
            loadImage("Icon_Incorrect.png"));
    /**
     * An icon used to pop a dialog to let players to do accusation
     */
    public static final ImageIcon ACCUSE_ICON = new ImageIcon(
            loadImage("Icon_Accusation.png"));

}
