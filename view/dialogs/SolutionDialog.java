package view.dialogs;


import game.Suggestion;
import ui.GUIClient;
import view.PlayerPanelCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SolutionDialog extends JDialog implements WindowListener {


    public SolutionDialog(GUIClient guiClient, Window windowForComponent, String game_solution) {
        super(windowForComponent, game_solution);
        this.addWindowListener(this);
        Suggestion solution = guiClient.getSolution();
        JLabel character = new JLabel();
        character.setIcon(PlayerPanelCanvas.CHARACTER_IMG[solution.character.ordinal()]);
        JLabel weapon = new JLabel();
        weapon.setIcon(PlayerPanelCanvas.WEAPON_IMG[solution.weapon.ordinal()]);
        JLabel location = new JLabel();
        location.setIcon(PlayerPanelCanvas.LOCATION_IMG[solution.location.ordinal()]);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new Button("Return"), BorderLayout.SOUTH);
        mainPanel.add(character, BorderLayout.WEST);
        mainPanel.add(weapon, BorderLayout.CENTER);
        mainPanel.add(location, BorderLayout.EAST);
        this.add(mainPanel);
        // and show the dialog
        this.add(mainPanel);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setModal(true);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(guiClient);
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
}
