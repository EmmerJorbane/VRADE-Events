import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 * UserTestCases
 *
 * This test class contains a variety of test cases
 * for the User class, testing the usability for
 * constructors, getters, setters, seat management,
 * and balance operations.
 *
 * @author Arnav Nayak
 * @version November 10th, 2025
 */
public class UserTestCases {

    private User user;
    private Seat seat1;
    private Seat seat2;
    private ArrayList<Seat> seats;

    @Before
    public void setUp() {
        seats = new ArrayList<>();
        UserManager um = new UserManager("user123.txt");
        user = new User("user123", false, "password123", 200.0, seats, um);
        EventManager em = new EventManager();
        seat1 = new Seat('A', 1, user, "S", "Concert", user.getUsername(), em);
        seat2 = new Seat('B', 2, user, "M", "Movie", user.getUsername(), em);

    }

    @Test(timeout = 1000)
    public void testConstructorAndGetters() {
        assertEquals("user123", user.getUsername());
        assertEquals(false, user.getAdmin());
        assertEquals("password123", user.getPassword());
        assertEquals(200.0, user.getBalance(), 0.001);
        assertTrue(user.getSeatList().isEmpty());
    }

    @Test(timeout = 1000)
    public void testSetters() {
        ArrayList<Seat> newSeats = new ArrayList<>();
        user.setUsername("newUser");
        user.setPassword("newPass");
        user.setBalance(500.0);
        user.setName("New Name");
        user.setSeatList(newSeats);

        assertEquals("newUser", user.getUsername());
        assertEquals("newPass", user.getPassword());
        assertEquals(500.0, user.getBalance(), 0.001);
        assertEquals("New Name", user.getName());
        assertEquals(newSeats, user.getSeatList());
    }

    @Test(timeout = 1000)
    public void testAddSeat() {
        user.addSeat(seat1);
        assertTrue(user.getSeatList().contains(seat1));
        assertEquals(user, seat1.getUser());
    }

    @Test(timeout = 1000)
    public void testRemoveSeat() {
        user.addSeat(seat1);
        user.addSeat(seat2);
        user.removeSeat(seat1);

        assertFalse(user.getSeatList().contains(seat1));
        assertNull(seat1.getUser());
        assertTrue(user.getSeatList().contains(seat2));
    }

    @Test(timeout = 1000)
    public void testSubtractBalance() {
        user.subtractBalance(50.0);
        assertEquals(150.0, user.getBalance(), 0.001);
    }

    @Test(timeout = 1000)
    public void testAddBalance() {
        user.addBalance(25.0);
        assertEquals(225.0, user.getBalance(), 0.001);
    }
}
