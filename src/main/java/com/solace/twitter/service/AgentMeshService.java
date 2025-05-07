package com.solace.twitter.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for interacting with Solace Agent Mesh API
 */
public class AgentMeshService {
    private static final Logger LOGGER = Logger.getLogger(AgentMeshService.class.getName());
    
    private final ConfigManager configManager;
    private final CloseableHttpClient httpClient;
    
    public AgentMeshService(ConfigManager configManager) {
        this.configManager = configManager;
        this.httpClient = HttpClients.createDefault();
    }
    
    /**
     * Process a question through Solace Agent Mesh
     * @param question Question to process
     * @return AI-generated answer
     */
    public String processQuestion(String question) {
        if (!configManager.isConfigValid()) {
            LOGGER.severe("Cannot process question: Configuration is invalid");
            throw new IllegalStateException("Configuration is invalid");
        }
        
        try {
            // Create request to Agent Mesh API
            String endpoint = configManager.getConfig(ConfigManager.AGENT_MESH_ENDPOINT);
            HttpPost request = new HttpPost(endpoint);
            
            // Set headers
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + configManager.getConfig(ConfigManager.AGENT_MESH_API_KEY));
            
            // Create request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("message", question);
            requestBody.put("model", "solace-chat"); // Use Solace Chat model
            
            // Set request entity
            request.setEntity(new StringEntity(requestBody.toString()));
            
            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                
                if (entity != null) {
                    // Parse response
                    String responseString = EntityUtils.toString(entity);
                    JSONObject responseJson = new JSONObject(responseString);
                    
                    // Extract answer from response
                    if (responseJson.has("response")) {
                        return responseJson.getString("response");
                    } else {
                        LOGGER.warning("Unexpected response format: " + responseString);
                        return "Sorry, I couldn't process your question at this time.";
                    }
                } else {
                    LOGGER.warning("Empty response from Agent Mesh API");
                    return "Sorry, I couldn't process your question at this time.";
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to process question through Agent Mesh API", e);
            return "Sorry, I encountered an error while processing your question.";
        }
    }
    
    /**
     * Close the HTTP client
     */
    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close HTTP client", e);
        }
    }
}