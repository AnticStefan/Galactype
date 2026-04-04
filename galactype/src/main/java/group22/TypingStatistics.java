package group22;

import java.util.ArrayList;
import java.util.List;

/**
 * Accumulates and tracks a player's typing performance across multiple game sessions,
 * including total words typed, errors, time played, peak WPM, and a full session history.
 */
public class TypingStatistics {
    private int totalWordsTyped;
    private int totalErrors;
    private long totalTimePlayed;
    private float peakWPM;
    private float averageWPM;
    private List<SessionResult> sessionHistory;

    /**
     * Constructs a new TypingStatistics instance with all counters at zero and an empty history.
     */
    public TypingStatistics() {
        sessionHistory = new ArrayList<>();
    }

    /**
     * Records a completed session, updating all aggregate counters and the session history.
     *
     * @param result the {@link SessionResult} from the finished session
     */
    public void recordSession(SessionResult result) {
        sessionHistory.add(result);
        totalWordsTyped += result.getWordsTyped();
        totalErrors += result.getErrors();
        totalTimePlayed += result.getDurationMs();
        if (result.getWpm() > peakWPM) {
            peakWPM = result.getWpm();
        }
        updateAverageWPM(result.getWpm());
    }

    /**
     * Recalculates the running average WPM incorporating the latest session's speed.
     *
     * @param sessionWPM the WPM achieved in the most recent session
     */
    public void updateAverageWPM(float sessionWPM) {
        int count = sessionHistory.size();
        if (count == 0) return;
        averageWPM = ((averageWPM * (count - 1)) + sessionWPM) / count;
    }

    /**
     * Returns the player's average words per minute across all recorded sessions.
     *
     * @return the average WPM
     */
    public float getAverageWPM() {
        return averageWPM;
    }

    /**
     * Returns the highest words per minute the player has achieved in any single session.
     *
     * @return the peak WPM
     */
    public float getPeakWPM() {
        return peakWPM;
    }

    /**
     * Calculates and returns the player's overall accuracy as a percentage across all sessions.
     *
     * @return accuracy between 0.0 and 100.0; returns 100.0 if no attempts have been made
     */
    public float getAccuracyPercent() {
        int total = totalWordsTyped + totalErrors;
        if (total == 0) return 100f;
        return (totalWordsTyped / (float) total) * 100f;
    }

    /**
     * Returns the total number of errors made across all sessions.
     *
     * @return the total error count
     */
    public int getTotalErrors() {
        return totalErrors;
    }

    /**
     * Returns the total number of words successfully typed across all sessions.
     *
     * @return the total words typed count
     */
    public int getTotalWordsTyped() {
        return totalWordsTyped;
    }

    /**
     * Returns the cumulative time the player has spent in game sessions.
     *
     * @return total time played in milliseconds
     */
    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    /**
     * Returns the full history of recorded sessions in the order they were played.
     *
     * @return a {@link List} of {@link SessionResult} objects
     */
    public List<SessionResult> getSessionHistory() {
        return sessionHistory;
    }

    /**
     * Resets all statistics and clears the session history.
     */
    public void reset() {
        totalWordsTyped = 0;
        totalErrors = 0;
        totalTimePlayed = 0;
        peakWPM = 0f;
        averageWPM = 0f;
        sessionHistory.clear();
    }
}
