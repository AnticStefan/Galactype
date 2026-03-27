package group22;

import group22.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerAccountTest {

    private PlayerAccount player;

    @BeforeEach
    void setUp() {
        player = new PlayerAccount("eric", "password69");
    }

    @Test
    void accountTypeIsPlayer() {
        assertEquals(AccountType.PLAYER, player.getAccountType());
    }

    @Test
    void usernameIsSet() {
        assertEquals("eric", player.getUsername());
    }

    @Test
    void passwordCheckWorks() {
        assertTrue(player.checkPassword("password69"));
        assertFalse(player.checkPassword("wrongpassword"));
    }

    @Test
    void setPasswordChangesPassword() {
        player.setPassword("newpass");
        assertTrue(player.checkPassword("newpass"));
        assertFalse(player.checkPassword("password69"));
    }

    @Test
    void initialLevelProgressIsOne() {
        assertEquals(1, player.getLevelProgress());
    }

    @Test
    void setLevelProgressUpdates() {
        player.setLevelProgress(3);
        assertEquals(3, player.getLevelProgress());
    }

    @Test
    void highScoreDefaultsToZero() {
        assertEquals(0, player.getHighScore(1));
    }

    @Test
    void updateHighScoreSavesHigherScore() {
        player.updateHighScore(1, 500);
        assertEquals(500, player.getHighScore(1));

        player.updateHighScore(1, 300);
        assertEquals(500, player.getHighScore(1)); // should not decrease

        player.updateHighScore(1, 800);
        assertEquals(800, player.getHighScore(1));
    }

    @Test
    void settingsAreInitialized() {
        assertNotNull(player.getSettings());
    }

    @Test
    void statisticsAreInitialized() {
        assertNotNull(player.getStatistics());
    }

    @Test
    void resetStatisticsClearsData() {
        player.getStatistics().recordSession(new SessionResult(1, 100, 10, 0, 60f, 100f, 30000L));
        player.resetStatistics();
        assertTrue(player.getStatistics().getSessionHistory().isEmpty());
    }
}
