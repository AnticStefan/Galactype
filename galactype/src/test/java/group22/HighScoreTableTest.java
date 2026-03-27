package group22;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreTableTest {

    @TempDir
    Path tempDir;

    private HighScoreTable table;

    @BeforeEach
    void setUp() {
        table = new HighScoreTable(tempDir.resolve("scores.json").toString(), 5);
    }

    @Test
    void addEntryAppearsInTopEntries() {
        table.addEntry("adam", 1000, 1);
        assertEquals(1, table.getTopEntries().size());
        assertEquals("adam", table.getTopEntries().get(0).getUsername());
    }

    @Test
    void topEntriesAreSortedByScoreDescending() {
        table.addEntry("adam", 500, 1);
        table.addEntry("eric", 1500, 1);
        table.addEntry("eliza", 1000, 1);

        List<HighScoreEntry> top = table.getTopEntries();
        assertEquals(1500, top.get(0).getScore());
        assertEquals(1000, top.get(1).getScore());
        assertEquals(500,  top.get(2).getScore());
    }

    @Test
    void maxEntriesLimitIsEnforced() {
        for (int i = 1; i <= 7; i++) {
            table.addEntry("player" + i, i * 100, 1);
        }
        assertEquals(5, table.getTopEntries().size());
    }

    @Test
    void getTopEntriesForLevelFiltersCorrectly() {
        table.addEntry("adam", 1000, 1);
        table.addEntry("eric",   800,  2);
        table.addEntry("eliza", 600,  1);

        List<HighScoreEntry> level1 = table.getTopEntriesForLevel(1);
        assertEquals(2, level1.size());
        assertTrue(level1.stream().allMatch(e -> e.getLevel() == 1));
    }

    @Test
    void resetClearsAllEntries() {
        table.addEntry("adam", 1000, 1);
        table.reset();
        assertTrue(table.getTopEntries().isEmpty());
    }

    @Test
    void saveAndLoadRoundTrip() {
        table.addEntry("adam", 1000, 1);
        table.addEntry("eric", 500, 2);
        table.save();

        HighScoreTable loaded = new HighScoreTable(tempDir.resolve("scores.json").toString(), 5);
        loaded.load();

        assertEquals(2, loaded.getTopEntries().size());
        assertEquals("adam", loaded.getTopEntries().get(0).getUsername());
    }

    @Test
    void loadFromMissingFileDoesNotCrash() {
        HighScoreTable fresh = new HighScoreTable(tempDir.resolve("nonexistent.json").toString(), 5);
        assertDoesNotThrow(fresh::load);
        assertTrue(fresh.getTopEntries().isEmpty());
    }
}
