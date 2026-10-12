import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------

/**
 * Test class for Main testing all possibilities.
 *
 * @author zach1
 * @version Oct 12, 2026
 */
public class MainTest
{
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    /**
     * Sets up console input and output for each test.
     */
    @BeforeEach
    public void setUp()
    {
        originalIn = System.in;
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    /**
     * Restores the original console input and output.
     */
    @AfterEach
    public void tearDown()
    {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Creates a game with the supplied simulated console input.
     *
     * @param input
     *            the input the game should receive
     * @return a new Main game
     */
    private Main createGame(String input)
    {
        String completeInput = "2\n" + input;

        System.setIn(new ByteArrayInputStream(
            completeInput.getBytes(StandardCharsets.UTF_8)));

        return new Main();
    }

    /**
     * Tests that an invalid menu choice displays an error.
     */
    @Test
    public void testInvalidMenuChoice()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(99));
        assertTrue(output.toString().contains("Invalid choice"));
    }

    /**
     * Tests that starting a mission begins the travel process.
     */
    @Test
    public void testStartMissionChoice()
    {
        Main game = createGame("0\n");

        assertTrue(game.handleChoice(1));
        assertTrue(output.toString().contains(
            "Continuing your expedition"));
    }

    /**
     * Tests that viewing the status displays ship information.
     */
    @Test
    public void testViewStatusChoice()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(2));
        assertTrue(output.toString().contains(
            "===== SHIP STATUS ====="));
        assertTrue(output.toString().contains("Health:"));
    }

    /**
     * Tests that choosing quit ends the game.
     */
    @Test
    public void testQuitChoice()
    {
        Main game = createGame("");

        assertFalse(game.handleChoice(5));
    }

    /**
     * Tests that an invalid choice does not end the game.
     */
    @Test
    public void testInvalidChoiceContinuesGame()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(99));
    }

    /**
     * Tests that starting a mission keeps the game running.
     */
    @Test
    public void testStartMissionContinuesGame()
    {
        Main game = createGame("0\n");

        assertTrue(game.handleChoice(1));
    }

    /**
     * Tests that viewing ship status keeps the game running.
     */
    @Test
    public void testViewStatusContinuesGame()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(2));
    }

    /**
     * Tests that the menu displays all five options.
     */
    @Test
    public void testMenuDisplaysAllOptions()
    {
        Main game = createGame("5\n");
        game.startGame();

        assertTrue(output.toString().contains(
            "1. Travel to another planet"));
        assertTrue(output.toString().contains("2. View status"));
        assertTrue(output.toString().contains(
            "3. View current missions"));
        assertTrue(output.toString().contains(
            "4. Repair ship / fix engine"));
        assertTrue(output.toString().contains("5. Quit"));
    }

    /**
     * Tests that nonnumeric input is handled without crashing.
     */
    @Test
    public void testNonNumberInput()
    {
        Main game = createGame("hello\n5\n");
        game.startGame();

        assertTrue(output.toString().contains(
            "Please enter a number"));
    }

    /**
     * Tests that the game has not been won at the start.
     */
    @Test
    public void testCheckWinInitiallyFalse()
    {
        Main game = createGame("");

        assertFalse(game.checkWin());
    }

    /**
     * Tests that the game has not been lost at the start.
     */
    @Test
    public void testCheckLossInitiallyFalse()
    {
        Main game = createGame("");

        assertFalse(game.checkLoss());
    }

    /**
     * Tests that viewing current missions keeps the game running.
     */
    @Test
    public void testViewMissionsChoice()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(3));
    }

    /**
     * Tests that repairing the ship keeps the game running.
     */
    @Test
    public void testRepairShipChoice()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(4));
    }

    /**
     * Tests that a negative menu choice does not end the game.
     */
    @Test
    public void testNegativeMenuChoice()
    {
        Main game = createGame("");

        assertTrue(game.handleChoice(-1));
        assertTrue(output.toString().contains("Invalid choice"));
    }
}