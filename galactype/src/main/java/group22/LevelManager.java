package group22;

import group22.enums.DifficultyLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the ordered collection of game levels and tracks which level is currently active.
 * Provides navigation (advance, set by number) and lookup methods for the UI and GameController.
 */
public class LevelManager {

    private List<LevelConfig> levels;
    private int currentLevelIndex;

    /**
     * Constructs a LevelManager, initializes the level list, and populates the default levels.
     */
    // Sets up the level list and starts at the first level.
    public LevelManager(){
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        initializeLevels();
    }

    /**
     * Returns the configuration for the level that is currently active.
     *
     * @return the current {@link LevelConfig}, or {@code null} if no levels are configured
     */
    // Returns the currently active level configuration.
    public LevelConfig getCurrentLevel(){
        if (levels.isEmpty()) {
            return null;
        }
        return levels.get(currentLevelIndex);
    }

    /**
     * Finds and returns the level configuration with the given level number.
     *
     * @param levelNumber the 1-based level number to look up
     * @return the matching {@link LevelConfig}, or {@code null} if not found
     */
    // Finds a level configuration by its level number.
    public LevelConfig getLevelByNumber(int levelNumber){
        for (LevelConfig level : levels) {
            if (level.getLevelNumber() == levelNumber) {
                return level;
            }
        }
        return null;
    }

    /**
     * Advances the current level index to the next level if one exists.
     *
     * @return {@code true} if the level was advanced; {@code false} if already on the last level
     */
    // Moves to the next level when one is available.
    public boolean advanceLevel(){
        if (isLastLevel()) {
            return false;
        }

        currentLevelIndex++;
        return true;
    }

    /**
     * Sets the active level to the one matching the given level number.
     * Does nothing if no level with that number exists.
     *
     * @param levelNumber the 1-based level number to switch to
     */
    // Sets the current level using a matching level number
    public void setLevel(int levelNumber){
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelNumber() == levelNumber) {
                currentLevelIndex = i;
                return;
            }
        }
    }

    /**
     * Returns the total number of levels configured in this manager.
     *
     * @return the number of levels
     */
    // Returns the total number of configured levels
    public int getTotalLevels(){
        return levels.size();
    }

    /**
     * Checks whether the currently active level is the final one.
     *
     * @return {@code true} if the current level is the last level; {@code false} otherwise
     */
    // Checks whether the current level is the final one
    public boolean isLastLevel(){
        return !levels.isEmpty() && currentLevelIndex == levels.size() - 1;
    }

    /**
     * Returns a copy of the complete list of configured levels.
     *
     * @return a new {@link List} containing all {@link LevelConfig} objects
     */
    // Returns a copy of all configured levels
    public List<LevelConfig> getAllLevels(){
        return new ArrayList<>(levels);
    }

    /**
     * Populates the level list with the default set of planet-themed levels,
     * ordered by distance from the sun: Mercury through Neptune.
     */
    // Builds the default set of level configurations.
    // One level per planet, ordered by distance from the sun: Mercury → Neptune.
    private void initializeLevels(){
        levels.add(new LevelConfig(1, 5,  10f, 2200, DifficultyLevel.EASY,   60000, 0.05f,
                "/PlanetImages/mercury.jpg", "Mercury"));
        levels.add(new LevelConfig(2, 7,  14f, 2000, DifficultyLevel.EASY,   65000, 0.05f,
                "/PlanetImages/venus.jpg", "Venus"));
        levels.add(new LevelConfig(3, 9,  18f, 1800, DifficultyLevel.MEDIUM, 70000, 0.05f,
                "/PlanetImages/earth.jpg", "Earth"));
        levels.add(new LevelConfig(4, 10, 22f, 1800, DifficultyLevel.MEDIUM, 75000, 0.05f,
                "/PlanetImages/mars.jpg", "Mars"));
        levels.add(new LevelConfig(5, 12, 26f, 1600, DifficultyLevel.HARD,   85000, 0.05f,
                "/PlanetImages/jupiter.jpg", "Jupiter"));
        levels.add(new LevelConfig(6, 13, 30f, 1600, DifficultyLevel.HARD,   90000, 0.05f,
                "/PlanetImages/saturn.jpg", "Saturn"));
        levels.add(new LevelConfig(7, 14, 36f, 1400, DifficultyLevel.DOOM,   95000, 0.05f,
                "/PlanetImages/urnaus.jpg", "Uranus"));
        levels.add(new LevelConfig(8, 14, 42f, 1400, DifficultyLevel.DOOM,   100000, 0.05f,
                "/PlanetImages/neptune.jpg", "Neptune"));
    }

}
