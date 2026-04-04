package group22;

/**
 * Stores per-player audio and display preferences, including master volume
 * and individual toggles for sound effects and background music.
 */
public class PlayerSettings {

    /** The master volume level, where 0.0 is silent and 1.0 is full volume. */
    private float volume;

    /** Whether sound effects are enabled. */
    private boolean sfxEnabled;

    /** Whether background music is enabled. */
    private boolean musicEnabled;

    /**
     * Constructs a PlayerSettings instance with default values.
     */
    public PlayerSettings() {

    }

    /**
     * Returns the current master volume level.
     *
     * @return the volume as a float between 0.0 (silent) and 1.0 (full)
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the master volume level.
     *
     * @param newVolume the new volume value between 0.0 and 1.0
     */
    public void setVolume(float newVolume) {
        volume = newVolume;
    }

    /**
     * Returns whether sound effects are currently enabled.
     *
     * @return {@code true} if SFX are enabled; {@code false} otherwise
     */
    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    /**
     * Sets whether sound effects are enabled.
     *
     * @param isEnabled {@code true} to enable SFX; {@code false} to disable
     */
    public void setSfxEnabled(boolean isEnabled) {
        sfxEnabled = isEnabled;
    }

    /**
     * Returns whether background music is currently enabled.
     *
     * @return {@code true} if music is enabled; {@code false} otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Sets whether background music is enabled.
     *
     * @param isEnabled {@code true} to enable music; {@code false} to disable
     */
    public void setMusicEnabled(boolean isEnabled) {
        musicEnabled = isEnabled;
    }
}
