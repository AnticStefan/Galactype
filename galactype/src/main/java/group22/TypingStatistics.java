package group22;

import java.util.ArrayList;
import java.util.List;

public class TypingStatistics {
    private int totalWordsTyped;
    private int totalErrors;
    private long totalTimePlayed;
    private float peakWPM;
    private float averageWPM;
    private List<SessionResult> sessionHistory;

    public TypingStatistics() {
        sessionHistory = new ArrayList<>();
    }

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

    public void updateAverageWPM(float sessionWPM) {
        int count = sessionHistory.size();
        if (count == 0) return;
        averageWPM = ((averageWPM * (count - 1)) + sessionWPM) / count;
    }

    public float getAverageWPM() {
        return averageWPM; 
    }

    public float getPeakWPM() {
        return peakWPM; 
    }

    public float getAccuracyPercent() {
        int total = totalWordsTyped + totalErrors;
        if (total == 0) return 100f;
        return (totalWordsTyped / (float) total) * 100f;
    }

    public int getTotalErrors() {
        return totalErrors; 
    }
    public int getTotalWordsTyped() {
        return totalWordsTyped; 
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed; 
    }

    public List<SessionResult> getSessionHistory() {
        return sessionHistory; 
    }

    public void reset() {
        totalWordsTyped = 0;
        totalErrors = 0;
        totalTimePlayed = 0;
        peakWPM = 0f;
        averageWPM = 0f;
        sessionHistory.clear();
    }
}
