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

public class AccountManager implements Savable {

    private List<Account> accounts;
    private final Path dataFilePath;
    private Account currentAccount;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public AccountManager(String dataFilePath) {
        this.dataFilePath = Paths.get(dataFilePath);
        this.accounts = new ArrayList<>();
    }

    public boolean createAccount(String username, String password, AccountType type) {
        if (accountExists(username)) return false;
        Account account = (type == AccountType.TEACHER) ? new TeacherAccount(username, password): new PlayerAccount(username, password);
        accounts.add(account);
        return true;
    }

    public Account login(String username, String password) {
        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.checkPassword(password)) {
                currentAccount = account;
                return account;
            }
        }
        return null;
    }

    public void logout() {
        currentAccount = null;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public boolean deleteAccount(String username) {
        return accounts.removeIf(a -> a.getUsername().equals(username));
    }

    public boolean accountExists(String username) {
        return accounts.stream().anyMatch(a -> a.getUsername().equals(username));
    }

    public List<PlayerAccount> getAllPlayerAccounts() {
        List<PlayerAccount> players = new ArrayList<>();
        for (Account a : accounts) {
            if (a instanceof PlayerAccount) {
                players.add((PlayerAccount) a);
            }
        }
        return players;
    }

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
