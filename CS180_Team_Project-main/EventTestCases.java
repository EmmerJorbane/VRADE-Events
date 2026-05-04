import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * EventTestCases
 *
 * A framework to run public test cases for the Event Class
 *
 * @author Ryan Poplar, L06
 * @version Nov 10th, 2025
 */

@RunWith(Enclosed.class)
public class EventTestCases {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(EventTestCases.TestCase.class);
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
     * This class contains all the test cases run to test the Event class.
     * It is intended to be run with JUnit and will output any error it runs
     * into while testing instances that could cause issues in the program.
     *
     * @author Ryan Poplar, L06
     * @version Nov 10th, 2025
     */
    public static class TestCase {

        @Test(timeout = 1000)
        public void testGetSetEventName() {
            EventManager em = new EventManager();
            Event e = new Event("Concert", "S", 2025, 11, 10, 20, 0, em);

            Assert.assertEquals("Concert", e.getEventName());

            e.setEventName("Show");
            Assert.assertEquals("Show", e.getEventName());

            e.setEventName("");
            Assert.assertEquals("", e.getEventName());

            e.setEventName(null);
            Assert.assertNull(e.getEventName());
        }

        @Test(timeout = 1000)
        public void testGetSetTheatreType() {
            Event e = new Event("Concert", "S", 2025, 11, 10, 20, 0, new EventManager());

            e.setTheatreType("M");
            Assert.assertEquals("M", e.getTheatreType());

            e.setTheatreType("L");
            Assert.assertEquals("L", e.getTheatreType());

            e.setTheatreType("X");
            Assert.assertEquals("L", e.getTheatreType());
        }

        @Test(timeout = 1000)
        public void testGetSetSeats() {
            Event e = new Event("Concert", "S", 2025, 11, 10, 20, 0, new EventManager());
            Seat[][] testSeats = new Seat[12][12];
            for (int i = 0; i < testSeats.length; i++) {
                for (int j = 0; j < testSeats[i].length; j++) {
                    testSeats[i][j] = new Seat();
                }
            }
            e.setSeats(testSeats);
            Assert.assertSame(testSeats, e.getSeats());
            Assert.assertTrue(e.getSeats()[0][1].getOpen());
        }

        @Test(timeout = 1000)
        public void getSeatTest() {
            EventManager em = new EventManager();
            Event test = new Event("Test", "S", 2025, 01, 01, 0, 0, em);
            Assert.assertEquals("A1", test.getSeat('A', 1).getSeat());
        }

        @Test(timeout = 1000)
        public void testCreateSeats() {
            EventManager em = new EventManager();
            Event small = new Event("SmallEvent", "S", 2025, 11, 10, 20, 0, em);
            Assert.assertEquals("SmallEvent", small.getEventName());
            Assert.assertEquals("S", small.getTheatreType());
            Assert.assertEquals(12, small.getSeats().length);
            Assert.assertEquals(12, small.getSeats()[0].length);
            Assert.assertNotNull(small.getSeats()[0][0]);

            Event medium = new Event("MediumEvent", "M", 2025, 11, 10, 20, 0, em);
            Assert.assertEquals("MediumEvent", medium.getEventName());
            Assert.assertEquals("M", medium.getTheatreType());
            Assert.assertEquals(18, medium.getSeats().length);
            Assert.assertEquals(18, medium.getSeats()[0].length);
            Assert.assertNotNull(medium.getSeats()[0][0]);

            Event large = new Event("LargeEvent", "L", 2025, 11, 10, 20, 0, em);
            Assert.assertEquals("LargeEvent", large.getEventName());
            Assert.assertEquals("L", large.getTheatreType());
            Assert.assertEquals(24, large.getSeats().length);
            Assert.assertEquals(24, large.getSeats()[0].length);
            Assert.assertNotNull(large.getSeats()[0][0]);

            Event garbage = new Event("Garbage", "G", 2025, 0, 10, 0, 0, null);
            Assert.assertEquals("Garbage", garbage.getEventName());
            Assert.assertEquals("S", garbage.getTheatreType());
            Assert.assertEquals(12, garbage.getSeats().length);
            Assert.assertEquals(12, garbage.getSeats()[0].length);
            Assert.assertNotNull(garbage.getSeats()[0][0]);
        }

        @Test(timeout = 1000)
        public void testReserveSingleSeat() {
            EventManager em = new EventManager();
            Event small = new Event("SmallEvent", "S", 2025, 11, 10, 20, 0, em);
            User u = new User("rpop1000", false, "0000", 100, new ArrayList<Seat>(), new UserManager("rpop1000.txt"));
            User u2 = new User("rpop2000", false, "1111", 100, new ArrayList<Seat>(), new UserManager("rpop2000.txt"));
            boolean test = small.reserveSingleSeat(u, "A8");
            Assert.assertTrue(test);

            boolean test2 = small.reserveSingleSeat(u2, "A8");
            Assert.assertFalse(test2);

            boolean test3 = small.reserveSingleSeat(u, "A13");
            Assert.assertFalse(test3);

            boolean test4 = small.reserveSingleSeat(u, "Z3");
            Assert.assertFalse(test4);

            boolean test5 = small.reserveSingleSeat(u, "asgeagaf");
            Assert.assertFalse(test5);


        }

