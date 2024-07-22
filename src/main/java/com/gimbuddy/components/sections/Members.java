package com.gimbuddy.components.sections;

import com.gimbuddy.components.Button;

import javax.swing.*;
import java.awt.*;

public class Members extends JPanel {
    private final JTextArea memberListArea;
    private final JPanel membersPanel;
    private final Button toggleButton;

    public Members() {
        setLayout(new BorderLayout());

        memberListArea = new JTextArea();
        memberListArea.setEditable(false);
        memberListArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder(""));
        membersPanel.add(new JScrollPane(memberListArea), BorderLayout.CENTER);

        toggleButton = new Button("Members");
        toggleButton.addActionListener(e -> toggleMembersPanel());

        add(toggleButton, BorderLayout.NORTH);
        add(membersPanel, BorderLayout.CENTER);
        membersPanel.setVisible(false);

        membersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                membersPanel.getBorder()
        ));
    }

    private void toggleMembersPanel() {
        boolean isVisible = membersPanel.isVisible();
        membersPanel.setVisible(!isVisible);
        toggleButton.setText(isVisible ? "Show Members" : "Hide Members");
    }

    public void setMemberList(String members) {
        memberListArea.setText(members);
    }
}
