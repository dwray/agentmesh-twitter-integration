package com.solace.twitter;

import com.formdev.flatlaf.FlatLightLaf;
import com.solace.twitter.gui.MainFrame;
import com.solace.twitter.service.AgentMeshService;
import com.solace.twitter.service.ConfigManager;
import com.solace.twitter.service.TwitterService;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the Solace Twitter Integration
 * This application monitors Twitter for Solace-related questions,
 * processes them through Solace Agent Mesh, and can optionally
 * reply with AI-generated answers.
 */
public class SolaceTwitterApp {
    private static final Logger LOGGER = Logger.getLogger(SolaceTwitterApp.class.getName());
    
    private ConfigManager configManager;
    private TwitterService twitterService;
    private AgentMeshService agentMeshService;
    private MainFrame mainFrame;
    
    public SolaceTwitterApp() {
        initializeServices();
        initializeGUI();
    }
    
    private void initializeServices() {
        try {
            // Initialize configuration manager
            configManager = new ConfigManager();
            
            // Initialize Agent Mesh service
            agentMeshService = new AgentMeshService(configManager);
            
            // Initialize Twitter service
            twitterService = new TwitterService(configManager, agentMeshService);
            
            LOGGER.info("All services initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize services", e);
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize application services: " + e.getMessage(),
                "Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initializeGUI() {
        try {
            // Set the look and feel
            FlatLightLaf.setup();
            
            // Create and show the main frame
            SwingUtilities.invokeLater(() -> {
                mainFrame = new MainFrame(configManager, twitterService, agentMeshService);
                mainFrame.setVisible(true);
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize GUI", e);
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize application GUI: " + e.getMessage(),
                "GUI Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    public void start() {
        try {
            // Start the Twitter service
            twitterService.startMonitoring();
            LOGGER.info("Application started successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            JOptionPane.showMessageDialog(mainFrame, 
                "Failed to start application: " + e.getMessage(),
                "Startup Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SolaceTwitterApp app = new SolaceTwitterApp();
        app.start();
    }
}