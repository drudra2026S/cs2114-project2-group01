import java.time.Duration;

// -------------------------------------------------------------------------
/**
 * Represents the player's spaceship. Tracks the ship's health, total damage
 * taken, engine status, current planet, and the Earth time left to finish the
 * mission. A ship can be built with or without a WorldBuild. With a world, it
 * can only travel to planets connected to its current planet. Without one (as
 * in EventTest), any planet other than the current one counts as reachable.
 *
 * @author ltstabler
 * @version Oct 12, 2026
 */
public class Spaceship
{
    /** The most health a ship can have. */
    public static final int MAX_HEALTH = 100;

    /** Earth time the player starts with. */
    public static final Duration START_TIME = Duration.ofHours(100);

    private int health;
    private int totalDamage;
    private boolean engineWorking;
    private Planet currentLocation;
    private Duration timeLeft;
    private WorldBuild world;

    // ----------------------------------------------------------
    /**
     * Creates a ship at the starting planet with full health, 0 total damage,
     * and a working engine. The ship has no world map, so travelTo() allows any
     * planet other than the current one.
     *
     * @param start
     *            the starting planet
     * @throws IllegalArgumentException
     *             if start is null
     */
    public Spaceship(Planet start)
    {
        this(start, null);
    }


    // ----------------------------------------------------------
    /**
     * Creates a ship at the starting planet that uses the given world to decide
     * which planets it can travel to.
     *
     * @param start
     *            the starting planet
     * @param world
     *            the game world (may be null for no route checking)
     * @throws IllegalArgumentException
     *             if start is null
     */
    public Spaceship(Planet start, WorldBuild world)
    {
        if (start == null)
        {
            throw new IllegalArgumentException(
                "Starting planet cannot be null.");
        }

        currentLocation = start;
        this.world = world;
        health = MAX_HEALTH;
        totalDamage = 0;
        engineWorking = true;
        timeLeft = START_TIME;
    }


    // ----------------------------------------------------------
    /**
     * Gets the Earth time left to get back to Earth.
     *
     * @return the time left
     */
    public Duration getTimeLeft()
    {
        return timeLeft;
    }


    // ----------------------------------------------------------
    /**
     * Gets the ship's current health.
     *
     * @return the current health
     */
    public int getHealth()
    {
        return health;
    }


    // ----------------------------------------------------------
    /**
     * Gets the total damage taken over the whole mission.
     *
     * @return the total damage
     */
    public int getTotalDamage()
    {
        return totalDamage;
    }


    // ----------------------------------------------------------
    /**
     * Gets the planet the ship is currently on.
     *
     * @return the current planet
     */
    public Planet getCurrentLocation()
    {
        return currentLocation;
    }


    // ----------------------------------------------------------
    /**
     * Checks whether the ship has been destroyed.
     *
     * @return true if health has reached 0
     */
    public boolean isDestroyed()
    {
        return health <= 0;
    }


    // ----------------------------------------------------------
    /**
     * Checks whether the engine is working.
     *
     * @return true if the engine is working
     */
    public boolean isEngineWorking()
    {
        return engineWorking;
    }


    // ----------------------------------------------------------
    /**
     * Checks whether the ship can travel.
     *
     * @return true if the ship isn't destroyed and the engine works
     */
    public boolean canTravel()
    {
        return !isDestroyed() && engineWorking;
    }


    // ----------------------------------------------------------
    /**
     * Checks whether the ship has run out of time.
     *
     * @return true if no time is left
     */
    public boolean isOutOfTime()
    {
        return timeLeft.isZero() || timeLeft.isNegative();
    }


    // ----------------------------------------------------------
    /**
     * Moves the ship to the destination.
     *
     * @param destination
     *            the planet to travel to
     * @return true if the ship moved; false if the ship can't travel,
     *             destination is null, or destination isn't connected to the
     *             current planet
     */
    public boolean travelTo(Planet destination)
    {
        if (destination == null || !canTravel()
            || destination == currentLocation)
        {
            return false;
        }

        if (world != null && !world.isConnected(currentLocation, destination))
        {
            return false;
        }

        currentLocation = destination;
        return true;
    }


    // ----------------------------------------------------------
    /**
     * Lowers health by amount (never below 0) and adds the full amount to total
     * damage.
     *
     * @param amount
     *            the damage to take
     * @throws IllegalArgumentException
     *             if amount is negative
     */
    public void takeDamage(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("Damage cannot be negative.");
        }

        totalDamage += amount;
        health = Math.max(0, health - amount);
    }


    // ----------------------------------------------------------
    /**
     * Raises health by amount, up to MAX_HEALTH. Does nothing if the ship is
     * destroyed.
     *
     * @param amount
     *            the amount to repair
     * @throws IllegalArgumentException
     *             if amount is negative
     */
    public void repair(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException(
                "Repair amount cannot be negative.");
        }

        if (isDestroyed())
        {
            return;
        }

        health = Math.min(MAX_HEALTH, health + amount);
    }


    // ----------------------------------------------------------
    /**
     * Uses up Earth time. Time left never goes below zero.
     *
     * @param earthTime
     *            the Earth time spent
     * @throws IllegalArgumentException
     *             if earthTime is null or negative
     */
    public void spendTime(Duration earthTime)
    {
        if (earthTime == null || earthTime.isNegative())
        {
            throw new IllegalArgumentException(
                "Time spent cannot be null or negative.");
        }

        timeLeft = timeLeft.minus(earthTime);

        if (timeLeft.isNegative())
        {
            timeLeft = Duration.ZERO;
        }
    }


    // ----------------------------------------------------------
    /**
     * Marks the engine as working again so the ship can travel.
     */
    public void fixEngine()
    {
        engineWorking = true;
    }


    // ----------------------------------------------------------
    /**
     * Marks the engine as broken so the ship can't travel. Called by an
     * engine-failure event.
     */
    public void disableEngine()
    {
        engineWorking = false;
    }


    // ----------------------------------------------------------
    /**
     * Returns the ship's health, location, engine status, and time left for the
     * mission summary.
     *
     * @return the ship's status
     */
    @Override
    public String toString()
    {
        return "Health: " + health + "/" + MAX_HEALTH + "\nLocation: "
            + currentLocation.getName() + "\nEngine: "
            + (engineWorking ? "Working" : "Broken") + "\nTime left: "
            + timeLeft.toHours() + " hours";
    }
}
