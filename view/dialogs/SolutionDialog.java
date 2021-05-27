package view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import game.Suggestion;
import ui.GUIClient;
import configs.WindowUtilities;
import view.PlayerPanelCanvas;


/**
 * This class is made for showing the game's solution when it is ended and de user suggested it
 *
 * @author G7EAS
 */

public class SolutionDialog extends JDialog implements ComponentListener {

    private final JLabel character;
    private final JLabel weapon;
    private final JLabel location;
    private final Suggestion solution;

    /**
     * Constructor method. This dialog contains the tree cards in the solution and buttons for exit or play again
     *
     * @param guiClient          GUI client which executed the game
     * @param windowForComponent Window passed
     * @param game_solution      Title of the dialog
     */
    public SolutionDialog(GUIClient guiClient, Window windowForComponent, String game_solution) {

        super(windowForComponent, game_solution);
        this.addComponentListener(this);

        solution = guiClient.getSolution();

        /* Panel which contains the character's solution image */
        character = new JLabel();
        character.setIcon(PlayerPanelCanvas.CHARACTER_IMG[solution.getCharacter().ordinal()]);
        /* Panel which contains the weapon's solution image */
        weapon = new JLabel();
        weapon.setIcon(PlayerPanelCanvas.WEAPON_IMG[solution.getWeapon().ordinal()]);
        /* Panel which contains the location's solution image */
        location = new JLabel();
        location.setIcon(PlayerPanelCanvas.LOCATION_IMG[solution.getLocation().ordinal()]);
        /* This panel contains the interaction with the user for exit or play again */
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton exit = new JButton("Exit");
        JButton playAgain = new JButton("Play again");
        /* Defining actions for the buttons */
        playAgain.addActionListener(e -> {
            new NumberSetupDialog(guiClient, SwingUtilities.windowForComponent(this),
                    "Setup Wizard");
            SolutionDialog.this.dispose();
        });
        exit.addActionListener(e -> System.exit(0));
        /* Including the buttons in the panel */
        buttonPanel.add(playAgain);
        buttonPanel.add(exit);
        /* Including card icons in the main panel */
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(character, BorderLayout.WEST);
        mainPanel.add(weapon, BorderLayout.CENTER);
        mainPanel.add(location, BorderLayout.EAST);


        // pack and show it
        this.add(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(guiClient);
        /* Updates the actual size for a possible later resize of the component */
        WindowUtilities.setLastWidth(this.getWidth());
        WindowUtilities.setLastheight(this.getHeight());
        this.setVisible(true);

    }

   /* When the component is resized, the event catches it and resize the images */
    @Override
    public void componentResized(ComponentEvent e) {

        WindowUtilities.setWidth(this.getWidth());
        WindowUtilities.setHeight(this.getHeight());

        /* Goes taking the card of solution and calls the image resizer from WindowUtilities */
        PlayerPanelCanvas.CHARACTER_IMG[solution.getCharacter().ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.CHARACTER_IMG[solution.getCharacter().ordinal()]);
        character.setIcon(PlayerPanelCanvas.CHARACTER_IMG[solution.getCharacter().ordinal()]);

        PlayerPanelCanvas.WEAPON_IMG[solution.getWeapon().ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.WEAPON_IMG[solution.getWeapon().ordinal()]);
        weapon.setIcon(PlayerPanelCanvas.WEAPON_IMG[solution.getWeapon().ordinal()]);

        PlayerPanelCanvas.LOCATION_IMG[solution.getLocation().ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.LOCATION_IMG[solution.getLocation().ordinal()]);
        location.setIcon(PlayerPanelCanvas.LOCATION_IMG[solution.getLocation().ordinal()]);

        /* After that, repaints to see the change */
        repaint();

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
}
