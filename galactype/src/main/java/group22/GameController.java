package group22;

import group22.enums.GameState;

public class GameController {

    private AccountManager accountManager;
    private LevelManager levelManager;
    private WordBank wordBank;
    private HighScoreTable highScoreTable;
    private DataManager dataManager;
    private GameSession currentSession;

    public GameController() {}

    // Call once at startup, loads all persistent data
    public void initialize(String dataPath, String wordBankPath) {
        dataManager = new DataManager(dataPath);
        accountManager = new AccountManager(dataPath + "/accounts.json");
        accountManager.load();
        levelManager = new LevelManager();
        wordBank = new WordBank(wordBankPath);
        wordBank.load();
        highScoreTable = dataManager.loadHighScoreTable();
    }

    // Start a new session for the given level number
    public void startGame(int levelNumber) {
        LevelConfig config = levelManager.getLevelByNumber(levelNumber);
        if (config == null) return;

        int highScore = 0;
        Account account = accountManager.getCurrentAccount();
        if (account instanceof PlayerAccount) {
            highScore = ((PlayerAccount) account).getHighScore(levelNumber);
        }

        currentSession = new GameSession(config, wordBank, highScore, 3);
        currentSession.start();
    }

    // Called every frame by the game loop (deltaTime in seconds)
    public void update(float deltaTime) {
        if (currentSession == null) return;
        currentSession.update(deltaTime);

        GameState state = currentSession.getState();
        if (state == GameState.GAME_OVER || state == GameState.LEVEL_COMPLETE) {
            onSessionEnd();
            currentSession = null;
        }
    }

    public void onKeyInput(char character) {
        if (currentSession != null) currentSession.onKeyTyped(character);
    }

    public void pauseGame() {
        if (currentSession != null) currentSession.pause();
    }

    public void resumeGame() {
        if (currentSession != null) currentSession.resume();
    }

    // Manually end the session (like player quits mid-game)
    public void endGame() {
        if (currentSession == null) return;
        currentSession.end();
        onSessionEnd();
        currentSession = null;
    }

    // Persist everything and hand control back to the UI to close
    public void saveAndExit() {
        accountManager.save();
        dataManager.saveHighScores(highScoreTable);
    }

    // Called whenever a session ends (normally or by quit)
    private void onSessionEnd() {
        if (currentSession == null) return;

        SessionResult result = currentSession.buildSessionResult();
        Account account = accountManager.getCurrentAccount();

        if (account instanceof PlayerAccount) {
            PlayerAccount player = (PlayerAccount) account;

            player.getStatistics().recordSession(result);
            player.updateHighScore(result.getLevelPlayed(), result.getScore());

            // Unlock next level if this one was completed cleanly
            if (currentSession.getState() == GameState.LEVEL_COMPLETE) {
                int next = result.getLevelPlayed() + 1;
                if (next > player.getLevelProgress()) {
                    player.setLevelProgress(next);
                }
            }

            dataManager.savePlayerProgress(player);
        }

        // Update the global leaderboard
        if (account != null) {
            highScoreTable.addEntry(account.getUsername(), result.getScore(), result.getLevelPlayed());
            dataManager.saveHighScores(highScoreTable);
        }

        accountManager.save();
    }

    // Persists the current high score table to disk
    public void saveHighScores() {
        dataManager.saveHighScores(highScoreTable);
    }

    // Getters for the UI layer
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    public LevelManager getLevelManager() {
        return levelManager; 
    }

    public HighScoreTable getHighScoreTable() {
        return highScoreTable; 
    }

    public GameSession getCurrentSession() {
        return currentSession; 
    }
}
