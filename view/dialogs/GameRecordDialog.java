package view.dialogs;

import item.card.Card;
import item.card.Character;
import item.card.Location;
import item.card.Weapon;
import configs.Configs;
import game.GameRecord;
import ui.GUIClient;
import view.PlayerPanelCanvas;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;

public class GameRecordDialog extends JDialog {

    private class CustomListRenderer extends JLabel implements ListCellRenderer<GameRecord> {

        public CustomListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GameRecord> list, GameRecord gameRecord, int i,
                                                      boolean isSelected, boolean cellHasFocus) {

            i++;
            String element = "Game " + i + " | " + gameRecord.getDateGame() + " | Winner: " + gameRecord.getGameWinner();
            setText(element);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }

    }

    private ArrayList<GameRecord> configurationsGameRecords = Configs.getConfiguration().getGameRecords();

    private DefaultListModel<GameRecord> gameRecordStringList = new DefaultListModel<>();
    private GameRecord selectedValue;

    public GameRecordDialog(GUIClient parent, Window windowForComponent, String string) {

        super(windowForComponent, string);

        listToDefaultListModel(configurationsGameRecords);

        // add a list of game results
        JList<GameRecord> list = new JList<>(this.gameRecordStringList);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(gameRecordStringList.size());
        list.setCellRenderer(new CustomListRenderer());

        JScrollPane listScroller = new JScrollPane(list);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel gameRecordsListColumn = new JPanel();
        gameRecordsListColumn.setLayout(new BoxLayout(gameRecordsListColumn, BoxLayout.Y_AXIS));
        gameRecordsListColumn.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        gameRecordsListColumn.setPreferredSize(new Dimension(400, 600));
        gameRecordsListColumn.setMaximumSize(new Dimension(400, 600));
        gameRecordsListColumn.setAlignmentY(Component.CENTER_ALIGNMENT);
        if (gameRecordStringList.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No game records saved.\nPlease, win a game!", "No records found", JOptionPane.WARNING_MESSAGE);
        } else {
            gameRecordsListColumn.add(listScroller);

            JPanel viewMoreColumn = new JPanel();
            viewMoreColumn.setLayout(new BoxLayout(viewMoreColumn, BoxLayout.Y_AXIS));
            viewMoreColumn.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            viewMoreColumn.setPreferredSize(new Dimension(800, 600));
            viewMoreColumn.setMaximumSize(new Dimension(800, 600));
            viewMoreColumn.setAlignmentY(Component.CENTER_ALIGNMENT);

            JPanel nameAndDatePanel = new JPanel();
            nameAndDatePanel.setLayout(new BoxLayout(nameAndDatePanel, BoxLayout.Y_AXIS));
            nameAndDatePanel.setMaximumSize(new Dimension(800, 95));
            nameAndDatePanel.setAlignmentY(Component.CENTER_ALIGNMENT);

            JLabel winnerLabel = new JLabel();
            winnerLabel.setFont(new Font("Calibre", 1, 40));
            winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel dateLabel = new JLabel();
            dateLabel.setFont(new Font("Calibre", 1, 20));
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
            sep1.setBackground(Color.darkGray);
            sep1.setVisible(false);

            nameAndDatePanel.add(winnerLabel);
            nameAndDatePanel.add(dateLabel);
            nameAndDatePanel.add(sep1);

            JLabel solutionCardsLabel = new JLabel();
            solutionCardsLabel.setFont(new Font("Calibre", 1, 40));
            solutionCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            solutionCardsLabel.setAlignmentY(Component.TOP_ALIGNMENT);

            JPanel cardsPanel = new JPanel();
            cardsPanel.setMaximumSize(new Dimension(800, 200));
            cardsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
            cardsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel characterImgLabel = new JLabel();
            JLabel weaponImgLabel = new JLabel();
            JLabel locationImgLabel = new JLabel();

            cardsPanel.add(characterImgLabel);
            cardsPanel.add(weaponImgLabel);
            cardsPanel.add(locationImgLabel);

            JPanel playerCardsPanel = new JPanel();
            playerCardsPanel.setMaximumSize(new Dimension(800, 200));
            playerCardsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
            playerCardsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel playerCardsLabel = new JLabel();
            playerCardsLabel.setFont(new Font("Calibre", 1, 40));
            playerCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerCardsLabel.setAlignmentY(Component.TOP_ALIGNMENT);

            viewMoreColumn.add(nameAndDatePanel);
            viewMoreColumn.add(solutionCardsLabel);
            viewMoreColumn.add(cardsPanel);
            viewMoreColumn.add(playerCardsLabel);
            viewMoreColumn.add(playerCardsPanel);

            list.addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) {
                    playerCardsPanel.removeAll();

                    selectedValue = list.getSelectedValue();

                    winnerLabel.setText("Winner: " + selectedValue.getGameWinner());
                    dateLabel.setText("Date: " + selectedValue.getDateGame());

                    sep1.setVisible(true);

                    solutionCardsLabel.setText("Solution cards");

                    characterImgLabel.setIcon(PlayerPanelCanvas.CHARACTER_IMG[selectedValue.getSolution().getCharacter().ordinal()]);
                    characterImgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    weaponImgLabel.setIcon(PlayerPanelCanvas.WEAPON_IMG[selectedValue.getSolution().getWeapon().ordinal()]);
                    weaponImgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    locationImgLabel.setIcon(PlayerPanelCanvas.LOCATION_IMG[selectedValue.getSolution().getLocation().ordinal()]);
                    locationImgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    playerCardsLabel.setText("Player cards");

                    for (Card c : selectedValue.getCardsPlayer()) {
                        if (c instanceof Character) {
                            playerCardsPanel.add(new JLabel(PlayerPanelCanvas.CHARACTER_IMG[((Character) c).ordinal()]));
                        } else if (c instanceof Weapon) {
                            playerCardsPanel.add(new JLabel(PlayerPanelCanvas.WEAPON_IMG[((Weapon) c).ordinal()]));
                        } else {
                            playerCardsPanel.add(new JLabel(PlayerPanelCanvas.LOCATION_IMG[((Location) c).ordinal()]));
                        }
                    }
                }
            });

            mainPanel.add(gameRecordsListColumn);
            mainPanel.add(Box.createRigidArea(new Dimension(15, 20)));
            mainPanel.add(viewMoreColumn);
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
    }

    private void listToDefaultListModel(ArrayList<GameRecord> list) {
        for (GameRecord elem : list) {
            this.gameRecordStringList.addElement(elem);
        }
    }
}
