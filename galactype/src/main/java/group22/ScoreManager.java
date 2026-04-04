package group22;

/**
 * Tracks the player's score, combo streak, word streak, and combo multiplier for a single
 * game session. Awards points for completed words, penalizes errors, and detects new high scores.
 */
public class ScoreManager {
    private static final int DEFAULT_COMBO_THRESHOLD = 5;

    private int currentScore;
    private int highScore;
    private int comboStreak;
    private int wordStreak;
    private float comboMultiplier;
    private int comboThreshold;
    private boolean newHighScore;

    /**
     * Constructs a ScoreManager for a new session, using the given pre existing high score
     * as the baseline to beat.
     *
     * @param highScore the player's best score for this level prior to the current session
     */
    public ScoreManager(int highScore) {
        this.highScore = Math.max(0, highScore);
        this.currentScore = 0;
        this.comboStreak = 0;
        this.wordStreak = 0;
        this.comboMultiplier = 1.0f;
        this.comboThreshold = DEFAULT_COMBO_THRESHOLD;
        this.newHighScore = false;
    }

    /**
     * Awards points for a successfully completed word, factoring in length, current WPM,
     * and the active combo multiplier. Updates the high score if the new total exceeds it.
     *
     * @param word the {@link Word} that was just completed
     * @param wpm  the player's current words per minute typing speed
     */
    public void addPoints(Word word, float wpm) {
        if (word == null) {
            return;
        }

        int points = calculateWordPoints(word, wpm);
        currentScore += points;
        wordStreak++;
        updateComboMultiplier();

        if (currentScore > highScore) {
            highScore = currentScore;
            newHighScore = true;
        }
    }

    /**
     * Increments the combo streak by one correct character and recalculates the multiplier.
     */
    // Increases the combo streak after a correct character input.
    public void onCorrectCharacter() {
        comboStreak++;
        updateComboMultiplier();
    }

    /**
     * Resets the combo streak, word streak, and multiplier back to their base values
     * after an input error.
     */
    public void onError() {
        comboStreak = 0;
        wordStreak = 0;
        comboMultiplier = 1.0f;
    }

    /**
     * Returns the player's current score for this session.
     *
     * @return the current score
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Returns the best score reached either before or during this session.
     *
     * @return the high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Returns the current streak of consecutive correct characters.
     *
     * @return the combo streak count
     */
    public int getComboStreak() {
        return comboStreak;
    }

    /**
     * Returns the current streak of consecutively completed words without errors.
     *
     * @return the word streak count
     */
    public int getWordStreak() {
        return wordStreak;
    }

    /**
     * Returns the active score multiplier derived from the current combo and word streaks.
     *
     * @return the combo multiplier (minimum 1.0, capped at 3.5)
     */
    public float getComboMultiplier() {
        return comboMultiplier;
    }

    /**
     * Returns whether the player has beaten the prior high score during this session.
     *
     * @return {@code true} if a new high score was set; {@code false} otherwise
     */
    // Reports whether this run has beaten the stored high score.
    public boolean isNewHighScore() {
        return newHighScore;
    }

    /**
     * Resets all session state (score, streaks, multiplier) while retaining the high score.
     */
    public void reset() {
        currentScore = 0;
        comboStreak = 0;
        wordStreak = 0;
        comboMultiplier = 1.0f;
        newHighScore = false;
    }

    /**
     * Calculates the raw point value for a completed word based on its length,
     * the player's current WPM, and the active combo multiplier.
     *
     * @param word the completed {@link Word}
     * @param wpm  the player's current words per minute speed
     * @return the calculated point value (minimum 1)
     */
    // Calculates the points awarded for a completed word.
    private int calculateWordPoints(Word word, float wpm) {
        int basePoints = word.getText().length() * 7; // slower score growth
        // Add modest bonus for typing speed
        float speedBonus = 1.0f + Math.min(wpm, 120f) / 120f * 0.35f;
        int points = Math.round(basePoints * comboMultiplier * speedBonus);
        return Math.max(1, points);
    }

    /**
     * Recomputes the combo multiplier from the current character and word streaks,
     * capping the result at 3.5 to prevent runaway scoring.
     */
    private void updateComboMultiplier() {
        float charComponent = (comboStreak / (float) comboThreshold) * 0.22f;
        float wordComponent = (wordStreak / 3f) * 0.2f;
        comboMultiplier = 1.0f + charComponent + wordComponent;
        // cap combo multiplier to prevent runaway scoring
        comboMultiplier = Math.min(comboMultiplier, 3.5f);
    }
}
