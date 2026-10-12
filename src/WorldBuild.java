import java.util.Random;

// -------------------------------------------------------------------------
/**
 * Creates and stores the game world: the planets, the missions on each planet,
 * which planets connect to each other, and the pool of random events used
 * during travel. Every run builds a different world. Earth is always the start
 * and finish, and 5 to 8 other planets are picked at random from a catalog of
 * 14. Each picked planet gets a slightly randomized time dilation, 1 or 2
 * random missions, and a random set of routes. The routes always form a loop
 * through every planet (so every planet is reachable and there is always a way
 * back to Earth), plus a few random shortcuts. WorldBuild(long seed) builds the
 * same world every time for a given seed, so tests are repeatable. Everything
 * is stored in FourWayLink chains, linked left to right. Because Planet does
 * not store its own missions or connections, each planet is wrapped in a
 * PlanetEntry that holds them.
 *
 * @author ltstabler
 * @version Oct 12, 2026
 */
public class WorldBuild
{
    /** Fewest non-Earth planets in a world. */
    public static final int MIN_PLANETS = 5;

    /** Most non-Earth planets in a world. */
    public static final int MAX_PLANETS = 8;

    /** Names of every planet that can appear (besides Earth). */
    private static final String[] PLANET_NAMES =
        { "Mars", "Venus", "Europa", "Titan", "Io", "Ganymede", "Callisto",
            "Enceladus", "Triton", "Kepler-22b", "Proxima b", "Trappist-1e",
            "Gliese 581g", "Kepler-452b" };

    /** Base time dilation factor for each planet in PLANET_NAMES. */
    private static final double[] PLANET_DILATIONS = { 1.2, 0.6, 0.8, 1.5, 0.7,
        1.1, 1.3, 0.9, 1.4, 2.0, 1.8, 2.5, 1.7, 2.2 };

    /** Objectives for every mission that can appear. */
    private static final String[] MISSION_OBJECTIVES = { "Collect Fuel",
        "Drill the Ice", "Collect Samples", "Repair the Beacon",
        "Map the Surface", "Mine Crystals", "Rescue the Stranded Crew",
        "Scan for Life", "Recover the Lost Probe", "Harvest Algae",
        "Gather Rare Metals", "Install a Weather Station", "Salvage the Wreck",
        "Collect Plasma", "Test the Atmosphere", "Plant a Flag",
        "Recharge the Batteries", "Study the Rings" };

    /** Resource for each mission in MISSION_OBJECTIVES. */
    private static final String[] MISSION_RESOURCES = { "Fuel Cell", "Ice Core",
        "Research Samples", "Beacon Part", "Survey Data", "Energy Crystal",
        "Crew Member", "Bio Scan", "Probe Core", "Algae Pod", "Rare Metal",
        "Sensor Kit", "Scrap Metal", "Plasma Canister", "Air Sample", "Flag",
        "Battery Pack", "Ring Dust" };

    // -------------------------------------------------------------------------
    /**
     * A planet plus the missions on it and the planets it connects to.
     */
    private static class PlanetEntry
    {
        private Planet planet;
        private FourWayLink<Mission> missions;
        private FourWayLink<Planet> connections;

        private PlanetEntry(Planet planet)
        {
            this.planet = planet;
            missions = new FourWayLink<>();
            connections = new FourWayLink<>();
        }
    }

    private FourWayLink<PlanetEntry> planets;
    private FourWayLink<Event> eventPool;
    private Planet earth;
    private Random random;

    // ----------------------------------------------------------
    /**
     * Creates a random game world, so each playthrough is different.
     */
    public WorldBuild()
    {
        this(System.nanoTime());
    }


    // ----------------------------------------------------------
    /**
     * Creates the game world using the given seed. The same seed always builds
     * the same planets, missions, routes, and random events.
     *
     * @param seed
     *            the seed for random choices
     */
    public WorldBuild(long seed)
    {
        planets = new FourWayLink<>();
        eventPool = new FourWayLink<>();
        random = new Random(seed);

        createPlanets();
        connectPlanets();
        createEventPool();
    }


