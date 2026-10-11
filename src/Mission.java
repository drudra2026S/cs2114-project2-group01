
import java.time.Duration;

// -------------------------------------------------------------------------
/**
 * Represents a mission that the player must complete.
 * Stores the objective, resource, reward, and completion status.
 *
 * @author  Rudra Dutta (drudra)
 * @version 2026.10.11
 */
public class Mission
{
    private String objective;
    private String resource;
    private boolean completed;
    private int reward;
    private int resourceCount;

    // ----------------------------------------------------------
    /**
     * Creates a mission with one required resource.
     *
     * @param objective the mission objective
     * @param resource the resource associated with the mission
     * @param reward the reward for completing the mission
     */
    public Mission(String objective, String resource, int reward)
    {
        this(objective, resource, reward, 1);
    }

    // ----------------------------------------------------------
    /**
     * Creates a mission with a specified number of resources.
     *
     * @param objective the mission objective
     * @param resource the resource associated with the mission
     * @param reward the reward for completing the mission
     * @param resourceCount the number of resources required
     */
    public Mission(String objective, String resource, int reward,
        int resourceCount)
    {
        if (objective == null || objective.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Objective cannot be empty.");
        }

        if (resource == null || resource.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Resource cannot be empty.");
        }

        if (reward <= 0)
        {
            throw new IllegalArgumentException(
                "Reward must be positive.");
        }

        if (resourceCount <= 0)
        {
            throw new IllegalArgumentException(
                "Resource count must be positive.");
        }

        this.objective = objective;
        this.resource = resource;
        this.reward = reward;
        this.resourceCount = resourceCount;
        completed = false;
    }

    // ----------------------------------------------------------
    /**
     * Gets the mission objective.
     *
     * @return the mission objective
     */
    public String getObjective()
    {
        return objective;
    }

    // ----------------------------------------------------------
    /**
     * Gets the required resource.
     *
     * @return the mission resource
     */
    public String getResource()
    {
        return resource;
    }

    // ----------------------------------------------------------
    /**
     * Checks whether the mission is completed.
     *
     * @return true if completed, false otherwise
     */
    public boolean isCompleted()
    {
        return completed;
    }

    // ----------------------------------------------------------
    /**
     * Gets the mission reward.
     *
     * @return the mission reward
     */
    public int getReward()
    {
        return reward;
    }
    
    // ----------------------------------------------------------
    /**
     * Gets the local time required to complete the mission.
     *
     * @return the mission duration in local hours
     */
    public Duration MissionTime()
    {
        if (resourceCount <= 3)
        {
            return Duration.ofHours(5);
        }

        return Duration.ofHours(10);
    }

    // ----------------------------------------------------------
    /**
     * Marks the mission as completed.
     */
    public void complete()
    {
        completed = true;
    }
}
