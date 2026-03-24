package group22;

public class SessionResult {
    private int levelPlayed;
    private int score;
    private int wordsTyped;
    private int errors;
    private float wpm;
    private float accuracy;
    private long durationMs;
    private long timestamp;

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

    public int getLevelPlayed() {
        return levelPlayed;
    }
    
    public int getScore() {
        return score;
    }

    public int getWordsTyped() {
        return wordsTyped;
    }

    public int getErrors() {
        return errors;
    }

    public float getWpm() {
        return wpm;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public long getDurationMs() { 
        return durationMs;
    }

    public long getTimestamp() { 
        return timestamp;
    }

}
