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

    // Builds the default set of level configurations
    private void initializeLevels(){
        levels.add(new LevelConfig(1, 5, 10f, 2000, DifficultyLevel.EASY, 60000));
        levels.add(new LevelConfig(2, 7, 15f, 1800, DifficultyLevel.EASY, 70000));
        levels.add(new LevelConfig(3, 9, 20f, 2000, DifficultyLevel.MEDIUM, 80000));
        levels.add(new LevelConfig(4, 11, 25f, 2200, DifficultyLevel.MEDIUM, 90000));
        levels.add(new LevelConfig(5, 14, 40f, 2500, DifficultyLevel.HARD, 100000));
        levels.add(new LevelConfig(5, 14, 60f, 2500, DifficultyLevel.DOOM, 100000));

    }

}
