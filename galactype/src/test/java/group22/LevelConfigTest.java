package group22;

import group22.enums.DifficultyLevel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LevelConfigTest {

    @Test
    void constructorSetsAllFields() {
        LevelConfig config = new LevelConfig(1, 5, 2.5f, 3000L, DifficultyLevel.EASY, 60000L);

        assertEquals(1, config.getLevelNumber());
        assertEquals(5, config.getMaxMeteors());
        assertEquals(2.5f, config.getMeteorSpeed(), 0.001f);
        assertEquals(3000L, config.getSpawnIntervalMs());
        assertEquals(DifficultyLevel.EASY, config.getWordDifficulty());
        assertEquals(60000L, config.getDurationMs());
    }

    @Test
    void defaultPowerUpSpawnChanceIsSet() {
        LevelConfig config = new LevelConfig(1, 5, 2.5f, 3000L, DifficultyLevel.EASY, 60000L);
        assertEquals(0.1f, config.getPowerUpSpawnChance(), 0.001f);
    }
}
