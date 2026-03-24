package group22;

import group22.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeacherAccountTest {

    private TeacherAccount teacher;
    private PlayerAccount player;

    @BeforeEach
    void setUp() {
        teacher = new TeacherAccount("mrsmith", "teach123");
        player = new PlayerAccount("bob", "student456");
    }

    @Test
    void accountTypeIsTeacher() {
        assertEquals(AccountType.TEACHER, teacher.getAccountType());
    }

    @Test
    void usernameIsSet() {
        assertEquals("mrsmith", teacher.getUsername());
    }

    @Test
    void viewPlayerStatisticsReturnsSameObject() {
        assertSame(player.getStatistics(), teacher.viewPlayerStatistics(player));
    }

    @Test
    void resetPlayerStatisticsClearsData() {
        player.getStatistics().recordSession(new SessionResult(1, 200, 15, 2, 55f, 88f, 45000L));
        assertFalse(player.getStatistics().getSessionHistory().isEmpty());

        teacher.resetPlayerStatistics(player);

        assertTrue(player.getStatistics().getSessionHistory().isEmpty());
    }

    @Test
    void resetPlayerPasswordAllowsNewPassword() {
        teacher.resetPlayerPassword(player, "newStudentPass");

        assertTrue(player.checkPassword("newStudentPass"));
        assertFalse(player.checkPassword("student456"));
    }
}
