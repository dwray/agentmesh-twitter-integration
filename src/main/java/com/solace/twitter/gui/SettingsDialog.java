package com.solace.twitter.gui;

import com.solace.twitter.service.ConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog for configuring application settings
 */
public class SettingsDialog extends JDialog {
    private final ConfigManager configManager;
    
    private JTextField twitterApiKeyField;
    private JTextField twitterApiSecretField;
    private JTextField twitterAccessTokenField;
    private JTextField twitterAccessSecretField;
    private JTextField agentMeshApiKeyField;
    private JTextField agentMeshEndpointField;
    private JCheckBox autoReplyCheckBox;
    private JTextArea keywordsTextArea;
    
    public SettingsDialog(Frame owner, ConfigManager configManager) {
        super(owner, "Settings", true);
        this.configManager = configManager;
        
        initializeUI();
        loadSettings();
    }
    
    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        setSize(600, 500);
        setLocationRelativeTo(getOwner());
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create Twitter settings panel
        JPanel twitterPanel = createTwitterPanel();
        
        // Create Agent Mesh settings panel
        JPanel agentMeshPanel = createAgentMeshPanel();
        
        // Create general settings panel
        JPanel generalPanel = createGeneralPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Twitter API", twitterPanel);
        tabbedPane.addTab("Agent Mesh API", agentMeshPanel);
        tabbedPane.addTab("General", generalPanel);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to dialog
        setContentPane(mainPanel);
    }
    
    /**
     * Create the Twitter settings panel
     * @return Twitter settings panel
     */
    private JPanel createTwitterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // API Key
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("API Key:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        twitterApiKeyField = new JTextField(20);
        panel.add(twitterApiKeyField, gbc);
        
        // API Secret
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("API Secret:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        twitterApiSecretField = new JTextField(20);
        panel.add(twitterApiSecretField, gbc);
        
        // Access Token
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Access Token:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        twitterAccessTokenField = new JTextField(20);
        panel.add(twitterAccessTokenField, gbc);
        
        // Access Secret
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Access Secret:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        twitterAccessSecretField = new JTextField(20);
        panel.add(twitterAccessSecretField, gbc);
        
        // Help text
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea helpTextArea = new JTextArea(
            "To obtain Twitter API credentials:\n" +
            "1. Go to https://developer.twitter.com/en/portal/dashboard\n" +
            "2. Create a new app or select an existing app\n" +
            "3. Navigate to the 'Keys and Tokens' tab\n" +
            "4. Generate or regenerate the required keys and tokens\n" +
            "5. Copy and paste them into the fields above"
        );
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setBackground(panel.getBackground());
        panel.add(helpTextArea, gbc);
        
        return panel;
    }
    
    /**
     * Create the Agent Mesh settings panel
     * @return Agent Mesh settings panel
     */
    private JPanel createAgentMeshPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // API Key
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("API Key:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        agentMeshApiKeyField = new JTextField(20);
        panel.add(agentMeshApiKeyField, gbc);
        
        // Endpoint
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Endpoint:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        agentMeshEndpointField = new JTextField(20);
        panel.add(agentMeshEndpointField, gbc);
        
        // Help text
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea helpTextArea = new JTextArea(
            "To obtain Solace Agent Mesh API credentials:\n" +
            "1. Contact your Solace representative or visit the Solace Cloud portal\n" +
            "2. Request access to the Agent Mesh API\n" +
            "3. Generate an API key\n" +
            "4. Copy and paste the key and endpoint URL into the fields above"
        );
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setBackground(panel.getBackground());
        panel.add(helpTextArea, gbc);
        
        return panel;
    }
    
    /**
     * Create the general settings panel
     * @return General settings panel
     */
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Auto-reply checkbox
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        autoReplyCheckBox = new JCheckBox("Automatically reply to tweets with AI-generated answers");
        panel.add(autoReplyCheckBox, gbc);
        
        // Keywords label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Search Keywords (comma-separated):"), gbc);
        
        // Keywords text area
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        keywordsTextArea = new JTextArea(5, 20);
        keywordsTextArea.setLineWrap(true);
        keywordsTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(keywordsTextArea);
        panel.add(scrollPane, gbc);
        
        return panel;
    }
    
    /**
     * Create the button panel
     * @return Button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveSettings);
        
        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        // Add buttons to panel
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    /**
     * Load settings from configuration manager
     */
    private void loadSettings() {
        // Twitter settings
        twitterApiKeyField.setText(configManager.getConfig(ConfigManager.TWITTER_API_KEY));
        twitterApiSecretField.setText(configManager.getConfig(ConfigManager.TWITTER_API_SECRET));
        twitterAccessTokenField.setText(configManager.getConfig(ConfigManager.TWITTER_ACCESS_TOKEN));
        twitterAccessSecretField.setText(configManager.getConfig(ConfigManager.TWITTER_ACCESS_SECRET));
        
        // Agent Mesh settings
        agentMeshApiKeyField.setText(configManager.getConfig(ConfigManager.AGENT_MESH_API_KEY));
        agentMeshEndpointField.setText(configManager.getConfig(ConfigManager.AGENT_MESH_ENDPOINT));
        
        // General settings
        autoReplyCheckBox.setSelected(configManager.getBooleanConfig(ConfigManager.AUTO_REPLY_ENABLED));
        keywordsTextArea.setText(configManager.getConfig(ConfigManager.SEARCH_KEYWORDS));
    }
    
    /**
     * Save settings to configuration manager
     * @param e Action event
     */
    private void saveSettings(ActionEvent e) {
        // Twitter settings
        configManager.setConfig(ConfigManager.TWITTER_API_KEY, twitterApiKeyField.getText());
        configManager.setConfig(ConfigManager.TWITTER_API_SECRET, twitterApiSecretField.getText());
        configManager.setConfig(ConfigManager.TWITTER_ACCESS_TOKEN, twitterAccessTokenField.getText());
        configManager.setConfig(ConfigManager.TWITTER_ACCESS_SECRET, twitterAccessSecretField.getText());
        
        // Agent Mesh settings
        configManager.setConfig(ConfigManager.AGENT_MESH_API_KEY, agentMeshApiKeyField.getText());
        configManager.setConfig(ConfigManager.AGENT_MESH_ENDPOINT, agentMeshEndpointField.getText());
        
        // General settings
        configManager.setConfig(ConfigManager.AUTO_REPLY_ENABLED, String.valueOf(autoReplyCheckBox.isSelected()));
        configManager.setConfig(ConfigManager.SEARCH_KEYWORDS, keywordsTextArea.getText());
        
        // Save configuration
        configManager.saveConfig();
        
        // Close dialog
        dispose();
    }
}