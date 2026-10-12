import java.time.Duration;
import java.util.Scanner;

/**
 * Runs the Every Second Counts space exploration game.
 * The player travels between planets, completes missions, collects
 * materials, and returns to Earth before the ship runs out of time
 * or is destroyed.
 *
 * @author Zachary
 */
public class Main
{
    private Scanner input;
    private Spaceship ship;
    private WorldBuild world;
    
    private Difficulty difficulty;

    /**
     * Creates one world and one spaceship for the entire expedition.
     */
    public Main()
    {
        input = new Scanner(System.in);
        difficulty = chooseDifficulty();
        world = new WorldBuild();

        Duration startingTime = Spaceship.START_TIME;

        if (difficulty == Difficulty.DEMO)
        {
            startingTime = Duration.ofHours(500);
        }

        ship = new Spaceship(
            world.getEarth(), world, startingTime);
    }

    /**
     * Starts and runs the game until the player wins, loses, or quits.
     */
    public void startGame()
    {
        System.out.println("Welcome to Every Second Counts!");

        boolean playing = true;

        while (playing)
        {
            if (checkWin())
            {
                System.out.println(
                    "\nCongratulations! You collected all the materials "
                    + "and returned to Earth!");
                break;
            }

            if (checkLoss())
            {
                System.out.println("\nYour expedition has failed.");
                break;
            }

            displayMenu();
            System.out.print("Choose an option: ");

            if (!input.hasNextInt())
            {
                System.out.println("Please enter a number.");
                input.nextLine();
                continue;
            }

            int choice = input.nextInt();
            input.nextLine();

            playing = handleChoice(choice);
        }

        System.out.println("Thanks for playing Every Second Counts!");
    }

    /**
     * Displays the available gameplay options.
     */
    private void displayMenu()
    {
        System.out.println("\n===== EVERY SECOND COUNTS =====");
        System.out.println("1. Travel to another planet");
        System.out.println("2. View status");
        System.out.println("3. View current missions");
        System.out.println("4. Repair ship / fix engine");
        System.out.println("5. Quit");
        System.out.println("==============================");
    }

    /**
     * Handles a menu choice.
     *
     * @param choice
     *            the player's menu selection
     * @return true if the game should continue
     */
    public boolean handleChoice(int choice)
    {
        switch (choice)
        {
            case 1:
                travelAndExplore();
                break;

            case 2:
                showStatus();
                break;

            case 3:
                showMissions();
                break;

            case 4:
                repairShip();
                break;

            case 5:
                return false;

            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }

        if (checkWin())
        {
            System.out.println(
                "\nCongratulations! You collected all the materials "
                + "and returned to Earth!");
            return false;
        }

        if (checkLoss())
        {
            System.out.println("\nYour expedition has failed.");
            return false;
        }

        return true;
    }

    /**
     * Allows the player to travel to a connected planet and encounter
     * a random event. The existing expedition is retained.
     */
    private void travelAndExplore()
    {
        if (!ship.canTravel())
        {
            System.out.println(
                "Your ship cannot travel. Repair it before continuing.");
            return;
        }

        Planet current = ship.getCurrentLocation();

        System.out.println("\nContinuing your expedition...");
        System.out.println("Current planet: " + current.getName());

        Planet[] destinations = world.getConnectedPlanets(current);

        if (destinations.length == 0)
        {
            System.out.println("There are no available destinations.");
            return;
        }

        System.out.println("\nChoose a destination:");

        for (int i = 0; i < destinations.length; i++)
        {
            System.out.println(
                (i + 1) + ". " + destinations[i].getName());
        }

        System.out.println("0. Cancel");
        System.out.print("Destination: ");

        if (!input.hasNextInt())
        {
            System.out.println("Please enter a number.");
            input.nextLine();
            return;
        }

        int selection = input.nextInt();
        input.nextLine();

        if (selection == 0)
        {
            System.out.println("Travel cancelled.");
            return;
        }

        if (selection < 1 || selection > destinations.length)
        {
            System.out.println("Invalid destination.");
            return;
        }

        Planet destination = destinations[selection - 1];

        Duration earthTime;

        if (difficulty == Difficulty.DEMO)
        {
            earthTime = Duration.ofHours(1);
        }
        else if (difficulty == Difficulty.EASY)
        {
            earthTime = Duration.ofHours(2);
        }
        else if (difficulty == Difficulty.HARD)
        {
            earthTime = Duration.ofHours(7);
        }
        else
        {
            earthTime = Duration.ofHours(5);
        }

        if (!ship.travelTo(destination))
        {
            System.out.println(
                "Travel failed. Check your ship and route.");
            return;
        }

        ship.spendTime(earthTime);

        System.out.println(
            "\nYou arrived at " + destination.getName() + ".");
        System.out.println(
            "Earth time spent: " + earthTime.toHours() + " hours.");

        if (checkLoss())
        {
            return;
        }

        Event event = world.getRandomEvent();
        System.out.println("\nEvent: " + event.getEventType());

        if (difficulty == Difficulty.DEMO)
        {
            if (event.getEventType().equalsIgnoreCase("Engine Failure"))
            {
                System.out.println(
                    "Demo mode: Your engine is protected from failure.");
            }
            else if (!event.getEventType().equalsIgnoreCase("Safe Travel"))
            {
                ship.takeDamage(Math.max(1, event.getDamage() / 4));
                System.out.println(
                    "Demo mode: Reduced event damage.");
            }
        }
        else
        {
            event.applyEffect(ship);
        }

        if (checkLoss())
        {
            return;
        }

        exploreMissions(destination);
    }

