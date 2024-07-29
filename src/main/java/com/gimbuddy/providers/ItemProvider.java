package com.gimbuddy.providers;

import net.runelite.api.Item;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;

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
        label.setPreferredSize(new Dimension(36, 32));

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
        JLabel quantityLabel = getQuantityLabel(quantity);

        label.add(quantityLabel, BorderLayout.NORTH);

        return label;
    }

    private JLabel getQuantityLabel(int quantity) {
        JLabel quantityLabel = new JLabel(formatQuantity(quantity), SwingConstants.LEFT) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.BLACK); // Shadow color
                g2d.drawString(getText(), 1, getHeight()); // Offset shadow
                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, getHeight() - 1); // Original text
                g2d.dispose();
            }
        };

        quantityLabel.setFont(FontManager.getRunescapeSmallFont());
        if (quantity >= 1000000) {
            quantityLabel.setForeground(Color.GREEN);
        } else if (quantity >= 100000) {
            quantityLabel.setForeground(Color.WHITE);
        } else {
            quantityLabel.setForeground(Color.YELLOW);
        }
        quantityLabel.setOpaque(false);
        return quantityLabel;
    }

    private ImageIcon fetchIcon(int itemId) {
        try {
            return new ImageIcon(itemManager.getImage(itemId).getScaledInstance(36, 32, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }

    private String formatQuantity(int quantity) {
        if (quantity >= 1000000) {
            return (quantity / 1000) + "k";
        } else if (quantity >= 100000) {
            return (quantity / 1000) + "k";
        } else if (quantity >= 10000) {
            return String.format("%.1fk", quantity / 1000.0);
        } else {
            return String.valueOf(quantity);
        }
    }
}
