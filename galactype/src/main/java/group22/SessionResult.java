package group22;

/**
 * Immutable snapshot of the outcome of a single game session, capturing score,
 * words typed, errors, WPM, accuracy, duration, and the timestamp it was recorded.
 */
public class SessionResult {
    private int levelPlayed;
    private int score;
    private int wordsTyped;
    private int errors;
    private float wpm;
    private float accuracy;
    private long durationMs;
    private long timestamp;

    /**
     * Constructs a SessionResult capturing all stats from a completed session.
     * The timestamp is set to the current system time at construction.
     *
     * @param level      the level number that was played
     * @param score      the total score achieved
     * @param wordsTyped the number of words successfully completed
     * @param errors     the total number of incorrect key presses
     * @param wpm        the player's words per minute at session end
     * @param accuracy   the percentage of words typed correctly (0–100)
     * @param durationMs the total elapsed session time in milliseconds
     */
    public SessionResult(int level, int score, int wordsTyped, int errors, float wpm, float accuracy, long durationMs) {
        this.levelPlayed = level;
        this.score = score;
        this.wordsTyped = wordsTyped;
        this.errors = errors;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.durationMs = durationMs;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Returns the level number that was played in this session.
     *
     * @return the level number (1 based)
     */
    public int getLevelPlayed() {
        return levelPlayed;
    }

    /**
     * Returns the total score achieved in this session.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the number of words the player successfully completed.
     *
     * @return the words typed count
     */
    public int getWordsTyped() {
        return wordsTyped;
    }

    /**
     * Returns the total number of incorrect key presses made during the session.
     *
     * @return the error count
     */
    public int getErrors() {
        return errors;
    }

    /**
     * Returns the player's words per minute typing speed at the end of the session.
     *
     * @return the WPM value
     */
    public float getWpm() {
        return wpm;
    }

    /**
     * Returns the player's typing accuracy as a percentage.
     *
     * @return accuracy between 0.0 and 100.0
     */
    public float getAccuracy() {
        return accuracy;
    }

    /**
     * Returns the total elapsed time of this session.
     *
     * @return duration in milliseconds
     */
    public long getDurationMs() {
        return durationMs;
    }

    /**
     * Returns the epoch millisecond timestamp when this result was created.
     *
     * @return the creation timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

}