    /**
     * Displays available missions on the current planet and lets
     * the player complete one mission.
     *
     * @param planet
     *            the planet being explored
     */
    private void exploreMissions(Planet planet)
    {
        Mission[] missions = world.getMissions(planet);

        if (missions.length == 0)
        {
            System.out.println("There are no missions here.");
            return;
        }

        boolean exploring = true;

        while (exploring)
        {
            System.out.println(
                "\nMissions on " + planet.getName() + ":");

            for (int i = 0; i < missions.length; i++)
            {
                String status = missions[i].isCompleted()
                    ? "COMPLETED" : "NOT COMPLETED";

                System.out.println(
                    (i + 1) + ". " + missions[i].getObjective()
                    + " - " + missions[i].getResource()
                    + " (" + status + ")");
            }

            System.out.println("0. Leave missions for later");
            System.out.print("Choose a mission to complete: ");

            if (!input.hasNextInt())
            {
                System.out.println("Please enter a number.");
                input.nextLine();
                continue;
            }

            int selection = input.nextInt();
            input.nextLine();

            if (selection == 0)
            {
                System.out.println(
                    "You can return to these missions later.");
                exploring = false;
                continue;
            }

            if (selection < 1 || selection > missions.length)
            {
                System.out.println("Invalid mission selection.");
                continue;
            }

            Mission mission = missions[selection - 1];

            if (mission.isCompleted())
            {
                System.out.println(
                    "That mission is already completed.");
                continue;
            }

            mission.complete();

            System.out.println(
                "\nMission completed: " + mission.getObjective());
            System.out.println("Collected: " + mission.getResource());
            System.out.println("Reward: " + mission.getReward());

            Duration timeCost = mission.missionTime(difficulty);

            System.out.println("Difficulty: " + difficulty);
            System.out.println("Mission time cost: " + timeCost.toHours()
                + " hours");

            ship.spendTime(timeCost);

            if (ship.isDestroyed() || ship.isOutOfTime())
            {
                exploring = false;
            }
        }
    }

    /**
     * Displays the current ship status and expedition progress.
     */
    private void showStatus()
    {
        System.out.println("\n===== SHIP STATUS =====");
        System.out.println(
            "Current planet: " + ship.getCurrentLocation().getName());
        System.out.println("Health: " + ship.getHealth() + "/100");
        System.out.println("Total damage: " + ship.getTotalDamage());
        System.out.println("Time remaining: " + ship.getTimeLeft());
        System.out.println(
            "Engine working: " + ship.isEngineWorking());
        System.out.println(
            "Missions completed: " + world.getCompletedMissionCount()
            + "/" + world.getTotalMissionCount());
        System.out.println("=======================");
    }

    /**
     * Displays missions throughout the world.
     */
    private void showMissions()
    {
        Planet[] planets = world.getPlanets();

        System.out.println("\n===== EXPEDITION MISSIONS =====");

        for (Planet planet : planets)
        {
            Mission[] missions = world.getMissions(planet);

            for (Mission mission : missions)
            {
                String status = mission.isCompleted()
                    ? "COMPLETED" : "NOT COMPLETED";

                System.out.println(
                    planet.getName() + ": "
                    + mission.getObjective() + " - "
                    + mission.getResource() + " (" + status + ")");
            }
        }

        System.out.println(
            "Completed: " + world.getCompletedMissionCount()
            + "/" + world.getTotalMissionCount());
    }

    /**
     * Repairs the ship's health and restores a disabled engine.
     */
    private void repairShip()
    {
        if (ship.isDestroyed())
        {
            System.out.println("The destroyed ship cannot be repaired.");
            return;
        }

        ship.repair(25);

        if (!ship.isEngineWorking())
        {
            ship.fixEngine();
            System.out.println("The engine has been repaired.");
        }

        System.out.println(
            "Ship repaired. Current health: " + ship.getHealth());
    }

    /**
     * Checks whether every mission is complete and the ship has
     * returned to Earth.
     *
     * @return true if the player has won
     */
    public boolean checkWin()
    {
        return world != null
            && ship != null
            && world.allMissionsCompleted()
            && ship.getCurrentLocation() == world.getEarth();
    }

    /**
     * Checks whether the spaceship has been destroyed or has run
     * out of time.
     *
     * @return true if the player has lost
     */
    public boolean checkLoss()
    {
        if (ship == null)
        {
            return false;
        }

        if (ship.isDestroyed())
        {
            System.out.println(
                "Your ship was destroyed! Your expedition has failed.");
            return true;
        }

        if (ship.isOutOfTime())
        {
            System.out.println(
                "You ran out of time! Your expedition has failed.");
            return true;
        }

        return false;
    }

    /**
     * Starts the game.
     *
     * @param args
     *            command-line arguments
     */
    public static void main(String[] args)
    {
        Main game = new Main();
        game.startGame();
    }
    
    private Difficulty chooseDifficulty()
    {
        System.out.println("Choose your difficulty:");
        System.out.println("1. Easy");
        System.out.println("2. Normal");
        System.out.println("3. Hard");
        System.out.println("4. Demo");
        System.out.print("Enter choice: ");

        while (input.hasNextInt())
        {
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1)
            {
                return Difficulty.EASY;
            }
            else if (choice == 2)
            {
                return Difficulty.NORMAL;
            }
            else if (choice == 3)
            {
                return Difficulty.HARD;
            }
            else if (choice == 4)
            {
                return Difficulty.DEMO;
            }

            System.out.println("Invalid choice. Enter 1, 2, 3, or 4:");
        }

        input.nextLine();
        return Difficulty.NORMAL;
    }
}