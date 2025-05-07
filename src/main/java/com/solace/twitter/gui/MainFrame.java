package com.solace.twitter.gui;

import com.solace.twitter.model.ProcessedTweet;
import com.solace.twitter.service.AgentMeshService;
import com.solace.twitter.service.ConfigManager;
import com.solace.twitter.service.TwitterService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Main application window
 */
public class MainFrame extends JFrame implements TwitterService.TweetUpdateListener {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private final ConfigManager configManager;
    private final TwitterService twitterService;
    private final AgentMeshService agentMeshService;
    
    private JTable tweetsTable;
    private DefaultTableModel tableModel;
    private JTextArea tweetTextArea;
    private JTextArea questionTextArea;
    private JTextArea answerTextArea;
    private JButton replyButton;
    private JButton settingsButton;
    private JButton startStopButton;
    private JLabel statusLabel;
    
    public MainFrame(ConfigManager configManager, TwitterService twitterService, AgentMeshService agentMeshService) {
        this.configManager = configManager;
        this.twitterService = twitterService;
        this.agentMeshService = agentMeshService;
        
        initializeUI();
        loadTweets();
        
        // Register as listener for tweet updates
        twitterService.addUpdateListener(this);
    }
    
    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        setTitle("Solace Twitter Integration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create table for tweets
        createTweetsTable();
        JScrollPane tableScrollPane = new JScrollPane(tweetsTable);
        
        // Create detail panel
        JPanel detailPanel = createDetailPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to main panel
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(detailPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Add main panel to frame
        setContentPane(mainPanel);
    }
    
    /**
     * Create the tweets table
     */
    private void createTweetsTable() {
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("Date");
        tableModel.addColumn("Username");
        tableModel.addColumn("Tweet");
        tableModel.addColumn("Replied");
        
        // Create table
        tweetsTable = new JTable(tableModel);
        tweetsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tweetsTable.setRowHeight(25);
        
        // Set column widths
        tweetsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        tweetsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        tweetsTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        tweetsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        // Add selection listener
        tweetsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tweetsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    displayTweetDetails(selectedRow);
                }
            }
        });
        
        // Add double-click listener to open tweet in browser
        tweetsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tweetsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        openTweetInBrowser(selectedRow);
                    }
                }
            }
        });
    }
    
    /**
     * Create the detail panel
     * @return Detail panel
     */
    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Tweet Details"));
        
        // Create tabbed pane for tweet, question, and answer
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create tweet text area
        tweetTextArea = new JTextArea();
        tweetTextArea.setEditable(false);
        tweetTextArea.setLineWrap(true);
        tweetTextArea.setWrapStyleWord(true);
        JScrollPane tweetScrollPane = new JScrollPane(tweetTextArea);
        tweetScrollPane.setPreferredSize(new Dimension(600, 200));
        
        // Create question text area
        questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
        
        // Create answer text area
        answerTextArea = new JTextArea();
        answerTextArea.setEditable(false);
        answerTextArea.setLineWrap(true);
        answerTextArea.setWrapStyleWord(true);
        JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
        
        // Add tabs
        tabbedPane.addTab("Tweet", tweetScrollPane);
        tabbedPane.addTab("Question", questionScrollPane);
        tabbedPane.addTab("Answer", answerScrollPane);
        
        // Create reply button
        replyButton = new JButton("Reply with AI Answer");
        replyButton.setEnabled(false);
        replyButton.addActionListener(this::replyToTweet);
        
        // Add components to detail panel
        detailPanel.add(tabbedPane, BorderLayout.CENTER);
        detailPanel.add(replyButton, BorderLayout.SOUTH);
        
        return detailPanel;
    }
    
    /**
     * Create the button panel
     * @return Button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 10));
        
        // Create left panel for status
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Status: Not monitoring");
        leftPanel.add(statusLabel);
        
        // Create right panel for buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Create settings button
        settingsButton = new JButton("Settings");
        settingsButton.addActionListener(this::openSettings);
        
        // Create start/stop button
        startStopButton = new JButton("Start Monitoring");
        startStopButton.addActionListener(this::toggleMonitoring);
        
        // Add buttons to right panel
        rightPanel.add(settingsButton);
        rightPanel.add(startStopButton);
        
        // Add panels to button panel
        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);
        
        return buttonPanel;
    }
    
    /**
     * Load tweets from the Twitter service
     */
    private void loadTweets() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get tweets from service
        List<ProcessedTweet> tweets = twitterService.getProcessedTweets();
        
        // Add tweets to table
        for (ProcessedTweet tweet : tweets) {
            addTweetToTable(tweet);
        }
    }
    
    /**
     * Add a tweet to the table
     * @param tweet Processed tweet
     */
    private void addTweetToTable(ProcessedTweet tweet) {
        tableModel.addRow(new Object[]{
            DATE_FORMAT.format(tweet.getCreatedAt()),
            tweet.getUsername(),
            tweet.getTweetText(),
            tweet.isReplied() ? "Yes" : "No"
        });
    }
    
    /**
     * Display tweet details
     * @param row Table row
     */
    private void displayTweetDetails(int row) {
        List<ProcessedTweet> tweets = twitterService.getProcessedTweets();
        if (row >= 0 && row < tweets.size()) {
            ProcessedTweet tweet = tweets.get(row);
            
            tweetTextArea.setText(tweet.getTweetText());
            questionTextArea.setText(tweet.getExtractedQuestion());
            answerTextArea.setText(tweet.getAnswer());
            
            replyButton.setEnabled(!tweet.isReplied());
        }
    }
    
    /**
     * Open tweet in browser
     * @param row Table row
     */
    private void openTweetInBrowser(int row) {
        List<ProcessedTweet> tweets = twitterService.getProcessedTweets();
        if (row >= 0 && row < tweets.size()) {
            ProcessedTweet tweet = tweets.get(row);
            
            try {
                Desktop.getDesktop().browse(new java.net.URI(tweet.getTwitterUrl()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to open browser: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Reply to a tweet
     * @param e Action event
     */
    private void replyToTweet(ActionEvent e) {
        int selectedRow = tweetsTable.getSelectedRow();
        if (selectedRow >= 0) {
            List<ProcessedTweet> tweets = twitterService.getProcessedTweets();
            if (selectedRow < tweets.size()) {
                ProcessedTweet tweet = tweets.get(selectedRow);
                
                // Confirm reply
                int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reply to this tweet with the AI-generated answer?",
                    "Confirm Reply",
                    JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        // Reply to tweet
                        twitterService.replyToTweet(tweet);
                        
                        // Update table
                        tableModel.setValueAt("Yes", selectedRow, 3);
                        
                        // Disable reply button
                        replyButton.setEnabled(false);
                        
                        JOptionPane.showMessageDialog(this,
                            "Reply sent successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Failed to send reply: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    /**
     * Open settings dialog
     * @param e Action event
     */
    private void openSettings(ActionEvent e) {
        SettingsDialog dialog = new SettingsDialog(this, configManager);
        dialog.setVisible(true);
    }
    
    /**
     * Toggle Twitter monitoring
     * @param e Action event
     */
    private void toggleMonitoring(ActionEvent e) {
        if (twitterService.isMonitoring()) {
            // Stop monitoring
            twitterService.stopMonitoring();
            startStopButton.setText("Start Monitoring");
            statusLabel.setText("Status: Not monitoring");
        } else {
            try {
                // Check if configuration is valid
                if (!configManager.isConfigValid()) {
                    JOptionPane.showMessageDialog(this,
                        "Please configure the application before starting monitoring.",
                        "Configuration Required",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Start monitoring
                twitterService.startMonitoring();
                startStopButton.setText("Stop Monitoring");
                statusLabel.setText("Status: Monitoring Twitter");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to start monitoring: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handle tweet update event
     * @param tweet Processed tweet
     */
    @Override
    public void onTweetProcessed(ProcessedTweet tweet) {
        // Add tweet to table on EDT
        SwingUtilities.invokeLater(() -> {
            addTweetToTable(tweet);
        });
    }
}