package group22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import group22.enums.AccountType;

/**
 * Handles reading and writing of all persistent game data, including accounts,
 * high scores, and per-player progress files, using a base directory on disk.
 */
public class DataManager {

    private final String basePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructs a DataManager that reads and writes files under the given base directory.
     *
     * @param basepath the root directory path for all data files
     */
    // Stores the base directory used for reading and writing game data
    public DataManager(String basepath){
        this.basePath = basepath;
    }

    /**
     * Serializes the given list of accounts and writes it to the accounts data file.
     *
     * @param accounts the list of {@link Account} objects to save
     */
    // Saves all account records to the accounts data file
    public void saveAccounts(List<Account> accounts){
        Path accountsPath = Paths.get(basePath, "accounts.json");
        try {
            if (accountsPath.getParent() != null) {
                Files.createDirectories(accountsPath.getParent());
            }
            Files.writeString(accountsPath, gson.toJson(accounts));
        } catch (IOException e) {
            System.err.println("Failed to save accounts: " + e.getMessage());
        }
    }

    /**
     * Reads the accounts data file and reconstructs the concrete account objects.
     *
     * @return a {@link List} of {@link Account} objects loaded from disk; empty if the file does not exist
     */
    // Loads all saved accounts and rebuilds their concrete account types
    public List<Account> loadAccounts(){
        Path accountsPath = Paths.get(basePath, "accounts.json");
        List<Account> accounts = new ArrayList<>();

        if (!Files.exists(accountsPath)) {
            return accounts;
        }

        try {
            String json = Files.readString(accountsPath);
            JsonArray jsonAccounts = gson.fromJson(json, JsonArray.class);

            if (jsonAccounts == null) {
                return accounts;
            }

            for (JsonElement element : jsonAccounts) {
                JsonObject accountObject = element.getAsJsonObject();
                AccountType accountType = AccountType.valueOf(accountObject.get("accountType").getAsString());

                if (accountType == AccountType.PLAYER) {
                    accounts.add(gson.fromJson(accountObject, PlayerAccount.class));
                } else if (accountType == AccountType.TEACHER) {
                    accounts.add(gson.fromJson(accountObject, TeacherAccount.class));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load accounts: " + e.getMessage());
        }

        return accounts;
    }

    /**
     * Serializes and writes the entries from the given high score table to disk.
     *
     * @param table the {@link HighScoreTable} whose entries should be saved
     */
    // Saves the high score table entries to disk
    public void saveHighScores(HighScoreTable table){
        Path highScoresPath = Paths.get(basePath, "highscores.json");
        try {
            if (highScoresPath.getParent() != null) {
                Files.createDirectories(highScoresPath.getParent());
            }
            Files.writeString(highScoresPath, gson.toJson(table.getTopEntries()));
        } catch (IOException e) {
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }

    /**
     * Reads the high scores file and returns a populated {@link HighScoreTable}.
     *
     * @return a {@link HighScoreTable} loaded from disk; empty table if the file does not exist
     */
    // Loads saved high score entries into a new high score table
    public HighScoreTable loadHighScoreTable(){
        Path highScoresPath = Paths.get(basePath, "highscores.json");
        HighScoreTable table = new HighScoreTable(highScoresPath.toString(), 100);

        if (!Files.exists(highScoresPath)) {
            return table;
        }

        try {
            String json = Files.readString(highScoresPath);
            Type listType = new TypeToken<List<HighScoreEntry>>(){}.getType();
            List<HighScoreEntry> entries = gson.fromJson(json, listType);
            if (entries != null) {
                for (HighScoreEntry entry : entries) {
                    table.addEntry(entry.getUsername(), entry.getScore(), entry.getLevel());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load high scores: " + e.getMessage());
        }

        return table;
    }

    /**
     * Serializes and writes the given player's progress to a per-player file.
     *
     * @param player the {@link PlayerAccount} whose progress should be saved
     */
    // Saves one player's progress into that player's own file
    public void savePlayerProgress(PlayerAccount player){
        Path playerProgressPath = Paths.get(basePath, "player-progress", player.getUsername() + ".json");
        try {
            if (playerProgressPath.getParent() != null) {
                Files.createDirectories(playerProgressPath.getParent());
            }
            Files.writeString(playerProgressPath, toJson(player));
        } catch (IOException e) {
            System.err.println("Failed to save player progress: " + e.getMessage());
        }
    }

    /**
     * Reads and returns the saved progress for the player with the given username.
     *
     * @param username the username whose progress file should be loaded
     * @return the deserialized {@link PlayerAccount}, or {@code null} if no file exists
     */
    // Loads the saved progress file for a specific player
    public PlayerAccount loadPlayerProgress(String username){
        Path playerProgressPath = Paths.get(basePath, "player-progress", username + ".json");

        if (!Files.exists(playerProgressPath)) {
            return null;
        }

        try {
            String json = Files.readString(playerProgressPath);
            return (PlayerAccount) fromJson(json, PlayerAccount.class);
        } catch (IOException e) {
            System.err.println("Failed to load player progress: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts the given object to its JSON string representation.
     *
     * @param object the object to serialize
     * @return the JSON string for the object
     */
    // Converts an object into its JSON representation
    private String toJson(Object object){
        return gson.toJson(object);
    }

    /**
     * Deserializes a JSON string into an instance of the specified class.
     *
     * @param json the JSON string to parse
     * @param type the target class to deserialize into
     * @return the deserialized object
     */
    // Rebuilds an object from JSON using the requested class type
    private Object fromJson(String json, Class<?> type){
        return gson.fromJson(json, type);
    }

}
