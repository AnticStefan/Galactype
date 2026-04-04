package group22;
import  group22.enums.DifficultyLevel;
import  group22.enums.PowerUpType;

/**
 * Represents a single word used in the game, carrying its text, difficulty tier,
 * and an optional powerup type if the word grants a special effect when typed.
 */
public class Word{
    //private instance variables
    private String text;
    private DifficultyLevel difficulty;
    private boolean isPowerUp;
    private PowerUpType powerUpType;

    /**
     * Constructs a regular Word with no powerup association.
     *
     * @param text       the word string the player must type
     * @param difficulty the {@link DifficultyLevel} tier this word belongs to
     */
    //constructor for when theres no powerup
    public Word(String text, DifficultyLevel difficulty){
        this.text = text;
        this.difficulty = difficulty;
    }

    /**
     * Constructs a powerup Word that grants a special effect when fully typed.
     *
     * @param text        the word string the player must type
     * @param difficulty  the {@link DifficultyLevel} tier this word belongs to
     * @param powerUpType the {@link PowerUpType} effect granted upon completion
     */
    //constructor for when there is a powerup word
    public Word(String text, DifficultyLevel difficulty, PowerUpType powerUpType){
        this.text = text;
        this.difficulty = difficulty;
        this.isPowerUp = true;
        this.powerUpType = powerUpType;
    }

    /**
     * Returns the text string of this word.
     *
     * @return the word text
     */
    //getters
    public String getText(){
        return this.text;
    }

    /**
     * Returns the difficulty tier this word belongs to.
     *
     * @return the {@link DifficultyLevel} of this word
     */
    public DifficultyLevel getDifficulty(){
        return this.difficulty;
    }

    /**
     * Returns whether typing this word triggers a powerup effect.
     *
     * @return {@code true} if this word is a powerup word; {@code false} otherwise
     */
    public boolean isPowerUP(){
        return this.isPowerUp;
    }

    /**
     * Returns the powerup type granted by this word, if any.
     *
     * @return the {@link PowerUpType}, or {@code null} if this is not a powerup word
     */
    public PowerUpType getPowerUpType(){
        return this.powerUpType;
    }

    /**
     * Returns the number of characters in this word.
     *
     * @return the length of the word text
     */
    //getting word length
    public int getLength(){
        return this.text.length();
    }
}
