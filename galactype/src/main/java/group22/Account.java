package group22;

import group22.enums.AccountType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract base class representing a user account in the Galactype system.
 * Stores credentials and account type; concrete subclasses define player or teacher behaviour.
 */
public abstract class Account {
    protected String username;
    protected String passwordHash;
    protected AccountType accountType;

    /**
     * Constructs a new Account with the given credentials and type.
     *
     * @param username the unique display name for this account
     * @param password the plain-text password (stored as a SHA-256 hash)
     * @param type     the role assigned to this account
     */
    public Account(String username, String password, AccountType type) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.accountType = type;
    }

    /**
     * Returns the username associated with this account.
     *
     * @return the username string
     */
    public String getUsername() { return username; }

    /**
     * Returns the account type (e.g. PLAYER or TEACHER).
     *
     * @return the {@link AccountType} of this account
     */
    public AccountType getAccountType() { return accountType; }

    /**
     * Verifies whether the given plain-text password matches the stored hash.
     *
     * @param password the plain-text password to check
     * @return {@code true} if the password matches; {@code false} otherwise
     */
    public boolean checkPassword(String password) {
        return hashPassword(password).equals(passwordHash);
    }

    /**
     * Updates the stored password hash to reflect the new password.
     *
     * @param newPassword the new plain-text password to set
     */
    public void setPassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    /**
     * Hashes a plain text password using SHA-256.
     *
     * @param password the plain-text password to hash
     * @return the hexadecimal SHA-256 digest of the password
     */
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
