import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author zach1
 *  @version Oct 9, 2026
 */
public class MainTest
{
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testInvalidMenuChoice()
    {
        Main game = new Main();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output =
            new ByteArrayOutputStream();

        System.setOut(new PrintStream(output));

        game.handleChoice(7);

        System.setOut(originalOut);

        assertTrue(output.toString().contains(
            "Invalid choice. Try again."));
    }
    
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testStartMissionChoice()
    {
        Main game = new Main();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output =
            new ByteArrayOutputStream();

        System.setOut(new PrintStream(output));

        try
        {
            game.handleChoice(1);
        }
        finally
        {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains(
            "Starting your mission..."));
    }

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testViewStatusChoice()
    {
        Main game = new Main();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output =
            new ByteArrayOutputStream();

        System.setOut(new PrintStream(output));

        try
        {
            game.handleChoice(2);
        }
        finally
        {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains(
            "===== SHIP STATUS ====="));

        assertTrue(output.toString().contains(
            "Ship status is not available yet."));
    }

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testQuitChoice()
    {
        Main game = new Main();

        assertFalse(game.handleChoice(3));
    }

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testInvalidChoiceContinuesGame()
    {
        Main game = new Main();

        assertTrue(game.handleChoice(7));
    }

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testStartMissionContinuesGame()
    {
        Main game = new Main();

        assertTrue(game.handleChoice(1));
    }

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testViewStatusContinuesGame()
    {
        Main game = new Main();

        assertTrue(game.handleChoice(2));
    }
    
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public void testMenuDisplaysAllOptions()
    {
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream output =
            new ByteArrayOutputStream();

        System.setIn(new java.io.ByteArrayInputStream(
            "3\n".getBytes()));

        System.setOut(new PrintStream(output));

        try
        {
            Main game = new Main();
            game.startGame();
        }
        finally
        {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String result = output.toString();

        assertTrue(result.contains("1. Start mission"));
        assertTrue(result.contains("2. View status"));
        assertTrue(result.contains("3. Quit"));
        assertTrue(result.contains("Thanks for playing!"));
    }
}
