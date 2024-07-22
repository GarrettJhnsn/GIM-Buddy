package com.gimbuddy.components.screens;

import com.gimbuddy.components.Button;
import com.gimbuddy.components.sections.Bank;
import com.gimbuddy.components.sections.Members;
import com.gimbuddy.providers.BankProvider;
import com.gimbuddy.providers.GroupProvider;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main extends PluginPanel {
    private JPanel connect;
    private JPanel home;
    private Members members;

    @Getter
    private Bank bank;
    private final GroupProvider groupService;

    @Setter
    private BankProvider bankProvider;
    private JLabel groupNameLabel;
    private JLabel groupRankLabel;

    @Inject
    private Gson gson;

    @Inject
    public Main(Client client, ItemManager itemManager, Gson gson) {
        this.gson = gson;
        this.groupService = new GroupProvider(client, gson);
        connect();
        home(itemManager);
    }

    private void connect() {
        connect = new Connect(groupService, bankProvider, this);
        setLayout(new BorderLayout());
        add(connect, BorderLayout.CENTER);
    }

    private void home(ItemManager itemManager) {
        home = new JPanel(new BorderLayout());

        groupNameLabel = new JLabel("", SwingConstants.CENTER);
        groupNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        groupRankLabel = new JLabel("", SwingConstants.CENTER);
        groupRankLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel nameRankPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        nameRankPanel.add(groupNameLabel);
        nameRankPanel.add(groupRankLabel);

        JPanel titleAndRankPanel = new JPanel(new BorderLayout());
        titleAndRankPanel.add(nameRankPanel, BorderLayout.CENTER);

        titleAndRankPanel.setBorder(BorderFactory.createTitledBorder("GIM Buddy"));

        members = new Members();
        members.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        bank = new Bank(itemManager);
        bank.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(members, BorderLayout.NORTH);
        centerPanel.add(bank, BorderLayout.CENTER);

        home.add(titleAndRankPanel, BorderLayout.NORTH);
        home.add(centerPanel, BorderLayout.CENTER);

        Button disconnectButton = new Button("Disconnect");
        JPanel disconnectButtonPanel = new JPanel(new BorderLayout());
        disconnectButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        disconnectButtonPanel.add(disconnectButton, BorderLayout.CENTER);

        disconnectButton.addActionListener(e -> goConnect());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(disconnectButtonPanel, BorderLayout.CENTER);
        home.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void goHome(String groupName, List<String> groupMembers, String serverAddress) {
        remove(connect);
        add(home, BorderLayout.CENTER);
        revalidate();
        repaint();

        bank.setGroupMembers(groupName, groupMembers);
        bank.setServerAddress(serverAddress); // Set the server address in Bank
        bankProvider.setGroupName(groupName);
        bank.setBankProvider(bankProvider);

        groupNameLabel.setText(groupName);

        String groupRank = groupService.fetchGroupRank(groupName, serverAddress);
        groupRankLabel.setText(groupRank);

        String updatedMemberList = groupService.updateMemberList(groupMembers);
        members.setMemberList(updatedMemberList);
    }

    private void goConnect() {
        remove(home);
        add(connect, BorderLayout.CENTER);
        revalidate();
        repaint();

        if (connect instanceof Connect) {
            ((Connect) connect).resetConnectButton();
        }
    }
}
