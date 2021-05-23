package view.dialogs;

import card.Card;
import card.Character;
import card.Location;
import card.Weapon;
import game.GameRecord;
import game.Suggestion;
import ui.GUIClient;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;

public class GameRecordDialog extends JDialog {

    private ArrayList<GameRecord> configurationsGameRecords = /*Configs.getConfiguration().getGameRecords()*/ testDataLoading();

    private ArrayList<GameRecord> testDataLoading() {
        Character c1 = Character.get(1);
        Location l1 = Location.get(1);
        Weapon w1 = Weapon.get(1);

        ArrayList<Card> cl1 = new ArrayList<>();
        cl1.add(c1);
        cl1.add(l1);
        cl1.add(w1);

        Suggestion s1 = new Suggestion(c1, w1, l1);

        GameRecord g1 = new GameRecord(s1, "PepitoPerez", cl1);

        ArrayList<GameRecord> grl = new ArrayList<>();
        grl.add(g1);

        return grl;
    }

    private DefaultListModel<String> gameRecordStringList = new DefaultListModel<>();

    public GameRecordDialog(GUIClient parent, Window windowForComponent, String string) {

        super(windowForComponent, string);

        listToDefaultListModel(configurationsGameRecords);

        // add a list of game results
        JList<String> list = new JList<>(this.gameRecordStringList); //data has type Object[]
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(gameRecordStringList.size());
        JScrollPane listScroller = new JScrollPane(list);

        JTextArea noGameRecordsText = new JTextArea();
        noGameRecordsText.setFont(new Font("Calibre", 1, 40));
        noGameRecordsText.setText("No game records saved.\nPlease, win a game!");
        noGameRecordsText.setWrapStyleWord(true);
        noGameRecordsText.setLineWrap(true);
        noGameRecordsText.setEditable(false);
        noGameRecordsText.setOpaque(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== first column, game record scrollable list panel =====

        JPanel firstRow = new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.Y_AXIS));
        firstRow.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        firstRow.setPreferredSize(new Dimension(400,600));
        firstRow.setMaximumSize(new Dimension(400,600));
        firstRow.setAlignmentY(Component.CENTER_ALIGNMENT);
        if (gameRecordStringList.isEmpty()){
            firstRow.add(noGameRecordsText);
            firstRow.setPreferredSize(new Dimension(400,200));
            mainPanel.add(firstRow);
            mainPanel.add(Box.createRigidArea(new Dimension(15, 20)));
        } else {
            firstRow.add(listScroller);

            // ===== second column, view more panel =====

            JPanel secondRow = new JPanel();
            secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.Y_AXIS));
            secondRow.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            secondRow.setPreferredSize(new Dimension(600,600));
            secondRow.setMaximumSize(new Dimension(600,600));
            secondRow.setAlignmentY(Component.CENTER_ALIGNMENT);

            mainPanel.add(firstRow);
            mainPanel.add(Box.createRigidArea(new Dimension(15, 20)));
            mainPanel.add(secondRow);
            mainPanel.add(Box.createRigidArea(new Dimension(15, 20)));
        }

        // pack and show it
        this.add(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        
    }

    private void listToDefaultListModel(ArrayList<GameRecord> list) {
        int i = 0;
        for (GameRecord elem: list){
            i++;
            String element = "Game " + i + " | " + elem.getDateGame() + " | Winner: " + elem.getGameWinner();
            this.gameRecordStringList.addElement(element);
        }
    }
}
