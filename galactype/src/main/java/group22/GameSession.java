package group22;

import group22.enums.GameState;
import group22.enums.PowerUpType;
import group22.interfaces.GameObserver;
import group22.interfaces.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSession implements Observable {

    // TENTATIVE TENTATIVE TENTATIVE
    private static final float SCREEN_HEIGHT = 600f;
    private static final float SCREEN_WIDTH = 800f;
    private static final float SPAWN_MARGIN = 50f;
    private static final long POWER_UP_DURATION_MS = 5000L;

    private final List<Meteor> activeMeteors;
    private final List<PowerUp> activePowerUps;
    private final ScoreManager scoreManager;
    private final LevelConfig levelConfig;
    private final WordBank wordBank;
    private final int maxHealth;
    private final List<GameObserver> observers;
    private final Random random;

    private GameState state;
    private int health;
    private float currentWPM;
    private long elapsedTimeMs;
    private long spawnTimerMs;
    private int wordsCompleted;
    private int totalErrors;
    private Meteor targetMeteor;

    public GameSession(LevelConfig config, WordBank wordBank, int highScore, int maxHealth) {
        this.levelConfig = config;
        this.wordBank = wordBank;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.scoreManager = new ScoreManager(highScore);
        this.state = GameState.PAUSED;
        this.activeMeteors = new ArrayList<>();
        this.activePowerUps = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.random = new Random();
    }

    public void start() {
        state = GameState.RUNNING;
        notifyObservers();
    }

    public void pause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            notifyObservers();
        }
    }

    public void resume() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            notifyObservers();
        }
    }

    public void end() {
        state = GameState.GAME_OVER;
        notifyObservers();
    }

    // Called every frame. deltaTime is in seconds.
    public void update(float deltaTime) {
        if (state != GameState.RUNNING) return;

        elapsedTimeMs += (long) (deltaTime * 1000);
        spawnTimerMs += (long) (deltaTime * 1000);

        if (elapsedTimeMs >= levelConfig.getDurationMs()) {
            state = GameState.LEVEL_COMPLETE;
            notifyObservers();
            return;
        }

        // Spawn on interval up to the max meteor cap
        if (spawnTimerMs >= levelConfig.getSpawnIntervalMs()
                && activeMeteors.size() < levelConfig.getMaxMeteors()) {
            spawnMeteor();
            spawnTimerMs = 0;
        }

        // FREEZE stops all movement; SLOW halves it
        float effectiveDelta = deltaTime;
        if (isPowerUpActive(PowerUpType.FREEZE_METEORS)) {
            effectiveDelta = 0f;
        } else if (isPowerUpActive(PowerUpType.SLOW_METEORS)) {
            effectiveDelta = deltaTime * 0.5f;
        }

        for (Meteor meteor : activeMeteors) {
            if (!meteor.isDestroyed()) {
                meteor.update(effectiveDelta);
            }
        }

        checkMeteorCollisions();
        calculateCurrentWPM();
        activePowerUps.removeIf(PowerUp::isExpired);
    }

    // Called by the UI on each keypress
    public void onKeyTyped(char character) {
        if (state != GameState.RUNNING) return;
        matchTypingToMeteor(character);
    }

    private void matchTypingToMeteor(char character) {
        // If no target locked, find a meteor whose next expected character matches
        if (targetMeteor == null || targetMeteor.isDestroyed()) {
            targetMeteor = null;
            for (Meteor meteor : activeMeteors) {
                if (!meteor.isDestroyed()) {
                    char expected = meteor.getWord().getText().charAt(meteor.getTypedProgress());
                    if (expected == character) {
                        targetMeteor = meteor;
                        break;
                    }
                }
            }
            if (targetMeteor == null) {
                scoreManager.onError();
                totalErrors++;
                notifyScoreChanged();
                return;
            }
        }

        // Check the next expected character on the locked target
        char expected = targetMeteor.getWord().getText().charAt(targetMeteor.getTypedProgress());

        if (character == expected) {
            scoreManager.onCorrectCharacter();
            targetMeteor.incrementTypedProgress();

            if (targetMeteor.isDestroyed()) {
                // Word fully typed
                scoreManager.addPoints(targetMeteor.getWord(), currentWPM);
                wordsCompleted++;
                notifyScoreChanged();

                if (targetMeteor.getWord().isPowerUP()) {
                    applyPowerUp(targetMeteor.getWord().getPowerUpType());
                }
                targetMeteor = null;
            }
        } else {
            // Wrong key, penalise and release the lock
            scoreManager.onError();
            totalErrors++;
            targetMeteor.resetTypedProgress();
            targetMeteor = null;
            notifyScoreChanged();
        }
    }

    private void spawnMeteor() {
        float x = SPAWN_MARGIN + random.nextFloat() * (SCREEN_WIDTH - 2 * SPAWN_MARGIN);

        Word word;
        if (random.nextFloat() < levelConfig.getPowerUpSpawnChance()) {
            word = wordBank.getPowerUpWord(levelConfig.getWordDifficulty());
        } else {
            word = wordBank.getRandomWord(levelConfig.getWordDifficulty());
        }

        activeMeteors.add(new Meteor(word, x, levelConfig.getMeteorSpeed()));
    }

    private void checkMeteorCollisions() {
        for (Meteor meteor : activeMeteors) {
            if (!meteor.isDestroyed() && meteor.hasReachedBottom(SCREEN_HEIGHT)) {
                meteor.destroy();

                if (meteor == targetMeteor) targetMeteor = null;

                // SHIELD absorbs one hit then deactivates
                boolean shielded = false;
                for (PowerUp p : activePowerUps) {
                    if (p.getType() == PowerUpType.SHIELD && p.isActive()) {
                        p.deactivate();
                        shielded = true;
                        break;
                    }
                }

                if (!shielded) {
                    health--;
                    for (GameObserver o : observers) o.onHealthChanged(health);
                }

                if (health <= 0) {
                    state = GameState.GAME_OVER;
                    notifyObservers();
                    return;
                }
            }
        }
        activeMeteors.removeIf(Meteor::isDestroyed);
    }

    private void applyPowerUp(PowerUpType type) {
        if (type == PowerUpType.EXTRA_LIFE) {
            health = Math.min(health + 1, maxHealth);
            for (GameObserver o : observers) o.onHealthChanged(health);
            return;
        }
        // SLOW, FREEZE, and SHIELD are time based (SHIELD deactivates on first hit)
        PowerUp powerUp = new PowerUp(type, POWER_UP_DURATION_MS);
        powerUp.activate();
        activePowerUps.add(powerUp);
    }

    private boolean isPowerUpActive(PowerUpType type) {
        for (PowerUp p : activePowerUps) {
            if (p.getType() == type && p.isActive()) return true;
        }
        return false;
    }

    private void calculateCurrentWPM() {
        if (elapsedTimeMs == 0) return;
        float minutes = elapsedTimeMs / 60000f;
        currentWPM = wordsCompleted / minutes;
    }

    public SessionResult buildSessionResult() {
        int totalAttempts = wordsCompleted + totalErrors;
        float accuracy = totalAttempts == 0 ? 100f : (wordsCompleted / (float) totalAttempts) * 100f;

        return new SessionResult(
                levelConfig.getLevelNumber(),
                scoreManager.getCurrentScore(),
                wordsCompleted,
                totalErrors,
                currentWPM,
                accuracy,
                elapsedTimeMs
        );
    }

    // Getters used by the UI and GameController
    public List<Meteor> getActiveMeteors()  {
        return activeMeteors; 
    }
    
    public List<PowerUp> getActivePowerUps() {
        return activePowerUps; 
    }

    public GameState getState() { 
        return state; 
    }

    public int getHealth() { 
        return health; 
    }

    public float getCurrentWPM() { 
        return currentWPM; 
    }
    
    public ScoreManager getScoreManager() { 
        return scoreManager; 
    }

    public long getElapsedTimeMs() { 
        return elapsedTimeMs; 
    }

    @Override
    public void addObserver(GameObserver observer) {observers.add(observer);}
    @Override
    public void removeObserver(GameObserver observer) {observers.remove(observer);}
    @Override
    public void notifyObservers() {
        for (GameObserver o : observers) o.onGameStateChanged(state);
    }

    private void notifyScoreChanged() {
        for (GameObserver o : observers) o.onScoreChanged(scoreManager.getCurrentScore());
    }
}
