import java.util.Random;

// -------------------------------------------------------------------------
/**
 * Creates and stores the game world: a map of planets, the missions on each
 * planet, and the pool of random events used during travel. The map is a
 * FourWayLink<Planet>. Each planet is a node, and its UP, DOWN, LEFT and RIGHT
 * links are the routes a ship can fly. Earth is the root (the head) of the map.
 * Every run builds a different map. Earth plus 5 to 8 planets are picked from a
 * catalog of 14, and the map grows outward from Earth: each new planet is
 * attached to a random planet already on the map, in a random open direction.
 * Every planet also has an (x, y) grid position, and no two planets share a
 * position, so Main can draw the map as a grid. Because FourWayLink can only
 * attach a NEW node to an existing one, the map is a tree: there is exactly one
 * path between any two planets, and every route works both ways, so there is
 * always a way back to Earth. WorldBuild(long seed) builds the same world every
 * time for a given seed, so tests are repeatable.
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
     *  One planet on the map: its node in the map, its grid
     * position, and its missions.
     */
    private static class Slot
    {
        private FourWayLink.Node<Planet> node;
        private int x;
        private int y;
        private FourWayLink<Mission> missions;

        private Slot(FourWayLink.Node<Planet> node, int x, int y)
        {
            this.node = node;
            this.x = x;
            this.y = y;
            missions = new FourWayLink<>();
        }
    }

    private FourWayLink<Planet> map;
    private Slot[] slots;
    private int slotCount;
    private FourWayLink<Event> eventPool;
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
     * the same map, missions, and random events.
     *
     * @param seed
     *            the seed for random choices
     */
    public WorldBuild(long seed)
    {
        map = new FourWayLink<>();
        slots = new Slot[MAX_PLANETS + 1];
        slotCount = 0;
        eventPool = new FourWayLink<>();
        random = new Random(seed);

        createPlanets();
        createEventPool();
    }


    // ----------------------------------------------------------
    /**
     * Puts Earth at (0, 0) as the root of the map, then attaches 5 to 8 random
     * planets one at a time, each with 1 or 2 random missions.
     */
    private void createPlanets()
    {
        Planet earth = new Planet("Earth", 1.0);
        slots[0] = new Slot(map.addRoot(earth), 0, 0);
        slotCount = 1;

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
            Slot slot = attachPlanet(planet);

            int missionCount = 1 + random.nextInt(2);

            for (int j = 0; j < missionCount; j++)
            {
                int m = missionOrder[nextMission % missionOrder.length];
                nextMission++;
                addToEnd(slot.missions, randomMission(m));
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Attaches a planet to a random planet already on the map, in a random
     * direction whose link is empty and whose grid spot is free.
     *
     * @param planet
     *            the planet to attach
     * @return the new planet's slot
     */
    private Slot attachPlanet(Planet planet)
    {
        FourWayLink.Direction[] dirs = FourWayLink.Direction.values();

        while (true)
        {
            Slot from = slots[random.nextInt(slotCount)];
            FourWayLink.Direction d = dirs[random.nextInt(dirs.length)];
            int x = from.x + dx(d);
            int y = from.y + dy(d);

            if (from.node.get(d) == null && findSlotAt(x, y) == null)
            {
                FourWayLink.Node<Planet> node = map.add(from.node, d, planet);
                Slot slot = new Slot(node, x, y);
                slots[slotCount] = slot;
                slotCount++;
                return slot;
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
     * Gets Earth, the start and finish planet (the head of the map).
     *
     * @return Earth
     */
    public Planet getEarth()
    {
        return map.head().value;
    }


    // ----------------------------------------------------------
    /**
     * Gets the map itself. Each node's UP, DOWN, LEFT and RIGHT links are the
     * routes from that planet.
     *
     * @return the map of planets
     */
    public FourWayLink<Planet> getMap()
    {
        return map;
    }


    // ----------------------------------------------------------
    /**
     * Gets the map node holding a planet, so callers can use get(Direction) to
     * look at its neighbors.
     *
     * @param planet
     *            the planet
     * @return its node, or null if the planet isn't on the map
     */
    public FourWayLink.Node<Planet> getNode(Planet planet)
    {
        Slot slot = findSlot(planet);
        return slot == null ? null : slot.node;
    }


    // ----------------------------------------------------------
    /**
     * Gets all planets in the world, Earth first.
     *
     * @return an array of every planet
     */
    public Planet[] getPlanets()
    {
        Planet[] result = new Planet[slotCount];

        for (int i = 0; i < slotCount; i++)
        {
            result[i] = slots[i].node.value;
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
        return map.size();
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

        for (int i = 0; i < slotCount; i++)
        {
            Planet p = slots[i].node.value;

            if (p.getName().equalsIgnoreCase(name.trim()))
            {
                return p;
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
        Slot slot = findSlot(planet);

        if (slot == null)
        {
            return new Mission[0];
        }

        Mission[] result = new Mission[slot.missions.size()];
        int i = 0;

        for (FourWayLink.Node<Mission> node =
            slot.missions.head(); node != null; node = node.right)
        {
            result[i] = node.value;
            i++;
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Gets the planet in a direction from a planet.
     *
     * @param planet
     *            the starting planet
     * @param d
     *            the direction to look
     * @return the neighboring planet, or null if there is no route that way
     */
    public Planet getNeighbor(Planet planet, FourWayLink.Direction d)
    {
        Slot slot = findSlot(planet);

        if (slot == null || d == null || slot.node.get(d) == null)
        {
            return null;
        }

        return slot.node.get(d).value;
    }


    // ----------------------------------------------------------
    /**
     * Gets the planets that can be traveled to from a planet, checking UP,
     * DOWN, LEFT and RIGHT in that order.
     *
     * @param planet
     *            the planet
     * @return an array of connected planets (empty if not in world)
     */
    public Planet[] getConnectedPlanets(Planet planet)
    {
        FourWayLink.Direction[] dirs = FourWayLink.Direction.values();
        Planet[] found = new Planet[dirs.length];
        int count = 0;

        for (FourWayLink.Direction d : dirs)
        {
            Planet neighbor = getNeighbor(planet, d);

            if (neighbor != null)
            {
                found[count] = neighbor;
                count++;
            }
        }

        Planet[] result = new Planet[count];

        for (int i = 0; i < count; i++)
        {
            result[i] = found[i];
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Gets the direction to fly to get from one planet to a neighbor.
     *
     * @param from
     *            the starting planet
     * @param to
     *            the destination planet
     * @return the direction, or null if the planets aren't connected
     */
    public FourWayLink.Direction getDirection(Planet from, Planet to)
    {
        if (to == null)
        {
            return null;
        }

        for (FourWayLink.Direction d : FourWayLink.Direction.values())
        {
            if (getNeighbor(from, d) == to)
            {
                return d;
            }
        }

        return null;
    }


    // ----------------------------------------------------------
    /**
     * Checks whether a ship can travel directly from one planet to another.
     *
     * @param from
     *            the starting planet
     * @param to
     *            the destination planet
     * @return true if the planets are linked on the map
     */
    public boolean isConnected(Planet from, Planet to)
    {
        return getDirection(from, to) != null;
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

        for (int i = 0; i < slotCount; i++)
        {
            for (FourWayLink.Node<Mission> m =
                slots[i].missions.head(); m != null; m = m.right)
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

        for (int i = 0; i < slotCount; i++)
        {
            count += slots[i].missions.size();
        }

        return count;
    }

    /**
     * How far x changes moving in a direction (RIGHT is +1).
     */
    private static int dx(FourWayLink.Direction d)
    {
        switch (d)
        {
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
            default:
                return 0;
        }
    }


    /**
     * How far y changes moving in a direction (DOWN is +1, like rows on a
     * screen).
     */
    private static int dy(FourWayLink.Direction d)
    {
        switch (d)
        {
            case UP:
                return -1;
            case DOWN:
                return 1;
            default:
                return 0;
        }
    }


    /**
     * Finds the slot for a planet, or null if it isn't on the map.
     */
    private Slot findSlot(Planet planet)
    {
        if (planet == null)
        {
            return null;
        }

        for (int i = 0; i < slotCount; i++)
        {
            if (slots[i].node.value == planet)
            {
                return slots[i];
            }
        }

        return null;
    }


    /**
     * Finds the slot at a grid position, or null if the spot is empty.
     */
    private Slot findSlotAt(int x, int y)
    {
        for (int i = 0; i < slotCount; i++)
        {
            if (slots[i].x == x && slots[i].y == y)
            {
                return slots[i];
            }
        }

        return null;
    }


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
     * Adds a value to the right end of a chain (used for the mission lists and
     * the event pool).
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
}
