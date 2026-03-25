package group22;

import group22.enums.DifficultyLevel;

public class LevelConfig {

    private final int levelNumber;
    private final int maxMeteors;
    private final float meteorSpeed;
    private final long spawnIntervalMs;
    private final DifficultyLevel wordDifficulty;
    private final long durationMs;
    private final float powerUpSpawnChance;
    private final String backgroundAsset;

    public LevelConfig(int levelNumber, int maxMeteors, float meteorSpeed,long spawnIntervalMs, DifficultyLevel difficulty, long durationMs) {
        this.levelNumber = levelNumber;
        this.maxMeteors = maxMeteors;
        this.meteorSpeed = meteorSpeed;
        this.spawnIntervalMs = spawnIntervalMs;
        this.wordDifficulty = difficulty;
        this.durationMs = durationMs;
        this.powerUpSpawnChance = 0.1f;
        this.backgroundAsset = null;
    }

    public int getLevelNumber() {
        return levelNumber; 
    }

    public int getMaxMeteors() {
        return maxMeteors;
    }

    public float getMeteorSpeed() {
        return meteorSpeed;
    }
    
    public long getSpawnIntervalMs() {
        return spawnIntervalMs;
    }

    public DifficultyLevel getWordDifficulty() {
        return wordDifficulty; 
    }

    public long getDurationMs() {
        return durationMs; 
    }

    public float getPowerUpSpawnChance() {
        return powerUpSpawnChance; 
    }
    
    public String getBackgroundAsset() {
        return backgroundAsset;
    }
}
