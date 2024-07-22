package com.gimbuddy.providers;

import net.runelite.api.Item;
import net.runelite.client.game.ItemManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class ItemProvider {
    private final ItemManager itemManager;

    public ItemProvider(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public JLabel getItemLabel(Map.Entry<String, Item> entry) {
        JLabel label = new JLabel();
        label.setOpaque(false);
        label.setPreferredSize(new Dimension(32, 32)); // Fixed size

        if (entry == null) {
            label.setIcon(null);
            label.setText("");
            return label;
        }

        String itemName = entry.getKey();
        Item item = entry.getValue();
        int quantity = item.getQuantity();

        int itemId = item.getId();
        ImageIcon itemIcon = fetchIcon(itemId);

        label.setIcon(Objects.requireNonNullElseGet(itemIcon, ImageIcon::new));

        label.setToolTipText(itemName);

        label.setLayout(new BorderLayout());
        JLabel quantityLabel = new JLabel(String.valueOf(quantity), SwingConstants.LEFT);
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 10));
        quantityLabel.setForeground(Color.WHITE);
        quantityLabel.setOpaque(false);
        label.add(quantityLabel, BorderLayout.NORTH);

        return label;
    }

    private ImageIcon fetchIcon(int itemId) {
        try {
            return new ImageIcon(itemManager.getImage(itemId).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }
}
