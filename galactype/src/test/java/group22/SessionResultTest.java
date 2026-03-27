package group22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionResultTest {

    @Test
    void constructorSetsFieldsCorrectly() {
        SessionResult result = new SessionResult(2, 1500, 30, 5, 60.0f, 85.7f, 90000L);

        assertEquals(2, result.getLevelPlayed());
        assertEquals(1500, result.getScore());
        assertEquals(30, result.getWordsTyped());
        assertEquals(5, result.getErrors());
        assertEquals(60.0f, result.getWpm());
        assertEquals(85.7f, result.getAccuracy(), 0.01f);
        assertEquals(90000L, result.getDurationMs());
    }

    @Test
    void timestampIsSetOnCreation() {
        long before = System.currentTimeMillis();
        SessionResult result = new SessionResult(1, 100, 10, 1, 50.0f, 90.0f, 30000L);
        long after = System.currentTimeMillis();

        assertTrue(result.getTimestamp() >= before);
        assertTrue(result.getTimestamp() <= after);
    }
}
