package view;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import game.Player;
import tile.Entrance;
import tile.Room;
import tile.Tile;
import ui.GUIClient;
import item.card.Card;
import item.card.Character;
import item.card.Location;
import item.card.Weapon;
import configs.WindowUtilities;
import view.dialogs.ExitRoomDialog;
import static ui.GUIClient.loadImage;

/**
 * This is a custom panel for displaying the player panel on the right. The major
 * functionalities are: displaying cards in hand; displaying cards left undealt;
 * displaying dices; providing buttons to move, roll dice, make suggestion and make
 * accusation; and displaying a profile picture.
 *
 * @author G7EAS
 */

public class PlayerPanelCanvas extends JPanel implements ComponentListener {

    // ============ some numbers for swing to set size =============

    /**
     * Panel width
     */
    public static int WIDTH = BoardCanvas.BOARD_IMG_WIDTH;

    /**
     * Panel height
     */
    private static int HEIGHT = BoardCanvas.BOARD_IMG_HEIGHT;

    /**
     * the height of sub-panel for displaying cards in hand
     */
    private static int SOUTH_PANEL_HEIGHT = HEIGHT / 4;

    /**
     * the height of sub-panel for displaying cards left undealt
     */
    private static int NORTH_PANEL_HEIGHT = SOUTH_PANEL_HEIGHT;

    /**
     * the height of sub-panel for displaying buttons, dices, and profile picture
     */
    private static int CENTRE_PANEL_HEIGHT = HEIGHT - (SOUTH_PANEL_HEIGHT
            + NORTH_PANEL_HEIGHT);

    /**
     * the width of the sub-panel for displaying profile picture
     */
    private static int WEST_PANEL_WIDTH = WIDTH / 4;

    /**
     * the width of the button panel on mid-east (of the BorderLayout, not of the
     * world...)
     */
    private static int EAST_PANEL_WIDTH = WIDTH / 2;

    /**
     * the width of the dice panel
     */
    private static int CENTRE_PANEL_WIDTH = WIDTH - (WEST_PANEL_WIDTH + EAST_PANEL_WIDTH);

    /**
     * the padding size on left
     */
    public static final int PADDING_LEFT = 0;

    /**
     * the padding size on right
     */
    public static final int PADDING_RIGHT = 0;

    /**
     * the padding size on top
     */
    private static final int PADDING_TOP = 0;

    private static int FONT_SIZE;

    // ============== swing components ======================

    /**
     * The sub-Panel for displaying remaining cards
     */
    private JPanel remainingCardsPanel;

    /**
     * The sub-panel for displaying cards in hand
     */
    private final JPanel cardsInHandPanel;

    private final JPanel buttonPanel;

    private JPanel dicePanel;

    /**
     * The label for displaying profile picture
     */
    private final JLabel profileLabel;

    /**
     * The labels for displaying dices
     */
    private JLabel[] diceLabels;

    /**
     * The Label for displaying remaining steps of current player
     */
    private final JLabel remainingStepLabel;

    /**
     * The button for entering / exiting room
     *
     */
    private final JButton enterExitRoom;

    private static JButton rollAgain;

    private final JLabel remainingCoins;

    /**
     * The button for moving up
     */
    private final JButton upButton;

    /**
     * The button for taking the secret passage
     */
    private final JButton secPasButton;

    /**
     * The button for moving left
     */
    private final JButton leftButton;

    /**
     * The button for moving down
     */
    private final JButton downButton;

    /**
     * The button for moving right
     */
    private final JButton rightButton;

    /**
     * The button for rolling dice
     */
    private final JButton rollDiceButton;

    /**
     * The button for ending turn
     */
    private final JButton endTurnButton;

    /**
     * The button for making suggestion
     */
    private final JButton suggestionButton;

    /**
     * The button for making accusation
     */
    private final JButton accusationButton;

    // ======== Test ========

    private JLabel remainingCardLabel;

    // ======== Other fields that actually holds logic =========

    /**
     * Game's main GUI
     */
    private final GUIClient gui;

    /**
     * The cards in current player's hand
     */
    private List<Card> cardsInHand;

    /**
     * The remaining cards that are not dealt
     */
    private List<Card> remainingCards;

    /**
     * Current player
     */
    private Character currentPlayer;

    /**
     * An array holding the number rolled by the player
     */
    private int[] diceRolled = null;

    /**
     * How many steps left to move
     */
    private int remainingSteps;

    private void createRemainingCards() {
        // =================== North, remaining cards =====================
        remainingCardsPanel = new JPanel();
        remainingCardsPanel.setBackground(null);
        remainingCardsPanel.setOpaque(false);
        remainingCardsPanel.setPreferredSize(new Dimension(WIDTH, NORTH_PANEL_HEIGHT));
        remainingCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        remainingCardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.addComponentListener(this);
        // a Label to show some artsy fonts
        remainingCardLabel = new JLabel(REMAINING_CARDS_IMAGE, SwingConstants.CENTER);
        remainingCardLabel.setFont(new Font("Calibre", 1, FONT_SIZE));
        remainingCardsPanel.add(remainingCardLabel);

        // display remaining cards.
        remainingCards = gui.getRemainingCards();
        if (remainingCards.isEmpty()) {
            remainingCardLabel.setToolTipText("There is no remaining item.card.");
        } else {
            remainingCardLabel.setToolTipText(
                    "There are " + remainingCards.size() + " remaining cards.");
        }

        for (Card c : remainingCards) {
            remainingCardsPanel.add(c.addCard(c));
        }
    }

