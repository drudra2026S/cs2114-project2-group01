
// -------------------------------------------------------------------------
/**
 * A linked chain data structure that can go in all four directions.
 * 
 * @author Ami Sato (amigato)
 * @version Oct 11, 2026
 */
public class FourWayLink<T>
{
    // -------------------------------------------------------------------------
    /**
     * An enum of the four different directions the next node can go in, with a
     * helper method opposite() to make it easier to go in "opposite" direction
     */
    public enum Direction
    {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction opposite()
        {
            switch (this)
            {
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                case LEFT:
                    return RIGHT;
                default:
                    return LEFT;
            }
        }
    }


    // -------------------------------------------------------------------------
    /**
     * Inner Node class of linked chain
     * 
     * @param <T>
     *            Type of object being stored in Node
     */
    public static class Node<T>
    {
        public T value;
        public Node<T> up, down, left, right;

        public Node(T value)
        {
            this.value = value;
        }


        public Node<T> get(Direction d)
        {
            switch (d)
            {
                case UP:
                    return up;
                case DOWN:
                    return down;
                case LEFT:
                    return left;
                default:
                    return right;
            }
        }


        private void set(Direction d, Node<T> n)
        {
            switch (d)
            {
                case UP:
                    up = n;
                    break;
                case DOWN:
                    down = n;
                    break;
                case LEFT:
                    left = n;
                    break;
                default:
                    right = n;
                    break;
            }
        }


        @Override
        public String toString()
        {
            return String.valueOf(value);
        }
    }

    private Node<T> head;
    private int size;

    public Node<T> head()
    {
        return head;
    }


    public int size()
    {
        return size;
    }


    public boolean isEmpty()
    {
        return size == 0;
    }


    // ----------------------------------------------------------
    /**
     * Adds a head Node to the linked chain, but just returns original head if
     * structure isn't empty.
     * 
     * @param value
     * @return either new head or original head if it wasn't already empty
     */
    public Node<T> addRoot(T value)
    {
        if (head == null)
        {
            head = new Node<>(value);
            size = 1;
        }
        return head;
    }


    // ----------------------------------------------------------
    /**
     * Adds a Node and connects it to a parent in a certain direction.
     * @param from Parent node
     * @param d    Direction to go in
     * @param value Data of new Node
     * @return the new Node
     */
    public Node<T> add(Node<T> from, Direction d, T value)
    {
        Node<T> node = new Node<>(value);
        Node<T> existing = from.get(d);

        from.set(d, node);
        node.set(d.opposite(), from);

        if (existing != null)
        {
            node.set(d, existing);
            existing.set(d.opposite(), node);
        }

        size++;
        return node;
    }
    
    /**
     * Removes a node. Its left/right neighbors are joined to each other,
     * and so are its up/down neighbors.
     * 
     * @param n Node to be removed
     */
    public void remove(Node<T> n) {
        if (n.left != null)  n.left.right = n.right;
        if (n.right != null) n.right.left = n.left;
        if (n.up != null)    n.up.down = n.down;
        if (n.down != null)  n.down.up = n.up;
 
        if (head == n) {
            head = n.right != null ? n.right
                 : n.left  != null ? n.left
                 : n.down  != null ? n.down
                 : n.up;
        }
 
        n.up = n.down = n.left = n.right = null;
        size--;
    }

}
