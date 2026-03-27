package group22; 

import group22.enums.DifficultyLevel;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private List<LevelConfig> levels; 
    private int currentLevelIndex;

    public LevelManager(){
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        initializeLevels();
    }

    public LevelConfig getCurrentLevel(){
        if (levels.isEmpty()) {
            return null;
        }
        return levels.get(currentLevelIndex);
    }

    public LevelConfig getLevelByNumber(int levelNumber){
        for (LevelConfig level : levels) {
            if (level.getLevelNumber() == levelNumber) {
                return level;
            }
        }
        return null;
    }

    public boolean advanceLevel(){
        if (isLastLevel()) {
            return false;
        }

        currentLevelIndex++;
        return true;
    }

    public void setLevel(int levelNumber){
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelNumber() == levelNumber) {
                currentLevelIndex = i;
                return;
            }
        }
    }

    public int getTotalLevels(){
        return levels.size();
    }

    public boolean isLastLevel(){
        return !levels.isEmpty() && currentLevelIndex == levels.size() - 1;
    }

    public List<LevelConfig> getAllLevels(){
        return new ArrayList<>(levels);
    }

    private void initializeLevels(){
        levels.add(new LevelConfig(1, 5, 1.0f, 2000, DifficultyLevel.EASY, 60000));
        levels.add(new LevelConfig(2, 7, 1.3f, 1700, DifficultyLevel.EASY, 70000));
        levels.add(new LevelConfig(3, 9, 1.6f, 1400, DifficultyLevel.MEDIUM, 80000));
        levels.add(new LevelConfig(4, 11, 1.9f, 1100, DifficultyLevel.MEDIUM, 90000));
        levels.add(new LevelConfig(5, 14, 2.2f, 900, DifficultyLevel.HARD, 100000));
    }

}
