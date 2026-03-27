package group22;

import group22.enums.AccountType;
import java.util.HashMap;
import java.util.Map;

public class PlayerAccount extends Account {
    private TypingStatistics statistics;
    private int levelProgress;
    private Map<Integer, Integer> highScores;
    private PlayerSettings settings;

    public PlayerAccount(String username, String password) {
        super(username, password, AccountType.PLAYER);
        this.statistics = new TypingStatistics();
        this.levelProgress = 1;
        this.highScores = new HashMap<>();
        this.settings = new PlayerSettings();
    }

    public TypingStatistics getStatistics() {
        return statistics;
    }
    
    public int getLevelProgress() {
        return levelProgress;
    }

    public void setLevelProgress(int level) {
        this.levelProgress = level;
    }

    public int getHighScore(int level) {
        return highScores.getOrDefault(level, 0);
    }

    public void updateHighScore(int level, int score) {
        if (score > getHighScore(level)) {
            highScores.put(level, score);
        }
    }

    public PlayerSettings getSettings() {
        return settings;
    }

    public void resetStatistics() {
        statistics.reset(); 
    }
}
