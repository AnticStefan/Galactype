package group22;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import group22.enums.DifficultyLevel;
import group22.enums.PowerUpType;

public class WordBank {
    private ArrayList<String> easyWords = new ArrayList<>();
    private ArrayList<String> mediumWords = new ArrayList<>();
    private ArrayList<String> hardWords = new ArrayList<>();
    private ArrayList<String> doomWords = new ArrayList<>();
    private ArrayList<Word> powerUpWords = new ArrayList<>();
    private String dataFilePath;

    public WordBank(String dataFilePath){
        this.dataFilePath = dataFilePath;
    }

    //method to load words from the data file into the word bank
    public void load(){
        //clearing in case there was values in wordbank before loading new ones in
        easyWords.clear();
        mediumWords.clear();
        hardWords.clear();
        doomWords.clear();
        powerUpWords.clear();

        //try for reading files 
        try (FileReader reader = new FileReader(dataFilePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            //referencing helper method below to load all values in to proper arraylist based on difficulty level and if its a powerup word or not
            loadSection(root, "easy", DifficultyLevel.EASY, easyWords);
            loadSection(root, "medium", DifficultyLevel.MEDIUM, mediumWords);
            loadSection(root, "hard", DifficultyLevel.HARD, hardWords);
            loadSection(root, "doom", DifficultyLevel.DOOM, doomWords);
        }  
        //catch if reading fails 
        catch (IOException e) {  
            throw new RuntimeException("Failed to load word bank from " + dataFilePath, e);
        }
    }

    //helper method to load words from JSON and is used in load
    /* 
    finds the requestions JSON section then loops through each word object in the section
    pulls out the text value and adds it to the word list
    if the word is marked as a power up, it also creates a word objects with the associated difficulty and poweruptype 
    then stores the powerup word in the Arraylist<word> for powerups     
    */
    private void loadSection(JsonObject root, String sectionName, DifficultyLevel difficulty, ArrayList<String> targetWords) {
        //makes sure each section exists in the JSON before going through it
        if (!root.has(sectionName) || !root.get(sectionName).isJsonArray()) {
            return;
        }

        //get the array of words for each difficulty section 
        JsonArray section = root.getAsJsonArray(sectionName);


        for (JsonElement element : section) {

            //if its not a json object, skip 
            if (!element.isJsonObject()) {
                continue;
            }

            //convert current element in section to json objects
            JsonObject wordObject = element.getAsJsonObject();

            //skip the entry in the json if there is no text field 
            if (!wordObject.has("text")) {
                continue;
            }

            //read the word from text
            String text = wordObject.get("text").getAsString();

            //make sure its not empty or blank 
            if (text == null || text.isBlank()) {
                continue;
            }

            //add word to the difficulty list
            targetWords.add(text);

            //check for if its a powerup.
            //if it is a powerup, store in powerUpWords arraylist 
           boolean isPowerUp = wordObject.has("isPowerUp") && wordObject.get("isPowerUp").getAsBoolean();

            // If it is a power-up word, also store it in the powerUpWords list
            if (isPowerUp) {

                // Get the power-up type from the JSON if it exists
                String powerUpTypeString = null;

                if (wordObject.has("powerUpType")) {
                    powerUpTypeString = wordObject.get("powerUpType").getAsString();
                }

                // Convert the JSON string into the PowerUpType enum used in Java
                PowerUpType powerUpType = parsePowerUpType(powerUpTypeString);

                // Only create the Word object if the power-up type was valid
                if (powerUpType != null) {
                    powerUpWords.add(new Word(text, difficulty, powerUpType));
                }
            }
        }
    }

    private PowerUpType parsePowerUpType(String powerUpTypeString) {

    // If the JSON value is missing or blank return null 
    if (powerUpTypeString == null || powerUpTypeString.trim().isEmpty()) {
        return null;
    }

    // Clean up the string by removing extra spaces and converting it to uppercase
    // so it matches enum naming style
    String normalized = powerUpTypeString.trim().toUpperCase();

    try {
        // Try to convert the cleaned string directly into a PowerUpType enum
        // Example: "FREEZE_METEORS" -> PowerUpType.FREEZE_METEORS
        return PowerUpType.valueOf(normalized);
    } catch (IllegalArgumentException e) {
        // If the string does not match any enum constant, return null
        // so the program does not crash while loading the file
        return null;
    }
}
    
    public Word getRandomWord(DifficultyLevel difficulty) {
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

        if (wordList.isEmpty()) {
            throw new IllegalStateException("No words loaded for difficulty: " + difficulty);
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
    
    
    public Word getPowerUpWord(DifficultyLevel difficulty){
        if (powerUpWords.isEmpty()) {
            throw new IllegalStateException("No power-up words loaded");
        }
        // Only allow power-up words at or below the current level's difficulty
        ArrayList<Word> eligible = new ArrayList<>();
        for (Word w : powerUpWords) {
            if (w.getDifficulty().ordinal() <= difficulty.ordinal()) {
                eligible.add(w);
            }
        }
        // Fall back to all power-up words if none qualify at this difficulty
        if (eligible.isEmpty()) {
            eligible = powerUpWords;
        }
        int randomIndex = (int) (Math.random() * eligible.size());
        return eligible.get(randomIndex);
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
