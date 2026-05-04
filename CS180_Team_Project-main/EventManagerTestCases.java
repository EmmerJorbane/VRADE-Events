import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import java.time.*;
import org.junit.Before;

import java.io.*;
import java.util.ArrayList;
/**
 * EventManagerTestCases
 *
 * A framework to run public test cases for Event Manager
 *
 * @author Emma Jordan,
 *
 * @version Nov 8, 2025
 */
@RunWith(Enclosed.class)
public class EventManagerTestCases {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(EventManagerTestCases.EventManagerTests.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
    /**
     * EventManagerTestCases
     *
     * A framework to run public test cases for Event Manager
     *
     * @author Emma Jordan,
     *
     * @version Nov 8, 2025
     */
    public static class EventManagerTests {

        private EventManager manager = new  EventManager();

        // Clears any existing events before testing
        @Before
        public void setup() {
            manager = new EventManager();

            ArrayList<Event> current = manager.getAllEvents();
            current.clear();

            File f = new File("Events.txt");
            if (f.exists()) {
                f.delete();
            }
        }

        // tests to see if an event is created properly
        @Test(timeout = 1000)
        public void createEventTest() {
            LocalDateTime now = LocalDateTime.now();
            manager.createEvent("TestMovie", "S", now.getYear(), now.getMonthValue(), 
                                now.getDayOfYear(), now.getHour(), now.getMinute());

            ArrayList<Event> events = manager.getAllEvents();
            Assert.assertEquals(1, events.size());
            Assert.assertEquals("TestMovie", events.get(0).getEventName());

        }
        // tests to see if an event is deleted properly
        @Test(timeout = 1000)
        public void deleteEventTest() {
            LocalDateTime now = LocalDateTime.now();
            manager.createEvent("TestMovie", "S", now.getYear(), now.getMonthValue(), 
                                now.getDayOfYear(), now.getHour(), now.getMinute());
            String deleted = manager.deleteEvent(manager.findEvent("TestMovie"));

            Assert.assertSame("The showing has been removed", deleted);
            Assert.assertTrue(manager.getAllEvents().isEmpty());
        }

        // tests to see if it can find an event succesfully
        @Test(timeout = 1000)
        public void findEventTest() {
            LocalDateTime now = LocalDateTime.now();
            manager.createEvent("TestMovie", "S", now.getYear(), now.getMonthValue(), 
                                now.getDayOfYear(), now.getHour(), now.getMinute());

            Event found = manager.findEvent("TestMovie");
            Assert.assertNotNull(found);
            Assert.assertEquals("TestMovie", found.getEventName());
        }

        // checks to see if all events are found
        @Test(timeout = 1000)
        public void getAllEventsTest() {
            LocalDateTime now = LocalDateTime.now();
            manager.createEvent("One", "S", now.getYear(), now.getMonthValue(), 
                                now.getDayOfMonth(), now.getHour(), now.getMinute());
            manager.createEvent("Two", "S", now.getYear(), now.getMonthValue(), 
                                now.getDayOfMonth(), now.getHour(), now.getMinute());

            ArrayList<Event> list = manager.getAllEvents();
            Assert.assertEquals(2, list.size());
        }

        // checks to see if events were taken from the file to local memory correctly
        @Test(timeout = 1000)
        public void loadEventsTest() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Events.txt"))) {
                bw.write("eventName,2025-11-08T12:00:00");
                bw.newLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            manager.loadEvents();
            Event foundEvent = manager.findEvent("eventName");
            Assert.assertNotNull(foundEvent);
            Assert.assertEquals("eventName", foundEvent.getEventName());
        }

        // checks if the event has been written to the file correctly
        @Test(timeout = 1000)
        public void saveEventsTest() {
            LocalDateTime now = LocalDateTime.now();
            manager.createEvent("TestMovie", "S", now.getYear(), now.getMonthValue(), 
                                now.getDayOfMonth(), now.getHour(), now.getMinute());
            manager.saveEvents();

            try (BufferedReader br = new BufferedReader(new FileReader("Events.txt"))) {
                String line = br.readLine();
                Assert.assertNotNull("File should contain at least one line", line);
                Assert.assertTrue("File should start with event name", line.startsWith("TestMovie,"));

            } catch (IOException ex) {
                Assert.fail("IOException occurred: " + ex.getMessage());
            }


        }

        // tests to see if events is not null but is empty (correct instantiation)
        @Test(timeout = 1000)
        public void eventManagerTest() {
            EventManager em = new EventManager();
            Assert.assertNotNull(em.getAllEvents());
            Assert.assertTrue(em.getAllEvents().isEmpty());
        }

        @Test(timeout = 1000)
        public void saveSeatsTest() {
            // Arrange the seats
            EventManager em = new EventManager();
            File f = new File("User.txt");
            UserManager um = new UserManager("User.txt");

            ArrayList<Seat> userSeats = new ArrayList<>();
            User user = new User("emma", false, "pass123", 100.0, userSeats, um);
            Event ev = new Event("Inside Out 2", "S", 2025, 11, 20, 19, 0, em);
            Seat[][] seats = ev.getSeats();

            // Assigned seatssss
            Seat taken = seats[0][0];  // A0
            taken.setUser(user);
            taken.setUsername(user.getUsername());
            taken.setEvent(ev.getEventName());

            Seat open = seats[0][1];   // A1
            open.setUser(null);
            open.setEvent(ev.getEventName());
            open.setUsername("");

            // this clears the list to make sure it doesnt affect the test case
            ArrayList<Event> events = em.getAllEvents();
            events.clear();
            events.add(ev);
            em.saveSeats();


            File seatFile = new File("Seats.txt");
            Assert.assertTrue("Seats.txt should exist after saveSeats() method has been called", seatFile.exists());

            ArrayList<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(seatFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // makes sure there are 144 seats in a small theater and it has the right name and take seat
            Assert.assertEquals(144, lines.size());
            String firstLine = lines.get(0);
            Assert.assertTrue("Taken seat line should contain username",
                    firstLine.contains("emma"));
            Assert.assertTrue("Line should contain event name",
                    firstLine.startsWith("Inside Out 2,"));

        }

    }
}
