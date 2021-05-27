package view.dialogs;

import game.Suggestion;
import ui.GUIClient;
import utilities.WindowUtilities;
import view.PlayerPanelCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SolutionDialog extends JDialog implements WindowListener, ComponentListener {

    private final JLabel character;
    private final JLabel weapon;
    private final JLabel location;
    private final Suggestion solution;

    public SolutionDialog(GUIClient guiClient, Window windowForComponent, String game_solution) {
        super(windowForComponent, game_solution);
        this.addComponentListener(this);
        this.addWindowListener(this);
        solution = guiClient.getSolution();
        character = new JLabel();
        character.setIcon(PlayerPanelCanvas.CHARACTER_IMG[solution.character.ordinal()]);
        weapon = new JLabel();
        weapon.setIcon(PlayerPanelCanvas.WEAPON_IMG[solution.weapon.ordinal()]);
        location = new JLabel();
        location.setIcon(PlayerPanelCanvas.LOCATION_IMG[solution.location.ordinal()]);
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton playAgain = new JButton("Return");

        playAgain.addActionListener(e -> {
            new NumberSetupDialog(guiClient, SwingUtilities.windowForComponent(this),
                    "Setup Wizard");
            SolutionDialog.this.dispose();
        });

        JButton exit = new JButton("Exit");

        exit.addActionListener(e -> SolutionDialog.this.dispose());

        buttonPanel.add(playAgain);
        buttonPanel.add(exit);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(character, BorderLayout.WEST);
        mainPanel.add(weapon, BorderLayout.CENTER);
        mainPanel.add(location, BorderLayout.EAST);
        // and show the dialog
        this.add(mainPanel);
        WindowUtilities.setLastWidth(mainPanel.getWidth());
        WindowUtilities.setLastheight(mainPanel.getHeight());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setModal(true);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(guiClient);
        WindowUtilities.setLastWidth(this.getWidth());
        WindowUtilities.setLastheight(this.getHeight());
        this.setVisible(true);

    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {

        WindowUtilities.setWidth(this.getWidth());
        WindowUtilities.setHeight(this.getHeight());

        PlayerPanelCanvas.CHARACTER_IMG[solution.character.ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.CHARACTER_IMG[solution.character.ordinal()]);
        character.setIcon(PlayerPanelCanvas.CHARACTER_IMG[solution.character.ordinal()]);

        PlayerPanelCanvas.WEAPON_IMG[solution.weapon.ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.WEAPON_IMG[solution.weapon.ordinal()]);
        weapon.setIcon(PlayerPanelCanvas.WEAPON_IMG[solution.weapon.ordinal()]);

        PlayerPanelCanvas.LOCATION_IMG[solution.location.ordinal()] = WindowUtilities.resizeImage(PlayerPanelCanvas.LOCATION_IMG[solution.location.ordinal()]);
        location.setIcon(PlayerPanelCanvas.LOCATION_IMG[solution.location.ordinal()]);

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
