
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class FourWayLinkTest
{
    private FourWayLink<String> list;
    private FourWayLink.Node<String> a;
    
    // Runs before every test, so each test starts with a fresh list containing just "A"
    @BeforeEach
    void setUp() {
        list = new FourWayLink<>();
        a = list.addRoot("A");
    }
 
    @Test
    void newListIsEmpty() {
        FourWayLink<String> empty = new FourWayLink<>();
        assertTrue(empty.isEmpty());
        assertNull(empty.head());
    }
 
    @Test
    void addRootSetsHeadAndSize() {
        assertSame(a, list.head());
        assertEquals(1, list.size());
    }
 
    @Test
    void addRootTwiceKeepsOriginal() {
        FourWayLink.Node<String> again = list.addRoot("Z");
        assertSame(a, again);
        assertEquals(1, list.size());
    }
 
    @Test
    void addRightLinksBothWays() {
        FourWayLink.Node<String> b = list.add(a, FourWayLink.Direction.RIGHT, "B");
        assertSame(b, a.right);
        assertSame(a, b.left);
        assertEquals(2, list.size());
    }
 
    @Test
    void addDownLinksBothWays() {
        FourWayLink.Node<String> c = list.add(a, FourWayLink.Direction.DOWN, "C");
        assertSame(c, a.down);
        assertSame(a, c.up);
    }
 
    @Test
    void addInOccupiedSpotSplicesBetween() {
        FourWayLink.Node<String> b = list.add(a, FourWayLink.Direction.RIGHT, "B");
        FourWayLink.Node<String> x = list.add(a, FourWayLink.Direction.RIGHT, "X");
 
        assertSame(x, a.right);
        assertSame(a, x.left);
        assertSame(b, x.right);
        assertSame(x, b.left);
    }
 
    @Test
    void removeMiddleJoinsNeighbors() {
        FourWayLink.Node<String> b = list.add(a, FourWayLink.Direction.RIGHT, "B");
        FourWayLink.Node<String> x = list.add(a, FourWayLink.Direction.RIGHT, "X");
 
        list.remove(x);
 
        assertSame(b, a.right);
        assertSame(a, b.left);
        assertNull(x.left);
        assertNull(x.right);
        assertEquals(2, list.size());
    }
 
    @Test
    void removeHeadMovesHead() {
        FourWayLink.Node<String> b = list.add(a, FourWayLink.Direction.RIGHT, "B");
        list.remove(a);
        assertSame(b, list.head());
        assertNull(b.left);
    }
 
    @Test
    void removeOnlyNodeEmptiesList() {
        list.remove(a);
        assertTrue(list.isEmpty());
        assertNull(list.head());
    }
 
    @Test
    void getReturnsNeighborInGivenDirection() {
        FourWayLink.Node<String> b = list.add(a, FourWayLink.Direction.RIGHT, "B");
        assertSame(b, a.get(FourWayLink.Direction.RIGHT));
        assertNull(a.get(FourWayLink.Direction.UP));
    }
 
    @Test
    void oppositeDirections() {
        assertEquals(FourWayLink.Direction.DOWN, FourWayLink.Direction.UP.opposite());
        assertEquals(FourWayLink.Direction.LEFT, FourWayLink.Direction.RIGHT.opposite());
    }

}
