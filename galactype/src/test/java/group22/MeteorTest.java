package group22;

import group22.enums.DifficultyLevel; // Needed for creating Word objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeteorTest {

    private Meteor meteor;
    private Word testWord;

    @BeforeEach
    void setUp() {
        // create a Word with a difficulty level
        testWord = new Word("test", DifficultyLevel.EASY);
        meteor = new Meteor(testWord, 100f, 50f);
    }

    @Test
    void constructorInitializesAllFieldsCorrectly() {
        assertEquals(testWord, meteor.getWord());
        assertEquals(100f, meteor.getXPosition());
        assertEquals(0f, meteor.getYPosition());
        assertEquals(50f, meteor.getSpeed());
        assertEquals(0, meteor.getTypedProgress());
        assertFalse(meteor.isDestroyed());
    }

    @Test
    void incrementTypedProgressIncreasesProgress() {
        meteor.incrementTypedProgress();
        assertEquals(1, meteor.getTypedProgress());
        assertFalse(meteor.isDestroyed());
    }

    @Test
    void incrementTypedProgressDestroysMeteorWhenWordCompleted() {
        for (int i = 0; i < testWord.getLength(); i++) {
            meteor.incrementTypedProgress();
        }
        assertTrue(meteor.isDestroyed());
        assertEquals(testWord.getLength(), meteor.getTypedProgress());
    }

    @Test
    void resetTypedProgressResetsProgressToZero() {
        meteor.incrementTypedProgress();
        meteor.resetTypedProgress();
        assertEquals(0, meteor.getTypedProgress());
        assertFalse(meteor.isDestroyed());
    }

    @Test
    void destroySetsIsDestroyedTrue() {
        meteor.destroy();
        assertTrue(meteor.isDestroyed());
    }

    @Test
    void setYPositionChangesYPosition() {
        meteor.setYPosition(200f);
        assertEquals(200f, meteor.getYPosition());
    }

    @Test
    void updateMovesMeteorBasedOnSpeedAndDeltaTime() {
        meteor.update(1f); // deltaTime = 1 second
        assertEquals(50f, meteor.getYPosition());
    }

    @Test
    void hasReachedBottomReturnsFalseWhenAboveScreenHeight() {
        assertFalse(meteor.hasReachedBottom(500f));
    }

    @Test
    void hasReachedBottomReturnsTrueWhenAtOrBelowScreenHeight() {
        meteor.setYPosition(500f);
        assertTrue(meteor.hasReachedBottom(500f));
    }
}