package com.solace.twitter.service;

import com.solace.twitter.model.ProcessedTweet;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for interacting with Twitter API
 * Monitors Twitter for Solace-related questions and processes them
 */
public class TwitterService {
    private static final Logger LOGGER = Logger.getLogger(TwitterService.class.getName());
    
    private final ConfigManager configManager;
    private final AgentMeshService agentMeshService;
    private Twitter twitter;
    private TwitterStream twitterStream;
    private boolean monitoring = false;
    
    // List of processed tweets
    private final List<ProcessedTweet> processedTweets = new CopyOnWriteArrayList<>();
    
    // Listeners for tweet updates
    private final List<TweetUpdateListener> updateListeners = new ArrayList<>();
    
    public TwitterService(ConfigManager configManager, AgentMeshService agentMeshService) {
        this.configManager = configManager;
        this.agentMeshService = agentMeshService;
        initializeTwitter();
    }
    
    /**
     * Initialize Twitter API client
     */
    private void initializeTwitter() {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
              .setOAuthConsumerKey(configManager.getConfig(ConfigManager.TWITTER_API_KEY))
              .setOAuthConsumerSecret(configManager.getConfig(ConfigManager.TWITTER_API_SECRET))
              .setOAuthAccessToken(configManager.getConfig(ConfigManager.TWITTER_ACCESS_TOKEN))
              .setOAuthAccessTokenSecret(configManager.getConfig(ConfigManager.TWITTER_ACCESS_SECRET));

            // Build the configuration once and reuse it
            Configuration config = cb.build();

            // Use the same configuration for both Twitter and TwitterStream
            TwitterFactory tf = new TwitterFactory(config);
            twitter = tf.getInstance();

            twitterStream = new TwitterStreamFactory(config).getInstance();
            
            LOGGER.info("Twitter API client initialized");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Twitter API client", e);
            throw new RuntimeException("Failed to initialize Twitter API client", e);
        }
    }
    
    /**
     * Start monitoring Twitter for Solace-related questions
     */
    public void startMonitoring() {
        if (monitoring) {
            LOGGER.info("Twitter monitoring is already active");
            return;
        }
        
        if (!configManager.isConfigValid()) {
            LOGGER.severe("Cannot start monitoring: Configuration is invalid");
            throw new IllegalStateException("Configuration is invalid");
        }
        
        try {
            // Get search keywords
            String keywordsStr = configManager.getConfig(ConfigManager.SEARCH_KEYWORDS);
            String[] keywords = keywordsStr.split(",");
            
            // Set up filtered stream
            FilterQuery filterQuery = new FilterQuery();
            filterQuery.track(keywords);
            
            // Set up status listener
            StatusListener listener = new StatusAdapter() {
                @Override
                public void onStatus(Status status) {
                    processStatus(status);
                }
                
                @Override
                public void onException(Exception ex) {
                    LOGGER.log(Level.SEVERE, "Twitter stream exception", ex);
                }
            };
            
            twitterStream.addListener(listener);
            twitterStream.filter(filterQuery);
            
            monitoring = true;
            LOGGER.info("Started monitoring Twitter for keywords: " + Arrays.toString(keywords));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start Twitter monitoring", e);
            throw new RuntimeException("Failed to start Twitter monitoring", e);
        }
    }
    
    /**
     * Stop monitoring Twitter
     */
    public void stopMonitoring() {
        if (!monitoring) {
            return;
        }
        
        twitterStream.shutdown();
        monitoring = false;
        LOGGER.info("Stopped monitoring Twitter");
    }
    
    /**
     * Process a Twitter status update
     * @param status Twitter status
     */
    private void processStatus(Status status) {
        try {
            // Skip retweets
            if (status.isRetweet()) {
                return;
            }
            
            String tweetText = status.getText();
            
            // Check if the tweet contains a question
            if (containsQuestion(tweetText)) {
                LOGGER.info("Found question in tweet: " + tweetText);
                
                // Extract the question
                String question = extractQuestion(tweetText);
                
                // Process the question through Agent Mesh
                String answer = agentMeshService.processQuestion(question);
                
                // Create processed tweet
                ProcessedTweet processedTweet = new ProcessedTweet(
                    status.getId(),
                    status.getUser().getScreenName(),
                    status.getUser().getName(),
                    status.getCreatedAt(),
                    tweetText,
                    question,
                    answer,
                    false
                );
                
                // Add to list of processed tweets
                processedTweets.add(processedTweet);
                
                // Notify listeners
                notifyUpdateListeners(processedTweet);
                
                // Auto-reply if enabled
                if (configManager.getBooleanConfig(ConfigManager.AUTO_REPLY_ENABLED)) {
                    replyToTweet(processedTweet);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to process tweet", e);
        }
    }
    
    /**
     * Check if a tweet contains a question
     * @param text Tweet text
     * @return true if the tweet contains a question
     */
    private boolean containsQuestion(String text) {
        return text.contains("?") || 
               text.toLowerCase().contains("how") ||
               text.toLowerCase().contains("what") ||
               text.toLowerCase().contains("when") ||
               text.toLowerCase().contains("where") ||
               text.toLowerCase().contains("why") ||
               text.toLowerCase().contains("who") ||
               text.toLowerCase().contains("which") ||
               text.toLowerCase().contains("can") ||
               text.toLowerCase().contains("could");
    }
    
    /**
     * Extract a question from tweet text
     * @param text Tweet text
     * @return Extracted question
     */
    private String extractQuestion(String text) {
        // Simple extraction - in a real app, this would be more sophisticated
        return text;
    }
    
    /**
     * Reply to a tweet with the AI-generated answer
     * @param tweet Processed tweet
     */
    public void replyToTweet(ProcessedTweet tweet) {
        try {
            // Format the reply
            String reply = "@" + tweet.getUsername() + " " + formatReply(tweet.getAnswer());
            
            // Send the reply
            StatusUpdate statusUpdate = new StatusUpdate(reply);
            statusUpdate.inReplyToStatusId(tweet.getTweetId());
            Status replyStatus = twitter.updateStatus(statusUpdate);
            
            // Update the processed tweet
            tweet.setReplied(true);
            
            LOGGER.info("Replied to tweet: " + tweet.getTweetId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to reply to tweet", e);
        }
    }
    
    /**
     * Format a reply to fit Twitter's character limit
     * @param answer AI-generated answer
     * @return Formatted reply
     */
    private String formatReply(String answer) {
        // Twitter's character limit is 280
        final int MAX_LENGTH = 280;
        
        if (answer.length() <= MAX_LENGTH) {
            return answer;
        }
        
        // Truncate and add ellipsis
        return answer.substring(0, MAX_LENGTH - 4) + "...";
    }
    
    /**
     * Get the list of processed tweets
     * @return List of processed tweets
     */
    public List<ProcessedTweet> getProcessedTweets() {
        return new ArrayList<>(processedTweets);
    }
    
    /**
     * Add a listener for tweet updates
     * @param listener Tweet update listener
     */
    public void addUpdateListener(TweetUpdateListener listener) {
        updateListeners.add(listener);
    }
    
    /**
     * Remove a listener for tweet updates
     * @param listener Tweet update listener
     */
    public void removeUpdateListener(TweetUpdateListener listener) {
        updateListeners.remove(listener);
    }
    
    /**
     * Notify all listeners of a new processed tweet
     * @param tweet Processed tweet
     */
    private void notifyUpdateListeners(ProcessedTweet tweet) {
        for (TweetUpdateListener listener : updateListeners) {
            listener.onTweetProcessed(tweet);
        }
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    /**
     * Interface for tweet update listeners
     */
    public interface TweetUpdateListener {
        void onTweetProcessed(ProcessedTweet tweet);
    }
}