package group22;

import group22.enums.AccountType;

/**
 * Represents a teacher account with administrative privileges over player accounts,
 * including the ability to view statistics and reset player passwords or progress.
 */
public class TeacherAccount extends Account {

    /**
     * Constructs a new TeacherAccount with the given credentials.
     *
     * @param username the unique username for this teacher
     * @param password the plain text password (stored as a hash via {@link Account})
     */
    public TeacherAccount(String username, String password) {
        super(username, password, AccountType.TEACHER);
    }

    /**
     * Returns the typing statistics for the given player account.
     *
     * @param player the {@link PlayerAccount} whose statistics to retrieve
     * @return the {@link TypingStatistics} for that player
     */
    public TypingStatistics viewPlayerStatistics(PlayerAccount player) {
        return player.getStatistics();
    }

    /**
     * Resets all cumulative typing statistics for the given player account.
     *
     * @param player the {@link PlayerAccount} whose statistics should be cleared
     */
    public void resetPlayerStatistics(PlayerAccount player) {
        player.resetStatistics();
    }

    /**
     * Sets a new password on the given player account.
     *
     * @param player      the {@link PlayerAccount} whose password should be changed
     * @param newPassword the new plain text password to assign
     */
    public void resetPlayerPassword(PlayerAccount player, String newPassword) {
        player.setPassword(newPassword);
    }
}
