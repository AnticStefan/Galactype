package group22;

import group22.enums.DifficultyLevel;
import group22.enums.PowerUpType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordTest {

    @Test
    void constructorWithoutPowerUpSetsFields() {
        Word word = new Word("hello", DifficultyLevel.EASY);

        assertEquals("hello", word.getText());
        assertEquals(DifficultyLevel.EASY, word.getDifficulty());
        assertFalse(word.isPowerUP());
        assertNull(word.getPowerUpType());
    }

    @Test
    void constructorWithPowerUpSetsFields() {
        Word word = new Word("boost", DifficultyLevel.MEDIUM, PowerUpType.SLOW_METEORS);

        assertEquals("boost", word.getText());
        assertEquals(DifficultyLevel.MEDIUM, word.getDifficulty());
        assertTrue(word.isPowerUP());
        assertEquals(PowerUpType.SLOW_METEORS, word.getPowerUpType());
    }
}