        @Test(timeout = 1000)
        public void testCancelSingleSeat() {
            EventManager em = new EventManager();
            Event small = new Event("SmallEvent", "S", 2025, 11, 10, 20, 0, em);
            User u = new User("rpop1000", false, "0000", 100, new ArrayList<Seat>(), new UserManager("rpop1000.txt"));
            User u2 = new User("rpop2000", false, "1111", 100, new ArrayList<Seat>(), new UserManager("rpop2000.txt"));
            small.reserveSingleSeat(u, "A8");

            boolean test = small.cancelSingleSeat(u2, "A8");
            Assert.assertFalse(test);

            boolean test2 = small.cancelSingleSeat(u, "A8");
            Assert.assertTrue(test2);

            boolean test3 = small.cancelSingleSeat(u2, "Z8");
            Assert.assertFalse(test3);

            boolean test4 = small.cancelSingleSeat(u, "A13");
            Assert.assertFalse(test4);

            boolean test5 = small.cancelSingleSeat(u, "asfasfwfwa");
            Assert.assertFalse(test5);



        }

        @Test(timeout = 1000)
        public void testReserveMultipleSeats() {
            EventManager em = new EventManager();
            Event small = new Event("SmallEvent", "S", 2025, 11, 10, 20, 0, em);
            User u = new User("rpop1000", false, "0000", 5000, new ArrayList<Seat>(), new UserManager("rpop1000.txt"));
            User u2 = new User("rpop2000", false, "1111", 0, new ArrayList<Seat>(), new UserManager("rpop2000.txt"));

            small.reserveMultipleSeats(u, "A1A12");

            boolean test = small.getSeat('A', 5).getUser().equals(u);
            Assert.assertTrue(test);

            boolean test2 = small.reserveMultipleSeats(u2, "A3A7");
            Assert.assertFalse(test2);

            boolean test3 = small.getSeat('A', 5).getUser().equals(u2);
            Assert.assertFalse(test3);

            boolean test4 = small.reserveMultipleSeats(u2, "B3C7");
            Assert.assertFalse(test4);

            boolean test5 = small.reserveMultipleSeats(u2, "asfasfwfwa");
            Assert.assertFalse(test5);

            boolean test6 = small.reserveMultipleSeats(u2, "B3B13");
            Assert.assertFalse(test6);

        }

        @Test(timeout = 1000)
        public void testCancelMultipleSeats() {
            EventManager em = new EventManager();
            Event small = new Event("SmallEvent", "S", 2025, 11, 10, 20, 0, em);

            User u = new User("rpop1000", false, "0000", 100, new ArrayList<Seat>(), new UserManager("rpop1000.txt"));
            User u2 = new User("rpop2000", false, "1111", 0, new ArrayList<Seat>(), new UserManager("rpop2000.txt"));

            boolean test1 = small.reserveMultipleSeats(u, "A1A12");
            Assert.assertTrue(test1);

            Assert.assertEquals(u, small.getSeat('A', 5).getUser());

            boolean test2 = small.cancelMultipleSeats(u2, "A1A12");
            Assert.assertFalse(test2);
            Assert.assertEquals(u, small.getSeat('A', 4).getUser());

            boolean test3 = small.cancelMultipleSeats(u, "A3A7");
            Assert.assertTrue(test3);
            for (int i = 3; i <= 7; i++) {
                Assert.assertNull(small.getSeat('A', i).getUser());
            }

            boolean test4 = small.reserveMultipleSeats(u2, "A3A7");
            Assert.assertTrue(test4);
            Assert.assertEquals(u2, small.getSeat('A', 4).getUser());

            boolean test5 = small.cancelMultipleSeats(u2, "A3A6");
            Assert.assertTrue(test5);

            Assert.assertEquals(u, small.getSeat('A', 2).getUser());
            Assert.assertNull(small.getSeat('A', 3).getUser());
            Assert.assertNull(small.getSeat('A', 5).getUser());

            boolean test6 = small.cancelMultipleSeats(u2, "B3C7");
            Assert.assertFalse(test6);

            boolean test7 = small.cancelMultipleSeats(u2, "asfasfwfwa");
            Assert.assertFalse(test7);

            boolean test8 = small.cancelMultipleSeats(u2, "B3B13");
            Assert.assertFalse(test8);
        }

        @Test(timeout = 1000)
        public void getYearTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals(2025, test.getYear());
        }

        @Test(timeout = 1000)
        public void getMonthTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals(1, test.getMonth());
        }

        @Test(timeout = 1000)
        public void getDayTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals(1, test.getDay());
        }

        @Test(timeout = 1000)
        public void getMinuteTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals(0, test.getMinute());
        }

        @Test(timeout = 1000)
        public void getHourTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals(0, test.getHour());
        }


        @Test(timeout = 1000)
        public void getFullTimeTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals("2025-01-01T00:00", test.getFullTime());
        }

        //iffy about
        @Test(timeout = 1000)
        public void setFullTimeTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            test.setFullTime("2026-02-02 01:01");
            boolean test2 = test.setFullTime("2026-0asfhg02 02:01");
            Assert.assertFalse(test2);
            Assert.assertEquals("2026-02-02T01:01", test.getFullTime());
        }

        @Test(timeout = 1000)
        public void toStringTest() {
            EventManager em =  new EventManager();
            Event test = new Event("Test", "S", 2025, 1, 1, 0, 0, em);
            Assert.assertEquals("Test (S) at 2025-01-01T00:00", test.toString());
        }


    }




}
