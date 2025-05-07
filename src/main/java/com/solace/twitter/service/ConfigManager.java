package com.solace.twitter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages application configuration including API keys and settings
 */
public class ConfigManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    
    // Configuration keys
    public static final String TWITTER_API_KEY = "twitter.api.key";
    public static final String TWITTER_API_SECRET = "twitter.api.secret";
    public static final String TWITTER_ACCESS_TOKEN = "twitter.access.token";
    public static final String TWITTER_ACCESS_SECRET = "twitter.access.secret";
    public static final String AGENT_MESH_API_KEY = "agentmesh.api.key";
    public static final String AGENT_MESH_ENDPOINT = "agentmesh.endpoint";
    public static final String AUTO_REPLY_ENABLED = "twitter.auto.reply";
    public static final String SEARCH_KEYWORDS = "twitter.search.keywords";
    
    private Properties properties;
    private boolean configLoaded = false;
    
    public ConfigManager() {
        properties = new Properties();
        loadConfig();
    }
    
    /**
     * Load configuration from file
     */
    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                configLoaded = true;
                LOGGER.info("Configuration loaded successfully");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load configuration", e);
            }
        } else {
            // Create default configuration
            setDefaultConfig();
            saveConfig();
            LOGGER.info("Created default configuration");
        }
    }
    
    /**
     * Save configuration to file
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Solace Twitter Integration Configuration");
            LOGGER.info("Configuration saved successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save configuration", e);
        }
    }
    
    /**
     * Set default configuration values
     */
    private void setDefaultConfig() {
        properties.setProperty(TWITTER_API_KEY, "");
        properties.setProperty(TWITTER_API_SECRET, "");
        properties.setProperty(TWITTER_ACCESS_TOKEN, "");
        properties.setProperty(TWITTER_ACCESS_SECRET, "");
        properties.setProperty(AGENT_MESH_API_KEY, "");
        properties.setProperty(AGENT_MESH_ENDPOINT, "https://api.solace.cloud/agent-mesh/v1");
        properties.setProperty(AUTO_REPLY_ENABLED, "false");
        properties.setProperty(SEARCH_KEYWORDS, "solace,pubsub+,event mesh,event portal,event broker");
    }
    
    /**
     * Get a configuration value
     * @param key Configuration key
     * @return Configuration value or empty string if not found
     */
    public String getConfig(String key) {
        return properties.getProperty(key, "");
    }
    
    /**
     * Get a configuration value as boolean
     * @param key Configuration key
     * @return Boolean value or false if not found
     */
    public boolean getBooleanConfig(String key) {
        return Boolean.parseBoolean(properties.getProperty(key, "false"));
    }
    
    /**
     * Set a configuration value
     * @param key Configuration key
     * @param value Configuration value
     */
    public void setConfig(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Check if configuration is loaded and contains required values
     * @return true if configuration is valid
     */
    public boolean isConfigValid() {
        if (!configLoaded) {
            return false;
        }
        
        // Check if required configuration values are present
        return !getConfig(TWITTER_API_KEY).isEmpty() &&
               !getConfig(TWITTER_API_SECRET).isEmpty() &&
               !getConfig(TWITTER_ACCESS_TOKEN).isEmpty() &&
               !getConfig(TWITTER_ACCESS_SECRET).isEmpty() &&
               !getConfig(AGENT_MESH_API_KEY).isEmpty();
    }
}