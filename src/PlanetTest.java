import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlanetTest
{
    private Planet slow;  // local clocks run slow: 1 local hour = 2 Earth hours
    private Planet fast;  // local clocks run fast: 1 local hour = 30 Earth minutes
    private Planet same;
 
    @BeforeEach
    void setUp() {
        slow = new Planet("Slow", 2.0);
        fast = new Planet("Fast", 0.5);
        same = new Planet("Same", 1.0);
    }
 
    // ---- localToEarth ----
 
    @Test
    void localToEarthSlowPlanet() {
        assertEquals(Duration.ofHours(2), slow.localToEarth(Duration.ofHours(1)));
    }
 
    @Test
    void localToEarthFastPlanet() {
        assertEquals(Duration.ofMinutes(30), fast.localToEarth(Duration.ofHours(1)));
    }
 
    @Test
    void localToEarthSameRate() {
        assertEquals(Duration.ofHours(1), same.localToEarth(Duration.ofHours(1)));
    }
 
    @Test
    void localToEarthNonWholeFactor() {
        Planet p = new Planet("OneAndHalf", 1.5);
        assertEquals(Duration.ofMinutes(90), p.localToEarth(Duration.ofHours(1)));
    }
 
    @Test
    void localToEarthZeroStaysZero() {
        assertEquals(Duration.ZERO, slow.localToEarth(Duration.ZERO));
    }
 
    // ---- earthToLocal ----
 
    @Test
    void earthToLocalSlowPlanet() {
        assertEquals(Duration.ofMinutes(30), slow.earthToLocal(Duration.ofHours(1)));
    }
 
    @Test
    void earthToLocalFastPlanet() {
        assertEquals(Duration.ofHours(2), fast.earthToLocal(Duration.ofHours(1)));
    }
 
    @Test
    void earthToLocalSameRate() {
        assertEquals(Duration.ofHours(1), same.earthToLocal(Duration.ofHours(1)));
    }
    
    @Test
    void gettersReturnConstructorValues() {
        assertEquals("Slow", slow.getName());
        assertEquals(2.0, slow.getDilate());
    }
}
