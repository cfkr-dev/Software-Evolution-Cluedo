package view.dialogs;

import ui.GUIClient;

import javax.management.MBeanAttributeInfo;
import javax.swing.*;
import java.awt.*;

public class ChangeResolutionDialog extends JDialog {

    private final String[] RESOLUTIONS = {"800×600", "1024×768", "1280×720", "1920×1080"};

    private JComboBox<String> resolutionsComboBox;

    public ChangeResolutionDialog(GUIClient parent, Window windowForComponent, String string) {
        super(windowForComponent, string);

        // initialise the main panel, and set a vertical BoxLayout
        JPanel mainPanel = new JPanel();
        //mainPanel.setPreferredSize(new Dimension(300,200));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        resolutionsComboBox = new JComboBox<>(RESOLUTIONS);
        resolutionsComboBox.addActionListener(e -> {
            String currentResolution = RESOLUTIONS[resolutionsComboBox.getSelectedIndex()];
            int resolutionX = Integer.parseInt(currentResolution.substring(0, currentResolution.indexOf('×')));
            int resolutionY = Integer.parseInt(currentResolution.substring(currentResolution.indexOf('×') + 1));
            parent.changeResolution(new Dimension(resolutionX, resolutionY));
        });
        resolutionsComboBox.setPreferredSize(new Dimension(150,50));

        mainPanel.add(resolutionsComboBox);

        this.add(mainPanel);
        this.setModal(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }
}
