package group22.interfaces;

/**
 * Part of chain interface for GameSession
 * (We probably don't need this chain)
 */
public interface Observable {

    /**
     * Create game observer
     * @param observer
     */
    public void addObserver(GameObserver observer);

    /**
     * Remove game observer
     * @param observer
     */
    public void removeObserver(GameObserver observer);

    /**
     * Update all observers
     */
    public void notifyObservers();
}

