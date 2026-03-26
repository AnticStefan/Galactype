package group22.interfaces;

/**
 * Saving interface to be implemented by HighScoreTable and Account Manager
 */
public interface Savable {

    /**
     * Isolated save method
     */
    public void save();

    /**
     * Isolated load method
     */
    public void load();
}