    private void createDicePanel(){
        // panel for dices
        dicePanel = new JPanel();
        dicePanel.setBackground(null);
        dicePanel.setOpaque(false);
        dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.X_AXIS));
        dicePanel.setPreferredSize(new Dimension(CENTRE_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));

        // another panel to make the dice centre-aligned
        rollAgain = new JButton();
        rollAgain.setText("Roll again");
        rollAgain.setBackground(Color.WHITE);
        disableRollAgain();
        rollAgain.addActionListener(e -> {
            clickOnRollDice();
            gui.extractSalaryPlayer(1);
            setLabelCoins(gui.getGame().getCurrentPlayer());
            disableRollAgain();
        });

        JPanel diceGroup = new JPanel();


        diceGroup.add(rollAgain);
        diceGroup.setBackground(null);
        diceGroup.setOpaque(false);
        diceGroup.setLayout(new BoxLayout(diceGroup, BoxLayout.Y_AXIS));

        diceLabels = new JLabel[gui.getNumDices()];
        for (int i = 0; i < diceLabels.length; i++) {
            diceLabels[i] = new JLabel();
            diceLabels[i].setBorder(null);
            diceGroup.add(diceLabels[i], Component.CENTER_ALIGNMENT);

            // add gaps between dices. and do not add a gap after the last dice
            if (i != diceLabels.length - 1) {
                int gap = 0;
                // vary the gap according to number of dices
                if (diceLabels.length == 2) {
                    gap = 25;
                } else if (diceLabels.length == 3) {
                    gap = 5;
                }
                diceGroup.add(Box.createRigidArea(new Dimension(gap, gap)),
                        Component.CENTER_ALIGNMENT);
            }
        }

        diceGroup.setAlignmentY(Component.CENTER_ALIGNMENT);
        diceGroup.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        dicePanel.add(diceGroup);
        dicePanel.setBorder(BorderFactory.createEmptyBorder(PADDING_TOP, PADDING_LEFT,
                PADDING_RIGHT, PADDING_RIGHT));
    }

    /**
     * Construct a custom panel for display player related information
     *
     * @param guiClient --- the Main GUI of this game
     */
    public PlayerPanelCanvas(GUIClient guiClient) {

        this.gui = guiClient;

        // ================== BorderLayout =====================
        this.setLayout(new BorderLayout(5, 5));

        createRemainingCards();

        // ============== west, a player's profile picture ===============
        profileLabel = new JLabel();

        profileLabel.setOpaque(false);
        profileLabel
                .setPreferredSize(new Dimension(WEST_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(PADDING_TOP, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));

        // ============== centre, dice or dices ====================

        createDicePanel();

        // ============ east, buttons ===================
        buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setOpaque(false);
        buttonPanel
                .setPreferredSize(new Dimension(EAST_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // first row, a text
        remainingCoins = new JLabel();
        remainingCoins.setBackground(null);
        remainingCoins.setOpaque(false);
        remainingCoins.setFont(new Font("Calibre", 1, FONT_SIZE));
        remainingCoins.setForeground(Color.DARK_GRAY);
        remainingCoins.setAlignmentX(Component.CENTER_ALIGNMENT);
        remainingCoins.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT, PADDING_LEFT));

        remainingStepLabel = new JLabel();
        remainingStepLabel.setBackground(null);
        remainingStepLabel.setOpaque(false);
        remainingStepLabel.setFont(new Font("Calibre", 1, FONT_SIZE));
        remainingStepLabel.setForeground(Color.DARK_GRAY);
        remainingStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        remainingStepLabel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT, PADDING_LEFT));

        // second row, a grid layout to show movement buttons.
        JPanel movePanel = new JPanel();
        movePanel.setBackground(null);
        movePanel.setOpaque(false);
        movePanel.setLayout(new GridLayout(2, 3, 10, 10));
        movePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        movePanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));

        // six buttons for different directions, enter/exit room, and secret passage
        enterExitRoom = createButton(ENTER_DEFAULT_IMG, ENTER_PRESSED_IMG,
                MOVE_DISABLED_IMG, MOVE_BUTTON_SIZE);
        enterExitRoom.setEnabled(false);
        upButton = createButton(UP_DEFAULT_IMG, UP_PRESSED_IMG, MOVE_DISABLED_IMG,
                MOVE_BUTTON_SIZE);
        secPasButton = createButton(SECPAS_DEFAULT_IMG, SECPAS_PRESSED_IMG,
                MOVE_DISABLED_IMG, MOVE_BUTTON_SIZE);
        secPasButton.setEnabled(false);
        leftButton = createButton(LEFT_DEFAULT_IMG, LEFT_PRESSED_IMG, MOVE_DISABLED_IMG,
                MOVE_BUTTON_SIZE);
        downButton = createButton(DOWN_DEFAULT_IMG, DOWN_PRESSED_IMG, MOVE_DISABLED_IMG,
                MOVE_BUTTON_SIZE);
        rightButton = createButton(RIGHT_DEFAULT_IMG, RIGHT_PRESSED_IMG,
                MOVE_DISABLED_IMG, MOVE_BUTTON_SIZE);

        // add listener on them
        enterExitRoom.addActionListener(e -> {
            disableRollAgain();
            clickOnEnterExitRoom();
        });
        upButton.addActionListener(e -> {
            disableRollAgain();
            clickOnUp();
        });
        secPasButton.addActionListener(e -> {
            disableRollAgain();
            clickOnSecretPass();
        });
        leftButton.addActionListener(e -> {
            disableRollAgain();
            clickOnLeft();
        });
        downButton.addActionListener(e -> {
            disableRollAgain();
            clickOnDown();
        });
        rightButton.addActionListener(e -> {
            disableRollAgain();
            clickOnRight();
        });

        // add button into the panel
        movePanel.add(enterExitRoom);
        movePanel.add(upButton);
        movePanel.add(secPasButton);
        movePanel.add(leftButton);
        movePanel.add(downButton);
        movePanel.add(rightButton);

        // third row, another gridLayout to show action buttons.
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(null);
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new GridLayout(2, 2, 10, 10));
        actionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));

        // four action buttons for roll dice, end turn, suggestion, and accusation
        rollDiceButton = createButton(ROLLDICE_DEFAULT_IMG, ROLLDICE_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);
        endTurnButton = createButton(ENDTURN_DEFAULT_IMG, ENDTURN_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);
        suggestionButton = createButton(SUGGESTION_DEFAULT_IMG, SUGGESTION_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);
        accusationButton = createButton(ACCUSATION_DEFAULT_IMG, ACCUSATION_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);

        // add listeners
        rollDiceButton.addActionListener(e -> {
            clickOnRollDice();
        });
        endTurnButton.addActionListener(e -> {
            clickOnEndTurn();
            disableRollAgain();
        });
        suggestionButton.addActionListener(e -> {
            clickOnSuggestion();
            disableRollAgain();
        });
        accusationButton.addActionListener(e -> {
            clickOnAccusation();
            disableRollAgain();
        });

        // add button into the panel
        actionPanel.add(rollDiceButton);
        actionPanel.add(endTurnButton);
        actionPanel.add(suggestionButton);
        actionPanel.add(accusationButton);

        // put them together
        buttonPanel.add(remainingCoins);
        buttonPanel.add(remainingStepLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        buttonPanel.add(movePanel);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        buttonPanel.add(actionPanel);

        // ================= south, cards in hand =================
        cardsInHandPanel = new JPanel();
        cardsInHandPanel.setBackground(null);
        cardsInHandPanel.setOpaque(false);
        cardsInHandPanel.setPreferredSize(new Dimension(WIDTH, SOUTH_PANEL_HEIGHT));
        cardsInHandPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        cardsInHandPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        currentPlayer = gui.getCurrentPlayer();
        cardsInHand = gui.getPlayerByCharacter(currentPlayer).getCards();

        // add cards one by one in a row
        for (Card c : cardsInHand) {
            cardsInHandPanel.add(c.addCard(c));
        }

        // ========== Adding five components together ==============
        this.add(remainingCardsPanel, BorderLayout.NORTH);
        this.add(profileLabel, BorderLayout.WEST);
        this.add(dicePanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.EAST);
        this.add(cardsInHandPanel, BorderLayout.SOUTH);
        this.setVisible(true);

        // update the panel
        update();
        refreshScreen();
    }

    public void refreshScreen() {

        HEIGHT = WindowUtilities.getHeight();
        WIDTH = WindowUtilities.getWidth() / 2;
        SOUTH_PANEL_HEIGHT = HEIGHT / 4;
        NORTH_PANEL_HEIGHT = SOUTH_PANEL_HEIGHT;
        CENTRE_PANEL_HEIGHT = HEIGHT - (SOUTH_PANEL_HEIGHT
                + NORTH_PANEL_HEIGHT);
        EAST_PANEL_WIDTH = WIDTH / 2;
        WEST_PANEL_WIDTH = WIDTH / 4;
        CENTRE_PANEL_WIDTH = WIDTH - (EAST_PANEL_WIDTH + WEST_PANEL_WIDTH);
        MOVE_BUTTON_SIZE = new Dimension(WIDTH / 18, HEIGHT / 15);
        ACTION_BUTTON_SIZE = new Dimension(WIDTH / 11, HEIGHT / 15);
        FONT_SIZE = HEIGHT / 41;

        remainingCardsPanel.setPreferredSize(new Dimension(WIDTH, NORTH_PANEL_HEIGHT));
        buttonPanel.setPreferredSize(new Dimension(EAST_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));
        cardsInHandPanel.setPreferredSize(new Dimension(WIDTH, SOUTH_PANEL_HEIGHT));
        profileLabel.setPreferredSize(new Dimension(WEST_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));
        dicePanel.setPreferredSize(new Dimension(CENTRE_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));
        remainingStepLabel.setFont(new Font("Calibre", 1, FONT_SIZE));
        remainingCoins.setFont(new Font("Calibre", 1, FONT_SIZE));
    }

    /**
     * This method ask gui for game's status, and update the display of player panel.
     */
    public void update() {


        // ============== west, a player's character picture ===============
        currentPlayer = gui.getCurrentPlayer();
        profileLabel.setIcon(PROFILE_IMG[currentPlayer.ordinal()]);

        // ============== centre, dice or dices ====================
        if (remainingSteps == 0) {
            for (int i = 0; i < diceLabels.length; i++) {
                diceLabels[i].setIcon(null);
            }
        }

        // ============ east, buttons panel ===================
        validateButtons();


        // ================= south, cards in hand =================
        // remove old components
        for (Component com : cardsInHandPanel.getComponents()) {
            cardsInHandPanel.remove(com);
        }
        // add new player's components
        cardsInHand = gui.getPlayerByCharacter(currentPlayer).getCards();
        for (Card c : cardsInHand) {
            cardsInHandPanel.add(c.addCard(c));
        }

        cardsInHandPanel.setVisible(true);
        cardsInHandPanel.updateUI();
        cardsInHandPanel.repaint();

        // ================ Adding stuff ===================
        this.setVisible(true);
        this.updateUI();
        this.repaint();

        refreshScreen();
        resetImages();
    }

    /**
     * This method validates all buttons on the button panel. If any button should not be
     * pressed, disable it, forever prevent it from being pressed by player.
     */
    private void validateButtons() {
        // the text label for displaying remaining steps
        remainingStepLabel
                .setText("Remaining Steps: " + gui.getRemainingSteps(currentPlayer));

        /*
         * first if the player hasn't rolled dices, disable all buttons except roll button
         * and return
         */
        if (remainingSteps == 0) {
            enterExitRoom.setEnabled(false);
            upButton.setEnabled(false);
            secPasButton.setEnabled(false);
            leftButton.setEnabled(false);
            downButton.setEnabled(false);
            rightButton.setEnabled(false);
            suggestionButton.setEnabled(false);
            accusationButton.setEnabled(false);
            endTurnButton.setEnabled(false);
            rollDiceButton.setEnabled(true);
            return;
        }

        // let's disable most actions
        enterExitRoom.setEnabled(false);
        upButton.setEnabled(false);
        secPasButton.setEnabled(false);
        leftButton.setEnabled(false);
        downButton.setEnabled(false);
        rightButton.setEnabled(false);
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(true);
        endTurnButton.setEnabled(true);

        // now let's see what options are there for current player.
        Player player = gui.getPlayerByCharacter(currentPlayer);

        // if there are tiles in four directions
        if (gui.getBoard().lookNorth(player) != null) {
            // check if any other player standing there, then it's not an option
            boolean isBlocking = false;
            Tile tile = gui.getBoard().lookNorth(player);
            for (Player existingPlayer : gui.getAllPlayers()) {
                if (tile.equals(existingPlayer.getPosition())) {
                    isBlocking = true;
                    break;
                }
            }
            upButton.setEnabled(!isBlocking);
        }

        if (gui.getBoard().lookEast(player) != null) {
            boolean isBlocking = false;
            Tile tile = gui.getBoard().lookEast(player);
            for (Player existingPlayer : gui.getAllPlayers()) {
                if (tile.equals(existingPlayer.getPosition())) {
                    isBlocking = true;
                    break;
                }
            }
            rightButton.setEnabled(!isBlocking);
        }

        if (gui.getBoard().lookSouth(player) != null) {
            boolean isBlocking = false;
            Tile tile = gui.getBoard().lookSouth(player);
            for (Player existingPlayer : gui.getAllPlayers()) {
                if (tile.equals(existingPlayer.getPosition())) {
                    isBlocking = true;
                    break;
                }
            }
            downButton.setEnabled(!isBlocking);
        }

        if (gui.getBoard().lookWest(player) != null) {
            boolean isBlocking = false;
            Tile tile = gui.getBoard().lookWest(player);
            for (Player existingPlayer : gui.getAllPlayers()) {
                if (tile.equals(existingPlayer.getPosition())) {
                    isBlocking = true;
                    break;
                }
            }
            leftButton.setEnabled(!isBlocking);
        }

        // if the player is standing at an entrance to a room
        if (gui.getBoard().atEntranceTo(player) != null) {
            // since we combine two buttons together, we have to change the image
            enterExitRoom.setName("ENTER");
            enterExitRoom.setIcon(ENTER_DEFAULT_IMG);
            enterExitRoom.setRolloverIcon(ENTER_DEFAULT_IMG);
            enterExitRoom.setPressedIcon(ENTER_PRESSED_IMG);
            enterExitRoom.setEnabled(true);
        }

        // if the player is in a room, get the exits
        List<Entrance> entrances = gui.getBoard().lookForExit(player);
        if (entrances != null && !entrances.isEmpty()) {
            enterExitRoom.setName("EXIT");
            enterExitRoom.setIcon(EXIT_DEFAULT_IMG);
            enterExitRoom.setRolloverIcon(EXIT_DEFAULT_IMG);
            enterExitRoom.setPressedIcon(EXIT_PRESSED_IMG);

            Location room = entrances.get(0).toRoom().getRoom();

            if (room == Location.Kitchen || room == Location.Conservatory
                    || room == Location.Study || room == Location.Lounge) {
                // these rooms has only one exit, if it's blocked, disable the exit button
                boolean isBlocking = false;
                for (Player existingPlayer : gui.getAllPlayers()) {
                    if (entrances.get(0).equals(existingPlayer.getPosition())) {
                        isBlocking = true;
                        break;
                    }
                }
                enterExitRoom.setEnabled(!isBlocking);

            } else {
                // other rooms have more than one exit. Check if they are all blocked.
                boolean allBlocked = true;
                for (Entrance exit : entrances) {
                    boolean isBlocked = false;
                    for (Player existingPlayer : gui.getAllPlayers()) {
                        if (exit.equals(existingPlayer.getPosition())) {
                            isBlocked = true;
                            break;
                        }
                        isBlocked = false;
                    }

                    if (!isBlocked) {
                        allBlocked = false;
                        break;
                    }
                }

                if (allBlocked) {
                    enterExitRoom.setEnabled(false);
                } else {
                    enterExitRoom.setEnabled(true);
                }
            }
        }

        // if the player is in a room, and there is a secret passage
        if (gui.getBoard().lookForSecPas(player) != null) {
            // in a room, have a secret passage
            secPasButton.setEnabled(true);
        }

        // validate the roll dice button
        if (gui.getRemainingSteps(currentPlayer) == 0) {
            rollDiceButton.setEnabled(true);
        } else {
            rollDiceButton.setEnabled(false);
        }

        // enables the suggestion button only when the player is in a room
        if (gui.getPlayerPosition(currentPlayer) instanceof Room) {
            suggestionButton.setEnabled(true);
        } else {
            suggestionButton.setEnabled(false);
        }
    }

    /**
     * This method does all the work when the player clicked on up button. It move player
     * towards north by one tile, decrement remaining steps, end current player's turn if
     * necessary, and update the GUI.
     */
    public void clickOnUp() {
        Tile northTile = gui.getBoard()
                .lookNorth(gui.getPlayerByCharacter(currentPlayer));

        if (northTile != null) { // this shouldn't be necessary
            gui.movePlayer(currentPlayer, northTile);
        }

        // decrementing remaining steps
        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }

        // update the GUI
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on left button. It move
     * player towards west by one tile, decrement remaining steps, end current player's
     * turn if necessary, and update the GUI.
     */
    public void clickOnLeft() {
        Tile westTile = gui.getBoard().lookWest(gui.getPlayerByCharacter(currentPlayer));
        if (westTile != null) {
            gui.movePlayer(currentPlayer, westTile);
        }
        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on down button. It move
     * player towards south by one tile, decrement remaining steps, end current player's
     * turn if necessary, and update the GUI.
     */
    public void clickOnDown() {
        Tile southTile = gui.getBoard()
                .lookSouth(gui.getPlayerByCharacter(currentPlayer));
        if (southTile != null) {
            gui.movePlayer(currentPlayer, southTile);
        }
        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on right button. It move
     * player towards east by one tile, decrement remaining steps, end current player's
     * turn if necessary, and update the GUI.
     */
    public void clickOnRight() {
        Tile eastTile = gui.getBoard().lookEast(gui.getPlayerByCharacter(currentPlayer));
        if (eastTile != null) {
            gui.movePlayer(currentPlayer, eastTile);
        }
        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on enter/exit button. It move
     * the player in or out of room, decrement remaining steps, end current player's turn
     * if necessary, pop up a suggestion dialog if necessary, and update the GUI.
     */
    public void clickOnEnterExitRoom() {

        if (enterExitRoom.getName().equals("ENTER")) {
            Room room = gui.getBoard()
                    .atEntranceTo(gui.getPlayerByCharacter(currentPlayer));
            if (room != null) {
                gui.movePlayer(currentPlayer, room);
            }
            remainingSteps = 0;
            gui.setRemainingSteps(currentPlayer, remainingSteps);
            gui.update();
            gui.popUpSuggestion();
            gui.currentPlayerEndTurn();
        } else {
            List<Entrance> entrances = gui.getBoard()
                    .lookForExit(gui.getPlayerByCharacter(currentPlayer));
            Location room = entrances.get(0).toRoom().getRoom();

            if (room == Location.Kitchen || room == Location.Conservatory
                    || room == Location.Study || room == Location.Lounge) {
                // these rooms has only one exit, just step out.
                if (entrances.get(0) != null) {
                    gui.movePlayer(currentPlayer, entrances.get(0));
                }
            } else {
                // pop up a dialog to choose which room to exit
                new ExitRoomDialog(gui, SwingUtilities.windowForComponent(this),
                        "Exit Room", room);
            }

            remainingSteps--;
            gui.setRemainingSteps(currentPlayer, remainingSteps);
            if (remainingSteps == 0) {
                gui.currentPlayerEndTurn();
            }
        }
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on secret passage button. It
     * move the player to the room at the end of this room's secret passage, decrement
     * remaining steps, end current player's turn if necessary, pop up a suggestion dialog
     * if necessary, and update the GUI.
     */
    public void clickOnSecretPass() {
        Room secPasTo = gui.getBoard()
                .lookForSecPas(gui.getPlayerByCharacter(currentPlayer));
        if (secPasTo != null) {
            gui.movePlayer(currentPlayer, secPasTo);
        }

        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        gui.update();
        gui.popUpSuggestion();
        gui.currentPlayerEndTurn();
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on roll dice button. It roll
     * the dices, get a total number, and update the GUI.
     */
    public void clickOnRollDice() {
        diceRolled = gui.rollDice();
        for (int i = 0; i < diceLabels.length; i++) {
            if (diceRolled != null) {
                diceLabels[i].setIcon(DICE_IMG[diceRolled[i]]);
            }
        }
        remainingSteps = 0;
        for (int i : diceRolled) {
            remainingSteps += (i + 1);
        }
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        rollDiceButton.setEnabled(false);
        if (gui.getPlayerByCharacter(gui.getCurrentPlayer()).feasibleOperation(1) && !rollAgain.isEnabled()){
            activateRollAgain();
        }
        else {
            disableRollAgain();
        }
        update();
    }

    /**
     * This method does all the work when the player clicked on end turn button. It ends
     * current player's turn, and update the GUI.
     */
    public void clickOnEndTurn() {
        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, 0);
        gui.currentPlayerEndTurn();
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on suggestion button. It pops
     * up a dialog for making suggestion, and after the player has made suggestion, update
     * the GUI.
     */
    public void clickOnSuggestion() {
        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        gui.popUpSuggestion();
        gui.currentPlayerEndTurn();
        gui.update();
    }

    /**
     * This method does all the work when the player clicked on accusation button. It pops
     * up a dialog for making accusation, and after the player has made accusation, update
     * the GUI.
     */
    public void clickOnAccusation() {
        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        gui.popUpAccusation();
        gui.currentPlayerEndTurn();
        gui.update();
    }

    /**
     * This method exists to respond a short-cut key input for moving up. It simply check
     * whether up button is enabled. If yes, then click on it, if no, do nothing.
     */
    public void tryClickOnUp() {
        if (upButton.isEnabled()) {
            clickOnUp();
        }
    }

    /**
     * This method exists to respond a short-cut key input for moving left. It simply
     * check whether left button is enabled. If yes, then click on it, if no, do nothing.
     */
    public void tryClickOnLeft() {
        if (leftButton.isEnabled()) {
            clickOnLeft();
        }
    }

    /**
     * This method exists to respond a short-cut key input for moving down. It simply
     * check whether down button is enabled. If yes, then click on it, if no, do nothing.
     */
    public void tryClickOnDown() {
        if (downButton.isEnabled()) {
            clickOnDown();
        }
    }

    /**
     * This method exists to respond a short-cut key input for moving right. It simply
     * check whether right button is enabled. If yes, then click on it, if no, do nothing.
     */
    public void tryClickOnRight() {
        if (rightButton.isEnabled()) {
            clickOnRight();
        }
    }

    /**
     * This method exists to respond a short-cut key input for enter/exit room button. It
     * simply check whether enter/exit room button is enabled. If yes, then click on it,
     * if no, do nothing.
     */
    public void tryClickOnEnterExitRoom() {
        if (enterExitRoom.isEnabled()) {
            clickOnEnterExitRoom();
        }
    }

    /**
     * This method exists to respond a short-cut key input for secret passage button. It
     * simply check whether secret passage button is enabled. If yes, then click on it, if
     * no, do nothing.
     */
    public void tryClickOnSecretPass() {
        if (secPasButton.isEnabled()) {
            clickOnSecretPass();
        }
    }

    /**
     * This method exists to respond a short-cut key input for roll dice button. It simply
     * check whether roll dice button is enabled. If yes, then click on it, if no, do
     * nothing.
     */
    public void tryClickOnRollDice() {
        if (rollDiceButton.isEnabled()) {
            clickOnRollDice();
        }
    }

    /**
     * This method exists to respond a short-cut key input for end turn button. It simply
     * check whether end turn button is enabled. If yes, then click on it, if no, do
     * nothing.
     */
    public void tryClickOnEndTurn() {
        if (endTurnButton.isEnabled()) {
            clickOnEndTurn();
        }
    }

    /**
     * A helper method to create a JButton object, and set its attributes to fit the GUI.
     *
     * @param defaultIcon  --- the image used as the default image
     * @param pressedIcon  --- the image used as the "pressed" image
     * @param disabledIcon --- the image used as the disabled image
     * @param dimension    --- the preferred size of this JButton
     * @return --- a JButton instance
     */
    private JButton createButton(ImageIcon defaultIcon, ImageIcon pressedIcon,
                                 ImageIcon disabledIcon, Dimension dimension) {
        JButton button = new JButton();
        button.setBackground(null);
        button.setPreferredSize(dimension);
        // make the button transparent
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        // set Images
        button.setIcon(defaultIcon);
        button.setRolloverIcon(defaultIcon);
        button.setPressedIcon(pressedIcon);
        button.setDisabledIcon(disabledIcon);
        return button;
    }

    /**
     * A helper method to create a JLabel object for displaying the cards.
     *
     * @param cardImg --- the image used to display the item.card
     * @param example --- a Character or a Weapon or a Location item.card, as example. This
     *                argument is used to decide the type of this item.card, and look for
     *                appropriate image accordingly
     * @return --- a JLabel object to represents the item.card
     */
    private static JLabel[] createCardLabel(ImageIcon[] cardImg, Card example) {
        JLabel[] cards = new JLabel[cardImg.length];
        for (int i = 0; i < cardImg.length; i++) {
            Card c;
            if (example instanceof Character) {
                c = Character.get(i);
            } else if (example instanceof Weapon) {
                c = Weapon.get(i);
            } else {
                c = Location.get(i);
            }
            cards[i] = new JLabel(cardImg[i]);
            cards[i].setBorder(null);
            cards[i].setToolTipText(c.toString());
        }
        return cards;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(PLAYER_PANEL, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    // ============== Static Images ========================

    public static ImageIcon REMAINING_CARDS_IMAGE = new ImageIcon(loadImage("Remaining_Cards.png"), "Remaining_Cards.png");
    /**
     * The background image of player panel
     */
    private static final Image PLAYER_PANEL = GUIClient
            .loadImage("Player_Panel_Background.png");

    /**
     * Six profile images
     */
    public static final ImageIcon[] PROFILE_IMG = {
            new ImageIcon(loadImage("Profile_Miss_Scarlet.png"), "Profile_Miss_Scarlet.png"),
            new ImageIcon(loadImage("Profile_Colonel_Mustard.png"), "Profile_Colonel_Mustard.png"),
            new ImageIcon(loadImage("Profile_Mrs_White.png"), "Profile_Mrs_White.png"),
            new ImageIcon(loadImage("Profile_The_Reverend_Green.png"), "Profile_The_Reverend_Green.png"),
            new ImageIcon(loadImage("Profile_Mrs_Peacock.png"), "Profile_Mrs_Peacock.png"),
            new ImageIcon(loadImage("Profile_Professor_Plum.png"), "Profile_Professor_Plum.png")};

    public static final ImageIcon[] PROFILE_DIALOG_IMG = {
            new ImageIcon(loadImage("Profile_Miss_Scarlet.png"), "Profile_Miss_Scarlet.png"),
            new ImageIcon(loadImage("Profile_Colonel_Mustard.png"), "Profile_Colonel_Mustard.png"),
            new ImageIcon(loadImage("Profile_Mrs_White.png"), "Profile_Mrs_White.png"),
            new ImageIcon(loadImage("Profile_The_Reverend_Green.png"), "Profile_The_Reverend_Green.png"),
            new ImageIcon(loadImage("Profile_Mrs_Peacock.png"), "Profile_Mrs_Peacock.png"),
            new ImageIcon(loadImage("Profile_Professor_Plum.png"), "Profile_Professor_Plum.png")};
    /**
     * Images for displaying dices
     */
    private static final ImageIcon[] DICE_IMG = {
            new ImageIcon(loadImage("Dice_1.png"), "Dice_1.png"),
            new ImageIcon(loadImage("Dice_2.png"), "Dice_2.png"),
            new ImageIcon(loadImage("Dice_3.png"), "Dice_3.png"),
            new ImageIcon(loadImage("Dice_4.png"), "Dice_4.png"),
            new ImageIcon(loadImage("Dice_5.png"), "Dice_5.png"),
            new ImageIcon(loadImage("Dice_6.png"), "Dice_6.png")};

    /**
     * Images for displaying Character cards
     */
    public static final ImageIcon[] CHARACTER_IMG = {
            new ImageIcon(loadImage("Character_Miss_Scarlet.png"), "Character_Miss_Scarlet.png"),
            new ImageIcon(loadImage("Character_Colonel_Mustard.png"), "Character_Colonel_Mustard.png"),
            new ImageIcon(loadImage("Character_Mrs_White.png"), "Character_Mrs_White.png"),
            new ImageIcon(loadImage("Character_The_Reverend_Green.png"), "Character_The_Reverend_Green.png"),
            new ImageIcon(loadImage("Character_Mrs_Peacock.png"), "Character_Mrs_Peacock.png"),
            new ImageIcon(loadImage("Character_Professor_Plum.png"), "Character_Professor_Plum.png")};

    /**
     * Images for displaying Weapon cards
     */
    public static final ImageIcon[] WEAPON_IMG = {
            new ImageIcon(loadImage("Weapon_Candlestick.png"), "Weapon_Candlestick.png"),
            new ImageIcon(loadImage("Weapon_Dagger.png"), "Weapon_Dagger.png"),
            new ImageIcon(loadImage("Weapon_Lead_Pipe.png"), "Weapon_Lead_Pipe.png"),
            new ImageIcon(loadImage("Weapon_Revolver.png"), "Weapon_Revolver.png"),
            new ImageIcon(loadImage("Weapon_Rope.png"), "Weapon_Rope.png"),
            new ImageIcon(loadImage("Weapon_Spanner.png"), "Weapon_Spanner.png")};

    /**
     * Images for displaying Location cards
     */
    public static final ImageIcon[] LOCATION_IMG = {
            new ImageIcon(loadImage("Location_Kitchen.png"), "Location_Kitchen.png"),
            new ImageIcon(loadImage("Location_Ball_room.png"), "Location_Ball_room.png"),
            new ImageIcon(loadImage("Location_Conservatory.png"), "Location_Conservatory.png"),
            new ImageIcon(loadImage("Location_Billard_Room.png"), "Location_Billard_Room.png"),
            new ImageIcon(loadImage("Location_Library.png"), "Location_Library.png"),
            new ImageIcon(loadImage("Location_Study.png"), "Location_Study.png"),
            new ImageIcon(loadImage("Location_Hall.png"), "Location_Hall.png"),
            new ImageIcon(loadImage("Location_Lounge.png"), "Location_Lounge.png"),
            new ImageIcon(loadImage("Location_Dining_Room.png"), "Location_Dining_Room.png")};

    /**
     * JLabel objects for displaying Character cards
     */
    public static final JLabel[] CHARACTER_LABELS = createCardLabel(CHARACTER_IMG,
            Character.get(0));

    /**
     * JLabel objects for displaying Weapon cards
     */
    public static final JLabel[] WEAPON_LABELS = createCardLabel(WEAPON_IMG,
            Weapon.get(0));

    /**
     * JLabel objects for displaying Location cards
     */
    public static final JLabel[] LOCATION_LABELS = createCardLabel(LOCATION_IMG,
            Location.get(0));

    /**
     * The preferred size of move buttons
     */
    private static Dimension MOVE_BUTTON_SIZE = new Dimension(WIDTH / 18, HEIGHT / 15);

    /**
     * The preferred size of action buttons
     */
    private static Dimension ACTION_BUTTON_SIZE = new Dimension(WIDTH / 11, HEIGHT / 15);

    /**
     * An image for displaying disabled action button
     */
    private static ImageIcon ACTION_DISABLED_IMG = new ImageIcon(
            loadImage("Button_Action_Disabled.png"), "Button_Action_Disabled.png");

    /**
     * An image for displaying disabled move button
     */
    private static ImageIcon MOVE_DISABLED_IMG = new ImageIcon(
            loadImage("Button_Movement_Disabled.png"), "Button_Movement_Disabled.png");

    /**
     * An image for displaying default up button
     */
    private static ImageIcon UP_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Up_Default.png"), "Button_Up_Default.png");

    /**
     * An image for displaying pressed up button
     */
    private static ImageIcon UP_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Up_Pressed.png"), "Button_Up_Pressed.png");

    /**
     * An image for displaying default down button
     */
    private static ImageIcon DOWN_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Down_Default.png"), "Button_Down_Default.png");

    /**
     * An image for displaying pressed down button
     */
    private static ImageIcon DOWN_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Down_Pressed.png"), "Button_Down_Pressed.png");

    /**
     * An image for displaying default left button
     */
    private static ImageIcon LEFT_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Left_Default.png"), "Button_Left_Default.png");

    /**
     * An image for displaying pressed left button
     */
    private static ImageIcon LEFT_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Left_Pressed.png"), "Button_Left_Pressed.png");

    /**
     * An image for displaying default right button
     */
    private static ImageIcon RIGHT_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Right_Default.png"), "Button_Right_Default.png");

    /**
     * An image for displaying pressed right button
     */
    private static ImageIcon RIGHT_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Right_Pressed.png"), "Button_Right_Pressed.png");

    /**
     * An image for displaying default enter room button
     */
    private static ImageIcon ENTER_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_EnterRoom_Default.png"), "Button_EnterRoom_Default.png");

    /**
     * An image for displaying pressed enter room button
     */
    private static ImageIcon ENTER_PRESSED_IMG = new ImageIcon(
            loadImage("Button_EnterRoom_Pressed.png"), "Button_EnterRoom_Pressed.png");

    /**
     * An image for displaying default exit room button
     */
    private static ImageIcon EXIT_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_ExitRoom_Default.png"), "Button_ExitRoom_Default.png");

    /**
     * An image for displaying pressed exit room button
     */
    private static ImageIcon EXIT_PRESSED_IMG = new ImageIcon(
            loadImage("Button_ExitRoom_Pressed.png"), "Button_ExitRoom_Pressed.png");

    /**
     * An image for displaying default secret passage button
     */
    private static ImageIcon SECPAS_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_SecretPass_Default.png"), "Button_SecretPass_Default.png");

    /**
     * An image for displaying pressed secret passage button
     */
    private static ImageIcon SECPAS_PRESSED_IMG = new ImageIcon(
            loadImage("Button_SecretPass_Pressed.png"), "Button_SecretPass_Pressed.png");

    /**
     * An image for displaying default roll dice button
     */
    private static ImageIcon ROLLDICE_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_RollDice_Default.png"), "Button_RollDice_Default.png");

    /**
     * An image for displaying pressed roll dice button
     */
    private static ImageIcon ROLLDICE_PRESSED_IMG = new ImageIcon(
            loadImage("Button_RollDice_Pressed.png"), "Button_RollDice_Pressed.png");

    /**
     * An image for displaying default end turn button
     */
    private static ImageIcon ENDTURN_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_EndTuen_Default.png"), "Button_EndTuen_Default.png");

    /**
     * An image for displaying pressed end turn button
     */
    private static ImageIcon ENDTURN_PRESSED_IMG = new ImageIcon(
            loadImage("Button_EndTuen_Pressed.png"), "Button_EndTuen_Pressed.png");

    /**
     * An image for displaying default suggestion button
     */
    private static ImageIcon SUGGESTION_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Suggestion_Default.png"), "Button_Suggestion_Default.png");

    /**
     * An image for displaying pressed suggestion button
     */
    private static ImageIcon SUGGESTION_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Suggestion_Pressed.png"), "Button_Suggestion_Pressed.png");

    /**
     * An image for displaying default accusation button
     */
    private static ImageIcon ACCUSATION_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Accusation_Default.png"), "Button_Accusation_Default.png");

    /**
     * An image for displaying pressed accusation button
     */
    private static ImageIcon ACCUSATION_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Accusation_Pressed.png"), "Button_Accusation_Pressed.png");

    public void resetImages(){
        REMAINING_CARDS_IMAGE = WindowUtilities.resizeImage(REMAINING_CARDS_IMAGE); // <--- Finally, the resizer works correctly :D
        PROFILE_IMG[currentPlayer.ordinal()] = WindowUtilities.resizeImage(PROFILE_IMG[currentPlayer.ordinal()]);

        // Create a method with all label setters. Very important: we need to know inside which are the all current player images!!!
        profileLabel.setIcon(PROFILE_IMG[currentPlayer.ordinal()]);
        remainingCardLabel.setIcon(REMAINING_CARDS_IMAGE);


        for (Card c : cardsInHand) {
            c.resizeImage(c);
        }

        for (Card c : remainingCards) {
            c.resizeImage(c);
        }

        /*Resizing upbutton*/
        UP_DEFAULT_IMG = WindowUtilities.resizeImage(UP_DEFAULT_IMG);
        UP_PRESSED_IMG = WindowUtilities.resizeImage(UP_PRESSED_IMG);
        MOVE_DISABLED_IMG = WindowUtilities.resizeImage(MOVE_DISABLED_IMG);
        upButton.setPreferredSize(new Dimension(MOVE_DISABLED_IMG.getIconWidth(), MOVE_DISABLED_IMG.getIconWidth()));
        upButton.setRolloverIcon(UP_DEFAULT_IMG);
        upButton.setIcon(UP_DEFAULT_IMG);
        upButton.setPressedIcon(UP_PRESSED_IMG);
        upButton.setDisabledIcon(MOVE_DISABLED_IMG);

        DOWN_DEFAULT_IMG = WindowUtilities.resizeImage(DOWN_DEFAULT_IMG);
        DOWN_PRESSED_IMG = WindowUtilities.resizeImage(DOWN_PRESSED_IMG);
        downButton.setPreferredSize(new Dimension(MOVE_DISABLED_IMG.getIconWidth(), MOVE_DISABLED_IMG.getIconWidth()));
        downButton.setRolloverIcon(DOWN_DEFAULT_IMG);
        downButton.setIcon(DOWN_DEFAULT_IMG);
        downButton.setPressedIcon(DOWN_PRESSED_IMG);
        downButton.setDisabledIcon(MOVE_DISABLED_IMG);

        LEFT_DEFAULT_IMG = WindowUtilities.resizeImage(LEFT_DEFAULT_IMG);
        LEFT_PRESSED_IMG = WindowUtilities.resizeImage(LEFT_PRESSED_IMG);
        leftButton.setPreferredSize(new Dimension(MOVE_DISABLED_IMG.getIconWidth(), MOVE_DISABLED_IMG.getIconWidth()));
        leftButton.setRolloverIcon(LEFT_DEFAULT_IMG);
        leftButton.setIcon(LEFT_DEFAULT_IMG);
        leftButton.setPressedIcon(LEFT_PRESSED_IMG);
        leftButton.setDisabledIcon(MOVE_DISABLED_IMG);

        RIGHT_DEFAULT_IMG = WindowUtilities.resizeImage(RIGHT_DEFAULT_IMG);
        RIGHT_PRESSED_IMG = WindowUtilities.resizeImage(RIGHT_PRESSED_IMG);
        rightButton.setPreferredSize(new Dimension(MOVE_DISABLED_IMG.getIconWidth(), MOVE_DISABLED_IMG.getIconWidth()));
        rightButton.setRolloverIcon(RIGHT_DEFAULT_IMG);
        rightButton.setIcon(RIGHT_DEFAULT_IMG);
        rightButton.setPressedIcon(RIGHT_PRESSED_IMG);
        rightButton.setDisabledIcon(MOVE_DISABLED_IMG);

        ENTER_DEFAULT_IMG = WindowUtilities.resizeImage(ENTER_DEFAULT_IMG);
        ENTER_PRESSED_IMG = WindowUtilities.resizeImage(ENTER_PRESSED_IMG);
        enterExitRoom.setPreferredSize(new Dimension(MOVE_DISABLED_IMG.getIconWidth(), MOVE_DISABLED_IMG.getIconWidth()));
        enterExitRoom.setRolloverIcon(ENTER_DEFAULT_IMG);
        enterExitRoom.setIcon(ENTER_DEFAULT_IMG);
        enterExitRoom.setPressedIcon(ENTER_PRESSED_IMG);
        enterExitRoom.setDisabledIcon(MOVE_DISABLED_IMG);

        SECPAS_DEFAULT_IMG = WindowUtilities.resizeImage(SECPAS_DEFAULT_IMG);
        SECPAS_PRESSED_IMG = WindowUtilities.resizeImage(SECPAS_PRESSED_IMG);
        secPasButton.setPreferredSize(new Dimension(MOVE_DISABLED_IMG.getIconWidth(), MOVE_DISABLED_IMG.getIconWidth()));
        secPasButton.setRolloverIcon(SECPAS_DEFAULT_IMG);
        secPasButton.setIcon(SECPAS_DEFAULT_IMG);
        secPasButton.setPressedIcon(SECPAS_PRESSED_IMG);
        secPasButton.setDisabledIcon(MOVE_DISABLED_IMG);

        ACTION_DISABLED_IMG = WindowUtilities.resizeImage(ACTION_DISABLED_IMG);
        ROLLDICE_DEFAULT_IMG = WindowUtilities.resizeImage(ROLLDICE_DEFAULT_IMG);
        ROLLDICE_PRESSED_IMG = WindowUtilities.resizeImage(ROLLDICE_PRESSED_IMG);
        rollDiceButton.setPreferredSize(new Dimension(ACTION_DISABLED_IMG.getIconWidth(), ACTION_DISABLED_IMG.getIconWidth()));
        rollDiceButton.setRolloverIcon(ROLLDICE_DEFAULT_IMG);
        rollDiceButton.setIcon(ROLLDICE_DEFAULT_IMG);
        rollDiceButton.setPressedIcon(ROLLDICE_PRESSED_IMG);
        rollDiceButton.setDisabledIcon(ACTION_DISABLED_IMG);

        ENDTURN_DEFAULT_IMG = WindowUtilities.resizeImage(ENDTURN_DEFAULT_IMG);
        ENDTURN_PRESSED_IMG = WindowUtilities.resizeImage(ENDTURN_PRESSED_IMG);
        endTurnButton.setPreferredSize(new Dimension(ACTION_DISABLED_IMG.getIconWidth(), ACTION_DISABLED_IMG.getIconWidth()));
        endTurnButton.setRolloverIcon(ENDTURN_DEFAULT_IMG);
        endTurnButton.setIcon(ENDTURN_DEFAULT_IMG);
        endTurnButton.setPressedIcon(ENDTURN_PRESSED_IMG);
        endTurnButton.setDisabledIcon(ACTION_DISABLED_IMG);

        SUGGESTION_DEFAULT_IMG = WindowUtilities.resizeImage(SUGGESTION_DEFAULT_IMG);
        SUGGESTION_PRESSED_IMG = WindowUtilities.resizeImage(SUGGESTION_PRESSED_IMG);
        suggestionButton.setPreferredSize(new Dimension(ACTION_DISABLED_IMG.getIconWidth(), ACTION_DISABLED_IMG.getIconWidth()));
        suggestionButton.setRolloverIcon(SUGGESTION_DEFAULT_IMG);
        suggestionButton.setIcon(SUGGESTION_DEFAULT_IMG);
        suggestionButton.setPressedIcon(SUGGESTION_PRESSED_IMG);
        suggestionButton.setDisabledIcon(ACTION_DISABLED_IMG);

        ACCUSATION_DEFAULT_IMG = WindowUtilities.resizeImage(ACCUSATION_DEFAULT_IMG);
        ACCUSATION_PRESSED_IMG = WindowUtilities.resizeImage(ACCUSATION_PRESSED_IMG);
        accusationButton.setPreferredSize(new Dimension(ACTION_DISABLED_IMG.getIconWidth(), ACTION_DISABLED_IMG.getIconWidth()));
        accusationButton.setRolloverIcon(ACCUSATION_DEFAULT_IMG);
        accusationButton.setIcon(ACCUSATION_DEFAULT_IMG);
        accusationButton.setPressedIcon(ACCUSATION_PRESSED_IMG);
        accusationButton.setDisabledIcon(ACTION_DISABLED_IMG);

        if (diceRolled != null) {
            for (int i = 0; i < diceRolled.length; i++) {
                DICE_IMG[diceRolled[i]] = WindowUtilities.resizeImage(DICE_IMG[diceRolled[i]]);
                diceLabels[i].setIcon(DICE_IMG[diceRolled[i]]);
            }
        }
        rollAgain.setPreferredSize(new Dimension(ACTION_DISABLED_IMG.getIconWidth(), ACTION_DISABLED_IMG.getIconHeight()));
        // Reload all canvas components without calling update method
        this.updateUI();
        this.repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    public static void activateRollAgain(){
        rollAgain.setEnabled(true);
        rollAgain.setVisible(true);
    }

    public static void disableRollAgain(){
        rollAgain.setEnabled(false);
        rollAgain.setVisible(false);
    }


    public void setLabelCoins(Character character){
        remainingCoins.setText("Remaining coins: " + gui.getGame().getPlayerByCharacter(character).getSalary().getCoins());
    }

    public JPanel getRemainingCardsPanel() {
        return remainingCardsPanel;
    }
}
