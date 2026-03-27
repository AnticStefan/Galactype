package group22;


public class Meteor{
    //instance variables 
    private Word word;
    private float xPosition;
    private float yPosition = 550; //starts above the screen height 
    private float speed;           //tentative 

    private int typedProgress;
    private boolean isDestroyed;

    public Meteor(Word word, float xPosition, float speed){
        this.word = word;
        this.xPosition = xPosition;
        this.speed = speed;
        this.typedProgress = 0;
        this.isDestroyed = false;
    }

    //getters for all instance variables 
    public Word getWord(){
        return this.word;
    }

    public float getXPosition(){
        return this.xPosition;
    }

    public float getYPosition(){
        return this.yPosition;
    }

    public float getSpeed(){
        return this.speed;
    }

    public int getTypedProgress(){
        return this.typedProgress;
    }

    public boolean isDestroyed(){
        return this.isDestroyed;
    }

    //y position setter
    public void setYPosition(float yPosition){
        this.yPosition = yPosition;
    }

    //meteor destruction method
    public void destroy(){
        this.isDestroyed = true;
    }

    //method for updating the users typing progress on the meteor
   public void incrementTypedProgress(){
        this.typedProgress++;
        if(this.typedProgress >= this.word.getLength()){
            destroy();
        }
    }
    
    //method to check if the meteor has reached the bottom of the screen
    //NOTE: Can adjust this to maybe check screenheight -50 to make it easier for user and it might look visually better 
    public boolean hasReachedBottom(float screenHeight){
        return this.yPosition <= screenHeight;
    }

    public void update(float deltaTime){
        //update the y position based on speed and deltaTime
        this.yPosition -= this.speed * deltaTime;
    }

}