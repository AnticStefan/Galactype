package group22;

/**
 *
 */
public class PlayerSettings {

    /** */
    private float volume;

    /** */
    private boolean sfxEnabled;

    /** */
    private boolean musicEnabled;

    /**
     *
     */
    public PlayerSettings() {

    }

    /**
     *
     * @return volume
     */
    public float getVolume() {
        return volume;
    }

    /**
     *
     * @param newVolume 
     */
    public void setVolume(float newVolume) {
        volume = newVolume;
    }

    /**
     *
     * @return if the sound effects are enabled or not
     */
    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    /**
     *
     * @param isEnabled
     */
    public void setSfxEnabled(boolean isEnabled) {
        sfxEnabled = isEnabled;
    }

    /**
     *
     * @return if the music is enabled or not
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     *
     * @param isEnabled
     */
    public void setMusicEnabled(boolean isEnabled) {
        musicEnabled = isEnabled;
    }
}
