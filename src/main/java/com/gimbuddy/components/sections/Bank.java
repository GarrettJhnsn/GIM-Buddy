package com.gimbuddy.components.sections;

import com.gimbuddy.components.Button;
import com.gimbuddy.components.HintTextField;
import com.gimbuddy.providers.BankProvider;
import com.gimbuddy.providers.ItemProvider;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Bank extends JPanel {
    private final JPanel bankItemsPanel;
    private final JPanel bankPanel;
    private final Button toggleButton;
    private final HintTextField searchField;
    private final JComboBox<String> bankSelectComboBox;
    private Map<String, Item> bankItems;

    @Setter
    private BankProvider bankProvider;
    private final ItemProvider itemProvider;
    private String groupName;
    private String serverAddress;

    @Inject
    public Bank(ItemManager itemManager) {
        this.itemProvider = new ItemProvider(itemManager);
        setLayout(new BorderLayout());

        bankItemsPanel = new JPanel();
        bankItemsPanel.setLayout(new GridBagLayout());
        bankItemsPanel.setMinimumSize(new Dimension(300, 200));

        bankPanel = new JPanel(new BorderLayout());
        bankPanel.setBorder(BorderFactory.createTitledBorder(""));
        bankPanel.add(bankItemsPanel, BorderLayout.CENTER);

        searchField = new HintTextField("Search");
        searchField.setPreferredSize(new Dimension(0, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterBankItems(searchField.getText());
            }
        });

        bankSelectComboBox = new JComboBox<>();
        bankSelectComboBox.setPreferredSize(new Dimension(150, 30));
        bankSelectComboBox.addActionListener(e -> {
            String selectedBank = (String) bankSelectComboBox.getSelectedItem();
            if (bankProvider != null && serverAddress != null) {
                bankItems = bankProvider.fetchBankItems(selectedBank, serverAddress);
                displayBankItems(bankItems); // Display items after fetching
                searchField.dispatchEvent(new KeyEvent(searchField, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, '\0')); // Trigger key release event
            }
        });

        toggleButton = new Button("Bank");
        toggleButton.addActionListener(e -> toggleBankPanel());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        topPanel.add(toggleButton, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 2, 5, 2);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        topPanel.add(bankSelectComboBox, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        topPanel.add(searchField, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(bankPanel, BorderLayout.CENTER);

        bankPanel.setVisible(false);
        searchField.setVisible(false);
        bankSelectComboBox.setVisible(false);
    }

    public void setGroupMembers(String groupName, List<String> groupMembers) {
        this.groupName = groupName;
        bankSelectComboBox.removeAllItems();
        bankSelectComboBox.addItem("All");
        bankSelectComboBox.addItem("Group Storage");

        for (String member : groupMembers) {
            bankSelectComboBox.addItem(member);
        }
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void toggleBankPanel() {
        boolean isVisible = bankPanel.isVisible();
        bankPanel.setVisible(!isVisible);
        searchField.setVisible(!isVisible);
        bankSelectComboBox.setVisible(!isVisible);
        toggleButton.setText(isVisible ? "Bank" : "Hide Bank");

        if (!isVisible && groupName != null && !groupName.isEmpty() && serverAddress != null) {
            bankItems = bankProvider.fetchBankItems("All", serverAddress);
            displayBankItems(bankItems);
        }
    }

    private void filterBankItems(String query) {
        if (bankItems == null) return;

        Map<String, Item> filteredItems = bankItems.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        displayBankItems(filteredItems);
    }

    public void displayBankItems(Map<String, Item> items) {
        SwingUtilities.invokeLater(() -> {
            bankItemsPanel.removeAll();
            GridBagConstraints gbc = new GridBagConstraints();

            int itemPaddingVertical = 8;
            int itemPaddingHorizontal = 8;
            int itemSize = 32 + 2 * itemPaddingVertical;

            gbc.insets = new Insets(itemPaddingVertical, itemPaddingHorizontal, itemPaddingVertical, itemPaddingHorizontal);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;

            int row = 0;
            int col = 0;
            final int columns = 4;
            final int maxItems = 28;

            int displayedItemsCount = 0;
            for (Map.Entry<String, Item> entry : items.entrySet()) {
                if (displayedItemsCount >= maxItems) {
                    break;
                }
                JLabel label = itemProvider.getItemLabel(entry);
                gbc.gridx = col;
                gbc.gridy = row;
                bankItemsPanel.add(label, gbc);

                col++;
                if (col >= columns) {
                    col = 0;
                    row++;
                }
                displayedItemsCount++;
            }

            bankItemsPanel.revalidate();
            bankItemsPanel.repaint();

            int rows = (int) Math.ceil((double) displayedItemsCount / columns);
            int panelWidth = columns * itemSize + (columns - 1) * itemPaddingHorizontal;
            int panelHeight = rows * itemSize;
            bankItemsPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

            int panelMargin = 10;
            bankItemsPanel.setBorder(BorderFactory.createEmptyBorder(panelMargin, panelMargin, panelMargin, panelMargin));

            revalidate();
            repaint();
        });
    }
}
