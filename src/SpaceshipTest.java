import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests the Spaceship class.
 *
 * @author ltstabler
 * @version Oct 12, 2026
 */
public class SpaceshipTest
{
    private WorldBuild world;
    private Planet earth;
    private Planet mars;   // a planet connected to Earth
    private Planet europa; // a planet in the world NOT connected to Earth
    private Spaceship ship;

    // ----------------------------------------------------------
    /**
     * Sets up a seeded world and a ship at Earth before each test. The world is
     * random, so the test picks one planet that is connected to Earth and one
     * that isn't.
     */
    @BeforeEach
    public void setUp()
    {
        world = new WorldBuild(42);
        earth = world.getEarth();
        mars = world.getConnectedPlanets(earth)[0];
        europa = null;

        for (Planet p : world.getPlanets())
        {
            if (p != earth && !world.isConnected(earth, p))
            {
                europa = p;
            }
        }

        assertNotNull(europa, "seed 42 should have a planet not next to Earth");
        ship = new Spaceship(earth, world);
    }


    // ----------------------------------------------------------
    /**
     * Tests that a new ship starts at full health with a working engine.
     */
    @Test
    public void testConstructor()
    {
        assertEquals(Spaceship.MAX_HEALTH, ship.getHealth());
        assertEquals(0, ship.getTotalDamage());
        assertSame(earth, ship.getCurrentLocation());
        assertTrue(ship.canTravel());
        assertTrue(ship.isEngineWorking());
        assertFalse(ship.isDestroyed());
        assertEquals(Spaceship.START_TIME, ship.getTimeLeft());
    }


    // ----------------------------------------------------------
    /**
     * Tests that a null starting planet is rejected.
     */
    @Test
    public void testConstructorNullStart()
    {
        assertThrows(IllegalArgumentException.class, () -> new Spaceship(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> new Spaceship(null, world));
    }


    // ----------------------------------------------------------
    /**
     * Tests taking damage, including going past 0 health.
     */
    @Test
    public void testTakeDamage()
    {
        ship.takeDamage(30);
        assertEquals(70, ship.getHealth());
        assertEquals(30, ship.getTotalDamage());

        ship.takeDamage(150);
        assertEquals(0, ship.getHealth());
        assertTrue(ship.isDestroyed());
        assertEquals(180, ship.getTotalDamage());
    }


    // ----------------------------------------------------------
    /**
     * Tests that negative damage is rejected and health doesn't change.
     */
    @Test
    public void testTakeNegativeDamage()
    {
        assertThrows(IllegalArgumentException.class, () -> ship.takeDamage(-5));
        assertEquals(Spaceship.MAX_HEALTH, ship.getHealth());
        assertEquals(0, ship.getTotalDamage());
    }


    // ----------------------------------------------------------
    /**
     * Tests that repairing never goes over MAX_HEALTH.
     */
    @Test
    public void testRepairCapsAtMax()
    {
        ship.takeDamage(20);
        ship.repair(50);
        assertEquals(Spaceship.MAX_HEALTH, ship.getHealth());

        ship.takeDamage(40);
        ship.repair(10);
        assertEquals(70, ship.getHealth());
    }


    // ----------------------------------------------------------
    /**
     * Tests that a destroyed ship can't be repaired.
     */
    @Test
    public void testRepairDestroyedShip()
    {
        ship.takeDamage(200);
        ship.repair(50);
        assertEquals(0, ship.getHealth());
        assertTrue(ship.isDestroyed());
    }


    // ----------------------------------------------------------
    /**
     * Tests that a negative repair amount is rejected.
     */
    @Test
    public void testRepairNegative()
    {
        assertThrows(IllegalArgumentException.class, () -> ship.repair(-1));
    }


    // ----------------------------------------------------------
    /**
     * Tests traveling to a connected planet.
     */
    @Test
    public void testTravelToConnected()
    {
        assertTrue(ship.travelTo(mars));
        assertSame(mars, ship.getCurrentLocation());
    }


    // ----------------------------------------------------------
    /**
     * Tests that the ship won't travel to an unconnected planet.
     */
    @Test
    public void testTravelToNotConnected()
    {
        assertFalse(ship.travelTo(europa));
        assertSame(earth, ship.getCurrentLocation());

        Planet neptune = new Planet("Neptune", 3.0);
        assertFalse(ship.travelTo(neptune));
        assertSame(earth, ship.getCurrentLocation());
    }


    // ----------------------------------------------------------
    /**
     * Tests that traveling to null returns false without throwing.
     */
    @Test
    public void testTravelToNull()
    {
        assertFalse(ship.travelTo(null));
        assertSame(earth, ship.getCurrentLocation());
    }


    // ----------------------------------------------------------
    /**
     * Tests that traveling to the planet the ship is on returns false.
     */
    @Test
    public void testTravelToCurrentPlanet()
    {
        assertFalse(ship.travelTo(earth));
    }


    // ----------------------------------------------------------
    /**
     * Tests that a ship with no world can travel to any other planet.
     */
    @Test
    public void testTravelWithoutWorld()
    {
        Spaceship freeShip = new Spaceship(earth);
        assertTrue(freeShip.travelTo(europa));
        assertSame(europa, freeShip.getCurrentLocation());
    }


    // ----------------------------------------------------------
    /**
     * Tests disabling and fixing the engine.
     */
    @Test
    public void testDisableAndFixEngine()
    {
        ship.disableEngine();
        assertFalse(ship.canTravel());
        assertFalse(ship.travelTo(mars));
        assertSame(earth, ship.getCurrentLocation());

        ship.fixEngine();
        assertTrue(ship.canTravel());
        assertTrue(ship.travelTo(mars));
    }


    // ----------------------------------------------------------
    /**
     * Tests that a destroyed ship can't travel.
     */
    @Test
    public void testDestroyedCannotTravel()
    {
        ship.takeDamage(Spaceship.MAX_HEALTH);
        assertFalse(ship.canTravel());
        assertFalse(ship.travelTo(mars));
    }


    // ----------------------------------------------------------
    /**
     * Tests spending time, which never goes below zero.
     */
    @Test
    public void testSpendTime()
    {
        ship.spendTime(Duration.ofHours(30));
        assertEquals(Duration.ofHours(70), ship.getTimeLeft());
        assertFalse(ship.isOutOfTime());

        ship.spendTime(Duration.ofHours(500));
        assertEquals(Duration.ZERO, ship.getTimeLeft());
        assertTrue(ship.isOutOfTime());
    }


    // ----------------------------------------------------------
    /**
     * Tests that null or negative time is rejected.
     */
    @Test
    public void testSpendTimeInvalid()
    {
        assertThrows(
            IllegalArgumentException.class,
            () -> ship.spendTime(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> ship.spendTime(Duration.ofHours(-1)));
        assertEquals(Spaceship.START_TIME, ship.getTimeLeft());
    }


    // ----------------------------------------------------------
    /**
     * Tests the status summary.
     */
    @Test
    public void testToString()
    {
        ship.disableEngine();
        String status = ship.toString();
        assertTrue(status.contains("Health: 100/100"));
        assertTrue(status.contains("Location: Earth"));
        assertTrue(status.contains("Engine: Broken"));
        assertTrue(status.contains("Time left: 100 hours"));
    }


    // ----------------------------------------------------------
    /**
     * Tests that events from Rudra's Event class affect the ship.
     */
    @Test
    public void testEventsAffectShip()
    {
        new Event("Engine Failure", 15).applyEffect(ship);
        assertEquals(85, ship.getHealth());
        assertFalse(ship.canTravel());
    }
}
