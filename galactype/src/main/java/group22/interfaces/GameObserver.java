package group22.interfaces;

import group22.enums.GameState;

/**
 * Top of chain for GameSessionInterfaces
 * (We probably don't need this chain)
 */
public interface GameObserver {

    /**
     * Process when the game state is changed
     * @param state
     */
    public void onGameStateChanged(GameState state);

    /**
     * Process when the score is changed
     * @param score
     */
    public void onScoreChanged(int score);

    /**
     * Process when health is changed
     * @param health
     */
    public void onHealthChanged(int health);
}
