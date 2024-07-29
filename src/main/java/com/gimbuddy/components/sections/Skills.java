package com.gimbuddy.components.sections;

import com.gimbuddy.providers.SkillProvider;
import com.gimbuddy.providers.GroupProvider;
import com.gimbuddy.models.SkillsModel;
import com.google.gson.Gson;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Skills extends JPanel {
    private static final Logger logger = Logger.getLogger(Skills.class.getName());

    private final JButton skillsButton;
    private final JPanel skillsPanel;
    private final JPanel subButtonPanel;
    private final SkillProvider skillProvider;
    private final GroupProvider groupProvider;
    private final Map<String, Integer> skillIds;
    private boolean skillsVisible = false;

    @Inject
    public Skills(Gson gson, GroupProvider groupProvider) {
        this.skillProvider = new SkillProvider(gson, groupProvider);
        this.groupProvider = groupProvider;

        setLayout(new BorderLayout());

        skillIds = SkillsModel.getSkillIds();

        JButton trackingButton = new JButton("Tracking");
        skillsButton = new JButton("Skills");
        JButton addSkillButton = new JButton("+");

        skillsPanel = new JPanel();
        skillsPanel.setLayout(new BoxLayout(skillsPanel, BoxLayout.Y_AXIS));
        skillsPanel.setBorder(BorderFactory.createEmptyBorder());
        skillsPanel.setVisible(false);

        subButtonPanel = new JPanel(new GridBagLayout());
        subButtonPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        subButtonPanel.add(skillsButton, gbc);

        gbc.gridx = 1;
        subButtonPanel.add(addSkillButton, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        gbc.gridy = 0;
        buttonPanel.add(trackingButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(subButtonPanel, gbc);

        add(buttonPanel, BorderLayout.NORTH);
        add(skillsPanel, BorderLayout.CENTER);

        trackingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSkillsPanel();
            }
        });

        skillsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSkillsButton();
            }
        });

        addSkillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewSkill();
            }
        });
    }

    private void toggleSkillsPanel() {
        skillsVisible = !skillsVisible;
        skillsPanel.setVisible(skillsVisible);
        subButtonPanel.setVisible(skillsVisible);

        revalidate();
        repaint();
    }

    private void toggleSkillsButton() {
        if (skillsButton.getText().equals("Skills")) {
            skillsButton.setText("Hide Skills");
            displayPinnedSkills();
        } else {
            skillsButton.setText("Skills");
            skillsPanel.setVisible(false);
        }
        revalidate();
        repaint();
    }

    private void displayPinnedSkills() {
        SwingUtilities.invokeLater(() -> {
            String groupName = groupProvider.getCurrentGroupName();
            if (groupName == null) {
                logger.severe("Group name is null.");
                return;
            }

            Map<String, List<Map<String, Object>>> memberSkills = skillProvider.getPinnedSkills(groupName);

            skillsPanel.removeAll();
            skillsPanel.setLayout(new BoxLayout(skillsPanel, BoxLayout.Y_AXIS));

            for (Map.Entry<String, List<Map<String, Object>>> entry : memberSkills.entrySet()) {
                String memberName = entry.getKey();
                List<Map<String, Object>> skills = entry.getValue();

                if (skills.isEmpty()) {
                    continue;
                }

                JPanel memberPanel = new JPanel();
                memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));
                memberPanel.setBorder(BorderFactory.createTitledBorder(memberName));

                List<Map<String, Object>> sortedSkills = skills.stream()
                        .sorted((a, b) -> {
                            double percentageA = calculatePercentage((Double) a.get("level"), (Double) a.get("baseLevel"), (Double) a.get("goal"));
                            double percentageB = calculatePercentage((Double) b.get("level"), (Double) b.get("baseLevel"), (Double) b.get("goal"));
                            return Double.compare(percentageB, percentageA);
                        })
                        .collect(Collectors.toList());

                for (Map<String, Object> skillData : sortedSkills) {
                    String skillName = (String) skillData.get("name");
                    int currentLevel = ((Double) skillData.get("level")).intValue();
                    int baseLevel = ((Double) skillData.get("baseLevel")).intValue();
                    int goal = ((Double) skillData.get("goal")).intValue();

                    double percentageCompleted = calculatePercentage(currentLevel, baseLevel, goal);

                    Color progressColor;
                    if(percentageCompleted == 0) {
                        progressColor = Color.GRAY;
                    } else if (percentageCompleted <= 33) {
                        progressColor = Color.RED;
                    } else if (percentageCompleted <= 66) {
                        progressColor = Color.ORANGE;
                    } else {
                        progressColor = Color.GREEN;
                    }

                    JPanel skillPanel = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(2, 2, 2, 2); // Adjusted padding

                    JLabel skillLabel = new JLabel(skillName);
                    skillLabel.setFont(skillLabel.getFont().deriveFont(14f));

                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.weightx = 0.33;
                    skillPanel.add(skillLabel, gbc);

                    JLabel levelGoalLabel = new JLabel(currentLevel + "/" + goal);
                    levelGoalLabel.setFont(levelGoalLabel.getFont().deriveFont(14f));
                    gbc.gridx = 2;
                    gbc.weightx = 0.33;
                    gbc.anchor = GridBagConstraints.CENTER;
                    skillPanel.add(levelGoalLabel, gbc);

                    JLabel percentageLabel = new JLabel(String.format("%d%%", (int) percentageCompleted));
                    percentageLabel.setFont(percentageLabel.getFont().deriveFont(14f));
                    percentageLabel.setForeground(progressColor);
                    gbc.gridx = 3;
                    gbc.weightx = 0.33;
                    gbc.anchor = GridBagConstraints.EAST;
                    skillPanel.add(percentageLabel, gbc);

                    skillPanel.setBorder(new EmptyBorder(2, 2, 2, 2)); // Adjusted padding

                    skillLabel.setToolTipText("Base Level: " + baseLevel);

                    skillPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (e.isPopupTrigger()) {
                                showContextMenu(e, skillName);
                            }
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if (e.isPopupTrigger()) {
                                showContextMenu(e, skillName);
                            }
                        }

                        private void showContextMenu(MouseEvent e, String skillName) {
                            JPopupMenu contextMenu = new JPopupMenu();
                            JMenuItem unpinItem = new JMenuItem("Unpin " + skillName);
                            unpinItem.setFont(unpinItem.getFont().deriveFont(14f));
                            unpinItem.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent event) {
                                    unpinSkill(skillIds.get(skillName));
                                }
                            });
                            contextMenu.add(unpinItem);
                            contextMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    });

                    memberPanel.add(skillPanel);
                }

                skillsPanel.add(memberPanel);
            }

            skillsPanel.setVisible(true);
            revalidate();
            repaint();
        });
    }

    private double calculatePercentage(double currentLevel, double baseLevel, double goal) {
        if (currentLevel == goal) {
            return 100.0;
        } else if (goal != baseLevel) {
            return ((currentLevel - baseLevel) / (goal - baseLevel)) * 100.0;
        }
        return 0;
    }

    private void unpinSkill(int skillID) {
        String userName = groupProvider.getCurrentUserName();
        if (userName == null) {
            logger.severe("User name is null.");
            return;
        }

        new Thread(() -> {
            boolean success = skillProvider.unpinSkill(userName, skillID);
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    logger.info("Skill unpinned successfully!");
                    displayPinnedSkills();
                } else {
                    logger.severe("Failed to unpin skill.");
                }
            });
        }).start();
    }

    private void addNewSkill() {
        String[] skillNames = skillIds.keySet().toArray(new String[0]);
        String skillName = (String) JOptionPane.showInputDialog(this, "Select a skill to pin:", "Add Skill",
                JOptionPane.PLAIN_MESSAGE, null, skillNames, skillNames[0]);

        if (skillName != null) {
            int skillID = skillIds.get(skillName);
            String userName = groupProvider.getCurrentUserName();
            if (userName == null) {
                logger.severe("User name is null.");
                return;
            }

            String goalStr = JOptionPane.showInputDialog(this, "Set a goal (1-99):", "Set Goal", JOptionPane.PLAIN_MESSAGE);
            if (goalStr == null || goalStr.isEmpty()) {
                logger.severe("Goal input is null or empty.");
                return;
            }

            int goal;
            try {
                goal = Integer.parseInt(goalStr);
                if (goal < 1 || goal > 99) {
                    JOptionPane.showMessageDialog(this, "Goal must be between 1 and 99.", "Invalid Goal", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format for goal.", "Invalid Goal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new Thread(() -> {
                boolean success = skillProvider.pinSkill(userName, skillID, goal);
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        logger.info("Skill pinned successfully!");
                        displayPinnedSkills();
                    } else {
                        logger.severe("Failed to pin skill.");
                    }
                });
            }).start();
        }
    }
}
