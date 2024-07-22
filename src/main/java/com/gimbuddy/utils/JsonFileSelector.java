package com.gimbuddy.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;

public class JsonFileSelector extends JDialog {
    private final JCheckBox rememberFileCheckbox;
    private String filePath;
    private final JLabel fileSelectedLabel;
    private static final String LOCAL_DB_FILE = "utils/local_db.txt";

    public JsonFileSelector() {
        super((Frame) null, "GIM Buddy Firestore Auth", true);
        setSize(400, 250);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JButton browseButton = new JButton("Browse");
        browseButton.setPreferredSize(new Dimension(80, 25));
        browseButton.addActionListener(new BrowseButtonListener());

        JButton loadButton = new JButton("Load");
        loadButton.setPreferredSize(new Dimension(80, 25));
        loadButton.addActionListener(new LoadButtonListener());

        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(80, 25));
        resetButton.addActionListener(new ResetButtonListener());

        rememberFileCheckbox = new JCheckBox("Remember file");

        fileSelectedLabel = new JLabel("", JLabel.CENTER);
        fileSelectedLabel.setForeground(new Color(0, 128, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(browseButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(resetButton);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        JLabel instructionLabel = new JLabel("Please select and load your Firestore Auth JSON key.", JLabel.CENTER);

        JLabel linkLabel = new JLabel("<html><a href=''>Click here for more instructions</a></html>");
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openWebpage();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(rememberFileCheckbox, BorderLayout.NORTH);
        bottomPanel.add(fileSelectedLabel, BorderLayout.SOUTH);

        contentPanel.add(instructionLabel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
        add(linkLabel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);

        loadRememberedFilePath();
    }

    private void loadRememberedFilePath() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOCAL_DB_FILE))) {
            String rememberedPath = reader.readLine();
            if (rememberedPath != null && !rememberedPath.isEmpty()) {
                File file = new File(rememberedPath);
                if (file.exists()) {
                    filePath = rememberedPath;
                    rememberFileCheckbox.setSelected(true);
                    fileSelectedLabel.setText("✔ File selected");
                } else {
                    JOptionPane.showMessageDialog(this, "Remembered file not found.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class BrowseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(JsonFileSelector.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.exists()) {
                    filePath = selectedFile.getPath();
                    fileSelectedLabel.setText("✔ File selected");
                } else {
                    JOptionPane.showMessageDialog(JsonFileSelector.this, "Selected file not found.");
                }
            }
        }
    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (filePath == null || filePath.isEmpty()) {
                JOptionPane.showMessageDialog(JsonFileSelector.this, "Please select a JSON file first.");
            } else {
                if (rememberFileCheckbox.isSelected()) {
                    saveFilePathToLocalDB(filePath);
                }
                dispose();
            }
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetFilePathInLocalDB();
            filePath = null;
            rememberFileCheckbox.setSelected(false);
            fileSelectedLabel.setText("No file selected");
        }
    }

    private void saveFilePathToLocalDB(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOCAL_DB_FILE))) {
            writer.write(filePath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save the file path.");
        }
    }

    private void resetFilePathInLocalDB() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOCAL_DB_FILE))) {
            writer.write("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to reset the file path.");
        }
    }

    public String getFilePath() {
        setVisible(true);
        return filePath;
    }

    private void openWebpage() {
        try {
            URI uri = new URI("https://github.com/GarrettJhnsn/gim-buddy/blob/main/README.md");
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String selectFile() {
        JsonFileSelector selector = new JsonFileSelector();
        return selector.getFilePath();
    }
}
