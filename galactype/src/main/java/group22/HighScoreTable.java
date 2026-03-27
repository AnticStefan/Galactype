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

public class HighScoreTable implements Savable {

    private List<HighScoreEntry> entries;
    private final int maxEntries;
    private final Path dataFilePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HighScoreTable(String dataFilePath, int maxEntries) {
        this.dataFilePath = Paths.get(dataFilePath);
        this.maxEntries = maxEntries;
        this.entries = new ArrayList<>();
    }

    public void addEntry(String username, int score, int level) {
        entries.add(new HighScoreEntry(username, score, level));
        entries.sort(Comparator.comparingInt(HighScoreEntry::getScore).reversed());
        if (entries.size() > maxEntries) {
            entries = entries.subList(0, maxEntries);
        }
    }

    public List<HighScoreEntry> getTopEntries() {
        return new ArrayList<>(entries);
    }

    public List<HighScoreEntry> getTopEntriesForLevel(int level) {
        return entries.stream().filter(e -> e.getLevel() == level).collect(Collectors.toList());
    }

    public void reset() {
        entries.clear();
    }

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
