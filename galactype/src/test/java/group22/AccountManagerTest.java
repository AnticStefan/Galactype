package group22;

import group22.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountManagerTest {

    private AccountManager manager;

    @BeforeEach
    void setUp() {
        manager = new AccountManager("target/test-accounts.json");
    }

    @Test
    void createAccountSucceeds() {
        assertTrue(manager.createAccount("Eliza", "pass1", AccountType.PLAYER));
    }

    @Test
    void createDuplicateAccountFails() {
        manager.createAccount("adam", "pass1", AccountType.PLAYER);
        assertFalse(manager.createAccount("adam", "pass2", AccountType.PLAYER));
    }

    @Test
    void loginWithCorrectCredentials() {
        manager.createAccount("adam", "pass1", AccountType.PLAYER);
        Account account = manager.login("adam", "pass1");
        assertNotNull(account);
        assertEquals("adam", account.getUsername());
    }

    @Test
    void loginWithWrongPasswordReturnsNull() {
        manager.createAccount("adam", "pass1", AccountType.PLAYER);
        assertNull(manager.login("adam", "wrongpass"));
    }

    @Test
    void loginSetCurrentAccount() {
        manager.createAccount("adam", "pass1", AccountType.PLAYER);
        manager.login("adam", "pass1");
        assertNotNull(manager.getCurrentAccount());
        assertEquals("adam", manager.getCurrentAccount().getUsername());
    }

    @Test
    void logoutClearsCurrentAccount() {
        manager.createAccount("adam", "pass1", AccountType.PLAYER);
        manager.login("adam", "pass1");
        manager.logout();
        assertNull(manager.getCurrentAccount());
    }

    @Test
    void accountExistsReturnsTrueAfterCreate() {
        manager.createAccount("eric", "pass", AccountType.TEACHER);
        assertTrue(manager.accountExists("eric"));
        assertFalse(manager.accountExists("nobody"));
    }

    @Test
    void deleteAccountRemovesIt() {
        manager.createAccount("adam", "pass1", AccountType.PLAYER);
        assertTrue(manager.deleteAccount("adam"));
        assertFalse(manager.accountExists("adam"));
    }

    @Test
    void deleteNonExistentAccountReturnsFalse() {
        assertFalse(manager.deleteAccount("ghost"));
    }

    @Test
    void getAllPlayerAccountsOnlyReturnsPlayers() {
        manager.createAccount("player1", "p1", AccountType.PLAYER);
        manager.createAccount("player2", "p2", AccountType.PLAYER);
        manager.createAccount("teacher1", "t1", AccountType.TEACHER);

        List<PlayerAccount> players = manager.getAllPlayerAccounts();
        assertEquals(2, players.size());
        assertTrue(players.stream().allMatch(p -> p.getAccountType() == AccountType.PLAYER));
    }

    @Test
    void saveAndLoadRoundTrip(@TempDir Path tempDir) {
        AccountManager original = new AccountManager(tempDir.resolve("accounts.json").toString());
        original.createAccount("adam", "pass1", AccountType.PLAYER);
        original.createAccount("mrsmith", "teach1", AccountType.TEACHER);
        original.save();

        AccountManager loaded = new AccountManager(tempDir.resolve("accounts.json").toString());
        loaded.load();

        assertTrue(loaded.accountExists("adam"));
        assertTrue(loaded.accountExists("mrsmith"));
        assertNotNull(loaded.login("adam", "pass1"));
        assertNotNull(loaded.login("mrsmith", "teach1"));
    }

    @Test
    void loadFromMissingFileDoesNotCrash() {
        AccountManager fresh = new AccountManager("target/nonexistent/accounts.json");
        assertDoesNotThrow(fresh::load);
    }
}
