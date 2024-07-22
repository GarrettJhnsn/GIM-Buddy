package com.gimbuddy.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class HintTextField extends JTextField {
    private final String hint;

    public HintTextField(String hint) {
        this.hint = hint;

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont()); // Use normal font size
            g2.setColor(Color.GRAY);
            FontMetrics fm = g2.getFontMetrics();
            int textHeight = fm.getHeight();
            int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
            g2.drawString(hint, getInsets().left, textY);
            g2.dispose();
        }
    }
}
