package group22.interfaces;

import group22.enums.GameState;

public interface GameObserver {
    public void onGameStateChanged(GameState state);
    public void onScoreChanged(int score);
    public void onHealthChanged(int health);
}
