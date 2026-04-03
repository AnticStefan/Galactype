package group22; 

import group22.enums.DifficultyLevel;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private List<LevelConfig> levels; 
    private int currentLevelIndex;

    // Sets up the level list and starts at the first level.
    public LevelManager(){
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        initializeLevels();
    }

    // Returns the currently active level configuration.
    public LevelConfig getCurrentLevel(){
        if (levels.isEmpty()) {
            return null;
        }
        return levels.get(currentLevelIndex);
    }

    // Finds a level configuration by its level number.
    public LevelConfig getLevelByNumber(int levelNumber){
        for (LevelConfig level : levels) {
            if (level.getLevelNumber() == levelNumber) {
                return level;
            }
        }
        return null;
    }

    // Moves to the next level when one is available.
    public boolean advanceLevel(){
        if (isLastLevel()) {
            return false;
        }

        currentLevelIndex++;
        return true;
    }

    // Sets the current level using a matching level number
    public void setLevel(int levelNumber){
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelNumber() == levelNumber) {
                currentLevelIndex = i;
                return;
            }
        }
    }

    // Returns the total number of configured levels
    public int getTotalLevels(){
        return levels.size();
    }

    // Checks whether the current level is the final one
    public boolean isLastLevel(){
        return !levels.isEmpty() && currentLevelIndex == levels.size() - 1;
    }

    // Returns a copy of all configured levels
    public List<LevelConfig> getAllLevels(){
        return new ArrayList<>(levels);
    }

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
        levels.add(new LevelConfig(5, 12, 28f, 1600, DifficultyLevel.HARD,   85000, 0.05f,
                "/PlanetImages/jupiter.jpg", "Jupiter"));
        levels.add(new LevelConfig(6, 13, 36f, 1600, DifficultyLevel.HARD,   90000, 0.05f,
                "/PlanetImages/saturn.jpg", "Saturn"));
        levels.add(new LevelConfig(7, 14, 50f, 1400, DifficultyLevel.DOOM,   95000, 0.05f,
                "/PlanetImages/urnaus.jpg", "Uranus"));
        levels.add(new LevelConfig(8, 14, 65f, 1400, DifficultyLevel.DOOM,   100000, 0.05f,
                "/PlanetImages/neptune.jpg", "Neptune"));
    }

}
