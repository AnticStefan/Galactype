package group22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerSettingsTest {

    @Test
    void defaultsAreZeroAndFalse() {
        PlayerSettings settings = new PlayerSettings();

        assertEquals(0f, settings.getVolume());
        assertFalse(settings.isSfxEnabled());
        assertFalse(settings.isMusicEnabled());
    }

    @Test
    void setVolumeUpdatesValue() {
        PlayerSettings settings = new PlayerSettings();
        settings.setVolume(0.8f);
        assertEquals(0.8f, settings.getVolume(), 0.001f);
    }

    @Test
    void setSfxEnabledToggles() {
        PlayerSettings settings = new PlayerSettings();
        settings.setSfxEnabled(true);
        assertTrue(settings.isSfxEnabled());
        settings.setSfxEnabled(false);
        assertFalse(settings.isSfxEnabled());
    }

    @Test
    void setMusicEnabledToggles() {
        PlayerSettings settings = new PlayerSettings();
        settings.setMusicEnabled(true);
        assertTrue(settings.isMusicEnabled());
    }
}
