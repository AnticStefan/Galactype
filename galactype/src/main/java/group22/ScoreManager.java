package group22;

public class ScoreManager {
    private static final int DEFAULT_COMBO_THRESHOLD = 5;

    private int currentScore;
    private int highScore;
    private int comboStreak;
    private float comboMultiplier;
    private int comboThreshold;
    private boolean newHighScore;


    // Initializes score tracking for a new manager instance.
    public ScoreManager(int highScore) {
        this.highScore = Math.max(0, highScore);
        this.currentScore = 0;
        this.comboStreak = 0;
        this.comboMultiplier = 1.0f;
        this.comboThreshold = DEFAULT_COMBO_THRESHOLD;
        this.newHighScore = false;
    }

    // Adds points for a completed word and updates the high score if needed.
    public void addPoints(Word word, float wpm) {
        if (word == null) {
            return;
        }

        currentScore += calculateWordPoints(word, wpm);
        if (currentScore > highScore) {
            highScore = currentScore;
            newHighScore = true;
        }
    }

    // Increases the combo streak after a correct character input.
    public void onCorrectCharacter() {
        comboStreak++;
        updateComboMultiplier();
    }

    // Resets combo progress after an input error.
    public void onError() {
        comboStreak = 0;
        comboMultiplier = 1.0f;
    }

    // Returns the current score for the ongoing session.
    public int getCurrentScore() {
        return currentScore;
    }

    // Returns the best score reached so far.
    public int getHighScore() {
        return highScore;
    }

    // Returns the current streak of correct characters.
    public int getComboStreak() {
        return comboStreak;
    }

    // Returns the active combo multiplier.
    public float getComboMultiplier() {
        return comboMultiplier;
    }

    // Reports whether this run has beaten the stored high score.
    public boolean isNewHighScore() {
        return newHighScore;
    }

    // Clears the current run state while keeping the saved high score.
    public void reset() {
        currentScore = 0;
        comboStreak = 0;
        comboMultiplier = 1.0f;
        newHighScore = false;
    }

    // Calculates the points awarded for a completed word.
    private int calculateWordPoints(Word word, float wpm) {
        return 1;
    }

    // Recomputes the multiplier based on the current combo streak.
    private void updateComboMultiplier() {
        comboMultiplier = 1.0f + (comboStreak / comboThreshold) * 0.5f;
    }
}
