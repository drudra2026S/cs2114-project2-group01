
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests the Event class.
 *
 * @author Rudra Dutta (drudra) 
 * @version 2026.10.11
 */
public class EventTest extends TestCase
{
    private Event event;

    // ----------------------------------------------------------
    /**
     * Sets up an event for testing.
     */
    public void setUp()
    {
        event = new Event("Asteroid Field", 20);
    }

    // ----------------------------------------------------------
    /**
     * Tests creating an event successfully.
     */
    public void testConstructor()
    {
        assertEquals("Asteroid Field", event.getEventType());
        assertEquals(20, event.getDamage());
    }

    // ----------------------------------------------------------
    /**
     * Tests that an empty event type is rejected.
     */
    public void testEmptyEventType()
    {
        Exception exception = null;

        try
        {
            new Event("", 20);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests that a null event type is rejected.
     */
    public void testNullEventType()
    {
        Exception exception = null;

        try
        {
            new Event(null, 20);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests that zero damage is rejected.
     */
    public void testZeroDamage()
    {
        Exception exception = null;

        try
        {
            new Event("Asteroid Field", 0);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests that negative damage is rejected.
     */
    public void testNegativeDamage()
    {
        Exception exception = null;

        try
        {
            new Event("Asteroid Field", -5);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests the event type getter.
     */
    public void testGetEventType()
    {
        assertEquals("Asteroid Field", event.getEventType());
    }

    // ----------------------------------------------------------
    /**
     * Tests the damage getter.
     */
    public void testGetDamage()
    {
        assertEquals(20, event.getDamage());
    }

    // ----------------------------------------------------------
    /**
     * Tests that an asteroid field damages the ship.
     */
    public void testApplyEffect()
    {
        // Update this constructor if Planet.java uses
        // different constructor parameters.
        Planet earth = new Planet("Earth", 1.0);
        Spaceship ship = new Spaceship(earth);

        event.applyEffect(ship);

        assertEquals(80, ship.getHealth());
        assertEquals(20, ship.getTotalDamage());
    }

    // ----------------------------------------------------------
    /**
     * Tests that safe travel ignores its stored damage.
     */
    public void testSafeTravel()
    {
        Event safeTravel = new Event("Safe Travel", 20);
        Planet earth = new Planet("Earth", 1.0);
        Spaceship ship = new Spaceship(earth);

        safeTravel.applyEffect(ship);

        assertEquals(100, ship.getHealth());
        assertEquals(0, ship.getTotalDamage());
        assertTrue(ship.canTravel());
    }

    // ----------------------------------------------------------
    /**
     * Tests that engine failure damages the ship
     * and disables its engine.
     */
    public void testEngineFailure()
    {
        Event engineFailure = new Event("Engine Failure", 15);
        Planet earth = new Planet("Earth", 1.0);
        Spaceship ship = new Spaceship(earth);

        engineFailure.applyEffect(ship);

        assertEquals(85, ship.getHealth());
        assertEquals(15, ship.getTotalDamage());
        assertFalse(ship.canTravel());
    }

    // ----------------------------------------------------------
    /**
     * Tests applying an event to a null ship.
     */
    public void testApplyEffectNullShip()
    {
        event.applyEffect(null);

        assertEquals(20, event.getDamage());
    }
}
