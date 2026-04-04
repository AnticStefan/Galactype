package group22;

/**
 * Represents a single entry in the high score table, capturing the player's username,
 * score, level achieved, and the timestamp at which the entry was recorded.
 */
public class HighScoreEntry {

    private final String username;
    private final int score;
    private final int level;
    private final long timestamp;

    /**
     * Constructs a new HighScoreEntry, capturing the current system time as the timestamp.
     *
     * @param username the username of the player who achieved this score
     * @param score    the score achieved
     * @param level    the level number on which the score was achieved
     */
    public HighScoreEntry(String username, int score, int level) {
        this.username  = username;
        this.score = score;
        this.level = level;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Returns the username associated with this entry.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the score recorded in this entry.
     *
     * @return the score value
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the level number on which this score was achieved.
     *
     * @return the level number
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the epoch-millisecond timestamp when this entry was created.
     *
     * @return the creation timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
}
