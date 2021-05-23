package view.dialogs;

import ui.GUIClient;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;

public class GameRecordDialog extends JDialog {

    private DefaultListModel<String> GameRecordList = new DefaultListModel</*GameRecord*/ String>();

    public GameRecordDialog(GUIClient parent, Window windowForComponent, String string/*, ArrayList<GameRecord> GameRecordList*/) {
        super(windowForComponent, string);

        ArrayList<String> lista = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            lista.add(String.valueOf(i));
        }
        for (String s: lista) {
            GameRecordList.addElement(s);
        }

        //listToDefaultListModel(/*GameRecordList*/ list);

        // add a list of game results
        JList</*GameRecord*/ String> list = new JList<>(this.GameRecordList); //data has type Object[]
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(GameRecordList.size());
        JScrollPane listScroller = new JScrollPane(list);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== first column, game record scrollable list panel =====

        JPanel firstRow = new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.Y_AXIS));
        firstRow.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        firstRow.setPreferredSize(new Dimension(400,600));
        firstRow.setAlignmentY(Component.CENTER_ALIGNMENT);
        firstRow.add(listScroller);

        // ===== second column, view more panel =====

        JPanel secondRow = new JPanel();
        secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.Y_AXIS));
        secondRow.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        secondRow.setPreferredSize(new Dimension(600,600));
        secondRow.setAlignmentY(Component.CENTER_ALIGNMENT);


        mainPanel.add(firstRow);
        mainPanel.add(Box.createRigidArea(new Dimension(15, 20)));
        mainPanel.add(secondRow);
        mainPanel.add(Box.createRigidArea(new Dimension(15, 20)));

        // pack and show it
        this.add(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        
    }

//    private void listToDefaultListModel(ArrayList</*GameRecord*/String> l) {
//        for (GameRecord elem: l){
//            this.GameRecordList.addElement(elem);
//        }
//    }
}
