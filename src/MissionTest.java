
import java.time.Duration;
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests the Mission class.
 *
 * @author Rudra
 * @version Oct 2026
 */
public class MissionTest extends TestCase
{
    private Mission mission;

    // ----------------------------------------------------------
    /**
     * Sets up a mission for testing.
     */
    public void setUp()
    {
        mission = new Mission("Collect Fuel", "Fuel Cell", 100);
    }

    // ----------------------------------------------------------
    /**
     * Tests creating a mission successfully.
     */
    public void testConstructor()
    {
        assertEquals("Collect Fuel", mission.getObjective());
        assertEquals("Fuel Cell", mission.getResource());
        assertEquals(100, mission.getReward());
        assertFalse(mission.isCompleted());
    }

    // ----------------------------------------------------------
    /**
     * Tests that an empty objective is rejected.
     */
    public void testEmptyObjective()
    {
        Exception exception = null;

        try
        {
            new Mission("", "Fuel Cell", 100);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests that a null objective is rejected.
     */
    public void testNullObjective()
    {
        Exception exception = null;

        try
        {
            new Mission(null, "Fuel Cell", 100);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests that an empty resource is rejected.
     */
    public void testEmptyResource()
    {
        Exception exception = null;

        try
        {
            new Mission("Collect Fuel", "", 100);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests that a nonpositive reward is rejected.
     */
    public void testInvalidReward()
    {
        Exception exception = null;

        try
        {
            new Mission("Collect Fuel", "Fuel Cell", 0);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }

    // ----------------------------------------------------------
    /**
     * Tests the objective getter.
     */
    public void testGetObjective()
    {
        assertEquals("Collect Fuel", mission.getObjective());
    }

    // ----------------------------------------------------------
    /**
     * Tests the resource getter.
     */
    public void testGetResource()
    {
        assertEquals("Fuel Cell", mission.getResource());
    }

    // ----------------------------------------------------------
    /**
     * Tests the completion status.
     */
    public void testIsCompleted()
    {
        assertFalse(mission.isCompleted());

        mission.complete();

        assertTrue(mission.isCompleted());
    }

    // ----------------------------------------------------------
    /**
     * Tests the reward getter.
     */
    public void testGetReward()
    {
        assertEquals(100, mission.getReward());
    }

    // ----------------------------------------------------------
    /**
     * Tests completing a mission multiple times.
     */
    public void testCompleteMultipleTimes()
    {
        mission.complete();
        mission.complete();

        assertTrue(mission.isCompleted());
    }

    // ----------------------------------------------------------
    /**
     * Tests the default mission duration.
     */
    public void testMissionTimeDefault()
    {
        assertEquals(
            Duration.ofHours(5), mission.MissionTime());
    }

    // ----------------------------------------------------------
    /**
     * Tests the duration for four or more resources.
     */
    public void testMissionTimeManyResources()
    {
        Mission largeMission = new Mission(
            "Collect Samples", "Research Samples", 100, 4);

        assertEquals(
            Duration.ofHours(10), largeMission.MissionTime());
    }

    // ----------------------------------------------------------
    /**
     * Tests that a nonpositive resource count is rejected.
     */
    public void testInvalidResourceCount()
    {
        Exception exception = null;

        try
        {
            new Mission(
                "Collect Fuel", "Fuel Cell", 100, 0);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }
}
