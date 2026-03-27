package group22;
import  group22.enums.DifficultyLevel;
import  group22.enums.PowerUpType;

public class Word{ 
    //private instance variables 
    private String text;
    private DifficultyLevel difficulty;
    private boolean isPowerUp;
    private PowerUpType powerUpType;

    //constructor for when theres no powerup
    public Word(String text, DifficultyLevel difficulty){
        this.text = text;
        this.difficulty = difficulty;
    }

    //constructor for when there is a powerup word 
    public Word(String text, DifficultyLevel difficulty, PowerUpType powerUpType){
        this.text = text;
        this.difficulty = difficulty;
        this.isPowerUp = true; 
        this.powerUpType = powerUpType;
    }

    //getters 
    public String getText(){
        return this.text;
    }

    public DifficultyLevel getDifficulty(){
        return this.difficulty;
    }
    
    public boolean isPowerUP(){
        return this.isPowerUp;
    }

    public PowerUpType getPowerUpType(){
        return this.powerUpType;
    }

    //getting word length 
    public int getLength(){
        return this.text.length();
    }
}
