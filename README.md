# Solace Twitter Integration

This application monitors Twitter/X for Solace-related questions, processes them through Solace Agent Mesh, and can optionally reply with AI-generated answers.

## Features

- Monitor Twitter for Solace-related keywords and questions
- Extract questions from tweets
- Process questions through Solace Agent Mesh API
- Display tweets, questions, and AI-generated answers in a GUI
- Optionally reply to tweets with AI-generated answers
- Configure Twitter API credentials, Agent Mesh API credentials, and other settings

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Twitter Developer Account with API credentials
- Solace Agent Mesh API credentials

## Project Structure

```
solace-twitter-integration/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── solace/
                    └── twitter/
                        ├── SolaceTwitterApp.java
                        ├── gui/
                        │   ├── MainFrame.java
                        │   └── SettingsDialog.java
                        ├── model/
                        │   └── ProcessedTweet.java
                        └── service/
                            ├── AgentMeshService.java
                            ├── ConfigManager.java
                            └── TwitterService.java
```

## Setup Instructions

1. Clone or download this repository
2. Create the directory structure as shown above and place the files in their respective directories
3. Build the project using Maven:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   java -jar target/solace-twitter-integration-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Configuration

On first run, the application will create a default `config.properties` file. You'll need to configure:

1. Twitter API credentials:
   - API Key
   - API Secret
   - Access Token
   - Access Secret

2. Solace Agent Mesh API credentials:
   - API Key
   - Endpoint URL

3. General settings:
   - Search keywords (comma-separated)
   - Auto-reply option

You can configure these settings through the Settings dialog in the application.

## Usage

1. Start the application
2. Configure the settings
3. Click "Start Monitoring" to begin monitoring Twitter
4. View tweets, questions, and AI-generated answers in the main window
5. Optionally reply to tweets with AI-generated answers

## Notes

- Double-click on a tweet in the table to open it in your default web browser
- The application will save all settings in a `config.properties` file in the application directory

## Dependencies

- Twitter4J for Twitter API integration
- Solace PubSub+ for messaging (if needed)
- Apache HttpClient for API requests
- JSON libraries for parsing API responses
- FlatLaf for modern Swing UI look and feel