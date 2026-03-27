package group22;

import group22.enums.DifficultyLevel;
import group22.enums.GameState;
import group22.interfaces.GameObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    // Helpers 

    /** Word bank with a single known word in every difficulty bucket. */
    private WordBank wordBank(Path dir, String word) throws IOException {
        String entry = "{\"text\":\"" + word + "\",\"isPowerUp\":false}";
        String json = "{\"easy\":[" + entry + "],\"medium\":[" + entry + "]," + "\"hard\":[" + entry + "],\"doom\":[" + entry + "]}";
        Path file = dir.resolve("words.json");
        Files.writeString(file, json);
        WordBank wb = new WordBank(file.toString());
        wb.load();
        return wb;
    }

    /** Word bank whose only word is a SHIELD power-up. */
    private WordBank shieldWordBank(Path dir) throws IOException {
        String entry = "{\"text\":\"guard\",\"isPowerUp\":true,\"powerUpType\":\"SHIELD\"}";
        String json = "{\"easy\":[" + entry + "],\"medium\":[" + entry + "]," + "\"hard\":[" + entry + "],\"doom\":[" + entry + "]}";
        Path file = dir.resolve("shield_words.json");
        Files.writeString(file, json);
        WordBank wb = new WordBank(file.toString());
        wb.load();
        return wb;
    }

    /** Shorthand for building a LevelConfig. spawnIntervalMs=0 means spawn every update. */
    private LevelConfig cfg(long durationMs, long spawnMs, float speed, int maxMeteors) {
        return new LevelConfig(1, maxMeteors, speed, spawnMs, DifficultyLevel.EASY, durationMs, 0f);
    }

    // State machine 

    @Test
    void initialStateIsPaused(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);
        assertEquals(GameState.PAUSED, session.getState());
    }

    @Test
    void startSetsRunning(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);
        session.start();
        assertEquals(GameState.RUNNING, session.getState());
    }

    @Test
    void pauseAndResumeTogglesState(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.pause();
        assertEquals(GameState.PAUSED, session.getState());
        session.resume();
        assertEquals(GameState.RUNNING, session.getState());
    }

    @Test
    void endSetsGameOver(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.end();
        assertEquals(GameState.GAME_OVER, session.getState());
    }

    @Test
    void updateDoesNothingWhenPaused(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);
        // Don't call start(), stays PAUSED
        session.update(1.0f);
        assertEquals(GameState.PAUSED, session.getState());
        assertEquals(0L, session.getElapsedTimeMs());
        assertTrue(session.getActiveMeteors().isEmpty());
    }

    @Test
    void levelCompletesWhenDurationExceeded(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(100L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.update(0.2f); // 200ms > 100ms level duration
        assertEquals(GameState.LEVEL_COMPLETE, session.getState());
    }

    // Observers 

    @Test
    void observersReceiveStateChanges(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 5), wordBank(tmp, "hi"), 0, 3);

        List<GameState> received = new ArrayList<>();
        session.addObserver(new GameObserver() {
            @Override public void onGameStateChanged(GameState s) { received.add(s); }
            @Override public void onScoreChanged(int score) {}
            @Override public void onHealthChanged(int health) {}
        });

        session.start();
        session.pause();
        session.resume();
        session.end();

        assertEquals(List.of(GameState.RUNNING, GameState.PAUSED, GameState.RUNNING, GameState.GAME_OVER), received);
    }

    // Typing logic 

    @Test
    void typingCorrectWordAddsScoreAndDestroysMeteor(@TempDir Path tmp) throws IOException {
        // spawnInterval= 0 so first update() spawns immediately
        GameSession session = new GameSession(cfg(60000L, 0, 1f, 1), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.update(0.001f); // spawn "hi"

        assertEquals(1, session.getActiveMeteors().size());

        session.onKeyTyped('h');
        session.onKeyTyped('i'); // word complete

        assertTrue(session.getScoreManager().getCurrentScore() > 0);
        assertTrue(session.getActiveMeteors().get(0).isDestroyed());
    }

    @Test
    void typingWrongCharResetsProgressAndReleasesLock(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 0, 1f, 1), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.update(0.001f); // spawn "hi"

        Meteor meteor = session.getActiveMeteors().get(0);

        session.onKeyTyped('h'); // correct locks onto meteor, progress = 1
        assertEquals(1, meteor.getTypedProgress());

        session.onKeyTyped('x'); // wrong resets progress, releases lock
        assertEquals(0, meteor.getTypedProgress());
    }

    @Test
    void keyTypedIgnoredWhenNotRunning(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 0, 1f, 1), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.update(0.001f); // spawn "hi"
        session.pause();

        Meteor meteor = session.getActiveMeteors().get(0);
        session.onKeyTyped('h');
        assertEquals(0, meteor.getTypedProgress()); // no progress while paused
    }

    // Collision and health

    @Test
    void meteorHittingBottomReducesHealth(@TempDir Path tmp) throws IOException {
        // speed=2000 px/s; SCREEN_HEIGHT=600; needs ~0.3s to reach bottom
        GameSession session = new GameSession(cfg(Long.MAX_VALUE / 2, 0, 2000f, 1), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.update(0.001f); // spawn, move to y≈2
        session.update(0.4f); // move to y≈802 > 600 to collision
        assertEquals(2, session.getHealth());
    }

    @Test
    void healthReachingZeroCausesGameOver(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(Long.MAX_VALUE / 2, 0, 2000f, 1), wordBank(tmp, "hi"), 0, 1);
        session.start();
        session.update(0.001f);
        session.update(0.4f); // collision to health=0 to GAME_OVER
        assertEquals(GameState.GAME_OVER, session.getState());
        assertEquals(0, session.getHealth());
    }

    @Test
    void shieldAbsorbsOneMeteorHit(@TempDir Path tmp) throws IOException {
        // powerUpSpawnChance=1.0 forces every spawn to be a powerup word
        LevelConfig shieldCfg = new LevelConfig(1, 1, 2000f, 0, DifficultyLevel.EASY, Long.MAX_VALUE / 2, 1.0f);
        GameSession session = new GameSession(shieldCfg, shieldWordBank(tmp), 0, 3);
        session.start();
        session.update(0.001f); // spawn "guard" (power-up)

        for (char c : "guard".toCharArray()) session.onKeyTyped(c); // SHIELD activated

        session.update(0.001f); // clear destroyed meteor (size to 0)
        session.update(0.4f);   // spawn new "guard", crashes to bottom shield absorbs

        assertEquals(3, session.getHealth()); // no damage taken
    }

    // Session result 

    @Test
    void buildSessionResultReflectsElapsedTimeAndLevel(@TempDir Path tmp) throws IOException {
        GameSession session = new GameSession(cfg(60000L, 9999999L, 1f, 1), wordBank(tmp, "hi"), 0, 3);
        session.start();
        session.update(0.5f); // 500ms elapsed

        SessionResult result = session.buildSessionResult();
        assertEquals(1, result.getLevelPlayed());
        assertEquals(0, result.getWordsTyped());
        assertEquals(100f, result.getAccuracy(), 0.01f); // no errors to 100%
        assertTrue(result.getDurationMs() >= 500);
    }
}
