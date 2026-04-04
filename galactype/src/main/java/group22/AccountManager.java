package group22;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import group22.enums.AccountType;
import group22.interfaces.Savable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all user accounts in the Galactype system, including creation,
 * authentication, deletion, and persistence via JSON serialization.
 */
public class AccountManager implements Savable {

    private List<Account> accounts;
    private final Path dataFilePath;
    private Account currentAccount;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructs an AccountManager that reads and writes account data at the given path.
     *
     * @param dataFilePath path to the JSON file used for account persistence
     */
    public AccountManager(String dataFilePath) {
        this.dataFilePath = Paths.get(dataFilePath);
        this.accounts = new ArrayList<>();
    }

    /**
     * Creates and registers a new account if the username is not already taken.
     *
     * @param username the desired username for the new account
     * @param password the plain-text password for the new account
     * @param type the role to assign to the new account
     * @return {@code true} if the account was created; {@code false} if the username is taken
     */
    public boolean createAccount(String username, String password, AccountType type) {
        if (accountExists(username)) return false;
        Account account = (type == AccountType.TEACHER) ? new TeacherAccount(username, password): new PlayerAccount(username, password);
        accounts.add(account);
        return true;
    }

    /**
     * Attempts to authenticate a user with the given credentials.
     *
     * @param username the username to look up
     * @param password the plain-text password to verify
     * @return the matching {@link Account} on success, or {@code null} if credentials are invalid
     */
    public Account login(String username, String password) {
        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.checkPassword(password)) {
                currentAccount = account;
                return account;
            }
        }
        return null;
    }

    /**
     * Clears the currently logged-in account, effectively logging out the user.
     */
    public void logout() {
        currentAccount = null;
    }

    /**
     * Returns the account that is currently logged in.
     *
     * @return the active {@link Account}, or {@code null} if no user is logged in
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     * Removes the account with the given username from the system.
     *
     * @param username the username of the account to delete
     * @return {@code true} if an account was found and removed; {@code false} otherwise
     */
    public boolean deleteAccount(String username) {
        return accounts.removeIf(a -> a.getUsername().equals(username));
    }

    /**
     * Checks whether an account with the given username already exists.
     *
     * @param username the username to search for
     * @return {@code true} if the username is taken; {@code false} otherwise
     */
    public boolean accountExists(String username) {
        return accounts.stream().anyMatch(a -> a.getUsername().equals(username));
    }

    /**
     * Returns a list of all registered player accounts.
     *
     * @return a {@link List} of {@link PlayerAccount} objects
     */
    public List<PlayerAccount> getAllPlayerAccounts() {
        List<PlayerAccount> players = new ArrayList<>();
        for (Account a : accounts) {
            if (a instanceof PlayerAccount) {
                players.add((PlayerAccount) a);
            }
        }
        return players;
    }

    /**
     * Serializes all accounts to the configured JSON file.
     */
    @Override
    public void save() {
        // Split into typed lists so GSON can serialize concrete types cleanly
        AccountData data = new AccountData();
        for (Account a : accounts) {
            if (a instanceof PlayerAccount) {
                data.players.add((PlayerAccount) a);
            } else if (a instanceof TeacherAccount) {
                data.teachers.add((TeacherAccount) a);
            }
        }
        try {
            if (dataFilePath.getParent() != null) {
                Files.createDirectories(dataFilePath.getParent());
            }
            Files.writeString(dataFilePath, gson.toJson(data));
        } catch (IOException e) {
            System.err.println("Failed to save accounts: " + e.getMessage());
        }
    }

    /**
     * Deserializes accounts from the configured JSON file, replacing the current list.
     */
    @Override
    public void load() {
        if (!Files.exists(dataFilePath)) return;
        try {
            String json = Files.readString(dataFilePath);
            AccountData data = gson.fromJson(json, AccountData.class);
            accounts.clear();
            if (data != null) {
                if (data.players != null) accounts.addAll(data.players);
                if (data.teachers != null) accounts.addAll(data.teachers);
            }
        } catch (IOException e) {
            System.err.println("Failed to load accounts: " + e.getMessage());
        }
    }

    // Wrapper used purely for JSON structure: { "players": [...], "teachers": [...] }
    private static class AccountData {
        List<PlayerAccount> players = new ArrayList<>();
        List<TeacherAccount> teachers = new ArrayList<>();
    }
}
