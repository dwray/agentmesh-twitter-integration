package com.solace.twitter.model;

import java.util.Date;

/**
 * Model class for a processed tweet with question and AI answer
 */
public class ProcessedTweet {
    private final long tweetId;
    private final String username;
    private final String userDisplayName;
    private final Date createdAt;
    private final String tweetText;
    private final String extractedQuestion;
    private final String answer;
    private boolean replied;
    
    /**
     * Constructor for ProcessedTweet
     * @param tweetId Twitter ID of the tweet
     * @param username Twitter username of the author
     * @param userDisplayName Display name of the author
     * @param createdAt Date the tweet was created
     * @param tweetText Full text of the tweet
     * @param extractedQuestion Question extracted from the tweet
     * @param answer AI-generated answer
     * @param replied Whether a reply has been sent
     */
    public ProcessedTweet(long tweetId, String username, String userDisplayName, 
                          Date createdAt, String tweetText, String extractedQuestion, 
                          String answer, boolean replied) {
        this.tweetId = tweetId;
        this.username = username;
        this.userDisplayName = userDisplayName;
        this.createdAt = createdAt;
        this.tweetText = tweetText;
        this.extractedQuestion = extractedQuestion;
        this.answer = answer;
        this.replied = replied;
    }
    
    /**
     * Get the Twitter ID of the tweet
     * @return Tweet ID
     */
    public long getTweetId() {
        return tweetId;
    }
    
    /**
     * Get the Twitter username of the author
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the display name of the author
     * @return Display name
     */
    public String getUserDisplayName() {
        return userDisplayName;
    }
    
    /**
     * Get the date the tweet was created
     * @return Creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Get the full text of the tweet
     * @return Tweet text
     */
    public String getTweetText() {
        return tweetText;
    }
    
    /**
     * Get the question extracted from the tweet
     * @return Extracted question
     */
    public String getExtractedQuestion() {
        return extractedQuestion;
    }
    
    /**
     * Get the AI-generated answer
     * @return Answer
     */
    public String getAnswer() {
        return answer;
    }
    
    /**
     * Check if a reply has been sent
     * @return true if a reply has been sent
     */
    public boolean isReplied() {
        return replied;
    }
    
    /**
     * Set whether a reply has been sent
     * @param replied true if a reply has been sent
     */
    public void setReplied(boolean replied) {
        this.replied = replied;
    }
    
    /**
     * Get a Twitter URL for the tweet
     * @return Twitter URL
     */
    public String getTwitterUrl() {
        return "https://twitter.com/" + username + "/status/" + tweetId;
    }
    
    @Override
    public String toString() {
        return "ProcessedTweet{" +
                "tweetId=" + tweetId +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                ", extractedQuestion='" + extractedQuestion + '\'' +
                ", replied=" + replied +
                '}';
    }
}