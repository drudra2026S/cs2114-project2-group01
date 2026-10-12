import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests the WorldBuild class.
 *
 * @author ltstabler
 * @version Oct 12, 2026
 */
public class WorldBuildTest
{
    private WorldBuild world;

    // ----------------------------------------------------------
    /**
     * Sets up a seeded world before each test.
     */
    @BeforeEach
    public void setUp()
    {
        world = new WorldBuild(42);
    }


    // ----------------------------------------------------------
    /**
     * Tests that the world has Earth plus at least 3 planets, and every planet
     * except Earth has a mission.
     */
    @Test
    public void testPlanetsAndMissions()
    {
        assertTrue(world.getPlanetCount() >= 4);

        Planet[] planets = world.getPlanets();
        assertEquals(world.getPlanetCount(), planets.length);

        for (Planet p : planets)
        {
            if (p != world.getEarth())
            {
                assertTrue(
                    world.getMissions(p).length >= 1,
                    p.getName() + " has no missions");
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Tests getting Earth.
     */
    @Test
    public void testGetEarth()
    {
        Planet earth = world.getEarth();
        assertNotNull(earth);
        assertEquals("Earth", earth.getName());
        assertSame(earth, world.getPlanets()[0]);
    }


    // ----------------------------------------------------------
    /**
     * Tests that every planet can be reached from Earth and that every
     * connection goes both ways (so there's always a path back).
     */
    @Test
    public void testEveryPlanetReachable()
    {
        Planet[] planets = world.getPlanets();
        boolean[] visited = new boolean[planets.length];
        Planet[] queue = new Planet[planets.length];
        int head = 0;
        int tail = 0;

        queue[tail++] = world.getEarth();
        visited[0] = true;

        while (head < tail)
        {
            Planet current = queue[head++];

            for (Planet next : world.getConnectedPlanets(current))
            {
                assertTrue(world.isConnected(next, current));

                for (int i = 0; i < planets.length; i++)
                {
                    if (planets[i] == next && !visited[i])
                    {
                        visited[i] = true;
                        queue[tail++] = next;
                    }
                }
            }
        }

        assertEquals(planets.length, tail);
    }


    // ----------------------------------------------------------
    /**
     * Tests finding planets by name, ignoring capitalization.
     */
    @Test
    public void testFindPlanet()
    {
        Planet planet = world.getPlanets()[1];
        String name = planet.getName();

        assertSame(planet, world.findPlanet(name));
        assertSame(planet, world.findPlanet(name.toLowerCase()));
        assertSame(planet, world.findPlanet(name.toUpperCase()));
        assertSame(world.getEarth(), world.findPlanet("earth"));
    }


    // ----------------------------------------------------------
    /**
     * Tests that every world has Earth plus 5 to 8 planets with no repeated
     * names, and every dilation factor is positive.
     */
    @Test
    public void testPlanetCountInRange()
    {
        for (long seed = 0; seed < 50; seed++)
        {
            WorldBuild w = new WorldBuild(seed);
            int others = w.getPlanetCount() - 1;
            assertTrue(
                others >= WorldBuild.MIN_PLANETS
                    && others <= WorldBuild.MAX_PLANETS);

            Planet[] all = w.getPlanets();

            for (int i = 0; i < all.length; i++)
            {
                assertTrue(all[i].getDilate() > 0);

                for (int j = i + 1; j < all.length; j++)
                {
                    assertFalse(all[i].getName().equals(all[j].getName()));
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Tests that the same seed builds the same planets in the same order.
     */
    @Test
    public void testSameSeedSameWorld()
    {
        Planet[] a = new WorldBuild(7).getPlanets();
        Planet[] b = new WorldBuild(7).getPlanets();

        assertEquals(a.length, b.length);

        for (int i = 0; i < a.length; i++)
        {
            assertEquals(a[i].getName(), b[i].getName());
            assertEquals(a[i].getDilate(), b[i].getDilate());
        }
    }


    // ----------------------------------------------------------
    /**
     * Tests that different seeds give different worlds (replayability).
     */
    @Test
    public void testDifferentSeedsDifferentWorlds()
    {
        String first = planetNames(new WorldBuild(1));
        boolean foundDifferent = false;

        for (long seed = 2; seed < 20; seed++)
        {
            if (!planetNames(new WorldBuild(seed)).equals(first))
            {
                foundDifferent = true;
            }
        }

        assertTrue(foundDifferent);
    }


    // ----------------------------------------------------------
    /**
     * Tests that every world, for many seeds, has every planet reachable from
     * Earth.
     */
    @Test
    public void testManySeedsAllReachable()
    {
        for (long seed = 0; seed < 50; seed++)
        {
            world = new WorldBuild(seed);
            testEveryPlanetReachable();
            testPlanetsAndMissions();
        }
    }


    /**
     * Joins a world's planet names into one string.
     */
    private String planetNames(WorldBuild w)
    {
        StringBuilder names = new StringBuilder();

        for (Planet p : w.getPlanets())
        {
            names.append(p.getName()).append(",");
        }

        return names.toString();
    }


    // ----------------------------------------------------------
    /**
     * Tests that bad names return null without throwing.
     */
    @Test
    public void testFindPlanetMissing()
    {
        assertNull(world.findPlanet("Pluto"));
        assertNull(world.findPlanet(null));
        assertNull(world.findPlanet(""));
    }


    // ----------------------------------------------------------
    /**
     * Tests connections for a planet that isn't in the world.
     */
    @Test
    public void testConnectionsUnknownPlanet()
    {
        Planet pluto = new Planet("Pluto", 1.0);
        assertEquals(0, world.getConnectedPlanets(pluto).length);
        assertEquals(0, world.getMissions(pluto).length);
        assertFalse(world.isConnected(world.getEarth(), pluto));
        assertFalse(world.isConnected(null, world.getEarth()));
    }


    // ----------------------------------------------------------
    /**
     * Tests mission counts in a new world.
     */
    @Test
    public void testNewWorldMissions()
    {
        assertFalse(world.allMissionsCompleted());
        assertEquals(0, world.getCompletedMissionCount());
        assertTrue(world.getTotalMissionCount() > 0);
    }


    // ----------------------------------------------------------
    /**
     * Tests completing every mission, and all but one.
     */
    @Test
    public void testCompletingMissions()
    {
        int total = world.getTotalMissionCount();
        Mission skipped = null;

        for (Planet p : world.getPlanets())
        {
            for (Mission m : world.getMissions(p))
            {
                if (skipped == null)
                {
                    skipped = m;
                }
                else
                {
                    m.complete();
                }
            }
        }

        assertFalse(world.allMissionsCompleted());
        assertEquals(total - 1, world.getCompletedMissionCount());

        skipped.complete();
        assertTrue(world.allMissionsCompleted());
        assertEquals(total, world.getCompletedMissionCount());
    }


    // ----------------------------------------------------------
    /**
     * Tests the event pool frequencies and that random events are never null.
     */
    @Test
    public void testRandomEventNeverNull()
    {
        assertEquals(5, world.getEventFrequency("Safe Travel"));
        assertEquals(10, world.getEventPoolSize());

        for (int i = 0; i < 100; i++)
        {
            Event e = world.getRandomEvent();
            assertNotNull(e);
            assertTrue(world.getEventFrequency(e.getEventType()) > 0);
        }
    }


    // ----------------------------------------------------------
    /**
     * Tests that the same seed gives the same events.
     */
    @Test
    public void testSameSeedSameEvents()
    {
        WorldBuild a = new WorldBuild(42);
        WorldBuild b = new WorldBuild(42);

        for (int i = 0; i < 10; i++)
        {
            assertEquals(
                a.getRandomEvent().getEventType(),
                b.getRandomEvent().getEventType());
        }
    }


    // ----------------------------------------------------------
    /**
     * Tests that Safe Travel comes up about half the time.
     */
    @Test
    public void testSafeTravelAboutHalf()
    {
        int safe = 0;
        int draws = 10000;

        for (int i = 0; i < draws; i++)
        {
            if (world.getRandomEvent().getEventType().equals("Safe Travel"))
            {
                safe++;
            }
        }

        double rate = (double)safe / draws;
        assertTrue(rate > 0.47 && rate < 0.53, "rate was " + rate);
    }


    // ----------------------------------------------------------
    /**
     * Tests that the no-argument constructor builds a full world.
     */
    @Test
    public void testRandomSeedConstructor()
    {
        WorldBuild random = new WorldBuild();
        int others = random.getPlanetCount() - 1;
        assertTrue(
            others >= WorldBuild.MIN_PLANETS
                && others <= WorldBuild.MAX_PLANETS);
        assertNotNull(random.getRandomEvent());
    }
}
