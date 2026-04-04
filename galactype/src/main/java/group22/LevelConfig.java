package group22;

import group22.enums.DifficultyLevel;

/**
 * Immutable configuration object that defines all parameters for a single game level,
 * including meteor speed, spawn rate, word difficulty, duration, and optional visual assets.
 */
public class LevelConfig {

    private final int levelNumber;
    private final int maxMeteors;
    private final float meteorSpeed;
    private final long spawnIntervalMs;
    private final DifficultyLevel wordDifficulty;
    private final long durationMs;
    private final float powerUpSpawnChance;
    private final String backgroundAsset;
    private final String planetName;

    /**
     * Constructs a LevelConfig with default  power up spawn chance and no visual assets.
     *
     * @param levelNumber      the 1-based level identifier
     * @param maxMeteors       the maximum number of meteors allowed on screen simultaneously
     * @param meteorSpeed      the fall speed of meteors in pixels per second
     * @param spawnIntervalMs  the time between meteor spawns in milliseconds
     * @param difficulty       the word difficulty tier for this level
     * @param durationMs       the total duration of the level in milliseconds
     */
    public LevelConfig(int levelNumber, int maxMeteors, float meteorSpeed, long spawnIntervalMs, DifficultyLevel difficulty, long durationMs) {
        this(levelNumber, maxMeteors, meteorSpeed, spawnIntervalMs, difficulty, durationMs, 0.05f, null, null);
    }

    /**
     * Constructs a LevelConfig with a specified power up spawn chance but no visual assets.
     *
     * @param levelNumber       the 1-based level identifier
     * @param maxMeteors        the maximum number of meteors allowed on screen simultaneously
     * @param meteorSpeed       the fall speed of meteors in pixels per second
     * @param spawnIntervalMs   the time between meteor spawns in milliseconds
     * @param difficulty        the word difficulty tier for this level
     * @param durationMs        the total duration of the level in milliseconds
     * @param powerUpSpawnChance the probability (0.0–1.0) that a spawned meteor carries a power up word
     */
    public LevelConfig(int levelNumber, int maxMeteors, float meteorSpeed, long spawnIntervalMs, DifficultyLevel difficulty, long durationMs, float powerUpSpawnChance) {
        this(levelNumber, maxMeteors, meteorSpeed, spawnIntervalMs, difficulty, durationMs, powerUpSpawnChance, null, null);
    }

    /**
     * Constructs a fully specified LevelConfig with all parameters.
     *
     * @param levelNumber the 1-based level identifier
     * @param maxMeteors the maximum number of meteors allowed on screen simultaneously
     * @param meteorSpeed the fall speed of meteors in pixels per second
     * @param spawnIntervalMs the time between meteor spawns in milliseconds
     * @param difficulty the word difficulty tier for this level
     * @param durationMs the total duration of the level in milliseconds
     * @param powerUpSpawnChance the probability (0.0–1.0) that a spawned meteor carries a power up word
     * @param backgroundAsset   the resource path for the level's background image, or {@code null}
     * @param planetName        the display name of the planet for this level, or {@code null}
     */
    public LevelConfig(int levelNumber, int maxMeteors, float meteorSpeed, long spawnIntervalMs, DifficultyLevel difficulty, long durationMs, float powerUpSpawnChance, String backgroundAsset, String planetName) {
        this.levelNumber = levelNumber;
        this.maxMeteors = maxMeteors;
        this.meteorSpeed = meteorSpeed;
        this.spawnIntervalMs = spawnIntervalMs;
        this.wordDifficulty = difficulty;
        this.durationMs = durationMs;
        this.powerUpSpawnChance = powerUpSpawnChance;
        this.backgroundAsset = backgroundAsset;
        this.planetName = planetName;
    }

    /**
     * Returns the 1-based level number.
     *
     * @return the level number
     */
    public int getLevelNumber() {
        return levelNumber;
    }

    /**
     * Returns the maximum number of meteors that may be active simultaneously.
     *
     * @return the meteor cap
     */
    public int getMaxMeteors() {
        return maxMeteors;
    }

    /**
     * Returns the fall speed of meteors for this level.
     *
     * @return the meteor speed in pixels per second
     */
    public float getMeteorSpeed() {
        return meteorSpeed;
    }

    /**
     * Returns the interval between consecutive meteor spawns.
     *
     * @return the spawn interval in milliseconds
     */
    public long getSpawnIntervalMs() {
        return spawnIntervalMs;
    }

    /**
     * Returns the word difficulty tier applied to spawned meteors.
     *
     * @return the {@link DifficultyLevel} for this level
     */
    public DifficultyLevel getWordDifficulty() {
        return wordDifficulty;
    }

    /**
     * Returns the total duration of this level.
     *
     * @return the level duration in milliseconds
     */
    public long getDurationMs() {
        return durationMs;
    }

    /**
     * Returns the probability that a spawned meteor will carry a  power up word.
     *
     * @return the  power up spawn chance as a float between 0.0 and 1.0
     */
    public float getPowerUpSpawnChance() {
        return powerUpSpawnChance;
    }

    /**
     * Returns the resource path for this level's background image.
     *
     * @return the background asset path, or {@code null} if none is set
     */
    public String getBackgroundAsset() {
        return backgroundAsset;
    }

    /**
     * Returns the display name of the planet associated with this level.
     *
     * @return the planet name, or {@code null} if none is set
     */
    public String getPlanetName() {
        return planetName;
    }
}
