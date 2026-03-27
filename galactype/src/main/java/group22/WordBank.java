package group22;

import java.lang.reflect.Array;
import java.util.ArrayList;

import group22.enums.DifficultyLevel;
import group22.enums.PowerUpType;

public class WordBank {
    private ArrayList<String> easyWords = new ArrayList<>();
    private ArrayList<String> mediumWords = new ArrayList<>();
    private ArrayList<String> hardWords = new ArrayList<>();
    private ArrayList<String> doomWords = new ArrayList<>();
    private ArrayList<String> powerUpWords = new ArrayList<>();
    private String dataFilePath;

    public WordBank(String dataFilePath){
        this.dataFilePath = dataFilePath;
    }

    //methd that returns ONE randomword 
    public Word getRandomWord(DifficultyLevel difficulty){
      
        ArrayList<String> wordList; //word list depending on the switch case below 

        //switch case based on difficulty level to set wordlist to the correct bank
        switch (difficulty) {
            case EASY:
                wordList = easyWords;
                break;
            case MEDIUM:
                wordList = mediumWords;
                break;
            case HARD:
                wordList = hardWords;
                break;
            case DOOM:
                wordList = doomWords;
                break;
            default:
                throw new IllegalStateException("Unexpected difficulty value: " + difficulty);
        }

        //random index generator to get random word 
        int randomIndex = (int) (Math.random() * wordList.size());

        //returning the wordlist at the random index geerated above and at the set difficulty level
        return new Word(wordList.get(randomIndex), difficulty); 
    }

    /**
     * Method that returns an arraylist of random words based on the difficulty and count parameters. 
     * <p>
     * It calls the getRandomWord method in this class, in a loop and adds the result to an arraylist which is then returned.
     * the loop increments until the count parameter has been reached
     * 
     * @param difficulty the difficulty level of the words to be returned
     * @param count the number of random words to be returned
     * @return an ArrayList of random words based on the difficulty and count parameters
     * @throws IllegalStateException if the difficulty parameter is not a valid difficulty level (EASY, MEDIUM, HARD)
     * @see #getRandomWord(DifficultyLevel)
     */
    public ArrayList<Word> getRandomWords(DifficultyLevel difficulty, int count) {
        ArrayList<Word> randomWords = new ArrayList<>(); //list to hold random words

        //basic for loop that calls getrandomword function above and adds it to an arraylist
        for (int i = 0; i < count; i++) {
            randomWords.add(getRandomWord(difficulty));
        }

        return randomWords;
    }
    /*
    public Word getPowerUpWord(DifficultyLevel difficulty){
        int randomIndex = (int) (Math.random() * powerUpWords.size());
        return new Word(powerUpWords.get(randomIndex), difficulty, ); 
    }
    */

   public void load(){
 
   }

   public int getWordCount(DifficultyLevel difficulty){


    switch (difficulty) {
        case EASY:
            return easyWords.size();
        case MEDIUM:
            return mediumWords.size();
        case HARD:
            return hardWords.size();
        case DOOM:
            return doomWords.size();
        default:
            throw new IllegalStateException("Unexpected difficulty value: " + difficulty);
    }
   }
}
