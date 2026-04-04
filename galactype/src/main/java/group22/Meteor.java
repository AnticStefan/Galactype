package group22;

/**
 * Represents a single meteor falling down the screen, carrying a word the player must type.
 * Tracks vertical position, typing progress, and destruction state.
 */
public class Meteor{
    //instance variables
    private Word word;
    private float xPosition;
    private float yPosition = 0f; // spawn at top of screen (JavaFX: y=0 is top)
    private float speed;           //tentative

    private int typedProgress;
    private boolean isDestroyed;

    /**
     * Constructs a new Meteor at the given horizontal position with the given word and speed.
     *
     * @param word      the {@link Word} the player must type to destroy this meteor
     * @param xPosition the initial horizontal position in pixels
     * @param speed     the fall speed in pixels per second
     */
    public Meteor(Word word, float xPosition, float speed){
        this.word = word;
        this.xPosition = xPosition;
        this.speed = speed;
        this.typedProgress = 0;
        this.isDestroyed = false;
    }

    /**
     * Returns the word attached to this meteor.
     *
     * @return the {@link Word} the player must type
     */
    //getters for all instance variables
    public Word getWord(){
        return this.word;
    }

    /**
     * Returns the horizontal position of this meteor.
     *
     * @return the x position in pixels
     */
    public float getXPosition(){
        return this.xPosition;
    }

    /**
     * Returns the current vertical position of this meteor.
     *
     * @return the y position in pixels (0 is the top of the screen)
     */
    public float getYPosition(){
        return this.yPosition;
    }

    /**
     * Returns the fall speed of this meteor.
     *
     * @return the speed in pixels per second
     */
    public float getSpeed(){
        return this.speed;
    }

    /**
     * Returns the number of characters the player has correctly typed so far.
     *
     * @return the typed progress index
     */
    public int getTypedProgress(){
        return this.typedProgress;
    }

    /**
     * Returns whether this meteor has been destroyed (fully typed or reached the bottom).
     *
     * @return {@code true} if destroyed; {@code false} otherwise
     */
    public boolean isDestroyed(){
        return this.isDestroyed;
    }

    /**
     * Sets the vertical position of this meteor.
     *
     * @param yPosition the new y position in pixels
     */
    //y position setter
    public void setYPosition(float yPosition){
        this.yPosition = yPosition;
    }

    /**
     * Marks this meteor as destroyed.
     */
    //meteor destruction method
    public void destroy(){
        this.isDestroyed = true;
    }

    /**
     * Resets the player's typing progress on this meteor back to zero.
     */
    //method for updating the users typing progress on the meteor
    public void resetTypedProgress(){
        this.typedProgress = 0;
    }

    /**
     * Advances the typing progress by one character, destroying the meteor if the word is complete.
     */
    public void incrementTypedProgress(){
        this.typedProgress++;
        if(this.typedProgress >= this.word.getLength()){
            destroy();
        }
    }

    /**
     * Checks whether this meteor has reached or passed the bottom boundary of the screen.
     *
     * @param screenHeight the height of the play area in pixels
     * @return {@code true} if the meteor's y position is at or beyond {@code screenHeight}
     */
    //method to check if the meteor has reached the bottom of the screen
    //NOTE: Can adjust this to maybe check screenheight -50 to make it easier for user and it might look visually better
    public boolean hasReachedBottom(float screenHeight){
        return this.yPosition >= screenHeight;
    }

    /**
     * Advances the meteor's vertical position based on its speed and the elapsed frame time.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
    public void update(float deltaTime){
        //update the y position based on speed and deltaTime
        this.yPosition += this.speed * deltaTime;
    }

}