    // ----------------------------------------------------------
    /**
     * Adds Earth, then picks 5 to 8 random planets from the catalog and gives
     * each one 1 or 2 random missions.
     */
    private void createPlanets()
    {
        earth = new Planet("Earth", 1.0);
        addPlanet(earth);

        int[] planetOrder = shuffledIndexes(PLANET_NAMES.length);
        int[] missionOrder = shuffledIndexes(MISSION_OBJECTIVES.length);
        int nextMission = 0;

        int count = MIN_PLANETS + random.nextInt(MAX_PLANETS - MIN_PLANETS + 1);

        for (int i = 0; i < count; i++)
        {
            int p = planetOrder[i];
            Planet planet = new Planet(
                PLANET_NAMES[p],
                randomDilation(PLANET_DILATIONS[p]));
            addPlanet(planet);

            int missionCount = 1 + random.nextInt(2);

            for (int j = 0; j < missionCount; j++)
            {
                int m = missionOrder[nextMission % missionOrder.length];
                nextMission++;
                addMission(planet, randomMission(m));
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Builds the routes. First a loop through every planet in random order,
     * starting and ending at Earth, so everything is reachable. Then 1 to 3
     * random shortcuts between planets.
     */
    private void connectPlanets()
    {
        Planet[] all = getPlanets();
        int others = all.length - 1;

        // Random loop: Earth -> others in shuffled order -> Earth
        int[] order = shuffledIndexes(others);
        Planet previous = earth;

        for (int i = 0; i < others; i++)
        {
            Planet next = all[order[i] + 1];
            connect(previous, next);
            previous = next;
        }

        connect(previous, earth);

        // Random shortcuts. Pairs that are already connected don't count,
        // so keep trying (with a limit, in case the map is nearly full).
        int shortcuts = 1 + random.nextInt(3);
        int added = 0;
        int attempts = 0;

        while (added < shortcuts && attempts < 50)
        {
            Planet a = all[random.nextInt(all.length)];
            Planet b = all[random.nextInt(all.length)];
            attempts++;

            if (a != b && !isConnected(a, b))
            {
                connect(a, b);
                added++;
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Fills the event pool. Events added more times are more likely. Safe
     * Travel's damage is ignored by Event, but Event requires a positive value,
     * so it uses 1. Damage for the others varies a little each run.
     */
    private void createEventPool()
    {
        addEvents(new Event("Safe Travel", 1), 5);
        addEvents(new Event("Space Junk", 5 + random.nextInt(11)), 2);
        addEvents(new Event("Asteroid Field", 20 + random.nextInt(11)), 2);
        addEvents(new Event("Engine Failure", 10 + random.nextInt(11)), 1);
    }


    // ----------------------------------------------------------
    /**
     * Gets Earth, the start and finish planet.
     *
     * @return Earth
     */
    public Planet getEarth()
    {
        return earth;
    }


    // ----------------------------------------------------------
    /**
     * Gets all planets in the world, Earth first.
     *
     * @return an array of every planet
     */
    public Planet[] getPlanets()
    {
        Planet[] result = new Planet[planets.size()];
        int i = 0;

        for (FourWayLink.Node<PlanetEntry> node =
            planets.head(); node != null; node = node.right)
        {
            result[i] = node.value.planet;
            i++;
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Gets how many planets are in the world, including Earth.
     *
     * @return the number of planets
     */
    public int getPlanetCount()
    {
        return planets.size();
    }


    // ----------------------------------------------------------
    /**
     * Finds a planet by name, ignoring capitalization.
     *
     * @param name
     *            the planet name
     * @return the planet, or null if name is null, empty, or not found
     */
    public Planet findPlanet(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            return null;
        }

        for (FourWayLink.Node<PlanetEntry> node =
            planets.head(); node != null; node = node.right)
        {
            if (node.value.planet.getName().equalsIgnoreCase(name.trim()))
            {
                return node.value.planet;
            }
        }

        return null;
    }


    // ----------------------------------------------------------
    /**
     * Gets the missions on a planet.
     *
     * @param planet
     *            the planet
     * @return an array of its missions (empty if none or not in world)
     */
    public Mission[] getMissions(Planet planet)
    {
        PlanetEntry entry = findEntry(planet);

        if (entry == null)
        {
            return new Mission[0];
        }

        Mission[] result = new Mission[entry.missions.size()];
        int i = 0;

        for (FourWayLink.Node<Mission> node =
            entry.missions.head(); node != null; node = node.right)
        {
            result[i] = node.value;
            i++;
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Gets the planets that can be traveled to from a planet.
     *
     * @param planet
     *            the planet
     * @return an array of connected planets (empty if not in world)
     */
    public Planet[] getConnectedPlanets(Planet planet)
    {
        PlanetEntry entry = findEntry(planet);

        if (entry == null)
        {
            return new Planet[0];
        }

        Planet[] result = new Planet[entry.connections.size()];
        int i = 0;

        for (FourWayLink.Node<Planet> node =
            entry.connections.head(); node != null; node = node.right)
        {
            result[i] = node.value;
            i++;
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Checks whether a ship can travel directly from one planet to another.
     *
     * @param from
     *            the starting planet
     * @param to
     *            the destination planet
     * @return true if the planets are connected
     */
    public boolean isConnected(Planet from, Planet to)
    {
        PlanetEntry entry = findEntry(from);

        if (entry == null || to == null)
        {
            return false;
        }

        for (FourWayLink.Node<Planet> node =
            entry.connections.head(); node != null; node = node.right)
        {
            if (node.value == to)
            {
                return true;
            }
        }

        return false;
    }


    // ----------------------------------------------------------
    /**
     * Gets a random event from the pool. Events added more times are more
     * likely.
     *
     * @return a random event
     * @throws IllegalStateException
     *             if the pool is empty
     */
    public Event getRandomEvent()
    {
        if (eventPool.isEmpty())
        {
            throw new IllegalStateException("The event pool is empty.");
        }

        int index = random.nextInt(eventPool.size());
        FourWayLink.Node<Event> node = eventPool.head();

        for (int i = 0; i < index; i++)
        {
            node = node.right;
        }

        return node.value;
    }


    // ----------------------------------------------------------
    /**
     * Counts how many times an event type appears in the pool.
     *
     * @param eventType
     *            the event type, ignoring capitalization
     * @return how many copies of that event are in the pool
     */
    public int getEventFrequency(String eventType)
    {
        int count = 0;

        for (FourWayLink.Node<Event> node =
            eventPool.head(); node != null; node = node.right)
        {
            if (node.value.getEventType().equalsIgnoreCase(eventType))
            {
                count++;
            }
        }

        return count;
    }


    // ----------------------------------------------------------
    /**
     * Gets how many events are in the pool, counting copies.
     *
     * @return the size of the event pool
     */
    public int getEventPoolSize()
    {
        return eventPool.size();
    }


    // ----------------------------------------------------------
    /**
     * Checks whether every mission on every planet is completed.
     *
     * @return true if all missions are completed
     */
    public boolean allMissionsCompleted()
    {
        return getCompletedMissionCount() == getTotalMissionCount();
    }


    // ----------------------------------------------------------
    /**
     * Counts the completed missions across all planets.
     *
     * @return the number of completed missions
     */
    public int getCompletedMissionCount()
    {
        int count = 0;

        for (FourWayLink.Node<PlanetEntry> p = planets.head(); p != null; p =
            p.right)
        {
            for (FourWayLink.Node<Mission> m =
                p.value.missions.head(); m != null; m = m.right)
            {
                if (m.value.isCompleted())
                {
                    count++;
                }
            }
        }

        return count;
    }


    // ----------------------------------------------------------
    /**
     * Counts the missions across all planets.
     *
     * @return the total number of missions
     */
    public int getTotalMissionCount()
    {
        int count = 0;

        for (FourWayLink.Node<PlanetEntry> p = planets.head(); p != null; p =
            p.right)
        {
            count += p.value.missions.size();
        }

        return count;
    }

    // ----------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------


    /**
     * Returns 0 to n - 1 in a random order (Fisher-Yates shuffle).
     */
    private int[] shuffledIndexes(int n)
    {
        int[] result = new int[n];

        for (int i = 0; i < n; i++)
        {
            result[i] = i;
        }

        for (int i = n - 1; i > 0; i--)
        {
            int j = random.nextInt(i + 1);
            int temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }

        return result;
    }


    /**
     * Changes a base dilation by up to 10% either way, rounded to one decimal
     * place and never below 0.5.
     */
    private double randomDilation(double base)
    {
        double changed = base * (0.9 + random.nextDouble() * 0.2);
        return Math.max(0.5, Math.round(changed * 10) / 10.0);
    }


    /**
     * Makes the mission at index i of the catalog with a random reward (50 to
     * 300, in steps of 50) and resource count (1 to 5).
     */
    private Mission randomMission(int i)
    {
        int reward = 50 * (1 + random.nextInt(6));
        int resourceCount = 1 + random.nextInt(5);

        return new Mission(
            MISSION_OBJECTIVES[i],
            MISSION_RESOURCES[i],
            reward,
            resourceCount);
    }


    /**
     * Adds a value to the right end of a chain.
     */
    private static <T> void addToEnd(FourWayLink<T> chain, T value)
    {
        if (chain.isEmpty())
        {
            chain.addRoot(value);
            return;
        }

        FourWayLink.Node<T> last = chain.head();

        while (last.right != null)
        {
            last = last.right;
        }

        chain.add(last, FourWayLink.Direction.RIGHT, value);
    }


    /**
     * Adds a planet to the world.
     */
    private void addPlanet(Planet planet)
    {
        if (planet == null || findEntry(planet) != null)
        {
            throw new IllegalArgumentException(
                "Planet cannot be null or added twice.");
        }

        addToEnd(planets, new PlanetEntry(planet));
    }


    /**
     * Adds a mission to a planet already in the world.
     */
    private void addMission(Planet planet, Mission mission)
    {
        PlanetEntry entry = findEntry(planet);

        if (entry == null || mission == null)
        {
            throw new IllegalArgumentException(
                "Mission must be non-null and go on a known planet.");
        }

        addToEnd(entry.missions, mission);
    }


    /**
     * Connects two planets both ways, unless they are already connected.
     */
    private void connect(Planet a, Planet b)
    {
        PlanetEntry entryA = findEntry(a);
        PlanetEntry entryB = findEntry(b);

        if (entryA == null || entryB == null || a == b)
        {
            throw new IllegalArgumentException(
                "Can only connect two different planets in the world.");
        }

        if (!isConnected(a, b))
        {
            addToEnd(entryA.connections, b);
            addToEnd(entryB.connections, a);
        }
    }


    /**
     * Adds an event to the pool the given number of times.
     */
    private void addEvents(Event event, int copies)
    {
        if (event == null || copies <= 0)
        {
            throw new IllegalArgumentException(
                "Event must be non-null with a positive number of copies.");
        }

        for (int i = 0; i < copies; i++)
        {
            addToEnd(eventPool, event);
        }
    }


    /**
     * Finds the entry holding a planet, or null if it isn't in the world.
     */
    private PlanetEntry findEntry(Planet planet)
    {
        if (planet == null)
        {
            return null;
        }

        for (FourWayLink.Node<PlanetEntry> node =
            planets.head(); node != null; node = node.right)
        {
            if (node.value.planet == planet)
            {
                return node.value;
            }
        }

        return null;
    }
}
