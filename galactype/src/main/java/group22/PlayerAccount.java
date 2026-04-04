package group22;

import group22.enums.AccountType;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a student/player account, extending {@link Account} with gameplay-specific
 * data including typing statistics, level progress, per-level high scores, and settings.
 */
public class PlayerAccount extends Account {
    private TypingStatistics statistics;
    private int levelProgress;
    private Map<Integer, Integer> highScores;
    private PlayerSettings settings;

    /**
     * Constructs a new PlayerAccount with default statistics, level progress of 1,
     * an empty high score map, and default settings.
     *
     * @param username the unique username for this player
     * @param password the plain-text password (stored as a hash via {@link Account})
     */
    public PlayerAccount(String username, String password) {
        super(username, password, AccountType.PLAYER);
        this.statistics = new TypingStatistics();
        this.levelProgress = 1;
        this.highScores = new HashMap<>();
        this.settings = new PlayerSettings();
    }

    /**
     * Returns this player's cumulative typing statistics.
     *
     * @return the {@link TypingStatistics} for this player
     */
    public TypingStatistics getStatistics() {
        return statistics;
    }

    /**
     * Returns the highest level number this player has unlocked.
     *
     * @return the current level progress (1-based)
     */
    public int getLevelProgress() {
        return levelProgress;
    }

    /**
     * Sets the player's level progress to the given level number.
     *
     * @param level the new level progress value (1-based)
     */
    public void setLevelProgress(int level) {
        this.levelProgress = level;
    }

    /**
     * Returns the player's best score for the specified level.
     *
     * @param level the level number to query
     * @return the high score for that level, or {@code 0} if none has been recorded
     */
    public int getHighScore(int level) {
        return highScores.getOrDefault(level, 0);
    }

    /**
     * Updates the stored high score for a level if the new score is greater than the current best.
     *
     * @param level the level number to update
     * @param score the score to compare against and potentially store
     */
    public void updateHighScore(int level, int score) {
        if (score > getHighScore(level)) {
            highScores.put(level, score);
        }
    }

    /**
     * Returns this player's audio and display settings.
     *
     * @return the {@link PlayerSettings} for this player
     */
    public PlayerSettings getSettings() {
        return settings;
    }

    /**
     * Resets all of this player's cumulative typing statistics.
     */
    public void resetStatistics() {
        statistics.reset();
    }
}
