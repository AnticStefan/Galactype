package group22;

import group22.enums.AccountType;

public class TeacherAccount extends Account {

    public TeacherAccount(String username, String password) {
        super(username, password, AccountType.TEACHER);
    }

    public TypingStatistics viewPlayerStatistics(PlayerAccount player) {
        return player.getStatistics();
    }

    public void resetPlayerStatistics(PlayerAccount player) {
        player.resetStatistics();
    }

    public void resetPlayerPassword(PlayerAccount player, String newPassword) {
        player.setPassword(newPassword);
    }
}
