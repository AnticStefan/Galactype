//NOTE: im not sure if we want to organize this into a package so if so, lmk 
package group22;

import group22.enums.PowerUpType;

public class PowerUp{
    //instance variables 
    private PowerUpType type;
    private long duration; 
    private long startTimeMS; 
    private boolean isActive; 

    //constructor for private variables 
    public PowerUp(PowerUpType type, long duration){
        this.type = type; 
        this.duration = duration; 
        this.startTimeMS = 0; 
        this.isActive = false; 
    }

    //method to activate powerup
    public void activate(){
        this.startTimeMS = System.currentTimeMillis();
        this.isActive = true; 
    }

    //sets isActive boolean value to false, used in other methods 
    public void deactivate(){
        this.isActive = false; 
    }

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

    //checks if powerup is active and if it has expired, if expired it deactivates the powerup and returns true, otherwise returns false
    public boolean isExpired(){
       return !isActive();
    }

    //getters
    public PowerUpType getType(){
        return this.type;
    }

    public long getRemainingMS(){
        if(isActive){
            long elapsedMS = System.currentTimeMillis() - this.startTimeMS;
            return Math.max(0, this.duration - elapsedMS);
        }
        return 0;
    }

} 

