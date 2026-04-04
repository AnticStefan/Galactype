package group22;

import group22.enums.GameState;
import group22.enums.PowerUpType;
import group22.interfaces.GameObserver;
import group22.interfaces.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a single active game session, managing meteor spawning, player input,
 * power-up effects, scoring, health, and observer notifications for one level run.
 */
public class GameSession implements Observable {

    // TENTATIVE TENTATIVE TENTATIVE
    private static final float SCREEN_HEIGHT = 600f;
    private static final float SCREEN_WIDTH = 800f;
    private static final float SPAWN_MARGIN = 50f;
    private static final long POWER_UP_DURATION_MS = 5000L;
    // Must match the CHAR_WIDTH estimate used in GameScreen for text rendering
    private static final double CHAR_WIDTH = 8.4; // change later

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

    /**
     * Constructs a new GameSession for the given level configuration.
     *
     * @param config    the {@link LevelConfig} defining this level's parameters
     * @param wordBank  the {@link WordBank} to draw words from during the session
     * @param highScore the player's existing high score for this level (used by ScoreManager)
     * @param maxHealth the number of lives the player starts with
     */
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

    /**
     * Transitions the session to the RUNNING state and notifies observers.
     */
    public void start() {
        state = GameState.RUNNING;
        notifyObservers();
    }

    /**
     * Pauses the session if it is currently running and notifies observers.
     */
    public void pause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            notifyObservers();
        }
    }

    /**
     * Resumes the session if it is currently paused and notifies observers.
     */
    public void resume() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            notifyObservers();
        }
    }

    /**
     * Immediately ends the session by setting its state to GAME_OVER and notifying observers.
     */
    public void end() {
        state = GameState.GAME_OVER;
        notifyObservers();
    }

    /**
     * Advances the session by one frame: updates elapsed time, spawns meteors,
     * moves meteors, checks collisions, recalculates WPM, and expires power-ups.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
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

    /**
     * Processes a single character typed by the player during a running session.
     *
     * @param character the character that was typed
     */
    // Called by the UI on each keypress
    public void onKeyTyped(char character) {
        if (state != GameState.RUNNING) return;
        matchTypingToMeteor(character);
    }

    /**
     * Attempts to match the typed character against the currently targeted meteor,
     * or acquires a new target if none is locked. Awards points or penalises errors accordingly.
     *
     * @param character the character that was typed
     */
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

    /**
     * Spawns a new meteor with a randomly selected word and a collision-safe x position.
     * Retries word selection up to 20 times to avoid meteors sharing a first letter.
     */
    private void spawnMeteor() {
        // Pick a word, retrying up to 20 times to avoid duplicate starting letters
        Word word;
        int maxRetries = 20;
        do {
            if (random.nextFloat() < levelConfig.getPowerUpSpawnChance()) {
                word = wordBank.getPowerUpWord(levelConfig.getWordDifficulty());
            } else {
                word = wordBank.getRandomWord(levelConfig.getWordDifficulty());
            }
            maxRetries--;
        } while (maxRetries > 0 && hasConflictingFirstLetter(word));

        // Clamp x so the word label doesn't overflow the screen edges
        double halfWordWidth = (word.getText().length() * CHAR_WIDTH) / 2.0;
        float minX = (float) (SPAWN_MARGIN + halfWordWidth);
        float maxX = (float) (SCREEN_WIDTH - SPAWN_MARGIN - halfWordWidth);
        if (minX > maxX) {
            minX = maxX = SCREEN_WIDTH / 2f; // fallback for extremely long words
        }
        float x = minX + random.nextFloat() * (maxX - minX);

        activeMeteors.add(new Meteor(word, x, levelConfig.getMeteorSpeed()));
    }

    /**
     * Checks whether any active meteor already starts with the same letter as the candidate word.
     *
     * @param candidate the {@link Word} to check for a first-letter conflict
     * @return {@code true} if a conflict exists; {@code false} otherwise
     */
    private boolean hasConflictingFirstLetter(Word candidate) {
        if (candidate.getText().isEmpty()) return false;
        char first = Character.toLowerCase(candidate.getText().charAt(0));
        for (Meteor meteor : activeMeteors) {
            if (!meteor.isDestroyed()) {
                String text = meteor.getWord().getText();
                if (!text.isEmpty() && Character.toLowerCase(text.charAt(0)) == first) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks all active meteors for bottom-of-screen collisions, applies shield or
     * health loss as appropriate, and triggers GAME_OVER if health reaches zero.
     */
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

    /**
     * Activates the given power-up type, applying its effect immediately or scheduling
     * it for time-based expiry.
     *
     * @param type the {@link PowerUpType} to apply
     */
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

    /**
     * Checks whether any active power-up of the given type is currently in effect.
     *
     * @param type the {@link PowerUpType} to query
     * @return {@code true} if an active power-up of that type exists; {@code false} otherwise
     */
    private boolean isPowerUpActive(PowerUpType type) {
        for (PowerUp p : activePowerUps) {
            if (p.getType() == type && p.isActive()) return true;
        }
        return false;
    }

    /**
     * Recalculates and stores the player's current words-per-minute based on elapsed time.
     */
    private void calculateCurrentWPM() {
        if (elapsedTimeMs == 0) return;
        float minutes = elapsedTimeMs / 60000f;
        currentWPM = wordsCompleted / minutes;
    }

    /**
     * Constructs a {@link SessionResult} summarising the outcome of this session.
     *
     * @return a {@link SessionResult} containing score, accuracy, WPM, and other stats
     */
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

    /**
     * Returns the list of meteors currently on screen.
     *
     * @return the active {@link List} of {@link Meteor} objects
     */
    // Getters used by the UI and GameController
    public List<Meteor> getActiveMeteors()  {
        return activeMeteors;
    }

    /**
     * Returns the list of currently active power-ups.
     *
     * @return the active {@link List} of {@link PowerUp} objects
     */
    public List<PowerUp> getActivePowerUps() {
        return activePowerUps;
    }

    /**
     * Returns the current state of this game session.
     *
     * @return the {@link GameState} of this session
     */
    public GameState getState() {
        return state;
    }

    /**
     * Returns the player's remaining health (lives).
     *
     * @return the current health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the player's current words-per-minute typing speed.
     *
     * @return the current WPM as a float
     */
    public float getCurrentWPM() {
        return currentWPM;
    }

    /**
     * Returns the score manager tracking points for this session.
     *
     * @return the {@link ScoreManager} for this session
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Returns the total elapsed time of this session.
     *
     * @return elapsed time in milliseconds
     */
    public long getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    /**
     * Registers an observer to receive game state and score notifications.
     *
     * @param observer the {@link GameObserver} to add
     */
    @Override
    public void addObserver(GameObserver observer) {observers.add(observer);}

    /**
     * Unregisters a previously added observer.
     *
     * @param observer the {@link GameObserver} to remove
     */
    @Override
    public void removeObserver(GameObserver observer) {observers.remove(observer);}

    /**
     * Notifies all registered observers of a game state change.
     */
    @Override
    public void notifyObservers() {
        for (GameObserver o : observers) o.onGameStateChanged(state);
    }

    /**
     * Notifies all registered observers that the score has changed.
     */
    private void notifyScoreChanged() {
        for (GameObserver o : observers) o.onScoreChanged(scoreManager.getCurrentScore());
    }
}
