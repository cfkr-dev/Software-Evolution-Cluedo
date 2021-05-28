package view.dialogs;

import configs.WindowUtilities;
import ui.GUIClient;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChangeResolutionDialog extends JDialog {

    private static class CustomComboBoxRenderer extends JLabel implements ListCellRenderer<Dimension> {

        public CustomComboBoxRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Dimension> list, Dimension dimension, int index, boolean isSelected, boolean cellHasFocus) {

            String element;
            if (dimension.width == WindowUtilities.getFullWidth() && dimension.height == WindowUtilities.getFullHeight()){
                element = dimension.width + "×" + dimension.height + " (Current)";
            } else {
                element = dimension.width + "×" + dimension.height;
            }
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

    private final String[] RESOLUTIONS = {"1024×768", "1024×720", "1280×720", "1920×1080", "1366×768"};
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private DefaultComboBoxModel<Dimension> dimenstionListModel = new DefaultComboBoxModel<>();
    private JComboBox<Dimension> resolutionsComboBox;
    private JButton applyButton;
    private JButton okButton;

    public ChangeResolutionDialog(GUIClient parent, Window windowForComponent, String string) {
        super(windowForComponent, string);

        // initialise the main panel, and set a vertical BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        resolutionsComboBox = new JComboBox<>();
        initializeResolutions();
        resolutionsComboBox.setModel(dimenstionListModel);
        resolutionsComboBox.setSelectedItem(new Dimension(WindowUtilities.getFullWidth(), WindowUtilities.getFullHeight()));
        resolutionsComboBox.setRenderer(new CustomComboBoxRenderer());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        applyButton = new JButton();
        applyButton.setText("Apply");
        applyButton.addActionListener(e -> {
            parent.changeResolution((Dimension) resolutionsComboBox.getSelectedItem());
        });

        okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(e -> {
            parent.changeResolution((Dimension) resolutionsComboBox.getSelectedItem());
            ChangeResolutionDialog.this.dispose();
        });

        buttonsPanel.add(applyButton);
        buttonsPanel.add(okButton);

        resolutionsComboBox.setPreferredSize(new Dimension(150, 50));

        mainPanel.add(resolutionsComboBox);
        mainPanel.add(buttonsPanel);

        this.add(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    private void initializeResolutions() {
        ArrayList<Dimension> sortingList = new ArrayList<>();

        sortingList.add(screenSize);
        Dimension currectSize = new Dimension(WindowUtilities.getFullWidth(), WindowUtilities.getFullHeight());
        if (!sortingList.contains(currectSize)) {
            sortingList.add(currectSize);
        }

        for (String resolution : RESOLUTIONS) {
            int resolutionX = Integer.parseInt(resolution.substring(0, resolution.indexOf('×')));
            int resolutionY = Integer.parseInt(resolution.substring(resolution.indexOf('×') + 1));
            Dimension dimension = new Dimension(resolutionX, resolutionY);
            if (!sortingList.contains(dimension)) {
                sortingList.add(dimension);
            }
        }

        sortingList.sort((o1, o2) -> {
            if (o1.width < o2.width) {
                return -1;
            } else if (o1.width > o2.width) {
                return 1;
            } else return Integer.compare(o1.height, o2.height);
        });

        dimenstionListModel.addAll(sortingList);
    }
}
