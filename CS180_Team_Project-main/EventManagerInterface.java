import java.util.ArrayList;
/**
 * EventManagerInterface
 *
 * This interface allows any type to act as an Event Manager,
 * essentially allowing them to alter Events (admin only)
 *
 * @author Emma Jordan,
 *
 * @version Nov 8, 2025
 *
 */
public interface EventManagerInterface {

    // create a new event
    String createEvent(String eventName, String theatreT, int year, int month, int day, int hour, int minute);

    // delete event, true if successful
    public String deleteEvent(Event e);

    // find event, true if successful
    public Event findEvent(String name);

    // returns an ArrayList of all events
    public ArrayList<Event> getAllEvents();

    // we can use this to populate the events array
    public void loadEvents();

    // to populate the file based on events array
    public void saveEvents();

    // we can use this to load in seats
    public void loadSeats();

    // we can use this to save seats to file
    public void saveSeats();
    
    // we can use this to parse into an event
    public Event parseEvents(String line);

}
