package view.dialogs;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import card.Card;
import card.Character;
import card.Location;
import card.Weapon;
import game.Game;
import game.Suggestion;
import tile.Position;
import tile.Room;
import ui.GUIClient;
import view.PlayerPanelCanvas;

/**
 * This class is a custom dialog for players to make a suggestion or accusation.
 * 
 * @author G7EAS
 *
 */
public class SuggestionDialog extends JDialog {
    /**
     * The preferred dimension for card displaying
     */
    private static final Dimension CARD_DIMENSION = new Dimension(
            PlayerPanelCanvas.CHARACTER_IMG[0].getIconWidth(),
            PlayerPanelCanvas.CHARACTER_IMG[0].getIconHeight());

    /**
     * A CardLayout to manage the panel switch
     */
    private final CardLayout cardLayout = new CardLayout();

    /**
     * The GUI of the game
     */
    private final GUIClient gui;

    /**
     * This flag indicates whether this is a suggestion or a accusation
     */
    private final boolean isAccusation;

    /**
     * Which character is in this suggestion/accusation?
     */
    private Character character;

    /**
     * Which weapon is in this suggestion/accusation?
     */
    private Weapon weapon;

    /**
     * Which location is in this suggestion/accusation?
     */
    private Location location;

    private final Game game;

    /**
     * Construct a dialog, let players choose character, weapon, location respectively,
     * and make the suggestion/accusation.
     * 
     * @param parent
     *            --- the Main GUI of this game
     * @param windowForComponent
     *            --- the owner component
     * @param title
     *            --- the tile of this dialog
     * @param isAccusation
     *            --- A flag indicates whether this is a suggestion or a accusation
     */
    public SuggestionDialog(GUIClient parent, Window windowForComponent, String title,
            boolean isAccusation, Game game) {
        super(windowForComponent, title);
        this.gui = parent;
        this.isAccusation = isAccusation;
        this.game = game;

        JPanel mainPanel = new JPanel();

        // we use card layout
        mainPanel.setLayout(cardLayout);
        addCharacterPanel(mainPanel);
        addWeaponPanel(mainPanel);
        addLocationPanel(mainPanel);

        // and show the dialog
        this.add(mainPanel);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setModal(true);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    /**
     * This method build the first panel of the CardLayout, for choosing a character.
     * 
     * @param mainPanel
     *            --- the parent Panel with CardLayout
     */
    private void addCharacterPanel(JPanel mainPanel) {

        JPanel characterPanel = new JPanel();
        characterPanel.setLayout(new BoxLayout(characterPanel, BoxLayout.Y_AXIS));
        characterPanel.setBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createSoftBevelBorder(BevelBorder.RAISED),
                                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)),
                        "Select Character:", TitledBorder.LEFT, TitledBorder.TOP));

        List<JRadioButton> rButtonList = new ArrayList<>();

        JPanel cardDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (JRadioButton b : rButtonList) {
                    if (b.isSelected()) {
                        // draw the selected card
                        g.drawImage(
                                PlayerPanelCanvas.CHARACTER_IMG[Integer
                                        .parseInt(b.getActionCommand())].getImage(),
                                0, 0, this);
                        break;
                    }
                }
            }
        };
        cardDisplay.setPreferredSize(CARD_DIMENSION);
        cardDisplay.setAlignmentY(TOP_ALIGNMENT);
        cardDisplay.setVisible(true);

        // radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        JPanel radioButtonsPanel = new JPanel();
        radioButtonsPanel.setLayout(new BoxLayout(radioButtonsPanel, BoxLayout.Y_AXIS));
        radioButtonsPanel.setAlignmentY(TOP_ALIGNMENT);

        // two buttons at bottom
        JButton confirm = new JButton("Next");
        confirm.setEnabled(false);
        JButton cancel = new JButton("Cancel");

        // an action listener for radio buttons
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardDisplay.repaint();
                if (!confirm.isEnabled()) {
                    confirm.setEnabled(true);
                }
            }
        };

        // add radio buttons
        for (int i = 0; i < Character.values().length; i++) {
            Character c = Character.get(i);
            JRadioButton rButton = new JRadioButton(c.toString(), false);
            rButton.setActionCommand(String.valueOf(i));
            rButton.addActionListener(al);
            rButton.setAlignmentX(LEFT_ALIGNMENT);
            radioButtonGroup.add(rButton);
            rButtonList.add(rButton);
            radioButtonsPanel.add(rButton);
            radioButtonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        }

        legendHighlights(radioButtonsPanel);


        // the middle panel to hold radio buttons and card display
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
        midPanel.add(radioButtonsPanel);
        midPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        midPanel.add(cardDisplay);
        midPanel.add(Box.createRigidArea(new Dimension(5, 5)));

        // a text prompt
        JLabel text = new JLabel("Who?");
        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.add(text);

        // an info text about the meaning of highlighted lines



        String name= "character";
        suspiciousHighlighted(rButtonList, name);

        // confirm button's listener
        confirm.addActionListener(e -> {
            for (JRadioButton b : rButtonList) {
                if (b.isSelected()) {
                    character = Character.get(Integer.parseInt(b.getActionCommand()));
                    break;
                }
            }
            cardLayout.next(mainPanel);
        });

        // cancel button's listener
        cancel.addActionListener(e -> SuggestionDialog.this.dispose());

        // bottom panel, which contains two buttons
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(cancel);
        buttonPane.add(Box.createRigidArea(new Dimension(30, 20)));
        buttonPane.add(confirm);

        // put top, middle, bottom button together
        characterPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        characterPanel.add(textPane);
        characterPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        characterPanel.add(midPanel);
        //characterPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        //characterPanel.add(infoText);
        characterPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        characterPanel.add(buttonPane);


        // finally, add this panel into CardLayout
        mainPanel.add(characterPanel);
    }

    /**
     * This method build the second panel of the CardLayout, for choosing a weapon.
     * 
     * @param mainPanel
     *            --- the parent Panel with CardLayout
     */
    private void addWeaponPanel(JPanel mainPanel) {

        JPanel weaponPanel = new JPanel();
        weaponPanel.setLayout(new BoxLayout(weaponPanel, BoxLayout.Y_AXIS));
        weaponPanel.setBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createSoftBevelBorder(BevelBorder.RAISED),
                                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)),
                        "Select Weapon:", TitledBorder.CENTER, TitledBorder.TOP));

        List<JRadioButton> rButtonList = new ArrayList<>();

        JPanel cardDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (JRadioButton b : rButtonList) {
                    if (b.isSelected()) {
                        g.drawImage(
                                PlayerPanelCanvas.WEAPON_IMG[Integer
                                        .parseInt(b.getActionCommand())].getImage(),
                                0, 0, this);
                        break;
                    }
                }
            }
        };
        cardDisplay.setPreferredSize(CARD_DIMENSION);
        cardDisplay.setAlignmentY(TOP_ALIGNMENT);
        cardDisplay.setVisible(true);

        // radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        JPanel radioButtonsPanel = new JPanel();
        radioButtonsPanel.setLayout(new BoxLayout(radioButtonsPanel, BoxLayout.Y_AXIS));
        radioButtonsPanel.setAlignmentY(TOP_ALIGNMENT);

        // two buttons at bottom
        JButton confirm = new JButton("Next");
        confirm.setEnabled(false);
        JButton cancel = new JButton("Previous");
        cancel.setEnabled(true);

        // an action listener for radio buttons
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardDisplay.repaint();
                if (!confirm.isEnabled()) {
                    confirm.setEnabled(true);
                }
            }
        };

        // add radio buttons
        for (int i = 0; i < Weapon.values().length; i++) {
            Weapon w = Weapon.get(i);
            JRadioButton rButton = new JRadioButton(w.toString(), false);
            rButton.setActionCommand(String.valueOf(i));
            rButton.addActionListener(al);
            rButton.setAlignmentX(LEFT_ALIGNMENT);
            radioButtonGroup.add(rButton);
            rButtonList.add(rButton);
            radioButtonsPanel.add(rButton);
            radioButtonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        }

        legendHighlights(radioButtonsPanel);

        // the middle panel to hold radio buttons and card display
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
        midPanel.add(radioButtonsPanel);
        midPanel.add(Box.createRigidArea(new Dimension(40, 15)));
        midPanel.add(cardDisplay);
        midPanel.add(Box.createRigidArea(new Dimension(5, 5)));

        // a text prompt
        JLabel text = new JLabel("...commited crime with weapon?");
        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.add(text);


        String name= "weapon";

        suspiciousHighlighted(rButtonList, name);


        // confirm button's listener
        confirm.addActionListener(e -> {
            for (JRadioButton b : rButtonList) {
                if (b.isSelected()) {
                    weapon = Weapon.get(Integer.parseInt(b.getActionCommand()));
                    break;
                }
            }
            cardLayout.next(mainPanel);
        });

        // cancel button's listener
        cancel.addActionListener(e -> cardLayout.previous(mainPanel));

        // bottom panel, which contains two buttons
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(cancel);
        buttonPane.add(Box.createRigidArea(new Dimension(30, 20)));
        buttonPane.add(confirm);

        // put top, middle, bottom button together
        weaponPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        weaponPanel.add(textPane);
        weaponPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        weaponPanel.add(midPanel);
        weaponPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        weaponPanel.add(buttonPane);

        // finally, add this panel into CardLayout
        mainPanel.add(weaponPanel);
    }

    private void legendHighlights(JPanel radioButtonsPanel) {
        JLabel infoHighlights = new JLabel("*Highlighted options are the known cards by user ");
        JPanel infoText = new JPanel();
        infoText.setAlignmentX(LEFT_ALIGNMENT);
        infoText.add(infoHighlights);

        radioButtonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        radioButtonsPanel.add(infoText);
    }

    /**
     * This method build the third panel of the CardLayout, for choosing a location.
     * 
     * @param mainPanel
     *            --- the parent Panel with CardLayout
     */
    private void addLocationPanel(JPanel mainPanel) {

        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.setBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createSoftBevelBorder(BevelBorder.RAISED),
                                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)),
                        "Select Location:", TitledBorder.RIGHT, TitledBorder.TOP));
        List<JRadioButton> rButtonList = new ArrayList<>();

        JPanel cardDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                for (JRadioButton b : rButtonList) {
                    super.paintComponent(g);
                    if (b.isSelected()) {
                        g.drawImage(
                                PlayerPanelCanvas.LOCATION_IMG[Integer
                                        .parseInt(b.getActionCommand())].getImage(),
                                0, 0, this);
                        break;
                    }
                }
            }
        };
        cardDisplay.setPreferredSize(CARD_DIMENSION);
        cardDisplay.setAlignmentY(CENTER_ALIGNMENT);
        cardDisplay.setVisible(true);

        // radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        JPanel radioButtonsPanel = new JPanel();
        radioButtonsPanel.setLayout(new BoxLayout(radioButtonsPanel, BoxLayout.Y_AXIS));
        radioButtonsPanel.setAlignmentY(CENTER_ALIGNMENT);

        // two buttons at bottom
        JButton confirm;
        if (isAccusation) {
            confirm = new JButton("Make Accusation!");
        } else {
            confirm = new JButton("Make Suggestion!");
        }
        JButton cancel = new JButton("Previous");

        // an action listener for radio buttons
        ActionListener al = e -> {
            cardDisplay.repaint();
            if (!confirm.isEnabled()) {
                confirm.setEnabled(true);
            }
        };

        // add radio buttons
        for (int i = 0; i < Location.values().length; i++) {
            Location l = Location.get(i);
            JRadioButton rButton = new JRadioButton(l.toString(), false);
            rButton.setActionCommand(String.valueOf(i));
            rButton.addActionListener(al);
            rButton.setAlignmentX(LEFT_ALIGNMENT);
            radioButtonGroup.add(rButton);
            rButtonList.add(rButton);
            radioButtonsPanel.add(rButton);
            radioButtonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        }

        legendHighlights(radioButtonsPanel);

        // if this is making suggestion, disable other rooms, only enable the current room
        if (!isAccusation) {
            Character currentPlayer = gui.getCurrentPlayer();
            Position pos = gui.getPlayerPosition(currentPlayer);
            if (pos instanceof Room) {
                Room room = (Room) pos;
                Location loc = room.getRoom();

                for (JRadioButton button : rButtonList) {
                    // disable other rooms, only current room is selectable
                    if (Integer.parseInt(button.getActionCommand()) == loc.ordinal()) {
                        button.setEnabled(true);
                        button.setSelected(true);
                    } else {
                        button.setEnabled(false);
                        button.setSelected(false);
                    }
                }
            }
        }

        // the middle panel to hold radio buttons and card display
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
        midPanel.add(radioButtonsPanel);
        midPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        midPanel.add(cardDisplay);
        midPanel.add(Box.createRigidArea(new Dimension(5, 5)));

        // a text prompt
        JLabel text = new JLabel("...in which room?");
        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.add(text);


        String name= "location";

        suspiciousHighlighted(rButtonList, name);

        // confirm button's listener
        confirm.addActionListener(e -> {
            for (JRadioButton b : rButtonList) {
                if (b.isSelected()) {
                    location = Location.get(Integer.parseInt(b.getActionCommand()));
                    break;
                }
            }
            Suggestion sug = new Suggestion(character, weapon, location);

            // dispose dialog
            SuggestionDialog.this.dispose();

            if (isAccusation) {
                // now make an accusation
                gui.makeAccusation(sug);
            } else {
                // now make a suggestion
                gui.makeSuggestion(sug);

            }
        });

        // confirm button at bottom
        cancel.addActionListener(e -> cardLayout.previous(mainPanel));

        // bottom panel, which contains two buttons
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(cancel);
        buttonPane.add(Box.createRigidArea(new Dimension(30, 20)));
        buttonPane.add(confirm);

        // put top, middle, bottom button together
        locationPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        locationPanel.add(textPane);
        locationPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        locationPanel.add(midPanel);
        locationPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        locationPanel.add(buttonPane);

        // finally, add this panel into CardLayout
        mainPanel.add(locationPanel);
    }

    private void suspiciousHighlighted(List<JRadioButton> buttonList, String name){

        Set<Card> getSuspiciousCards = game.getKnownCards();
        Set<Card> cardsWellSuggested = game.getCardsWellSuggested();

        getSuspiciousCards.addAll(cardsWellSuggested);


        if(name.equals("character")) {
            for (JRadioButton b : buttonList) {
                Character characterSelected = Character.get(Integer.parseInt(b.getActionCommand()));
                if(getSuspiciousCards.contains(characterSelected)) {
                    b.setBackground(Color.cyan);
                }
            }
        }
        else if(name.equals("weapon")){
            for (JRadioButton b : buttonList) {
                Weapon weaponSelected = Weapon.get(Integer.parseInt(b.getActionCommand()));
                if(getSuspiciousCards.contains(weaponSelected)) {
                    b.setBackground(Color.cyan);
                }
            }
        }
        else{
            for (JRadioButton b : buttonList) {
                Location locationSelected = Location.get(Integer.parseInt(b.getActionCommand()));
                if(getSuspiciousCards.contains(locationSelected)) {
                    b.setBackground(Color.cyan);
                }
            }
        }

    }

}
