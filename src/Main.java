import java.util.Scanner;

// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author zach1
 *  @version Oct 9, 2026
 */
public class Main
{
    
    private Scanner input;

    // ----------------------------------------------------------
    /**
     * Create a new Main object.
     */
    public Main()
    {
        input = new Scanner(System.in);
    }

    /**
     * Starts the Every Second Counts game.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        Main game = new Main();
        game.startGame();
    }

    /**
     * Runs the main gameplay loop.
     */
    public void startGame()
    {
        System.out.println("Welcome to Every Second Counts!");

        boolean playing = true;

        while (playing)
        {
            displayMenu();

            System.out.print("Choose an option: ");

            if (input.hasNextInt())
            {
                int choice = input.nextInt();
                playing = handleChoice(choice);
            }
            else
            {
                System.out.println("Please enter a number.");
                input.next();
            }
        }

        System.out.println("Thanks for playing!");
    }

    /**
     * Displays the available gameplay options.
     */
    private void displayMenu()
    {
        System.out.println();
        System.out.println("===== EVERY SECOND COUNTS =====");
        System.out.println("1. Start mission");
        System.out.println("2. View status");
        System.out.println("3. Quit");
        System.out.println("==============================");
    }

    /**
     * Handles the player's menu choice.
     * 
     * @param choice the selected menu option
     * @return true if the game should continue
     */
    public boolean handleChoice(int choice)
    {
        switch (choice)
        {
            case 1:
                System.out.println("Starting your mission...");
                System.out.println("Your space adventure is coming soon!");
                break;

            case 2:
                System.out.println("===== SHIP STATUS =====");
                System.out.println("Ship status is not available yet.");
                System.out.println("=======================");
                break;

            case 3:
                return false;

            default:
                System.out.println("Invalid choice. Try again.");
        }

        return true;
    }
}
