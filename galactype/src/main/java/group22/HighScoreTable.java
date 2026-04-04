package group22;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import group22.interfaces.Savable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maintains a ranked list of high score entries, capped at a configurable maximum.
 * Supports filtering by level and persisting the list to a JSON file.
 */
public class HighScoreTable implements Savable {

    private List<HighScoreEntry> entries;
    private final int maxEntries;
    private final Path dataFilePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructs an empty HighScoreTable backed by the given file path.
     *
     * @param dataFilePath the path to the JSON file used for persistence
     * @param maxEntries   the maximum number of entries to retain (lowest scores are dropped)
     */
    public HighScoreTable(String dataFilePath, int maxEntries) {
        this.dataFilePath = Paths.get(dataFilePath);
        this.maxEntries = maxEntries;
        this.entries = new ArrayList<>();
    }

    /**
     * Adds a new entry and re-sorts the table, trimming to the maximum size if needed.
     *
     * @param username the username of the player
     * @param score    the score to record
     * @param level    the level on which the score was achieved
     */
    public void addEntry(String username, int score, int level) {
        entries.add(new HighScoreEntry(username, score, level));
        entries.sort(Comparator.comparingInt(HighScoreEntry::getScore).reversed());
        if (entries.size() > maxEntries) {
            entries = entries.subList(0, maxEntries);
        }
    }

    /**
     * Returns a copy of all entries currently in the table, in descending score order.
     *
     * @return a {@link List} of all {@link HighScoreEntry} objects
     */
    public List<HighScoreEntry> getTopEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Returns all entries for a specific level, in the table's current sort order.
     *
     * @param level the level number to filter by
     * @return a {@link List} of {@link HighScoreEntry} objects matching the given level
     */
    public List<HighScoreEntry> getTopEntriesForLevel(int level) {
        return entries.stream().filter(e -> e.getLevel() == level).collect(Collectors.toList());
    }

    /**
     * Removes all entries from the table.
     */
    public void reset() {
        entries.clear();
    }

    /**
     * Serializes the current entries to the configured JSON file.
     */
    @Override
    public void save() {
        try {
            if (dataFilePath.getParent() != null) {
                Files.createDirectories(dataFilePath.getParent());
            }
            Files.writeString(dataFilePath, gson.toJson(entries));
        } catch (IOException e) {
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }

    /**
     * Deserializes entries from the configured JSON file, replacing the current list.
     */
    @Override
    public void load() {
        if (!Files.exists(dataFilePath)) return;
        try {
            String json = Files.readString(dataFilePath);
            Type listType = new TypeToken<List<HighScoreEntry>>() {}.getType();
            List<HighScoreEntry> loaded = gson.fromJson(json, listType);
            if (loaded != null) entries = loaded;
        } catch (IOException e) {
            System.err.println("Failed to load high scores: " + e.getMessage());
        }
    }
}
