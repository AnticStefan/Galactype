//NOTE: im not sure if we want to organize this into a package so if so, lmk
package group22;

import group22.enums.PowerUpType;

/**
 * Represents a time-limited (or single-use) power up that can be activated during a game session.
 * Tracks whether the power up is active and auto-deactivates when its duration expires.
 */
public class PowerUp{
    //instance variables
    private PowerUpType type;
    private long duration;
    private long startTimeMS;
    private boolean isActive;

    /**
     * Constructs a new PowerUp of the given type with the specified duration.
     * The power up starts in an inactive state.
     *
     * @param type     the {@link PowerUpType} that defines this power up's effect
     * @param duration the time the power up remains active after activation, in milliseconds
     */
    //constructor for private variables
    public PowerUp(PowerUpType type, long duration){
        this.type = type;
        this.duration = duration;
        this.startTimeMS = 0;
        this.isActive = false;
    }

    /**
     * Activates the power up, recording the current system time as the start time.
     */
    //method to activate powerup
    public void activate(){
        this.startTimeMS = System.currentTimeMillis();
        this.isActive = true;
    }

    /**
     * Immediately deactivates the power up regardless of remaining duration.
     */
    //sets isActive boolean value to false, used in other methods
    public void deactivate(){
        this.isActive = false;
    }

    /**
     * Returns whether the power up is currently active.
     * If active but its duration has elapsed, it is automatically deactivated first.
     *
     * @return {@code true} if the power up is active and not yet expired; {@code false} otherwise
     */
    //checks if powerup is active and if it has expired, if expired it deactivates the powerup and returns false, otherwise returns true
    public boolean isActive(){
        if(isActive){
            long elapsedMS = System.currentTimeMillis() - this.startTimeMS;
            if(elapsedMS >= this.duration){
                this.deactivate();
            }
        }
        return this.isActive;
    }

    /**
     * Returns whether the power up has expired (i.e. is no longer active).
     *
     * @return {@code true} if the power up is not active; {@code false} if still running
     */
    public boolean isExpired(){
       return !isActive();
    }

    /**
     * Returns the type of effect this power up applies.
     *
     * @return the {@link PowerUpType} of this power up
     */
    public PowerUpType getType(){
        return this.type;
    }

    /**
     * Returns the time remaining before this power up expires.
     *
     * @return remaining duration in milliseconds, or {@code 0} if not active
     */
    public long getRemainingMS(){
        if(isActive){
            long elapsedMS = System.currentTimeMillis() - this.startTimeMS;
            return Math.max(0, this.duration - elapsedMS);
        }
        return 0;
    }

}
