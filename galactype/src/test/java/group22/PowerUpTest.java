package group22;

import group22.enums.PowerUpType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {

    private PowerUp powerUp;

    @BeforeEach
    void setUp() {
        powerUp = new PowerUp(PowerUpType.SLOW_METEORS, 1000L);
    }

    @Test
    void getTypeReturnsCorrectType() {
        assertEquals(PowerUpType.SLOW_METEORS, powerUp.getType());
    }

    @Test
    void initiallyNotActive() {
        assertFalse(powerUp.isActive());
    }

    @Test
    void initiallyExpired() {
        assertTrue(powerUp.isExpired());
    }

    @Test
    void activateMakesActive() {
        powerUp.activate();
        assertTrue(powerUp.isActive());
    }

    @Test
    void activateMakesNotExpired() {
        powerUp.activate();
        assertFalse(powerUp.isExpired());
    }

    @Test
    void deactivateAfterActivateMakesInactive() {
        powerUp.activate();
        powerUp.deactivate();
        assertFalse(powerUp.isActive());
    }

    @Test
    void getRemainingMSIsZeroWhenNotActive() {
        assertEquals(0, powerUp.getRemainingMS());
    }

    @Test
    void getRemainingMSIsPositiveWhenActive() {
        powerUp.activate();
        assertTrue(powerUp.getRemainingMS() > 0);
    }

    @Test
    void getRemainingMSIsZeroAfterDeactivate() {
        powerUp.activate();
        powerUp.deactivate();
        assertEquals(0, powerUp.getRemainingMS());
    }

    @Test
    void isActiveReturnsFalseAfterDurationExpires() throws InterruptedException {
        PowerUp shortPowerUp = new PowerUp(PowerUpType.FREEZE_METEORS, 50L);
        shortPowerUp.activate();
        assertTrue(shortPowerUp.isActive());
        Thread.sleep(100);
        assertFalse(shortPowerUp.isActive());
    }

    @Test
    void isExpiredReturnsTrueAfterDurationExpires() throws InterruptedException {
        PowerUp shortPowerUp = new PowerUp(PowerUpType.SHIELD, 50L);
        shortPowerUp.activate();
        Thread.sleep(100);
        assertTrue(shortPowerUp.isExpired());
    }

    @Test
    void getRemainingMSIsZeroAfterDurationExpires() throws InterruptedException {
        PowerUp shortPowerUp = new PowerUp(PowerUpType.EXTRA_LIFE, 50L);
        shortPowerUp.activate();
        Thread.sleep(100);
        assertEquals(0, shortPowerUp.getRemainingMS());
    }

    @Test
    void differentTypesAreStoredCorrectly() {
        for (PowerUpType type : PowerUpType.values()) {
            PowerUp p = new PowerUp(type, 500L);
            assertEquals(type, p.getType());
        }
    }

    @Test
    void canReactivateAfterDeactivation() {
        powerUp.activate();
        powerUp.deactivate();
        powerUp.activate();
        assertTrue(powerUp.isActive());
    }
}
