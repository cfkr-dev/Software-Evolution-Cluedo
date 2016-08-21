package view;

import java.awt.BorderLayout;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleToLongFunction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import configs.Configs;
import game.Player;
import tile.Entrance;
import tile.Position;
import tile.Room;
import tile.Tile;
import ui.GUIClient;
import card.Card;
import card.Character;
import card.Location;
import card.Weapon;

import static ui.GUIClient.loadImage;

public class PlayerPanelCanvas extends JPanel {

    public static final int WIDTH = 700;
    public static final int HEIGHT = BoardCanvas.BOARD_IMG_HEIGHT;

    public static final int SOUTH_PANEL_HEIGHT = 220;
    public static final int NORTH_PANEL_HEIGHT = SOUTH_PANEL_HEIGHT;
    public static final int CENTRE_PANEL_HEIGHT = HEIGHT - SOUTH_PANEL_HEIGHT
            - NORTH_PANEL_HEIGHT;

    public static final int WEST_PANEL_WIDTH = 230;
    public static final int EAST_PANEL_WIDTH = 320;
    public static final int CENTRE_PANEL_WIDTH = WIDTH - WEST_PANEL_WIDTH
            - EAST_PANEL_WIDTH;

    public static final int PADDING_LEFT = BoardCanvas.PADDING_LEFT;
    public static final int PADDING_RIGHT = BoardCanvas.PADDING_RIGHT;
    public static final int PADDING_TOP = BoardCanvas.PADDING_TOP;
    public static final int PADDING_DOWN = BoardCanvas.PADDING_DOWN;

    private GUIClient gui;

    private List<Card> remainingCards;
    private JPanel remainingCardsPanel;
    private List<Card> cardsInHand;
    private JPanel cardsInHandPanel;

    private JLabel profileLabel;
    private JLabel[] diceLabels;
    private JLabel remainingStepLabel;

    private JButton EnterExitRoom;
    private JButton upButton;
    private JButton SecretPass;
    private JButton leftButton;
    private JButton downButton;
    private JButton rightButton;

    private JButton rollDiceButton;
    private JButton endTurnButton;
    private JButton suggestionButton;
    private JButton accusationButton;

    private Character currentPlayer;
    private int[] diceRolled = null;
    private int remainingSteps;

