import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
/**
 * SeatTestCases
 *
 * A framework to run public test cases for Seat
 *
 * @author Vaibhav Kanagala, L06
 * @version Nov 10th, 2025
 */

public class SeatRunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }


    /**
     * TestCase
     * 
     * This class contains all of the test cases that will be run using JUnit to test the Seat class.
     * It will print out any errors that it runs into along the way.
     */
    public static class TestCase {

        //Tests to see if getters work properly
        @Test(timeout = 1000)
        public void gettersTest() {
            User bob = new User("bob123", false, "0000", 0, new ArrayList<Seat>(), new UserManager("bob.txt"));
            EventManager em = new EventManager();
            Seat seat1 = new Seat('A', 1, bob, "S", "Avengers", "bob123", em);
            Assert.assertEquals('A', seat1.getRow());
            Assert.assertEquals(1, seat1.getNum());
            Assert.assertEquals(false, seat1.getOpen());
            Assert.assertEquals("A1", seat1.getSeat());
            Assert.assertEquals(10, seat1.getValue(), 0.0001);
            Assert.assertEquals(bob, seat1.getUser());
            Assert.assertEquals("S", seat1.getSize());
            Assert.assertEquals("Avengers", seat1.getEvent());
            Assert.assertEquals("bob123", seat1.getUsername());
        }

        //Tests to see if setters work properly
        @Test(timeout = 1000)
        public void settersTest() {
            User bob = new User("bob123", false, "0000", 0, new ArrayList<Seat>(), new UserManager("bob.txt"));
            Seat seat2 = new Seat();
            seat2.setRow('E');
            seat2.setNum(5);
            seat2.setUser(bob);
            seat2.setEvent("Avengers");
            seat2.setUsername("bob123");


            Assert.assertEquals('E', seat2.getRow());
            Assert.assertEquals(5, seat2.getNum());
            Assert.assertEquals(false, seat2.getOpen());
            Assert.assertEquals("E5", seat2.getSeat());
            Assert.assertEquals(14, seat2.getValue(), 0.0001);
            Assert.assertEquals(bob, seat2.getUser());
            Assert.assertEquals("S", seat2.getSize());
            Assert.assertEquals("Avengers", seat2.getEvent());
            Assert.assertEquals("bob123", seat2.getUsername());

            seat2.setUser(null);
            Assert.assertEquals(true, seat2.getOpen());
            Assert.assertEquals(null, seat2.getUser());
        }

        // Test to see if toString works
        @Test(timeout = 1000)
        public void toStringTest() {
            User bob = new User("bob123", false, "0000", 0, new ArrayList<Seat>(), new UserManager("bob.txt"));
            Seat seat2 = new Seat();
            seat2.setRow('E');
            seat2.setNum(5);
            seat2.setUser(bob);
            seat2.setEvent("Avengers");
            seat2.setUsername("bob123");


            Assert.assertEquals(
                "Seat - movie: Avengers, reserved by: bob123, row: E, col: 5, cost: 14.00",  
                seat2.toString());
        }


        //Tests to see is setValue method works properly    
        @Test(timeout = 1000)
        public void setValueTest() {
            User bob = new User("bob123", false, "0000", 0, new ArrayList<Seat>(), new UserManager("bob.txt"));
            EventManager em = new EventManager();

            Seat smallSeat1 = new Seat('A', 1, bob, "S", "Avengers", "bob123", em);
            Seat smallSeat2 = new Seat('E', 5, bob, "S", "Avengers", "bob123", em);
            Seat smallSeat3 = new Seat('Z', 5, bob, "S", "Avengers", "bob123", em);

            Seat medSeat1 = new Seat('A', 1, bob, "M", "Avengers", "bob123", em);
            Seat medseat2 = new Seat('G', 7, bob, "M", "Avengers", "bob123", em);
            Seat medSeat3 = new Seat('Z', 7, bob, "M", "Avengers", "bob123", em);

            Seat largeSeat1 = new Seat('A', 1, bob, "L", "Avengers", "bob123", em);
            Seat largeSeat2 = new Seat('I', 9, bob, "L", "Avengers", "bob123", em);
            Seat largeSeat3 = new Seat('Z', 9, bob, "L", "Avengers", "bob123", em);

            Assert.assertEquals(10, smallSeat1.getValue(), 0.0001);
            Assert.assertEquals(14, smallSeat2.getValue(), 0.0001);
            Assert.assertEquals(12, smallSeat3.getValue(), 0.0001);

            Assert.assertEquals(10, medSeat1.getValue(), 0.0001);
            Assert.assertEquals(14, medseat2.getValue(),  0.0001);
            Assert.assertEquals(12, medSeat3.getValue(), 0.0001);

            Assert.assertEquals(10, largeSeat1.getValue(), 0.0001);
            Assert.assertEquals(14, largeSeat2.getValue(), 0.0001);
            Assert.assertEquals(12, largeSeat3.getValue(), 0.0001);
        }


















    }
}
