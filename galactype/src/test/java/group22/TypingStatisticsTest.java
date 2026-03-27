package group22;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TypingStatisticsTest {

    private TypingStatistics stats;

    @BeforeEach
    void setUp() {
        stats = new TypingStatistics();
    }

    @Test
    void initialStateIsEmpty() {
        assertEquals(0, stats.getTotalWordsTyped());
        assertEquals(0, stats.getTotalErrors());
        assertEquals(0f, stats.getPeakWPM());
        assertEquals(0f, stats.getAverageWPM());
        assertTrue(stats.getSessionHistory().isEmpty());
    }

    @Test
    void recordSessionUpdatesStats() {
        SessionResult result = new SessionResult(1, 500, 20, 2, 70.0f, 90.0f, 60000L);
        stats.recordSession(result);

        assertEquals(20, stats.getTotalWordsTyped());
        assertEquals(2, stats.getTotalErrors());
        assertEquals(70.0f, stats.getPeakWPM(), 0.01f);
        assertEquals(1, stats.getSessionHistory().size());
    }

    @Test
    void peakWPMTracksHighest() {
        stats.recordSession(new SessionResult(1, 100, 10, 0, 50.0f, 100f, 30000L));
        stats.recordSession(new SessionResult(1, 200, 15, 1, 90.0f, 93f, 30000L));
        stats.recordSession(new SessionResult(1, 150, 12, 0, 75.0f, 100f, 30000L));

        assertEquals(90.0f, stats.getPeakWPM(), 0.01f);
    }

    @Test
    void averageWPMIsCorrect() {
        stats.recordSession(new SessionResult(1, 100, 10, 0, 60.0f, 100f, 30000L));
        stats.recordSession(new SessionResult(1, 100, 10, 0, 80.0f, 100f, 30000L));

        assertEquals(70.0f, stats.getAverageWPM(), 0.01f);
    }

    @Test
    void accuracyWithNoErrors() {
        stats.recordSession(new SessionResult(1, 100, 10, 0, 60.0f, 100f, 30000L));
        assertEquals(100f, stats.getAccuracyPercent(), 0.01f);
    }

    @Test
    void accuracyWithErrors() {
        // 10 words typed, 10 errors → 50% accuracy
        stats.recordSession(new SessionResult(1, 100, 10, 10, 40.0f, 50f, 30000L));
        assertEquals(50.0f, stats.getAccuracyPercent(), 0.01f);
    }

    @Test
    void resetClearsEverything() {
        stats.recordSession(new SessionResult(1, 500, 20, 3, 65.0f, 87f, 60000L));
        stats.reset();

        assertEquals(0, stats.getTotalWordsTyped());
        assertEquals(0, stats.getTotalErrors());
        assertEquals(0f, stats.getPeakWPM());
        assertEquals(0f, stats.getAverageWPM());
        assertTrue(stats.getSessionHistory().isEmpty());
    }
}