    public PlayerPanelCanvas(GUIClient guiClient) {

        this.gui = guiClient;
        remainingCards = gui.getRemainingCards();
        // cardsInHand = new ArrayList<>();

        // ================== BorderLayout =====================
        this.setLayout(new BorderLayout(5, 5));

        // =================== North, remaining cards =====================

        remainingCardsPanel = new JPanel();
        remainingCardsPanel.setBackground(null);
        remainingCardsPanel.setOpaque(false);
        remainingCardsPanel.setPreferredSize(new Dimension(WIDTH, NORTH_PANEL_HEIGHT));
        remainingCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // create Empty Border
        remainingCardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel text = new JLabel(new ImageIcon(loadImage("Remaining_Cards.png")),
                SwingConstants.CENTER);

        remainingCardsPanel.add(text);

        // display remaining cards.
        remainingCards = gui.getRemainingCards();

        for (Card c : remainingCards) {
            if (c instanceof Character) {
                Character ch = (Character) c;
                remainingCardsPanel.add(CHRACTER_LABELS[ch.ordinal()]);
            } else if (c instanceof Weapon) {
                Weapon we = (Weapon) c;
                remainingCardsPanel.add(WEAPON_LABELS[we.ordinal()]);
            } else {
                Location lo = (Location) c;
                remainingCardsPanel.add(LOCATION_LABELS[lo.ordinal()]);
            }
        }

        // ============== west, a player's character pic ===============
        profileLabel = new JLabel();
        profileLabel.setOpaque(false);
        profileLabel
                .setPreferredSize(new Dimension(WEST_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));

        // create Empty Border
        profileLabel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_TOP,
                PADDING_LEFT, PADDING_LEFT));

        // ============== centre, dice or dices ====================

        // panel for dices
        JPanel dicePanel = new JPanel();
        dicePanel.setBackground(null);
        dicePanel.setOpaque(false);
        dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.X_AXIS));

        // another panel to make the dice centre-aligned
        JPanel diceGroup = new JPanel();
        diceGroup.setBackground(null);
        diceGroup.setOpaque(false);
        diceGroup.setLayout(new BoxLayout(diceGroup, BoxLayout.Y_AXIS));

        // use JLabel as buttons
        diceLabels = new JLabel[gui.getNumDices()];
        for (int i = 0; i < diceLabels.length; i++) {
            diceLabels[i] = new JLabel();
            diceLabels[i].setBorder(null);
            diceGroup.add(diceLabels[i], Component.CENTER_ALIGNMENT);

            // add gaps between dices. and do not add a gap after the last dice
            if (i != diceLabels.length - 1) {
                int gap = 0;
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

        // create Empty Border
        dicePanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));

        // ============ east, buttons ===================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setOpaque(false);
        buttonPanel
                .setPreferredSize(new Dimension(EAST_PANEL_WIDTH, CENTRE_PANEL_HEIGHT));

        // create Empty Border
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // first row
        remainingStepLabel = new JLabel();
        remainingStepLabel.setBackground(null);
        remainingStepLabel.setOpaque(false);
        remainingStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // create Empty Border
        remainingStepLabel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT, PADDING_LEFT));

        // second row, a grid layout to show four direction buttons.
        JPanel movePanel = new JPanel();
        movePanel.setBackground(null);
        movePanel.setOpaque(false);
        movePanel.setLayout(new GridLayout(2, 3, 10, 10));
        movePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // create Empty Border
        movePanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));

        // four direction buttons
        EnterExitRoom = createButton(ENTER_DEFAULT_IMG, ENTER_PRESSED_IMG,
                MOVE_DISABLED_IMG, MOVE_BUTTON_SIZE);
        EnterExitRoom.setEnabled(false);
        upButton = createButton(UP_DEFAULT_IMG, UP_PRESSED_IMG, MOVE_DISABLED_IMG,
                MOVE_BUTTON_SIZE);
        SecretPass = createButton(SECPAS_DEFAULT_IMG, SECPAS_PRESSED_IMG,
                MOVE_DISABLED_IMG, MOVE_BUTTON_SIZE);
        SecretPass.setEnabled(false);
        leftButton = createButton(LEFT_DEFAULT_IMG, LEFT_PRESSED_IMG, MOVE_DISABLED_IMG,
                MOVE_BUTTON_SIZE);
        downButton = createButton(DOWN_DEFAULT_IMG, DOWN_PRESSED_IMG, MOVE_DISABLED_IMG,
                MOVE_BUTTON_SIZE);
        rightButton = createButton(RIGHT_DEFAULT_IMG, RIGHT_PRESSED_IMG,
                MOVE_DISABLED_IMG, MOVE_BUTTON_SIZE);

        // add listener on them
        EnterExitRoom.addActionListener(e -> clickOnEnterExitRoom());
        upButton.addActionListener(e -> clickOnUp());
        SecretPass.addActionListener(e -> clickOnSecretPass());
        leftButton.addActionListener(e -> clickOnLeft());
        downButton.addActionListener(e -> clickOnDown());
        rightButton.addActionListener(e -> clickOnRight());

        movePanel.add(EnterExitRoom);
        movePanel.add(upButton);
        movePanel.add(SecretPass);
        movePanel.add(leftButton);
        movePanel.add(downButton);
        movePanel.add(rightButton);

        // third row, another gridLayout
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(null);
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new GridLayout(2, 2, 10, 10));
        actionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // create Empty Border
        actionPanel.setBorder(BorderFactory.createEmptyBorder(PADDING_LEFT, PADDING_LEFT,
                PADDING_LEFT, PADDING_LEFT));

        rollDiceButton = createButton(ROLLDICE_DEFAULT_IMG, ROLLDICE_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);
        endTurnButton = createButton(ENDTURN_DEFAULT_IMG, ENDTURN_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);
        suggestionButton = createButton(SUGGESTION_DEFAULT_IMG, SUGGESTION_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);
        accusationButton = createButton(ACCUSATION_DEFAULT_IMG, ACCUSATION_PRESSED_IMG,
                ACTION_DISABLED_IMG, ACTION_BUTTON_SIZE);

        // add listeners
        rollDiceButton.addActionListener(e -> clickOnRollDice());
        endTurnButton.addActionListener(e -> clickOnEndTurn());
        suggestionButton.addActionListener(e -> clickOnSuggestion());
        accusationButton.addActionListener(e -> clickOnAccusation());

        actionPanel.add(rollDiceButton);
        actionPanel.add(endTurnButton);
        actionPanel.add(suggestionButton);
        actionPanel.add(accusationButton);

        // put them together
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
        // create Empty Border
        cardsInHandPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        currentPlayer = gui.getCurrentPlayer();
        cardsInHand = gui.getPlayerByCharacter(currentPlayer).getCards();

        for (Card c : cardsInHand) {
            if (c instanceof Character) {
                Character ch = (Character) c;
                cardsInHandPanel.add(CHRACTER_LABELS[ch.ordinal()]);
            } else if (c instanceof Weapon) {
                Weapon we = (Weapon) c;
                cardsInHandPanel.add(WEAPON_LABELS[we.ordinal()]);
            } else {
                Location lo = (Location) c;
                cardsInHandPanel.add(LOCATION_LABELS[lo.ordinal()]);
            }
        }

        // ================ Adding stuff together ===================
        this.add(remainingCardsPanel, BorderLayout.NORTH);
        this.add(profileLabel, BorderLayout.WEST);
        this.add(dicePanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.EAST);
        this.add(cardsInHandPanel, BorderLayout.SOUTH);
        this.setVisible(true);

        update();
    }

    public void update() {
        // ===== North, remaining cards, No need to update during the game =======
        // ============== west, a player's character pic ===============
        currentPlayer = gui.getCurrentPlayer();
        profileLabel.setIcon(PROFILE_IMG[currentPlayer.ordinal()]);

        // ============== centre, dice or dices ====================
        if (remainingSteps == 0) {
            for (int i = 0; i < diceLabels.length; i++) {
                diceLabels[i].setIcon(null);
            }
        }

        // ============ east, buttons panel ===================
        valuateButtons();

        // ================= south, cards in hand =================
        for (Component com : cardsInHandPanel.getComponents()) {
            cardsInHandPanel.remove(com);
        }

        cardsInHand = gui.getPlayerByCharacter(currentPlayer).getCards();
        for (Card c : cardsInHand) {
            if (c instanceof Character) {
                Character ch = (Character) c;
                cardsInHandPanel.add(CHRACTER_LABELS[ch.ordinal()]);

            } else if (c instanceof Weapon) {
                Weapon we = (Weapon) c;
                cardsInHandPanel.add(WEAPON_LABELS[we.ordinal()]);
            } else {
                Location lo = (Location) c;
                cardsInHandPanel.add(LOCATION_LABELS[lo.ordinal()]);
            }
        }

        cardsInHandPanel.setVisible(true);
        cardsInHandPanel.updateUI();
        cardsInHandPanel.repaint();

        this.add(cardsInHandPanel, BorderLayout.SOUTH);

        // ================ Adding stuff ===================
        this.setVisible(true);
        this.updateUI();
        this.repaint();
        this.repaint();
    }

    private void valuateButtons() {
        // the text label for displaying remaining steps
        remainingStepLabel
                .setText("Remaining Steps: " + gui.getRemainingSteps(currentPlayer));

        // first if the player hasn't rolled dices, disable all buttons and return
        if (remainingSteps == 0) {
            EnterExitRoom.setEnabled(false);
            upButton.setEnabled(false);
            SecretPass.setEnabled(false);
            leftButton.setEnabled(false);
            downButton.setEnabled(false);
            rightButton.setEnabled(false);
            suggestionButton.setEnabled(false);
            accusationButton.setEnabled(false);
            endTurnButton.setEnabled(false);
            rollDiceButton.setEnabled(true);
            return;
        }

        // first let's disable most actions
        EnterExitRoom.setEnabled(false);
        upButton.setEnabled(false);
        SecretPass.setEnabled(false);
        leftButton.setEnabled(false);
        downButton.setEnabled(false);
        rightButton.setEnabled(false);
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(true);
        endTurnButton.setEnabled(true);

        // now let's see what the player can do.
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
            // check if any other player standing there, then it's not an option
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
            // check if any other player standing there, then it's not an option
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
            // check if any other player standing there, then it's not an option
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
            EnterExitRoom.setName("ENTER");
            EnterExitRoom.setIcon(ENTER_DEFAULT_IMG);
            EnterExitRoom.setRolloverIcon(ENTER_DEFAULT_IMG);
            EnterExitRoom.setPressedIcon(ENTER_PRESSED_IMG);
            EnterExitRoom.setEnabled(true);
        }

        // if the player is in a room, get the exits
        List<Entrance> entrances = gui.getBoard().lookForExit(player);
        if (entrances != null && !entrances.isEmpty()) {
            EnterExitRoom.setName("EXIT");
            EnterExitRoom.setIcon(EXIT_DEFAULT_IMG);
            EnterExitRoom.setRolloverIcon(EXIT_DEFAULT_IMG);
            EnterExitRoom.setPressedIcon(EXIT_PRESSED_IMG);

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
                EnterExitRoom.setEnabled(!isBlocking);

            } else {
                /*
                 * other rooms have more than one exit. Check if they are all blocked.
                 */
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
                    EnterExitRoom.setEnabled(false);
                } else {
                    EnterExitRoom.setEnabled(true);
                }

            }

        }

        // if the player is in a room, and there is a secret passage
        if (gui.getBoard().lookForSecPas(player) != null) {
            // in a room, have a secret passage
            SecretPass.setEnabled(true);
        }

        if (gui.getRemainingSteps(currentPlayer) == 0) {
            rollDiceButton.setEnabled(true);
        } else {
            rollDiceButton.setEnabled(false);
        }

        if (gui.getPlayerPosition(currentPlayer) instanceof Room) {
            suggestionButton.setEnabled(true);
        } else {
            suggestionButton.setEnabled(false);
        }
    }

    public void clickOnUp() {
        Tile northTile = gui.getBoard()
                .lookNorth(gui.getPlayerByCharacter(currentPlayer));
        gui.movePlayer(currentPlayer, northTile);

        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }

        gui.update();
    }

    public void tryClickOnUp() {
        if (upButton.isEnabled()) {
            clickOnUp();
        }
    }

    public void clickOnLeft() {
        Tile westTile = gui.getBoard().lookWest(gui.getPlayerByCharacter(currentPlayer));
        gui.movePlayer(currentPlayer, westTile);

        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }

        gui.update();
    }

    public void tryClickOnLeft() {
        if (leftButton.isEnabled()) {
            clickOnLeft();
        }
    }

    public void clickOnDown() {
        Tile southTile = gui.getBoard()
                .lookSouth(gui.getPlayerByCharacter(currentPlayer));
        gui.movePlayer(currentPlayer, southTile);

        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }

        gui.update();
    }

    public void tryClickOnDown() {
        if (downButton.isEnabled()) {
            clickOnDown();
        }
    }

    public void clickOnRight() {
        Tile eastTile = gui.getBoard().lookEast(gui.getPlayerByCharacter(currentPlayer));
        gui.movePlayer(currentPlayer, eastTile);

        remainingSteps--;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        if (remainingSteps == 0) {
            gui.currentPlayerEndTurn();
        }

        gui.update();
    }

    public void tryClickOnRight() {
        if (downButton.isEnabled()) {
            clickOnRight();
        }
    }

    public void clickOnEnterExitRoom() {
        /*
         * This button only interact with room. Whenever this button is enabled, the
         * player is standing at entrance to (button text as "enter room") or in a room
         * (button text as "exit room").
         */
        if (EnterExitRoom.getName().equals("ENTER")) {

            Room room = gui.getBoard()
                    .atEntranceTo(gui.getPlayerByCharacter(currentPlayer));
            gui.movePlayer(currentPlayer, room);

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

                gui.movePlayer(currentPlayer, entrances.get(0));
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

    public void clickOnSecretPass() {
        Room secPasTo = gui.getBoard()
                .lookForSecPas(gui.getPlayerByCharacter(currentPlayer));
        gui.movePlayer(currentPlayer, secPasTo);

        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, remainingSteps);

        gui.update();

        gui.popUpSuggestion();

        gui.currentPlayerEndTurn();

        gui.update();
    }

    public void clickOnRollDice() {

        diceRolled = gui.rollDice(currentPlayer);
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
        gui.update();
    }

    public void tryClickOnRollDice() {
        if (rollDiceButton.isEnabled()) {
            clickOnRollDice();
        }
    }

    public void clickOnEndTurn() {
        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, 0);
        gui.currentPlayerEndTurn();
        gui.update();
    }

    public void clickOnSuggestion() {
        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, remainingSteps);

        gui.popUpSuggestion();

        gui.currentPlayerEndTurn();
        gui.update();
    }

    public void clickOnAccusation() {
        remainingSteps = 0;
        gui.setRemainingSteps(currentPlayer, remainingSteps);
        gui.popUpAccusation();
        gui.currentPlayerEndTurn();
        gui.update();
    }

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

    private static CardLabel[] createCardLabel(ImageIcon[] cardImg, Card example) {
        CardLabel[] cards = new CardLabel[cardImg.length];
        for (int i = 0; i < cardImg.length; i++) {
            Card c;
            if (example instanceof Character) {
                c = Character.get(i);
            } else if (example instanceof Weapon) {
                c = Weapon.get(i);
            } else {
                c = Location.get(i);
            }

            cards[i] = new CardLabel(cardImg[i], c);
            cards[i].setBorder(null);
            cards[i].setToolTipText(c.toString());
            addMouseListenerOnCardLabel(cards[i]);
        }
        return cards;
    }

    private static void addMouseListenerOnCardLabel(CardLabel cardLabel) {
        cardLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(PLAYER_PANEL, PADDING_LEFT, PADDING_TOP, WIDTH, HEIGHT, this);
    }

    // ============== Images ================================

    public static final Image PLAYER_PANEL = GUIClient
            .loadImage("Player_Panel_Background.png");

    public static final ImageIcon[] PROFILE_IMG = {
            new ImageIcon(loadImage("Profile_Miss_Scarlet.png")),
            new ImageIcon(loadImage("Profile_Colonel_Mustard.png")),
            new ImageIcon(loadImage("Profile_Mrs_White.png")),
            new ImageIcon(loadImage("Profile_The_Reverend_Green.png")),
            new ImageIcon(loadImage("Profile_Mrs_Peacock.png")),
            new ImageIcon(loadImage("Profile_Professor_Plum.png")) };

    public static final ImageIcon[] DICE_IMG = { new ImageIcon(loadImage("Dice_1.png")),
            new ImageIcon(loadImage("Dice_2.png")),
            new ImageIcon(loadImage("Dice_3.png")),
            new ImageIcon(loadImage("Dice_4.png")),
            new ImageIcon(loadImage("Dice_5.png")),
            new ImageIcon(loadImage("Dice_6.png")) };

    public static final ImageIcon[] CHARACTER_IMG = {
            new ImageIcon(loadImage("Character_Miss_Scarlet.png")),
            new ImageIcon(loadImage("Character_Colonel_Mustard.png")),
            new ImageIcon(loadImage("Character_Mrs_White.png")),
            new ImageIcon(loadImage("Character_The_Reverend_Green.png")),
            new ImageIcon(loadImage("Character_Mrs_Peacock.png")),
            new ImageIcon(loadImage("Character_Professor_Plum.png")) };

    public static final CardLabel[] CHRACTER_LABELS = createCardLabel(CHARACTER_IMG,
            Character.get(0));

    public static final ImageIcon[] WEAPON_IMG = {
            new ImageIcon(loadImage("Weapon_Candlestick.png")),
            new ImageIcon(loadImage("Weapon_Dagger.png")),
            new ImageIcon(loadImage("Weapon_Lead_Pipe.png")),
            new ImageIcon(loadImage("Weapon_Revolver.png")),
            new ImageIcon(loadImage("Weapon_Rope.png")),
            new ImageIcon(loadImage("Weapon_Spanner.png")) };

    public static final CardLabel[] WEAPON_LABELS = createCardLabel(WEAPON_IMG,
            Weapon.get(0));

    public static final ImageIcon[] LOCATION_IMG = {
            new ImageIcon(loadImage("Location_Kitchen.png")),
            new ImageIcon(loadImage("Location_Ball_room.png")),
            new ImageIcon(loadImage("Location_Conservatory.png")),
            new ImageIcon(loadImage("Location_Billard_Room.png")),
            new ImageIcon(loadImage("Location_Library.png")),
            new ImageIcon(loadImage("Location_Study.png")),
            new ImageIcon(loadImage("Location_Hall.png")),
            new ImageIcon(loadImage("Location_Lounge.png")),
            new ImageIcon(loadImage("Location_Dining_Room.png")) };

    public static final CardLabel[] LOCATION_LABELS = createCardLabel(LOCATION_IMG,
            Location.get(0));

    public static final ImageIcon ACTION_DISABLED_IMG = new ImageIcon(
            loadImage("Button_Action_Disabled.png"));
    public static final ImageIcon MOVE_DISABLED_IMG = new ImageIcon(
            loadImage("Button_Movement_Disabled.png"));

    public static final ImageIcon ACCUSATION_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Accusation_Default.png"));
    public static final ImageIcon ACCUSATION_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Accusation_Pressed.png"));

    public static final ImageIcon DOWN_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Down_Default.png"));
    public static final ImageIcon DOWN_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Down_Pressed.png"));

    public static final ImageIcon ENDTURN_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_EndTuen_Default.png"));
    public static final ImageIcon ENDTURN_PRESSED_IMG = new ImageIcon(
            loadImage("Button_EndTuen_Pressed.png"));

    public static final ImageIcon ENTER_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_EnterRoom_Default.png"));
    public static final ImageIcon ENTER_PRESSED_IMG = new ImageIcon(
            loadImage("Button_EnterRoom_Pressed.png"));

    public static final ImageIcon EXIT_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_ExitRoom_Default.png"));
    public static final ImageIcon EXIT_PRESSED_IMG = new ImageIcon(
            loadImage("Button_ExitRoom_Pressed.png"));

    public static final ImageIcon LEFT_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Left_Default.png"));
    public static final ImageIcon LEFT_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Left_Pressed.png"));

    public static final ImageIcon RIGHT_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Right_Default.png"));
    public static final ImageIcon RIGHT_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Right_Pressed.png"));

    public static final ImageIcon ROLLDICE_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_RollDice_Default.png"));
    public static final ImageIcon ROLLDICE_PRESSED_IMG = new ImageIcon(
            loadImage("Button_RollDice_Pressed.png"));

    public static final ImageIcon SECPAS_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_SecretPass_Default.png"));
    public static final ImageIcon SECPAS_PRESSED_IMG = new ImageIcon(
            loadImage("Button_SecretPass_Pressed.png"));

    public static final ImageIcon SUGGESTION_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Suggestion_Default.png"));
    public static final ImageIcon SUGGESTION_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Suggestion_Pressed.png"));

    public static final ImageIcon UP_DEFAULT_IMG = new ImageIcon(
            loadImage("Button_Up_Default.png"));
    public static final ImageIcon UP_PRESSED_IMG = new ImageIcon(
            loadImage("Button_Up_Pressed.png"));

    public static final Dimension MOVE_BUTTON_SIZE = new Dimension(85, 55);
    public static final Dimension ACTION_BUTTON_SIZE = new Dimension(135, 55);

}

class CardLabel extends JLabel {

    private Card card;

    public CardLabel(ImageIcon img, Card card) {
        super(img);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
