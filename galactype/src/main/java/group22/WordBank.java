package group22;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import group22.enums.DifficultyLevel;
import group22.enums.PowerUpType;

/**
 * Loads and stores the game's vocabulary, organized by difficulty tier and powerup status.
 * Provides random word selection for use during game sessions.
 */
public class WordBank {
    private ArrayList<String> easyWords = new ArrayList<>();
    private ArrayList<String> mediumWords = new ArrayList<>();
    private ArrayList<String> hardWords = new ArrayList<>();
    private ArrayList<String> doomWords = new ArrayList<>();
    private ArrayList<Word> powerUpWords = new ArrayList<>();
    private String dataFilePath;

    /**
     * Constructs a WordBank that will read word data from the given file path.
     * Call {@link #load()} before using any word retrieval methods.
     *
     * @param dataFilePath the path to the JSON word bank data file
     */
    public WordBank(String dataFilePath){
        this.dataFilePath = dataFilePath;
    }

    /**
     * Clears all existing words and reloads the word bank from the JSON data file.
     *
     * @throws RuntimeException if the file cannot be read
     */
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

    /**
     * Clears all existing words and reloads the word bank from the given InputStream.
     * Use this when running from a JAR where the word bank is a bundled resource.
     *
     * @param is an InputStream for the word bank JSON data
     */
    public void load(InputStream is) {
        easyWords.clear();
        mediumWords.clear();
        hardWords.clear();
        doomWords.clear();
        powerUpWords.clear();

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            loadSection(root, "easy", DifficultyLevel.EASY, easyWords);
            loadSection(root, "medium", DifficultyLevel.MEDIUM, mediumWords);
            loadSection(root, "hard", DifficultyLevel.HARD, hardWords);
            loadSection(root, "doom", DifficultyLevel.DOOM, doomWords);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load word bank from stream", e);
        }
    }

    /**
     * Parses a single difficulty section from the JSON root object, adding plain words to
     * {@code targetWords} and any powerup words to the shared powerup list.
     *
     * @param root        the root JSON object of the word bank file
     * @param sectionName the JSON key for the difficulty section (e.g. "easy")
     * @param difficulty  the {@link DifficultyLevel} to assign to words in this section
     * @param targetWords the list to add plain word strings into
     */
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

            // If it is a powerup word, also store it in the powerUpWords list
            if (isPowerUp) {

                // Get the powerup type from the JSON if it exists
                String powerUpTypeString = null;

                if (wordObject.has("powerUpType")) {
                    powerUpTypeString = wordObject.get("powerUpType").getAsString();
                }

                // Convert the JSON string into the PowerUpType enum used in Java
                PowerUpType powerUpType = parsePowerUpType(powerUpTypeString);

                // Only create the Word object if the powerup type was valid
                if (powerUpType != null) {
                    powerUpWords.add(new Word(text, difficulty, powerUpType));
                }
            }
        }
    }

    /**
     * Converts a raw string from JSON into the matching {@link PowerUpType} enum constant.
     * Returns {@code null} if the string is blank or does not match any known type.
     *
     * @param powerUpTypeString the raw string value from the JSON file
     * @return the matching {@link PowerUpType}, or {@code null} if invalid or missing
     */
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

    /**
     * Returns a single random word at the specified difficulty level.
     *
     * @param difficulty the {@link DifficultyLevel} of the word to retrieve
     * @return a randomly selected {@link Word} at the given difficulty
     * @throws IllegalStateException if no words are loaded for the given difficulty
     */
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

    /**
     * Returns a random powerup word at or below the specified difficulty level.
     * Falls back to all powerup words if none qualify at the given difficulty.
     *
     * @param difficulty the maximum {@link DifficultyLevel} to consider for eligible powerup words
     * @return a randomly selected powerup {@link Word}
     * @throws IllegalStateException if no powerup words have been loaded
     */
    public Word getPowerUpWord(DifficultyLevel difficulty){
        if (powerUpWords.isEmpty()) {
            throw new IllegalStateException("No powerup words loaded");
        }
        // Only allow powerup words at or below the current level's difficulty
        ArrayList<Word> eligible = new ArrayList<>();
        for (Word w : powerUpWords) {
            if (w.getDifficulty().ordinal() <= difficulty.ordinal()) {
                eligible.add(w);
            }
        }
        // Fall back to all powerup words if none qualify at this difficulty
        if (eligible.isEmpty()) {
            eligible = powerUpWords;
        }
        int randomIndex = (int) (Math.random() * eligible.size());
        return eligible.get(randomIndex);
    }

    /**
     * Returns the number of words loaded for the specified difficulty level.
     *
     * @param difficulty the {@link DifficultyLevel} to count words for
     * @return the number of words in that difficulty tier
     * @throws IllegalStateException if the difficulty value is unrecognized
     */
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
