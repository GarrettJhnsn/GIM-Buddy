package com.gimbuddy.components;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {

    public Button(String text) {
        super(text);
        setPreferredSize(new Dimension(150, 30));
        setMargin(new Insets(10, 10, 10, 10));
    }
}
