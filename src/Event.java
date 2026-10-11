
 // -------------------------------------------------------------------------
/**
 * Represents a random event that occurs during space travel.
 * Events can damage the spaceship or disable its engine.
 *
 * @author Rudra Dutta (drudra)
 * @version 2026.10.11
 */
public class Event
{
    private String eventType;
    private int damage;

    // ----------------------------------------------------------
    /**
     * Creates an event with a type and damage amount.
     *
     * @param eventType the type of event
     * @param damage the damage associated with the event
     */
    public Event(String eventType, int damage)
    {
        if (eventType == null || eventType.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Event type cannot be empty.");
        }

        if (damage <= 0)
        {
            throw new IllegalArgumentException(
                "Damage must be positive.");
        }

        this.eventType = eventType;
        this.damage = damage;
    }

    // ----------------------------------------------------------
    /**
     * Gets the event type.
     *
     * @return the event type
     */
    public String getEventType()
    {
        return eventType;
    }

    // ----------------------------------------------------------
    /**
     * Gets the event damage amount.
     *
     * @return the damage amount
     */
    public int getDamage()
    {
        return damage;
    }

    // ----------------------------------------------------------
    /**
     * Applies the event's effect to the spaceship.
     *
     * @param ship the spaceship affected by the event
     */
    public void applyEffect(Spaceship ship)
    {
        if (ship == null)
        {
            return;
        }

        if (eventType.equalsIgnoreCase("Safe Travel"))
        {
            // Safe travel does not damage the spaceship.
            // The stored damage value is ignored.
        }
        else if (eventType.equalsIgnoreCase("Engine Failure"))
        {
            ship.takeDamage(damage);
            ship.disableEngine();
        }
        else if (eventType.equalsIgnoreCase("Asteroid Field")
            || eventType.equalsIgnoreCase("Space Junk"))
        {
            ship.takeDamage(damage);
        }
        else
        {
            // No effect for an unrecognized event type.
        }
    }
}
