# Galactype

## Description

Galactype is a space themed typing game built for elementary school students. Players type falling words (meteors) before they reach the bottom of the screen. The game features multiple difficulty levels, power-ups, a high score leaderboard, and a teacher/parent control panel for managing student accounts and reviewing typing statistics.

---

## Required Libraries and Tools

### To run the pre-built JAR (recommended)

| Tool    | Version | Notes                       |
|---------|---------|-----------------------------|
| Java JDK | 17+    | Required to run the JAR     |

Download JDK 17 from [https://adoptium.net](https://adoptium.net) if not already installed.

### To build from source

| Tool/Library   | Version | Notes                              |
|----------------|---------|------------------------------------|
| Java JDK       | 17      | Required to compile and run        |
| Apache Maven   | 3.8+    | Build and dependency manager       |
| JavaFX         | 21      | Fetched automatically by Maven     |
| Google Gson    | 2.10.1  | Fetched automatically by Maven     |
| JUnit Jupiter  | 5.10.2  | Fetched automatically by Maven     |

---

## Running the Pre-Built JAR

A pre-built `galactype.jar` is included in the submission. To run it:

1. Ensure **Java 17+** is installed (`java -version` to check).
2. Open the parent directory of galactype
3. Run:
   ```
   java -jar galactype/target/galactype.jar
   ```

The main menu window will open.

---

## Building from Source

### Prerequisites

1. Install **Java JDK 17** from [https://adoptium.net](https://adoptium.net).
   - Verify: `java -version` should report `17.x.x`
2. Install **Apache Maven 3.8+** from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).
   - Verify: `mvn -version` should report `3.8.x` or higher

### Build Steps

1. Unzip the repository so you have the `group22/` directory.
2. Open a terminal and navigate to the Maven project directory:
   ```
   cd group22/galactype
   ```
3. Build the fat JAR (Maven will download all dependencies automatically):
   ```
   mvn package -DskipTests
   ```
   The output JAR will be at `target/galactype.jar`.

4. (Optional) Run the test suite:
   ```
   mvn test
   ```

5. Run the built JAR:
   ```
   `java -jar galactype/target/galactype.jar`
   ```

---

## User Guide

### Accounts

- **Player accounts** are for students. Players can log in, play the game, and view their scores.
- **Teacher accounts** act as a parental/teacher control panel and can manage player accounts and view statistics.

### Logging In

1. From the main menu, click **Login**.
2. Enter your username and password, then click **Login**.
3. New players can click **Create Account** on the login screen to register.

### Playing the Game

1. After logging in, click **Play**.
2. Select a difficulty level (Easy, Medium, Hard).
3. Words fall from the top of the screen as meteors. Type each word and press **Enter** to destroy it.
4. The game ends if a meteor reaches the bottom. Try to survive as long as possible and maximize your score.
5. Power-ups may appear during gameplay to help you.

### High Scores

- Click **High Scores** from the main menu to view the leaderboard.

### How to Play

- Click **How to Play** from the main menu for in-game instructions.

---

## Teacher / Parental Controls

The Teacher Control Panel allows a teacher or parent to:

- View typing statistics for any player account
- Create and delete player accounts
- Reset high scores

### Accessing the Teacher Control Panel

1. From the main menu, click **Teacher Controls** (or the equivalent parental controls button).
2. Log in with a **Teacher account** when prompted.

### Default Teacher Account

| Username  | Password   |
|-----------|------------|
| `teacher` | `password` |

> If this account has been changed, create a new teacher account by selecting **Account Type: Teacher** on the account creation screen.

---

## Data Storage

Account data and scores are saved as JSON files in the application's working directory (`galactype/`). No external database is required.

---

## Additional Notes

- The application window is fixed-size; do not attempt to resize it during gameplay.
- If the application fails to launch via `mvn javafx:run`, ensure your `JAVA_HOME` environment variable points to JDK 17.
- Tested on Windows 10/11 and macOS 13+.
