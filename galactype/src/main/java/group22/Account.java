package group22;

import group22.enums.AccountType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Account {
    protected String username;
    protected String passwordHash;
    protected AccountType accountType;

    public Account(String username, String password, AccountType type) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.accountType = type;
    }

    public String getUsername() { return username; }
    public AccountType getAccountType() { return accountType; }

    public boolean checkPassword(String password) {
        return hashPassword(password).equals(passwordHash);
    }

    public void setPassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    // Small security implementaiton 
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in Java
            throw new RuntimeException(e);
        }
    }
}
