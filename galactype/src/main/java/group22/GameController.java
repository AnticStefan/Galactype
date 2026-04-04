package group22;

import group22.enums.GameState;
import java.io.InputStream;

/**
 * Central controller that wires together all game subsystems (accounts, levels,
 * word bank, high scores, and the active session). Acts as the primary entry point
 * for the UI layer to drive game logic.
 */
public class GameController {

    private AccountManager accountManager;
    private LevelManager levelManager;
    private WordBank wordBank;
    private HighScoreTable highScoreTable;
    private DataManager dataManager;
    private GameSession currentSession;

    /**
     * Constructs a GameController with no subsystems initialized.
     * Call {@link #initialize(String, String)} before any other method.
     */
    public GameController() {}

    /**
     * Initializes all subsystems by loading persisted data from disk.
     * Must be called once at application startup before any game logic runs.
     *
     * @param dataPath     the directory containing account and high-score data files
     * @param wordBankPath the path to the word bank data file
     */
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

    /**
     * Initializes all subsystems, loading the word bank from an InputStream.
     * Use this overload when running from a JAR where word.json is a bundled resource.
     *
     * @param dataPath       the directory containing account and high-score data files
     * @param wordBankStream an InputStream for the word bank JSON resource
     */
    public void initialize(String dataPath, InputStream wordBankStream) {
        dataManager = new DataManager(dataPath);
        accountManager = new AccountManager(dataPath + "/accounts.json");
        accountManager.load();
        levelManager = new LevelManager();
        wordBank = new WordBank("");
        wordBank.load(wordBankStream);
        highScoreTable = dataManager.loadHighScoreTable();
    }

    /**
     * Creates and starts a new game session for the specified level.
     * Does nothing if the level number does not correspond to a known level.
     *
     * @param levelNumber the 1-based index of the level to start
     */
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

    /**
     * Advances the current game session by one frame.
     * Automatically triggers end-of-session logic when the session concludes.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
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

    /**
     * Forwards a typed character to the active game session.
     *
     * @param character the character the player typed
     */
    public void onKeyInput(char character) {
        if (currentSession != null) currentSession.onKeyTyped(character);
    }

    /**
     * Pauses the active game session if one is running.
     */
    public void pauseGame() {
        if (currentSession != null) currentSession.pause();
    }

    /**
     * Resumes the active game session if one is paused.
     */
    public void resumeGame() {
        if (currentSession != null) currentSession.resume();
    }

    /**
     * Forcibly ends the active session (e.g. the player quits mid-game)
     * and persists the resulting data.
     */
    // Manually end the session (like player quits mid-game)
    public void endGame() {
        if (currentSession == null) return;
        currentSession.end();
        onSessionEnd();
        currentSession = null;
    }

    /**
     * Persists all account and high-score data to disk before the application closes.
     */
    // Persist everything and hand control back to the UI to close
    public void saveAndExit() {
        accountManager.save();
        dataManager.saveHighScores(highScoreTable);
    }

    /**
     * Handles all post-session logic: recording statistics, updating high scores,
     * unlocking the next level, and persisting data to disk.
     */
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

    /**
     * Writes the current high score table to disk.
     */
    // Persists the current high score table to disk
    public void saveHighScores() {
        dataManager.saveHighScores(highScoreTable);
    }

    /**
     * Returns the account manager subsystem.
     *
     * @return the {@link AccountManager} used by this controller
     */
    // Getters for the UI layer
    public AccountManager getAccountManager() {
        return accountManager;
    }

    /**
     * Returns the level manager subsystem.
     *
     * @return the {@link LevelManager} used by this controller
     */
    public LevelManager getLevelManager() {
        return levelManager;
    }

    /**
     * Returns the global high score table.
     *
     * @return the {@link HighScoreTable} maintained by this controller
     */
    public HighScoreTable getHighScoreTable() {
        return highScoreTable;
    }

    /**
     * Returns the currently active game session.
     *
     * @return the active {@link GameSession}, or {@code null} if no session is running
     */
    public GameSession getCurrentSession() {
        return currentSession;
    }
}
