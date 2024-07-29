package com.gimbuddy.components.screens;

import com.gimbuddy.components.Button;
import com.gimbuddy.components.HintTextField;
import com.gimbuddy.providers.GroupProvider;
import com.gimbuddy.providers.BankProvider;
import lombok.Getter;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Connect extends JPanel {
    private final GroupProvider groupService;
    private final Main parentPanel;
    private HintTextField groupNameField;
    private HintTextField serverNameField;
    private Button connectButton;
    private JLabel serverStatusLabel;
    private final Button refreshButton;

    @Getter
    private String serverAddress;

    @Getter
    private final BankProvider bankProvider;

    @Inject
    public Connect(GroupProvider groupService, BankProvider bankProvider, Main parentPanel) {
        this.groupService = groupService;
        this.bankProvider = bankProvider;
        this.parentPanel = parentPanel;
        refreshButton = new Button("Refresh");
        connectPanel();

        String initialServerName = serverNameField.getText();
        if (!initialServerName.isEmpty()) {
            setServerAddress(initialServerName);
        }
        checkServerStatus();
    }

    private void connectPanel() {
        setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        groupNameField = new HintTextField("Group Ironman Name");
        groupNameField.setPreferredSize(new Dimension(0, 30));
        groupNameField.setHorizontalAlignment(JTextField.CENTER);
        connectButton = new Button("Connect");

        serverStatusLabel = new JLabel("Checking server status...");
        serverStatusLabel.setHorizontalAlignment(JLabel.CENTER);

        gbc.insets = new Insets(15, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        contentPanel.add(groupNameField, gbc);

        gbc.insets = new Insets(0, 10, 20, 10);
        gbc.gridy = 1;

        contentPanel.add(connectButton, gbc);
        contentPanel.setBorder(BorderFactory.createTitledBorder("GIM Buddy"));
        add(contentPanel, BorderLayout.CENTER);

        gbc.gridy = 2;

        contentPanel.add(serverStatusLabel, gbc);

        gbc.gridy = 3;

        serverNameField = new HintTextField("Server Name");
        serverNameField.setPreferredSize(new Dimension(0, 30));
        serverNameField.setHorizontalAlignment(JTextField.CENTER);

        contentPanel.add(serverNameField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 5, 10);
        contentPanel.add(refreshButton, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(2, 10, 5, 10);
        JLabel versionLabel = new JLabel("<html><i style='font-size:small; color:gray;'>0.1-alpha.2</i></html>");
        versionLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(versionLabel, gbc);

        refreshButton.addActionListener(e -> {
            setServerAddress(serverNameField.getText());
            checkServerStatus();
        });

        connectButton.addActionListener(e -> {
            String groupName = groupNameField.getText();
            connectButton.setText("Loading - please wait.");

            SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<String> doInBackground() {
                    return groupService.getGroupMembers(groupName, serverAddress);
                }

                @Override
                protected void done() {
                    try {
                        List<String> groupMembers = get();
                        if (groupMembers != null) {
                            parentPanel.goHome(groupName, groupMembers, serverAddress);
                        } else {
                            connectButton.setText("Connect");
                        }
                    } catch (Exception ex) {
                        connectButton.setText("Connect");
                    }
                }
            };
            worker.execute();
        });
    }

    private void setServerAddress(String serverAddress) {
        if (!serverAddress.startsWith("http://") && !serverAddress.startsWith("https://")) {
            serverAddress = "http://" + serverAddress;
        }
        this.serverAddress = serverAddress;
        groupService.setServerAddress(serverAddress);
    }

    private void checkServerStatus() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return groupService.isServerOnline(serverAddress);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean isOnline = get();
                    serverStatusLabel.setText(isOnline ? "Server is online" : "Server is offline");
                    serverStatusLabel.setForeground(isOnline ? Color.GREEN : Color.RED);
                } catch (Exception e) {
                    serverStatusLabel.setText("Server status unknown");
                    serverStatusLabel.setForeground(Color.GRAY);
                }
            }
        };
        worker.execute();
    }

    public void resetConnectButton() {
        if (connectButton != null) {
            connectButton.setText("Connect");
        }
    }
}
