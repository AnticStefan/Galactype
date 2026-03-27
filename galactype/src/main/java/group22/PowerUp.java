//NOTE: im not sure if we want to organize this into a package so if so, lmk 
package group22.classes;

import group22.enums.PowerUpType;

public class PowerUp{
    //instance variables 
    private PowerUpType type;
    private long duration; 
    private long startTimeMS; 
    private boolean isActive; 

    //constructer w private variables 
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

    public boolean isActive(){
        if(isActive){
            long elapsedMS = System.currentTimeMillis() - this.startTimeMS;
            if(elapsedMS >= this.duration){
                this.deactivate();
            }
        }
        return this.isActive;
    }

    public boolean isExpired(){
        if(isActive){
            long elapsedMS = System.currentTimeMillis() - this.startTimeMS;
            return elapsedMS >= this.duration;
        }
        return false; 
    }

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

