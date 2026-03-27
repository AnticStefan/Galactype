package group22;

public class HighScoreEntry {

    private final String username;
    private final int score;
    private final int level;
    private final long timestamp;

    public HighScoreEntry(String username, int score, int level) {
        this.username  = username;
        this.score = score;
        this.level = level;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUsername() { 
        return username; 
    }
    public int getScore() { 
        return score; 
    }

    public int getLevel() { 
        return level; 
    }

    public long getTimestamp() { 
        return timestamp; 
    }
}
