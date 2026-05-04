import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * This server class is responsible for server side commands for our movie theater reservation program, that
 * operates on port 4242. The server's first responsibility ensuring a secure transfering of information from
 * the client side application to the event, seats and user databases. It can handle multiple clients through a
 * safe, multithreaded approach, parses user commands, read and write database files, and formats a response
 * for the client.
 * This server runs on a continuous loop until the client requests it to be exited or until a critical error
 * occurs.
 *
 * @author David Goldfuss, Vaibhav Kanagala, Section L06
 *
 * @version Dec 6, 2025
 *
 */

public class Server implements ServerInterface, Runnable {
    UserManager userManager;
    EventManager eventManager;
    User loggedInUser;
    Socket socket;
    public Server(String userFileName, Socket socket) {
        userManager = new UserManager(userFileName);
        eventManager = new EventManager();
        this.socket = socket;
        userManager.readUsers();
        eventManager.loadEvents();
        eventManager.loadSeats();
    }
    private boolean loginUser(String username, String password) {
        User user = userManager.queryUsers(username);
        if (user == null) {
            return false;
        }
        this.loggedInUser = user;
        return userManager.validateUser(user, password);
    }

    private boolean createUser(String username, String name, String password) throws UserCreationException {
        User user = userManager.makeUser(username, false, password, 100.0, new ArrayList<Seat>());
        return true;
    }

    private void deleteAccount() {
        userManager.deleteUser(loggedInUser);
        loggedInUser = null;
    }

    private double getUserBalance() {
        return loggedInUser.getBalance();
    }

    private void updatePassword(String newPassword) {
        loggedInUser.setPassword(newPassword);
        userManager.writeUsers();
    }

    private boolean updateUsername(String newUsername) {
        if (userManager.queryUsers(newUsername) == null) {
            loggedInUser.setUsername(newUsername);
            return true;
        }
        return false;
    }

    private void updateName(String name) {
        loggedInUser.setName(name);
    }

    private ArrayList<String> listEventTimes() {
        ArrayList<Event> allEvents = eventManager.getAllEvents();
        ArrayList<String> eventTimes = new ArrayList<String>();

        for (Event event : allEvents) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String time = event.getTime().format(formatter);
            if (!eventTimes.contains(time)) {
                eventTimes.add(time);
            }
        }
        return eventTimes;
    }

    private ArrayList<String> listEvents() {
        ArrayList<Event> allEvents = eventManager.getAllEvents();
        ArrayList<String> eventMatches = new ArrayList<String>();

        for (Event event : allEvents) {
            int daysFromToday = Math.abs(Math.round(Duration.between(event.getTime(), LocalDateTime.now()).toDays()));
            // If the event takes place between 0 and 90 days from today
            // then add it to the valid events
            if (daysFromToday < 90 && daysFromToday > 0) {
                String format = event.getEventName();
                format += ", " + event.getMonth();
                format += "/" + event.getDay();
                format += " " + event.getHour();
                format += ":" + event.getMinute();
                if (event.getMinute() < 10) {
                    format += "0";
                }
                eventMatches.add(format);
                System.out.println("days is less than 90");
            }
            System.out.println(event.getEventName());
        }
        return eventMatches;
    }

    private ArrayList<Seat> listSeatsAtEvent(String eventName) {
        Event event = eventManager.findEvent(eventName);
        Seat[][] seats = event.getSeats();
        ArrayList<Seat> seatOptions = new ArrayList<Seat>();
        for (int row = 0; row < seats.length; row++) {
            for (int col = 0; col < seats[row].length; col++) {
                Seat seat = seats[row][col];
                seatOptions.add(seat);
            }
        }
        return seatOptions;
    }

    private boolean reserveSeat(String eventName, String seatID) {
        Event event = eventManager.findEvent(eventName);
        return event.reserveSingleSeat(loggedInUser, seatID);
    }

    private boolean cancelSeat(String eventName, String seatStr) {
        Event event = eventManager.findEvent(eventName);
        return event.cancelSingleSeat(loggedInUser, seatStr);
    }

    private boolean reserveMultipleSeats(String eventName, String seatsString) {
        Event event = eventManager.findEvent(eventName);
        return event.reserveMultipleSeats(loggedInUser, seatsString);
    }

    private boolean cancelMultipleSeats(String eventName, String seatsString) {
        Event event = eventManager.findEvent(eventName);
        return event.cancelMultipleSeats(loggedInUser, seatsString);
    }

    private ArrayList<Seat> getSeatsByUser() {
        ArrayList<Event> eventsList = eventManager.getAllEvents();
        ArrayList<Seat> userSeats = new ArrayList<Seat>();
        for (Event event : eventsList) {
            for (Seat[] row : event.getSeats()) {
                for (Seat seat : row) {
                    if (seat.getUsername().equals(loggedInUser.getUsername())) {
                        userSeats.add(seat);
                    }
                }
            }
        }
        return userSeats;
    }

    private boolean mainCommandMenu(BufferedReader in, PrintWriter out) {
        userManager.readUsers();
        eventManager.loadEvents();
        eventManager.loadSeats();

        String mainmenu;
        String[] mainmenuSplit;
        String mainmenuCommand = "";
        try {
            mainmenu = in.readLine();
            mainmenuSplit = mainmenu.split(" ");
            mainmenuCommand = mainmenuSplit[0];
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }


        switch (mainmenuCommand) {
            case "VSEAT": {
                if (loggedInUser.getAdmin()) {
                    out.println("ADMIN");
                    out.flush();
                    break;
                }
                ArrayList<Seat> ownedByUser = getSeatsByUser();
                if (ownedByUser.size() <= 0) {
                    out.println("CUSTOMER NONE");
                    out.flush();
                }
                String seatsString = "CUSTOMER ";
                for (Seat seat : ownedByUser) {
                    seatsString += seat.toString() + ",";
                }
                seatsString = seatsString.substring(0, seatsString.length() - 1);
                out.println(seatsString);
                out.flush();
                //handles view seat
                break;
            }
            case "CANCEL": {
                String seatID = mainmenuSplit[1];
                String eventName = mainmenuSplit[2];
                cancelSeat(eventName, seatID);
                out.println("CANCLED SEAT");
                out.flush();
                break;
            }
            case "VBALANCE": {
                if (loggedInUser.getAdmin()) {
                    out.println(
                            String.format("ADMIN %.2f",
                                    loggedInUser.getBalance()));
                    out.flush();
                } else {
                    out.println(
                            String.format("CUSTOMER %.2f",
                                    loggedInUser.getBalance()));
                    out.flush();
                }
                break;
            }
            case "VEVENT" : {
                ArrayList<String> events = listEvents();
                for (String event : events) {
                    out.println(event);
                }
                out.println("ENDEVENT");
                out.flush();
                break;
            }
            case "ADMIN": {
                String output = null;
                if (loggedInUser.getAdmin()) {
                    output = "TRUE";
                } else {
                    output = "FALSE";
                }
                out.println(output);
                out.flush();
                break;
            }
            case "PICKEVENT": {
                Event event = eventManager.findEvent(mainmenuSplit[1]);
                if (event == null) {
                    out.println("FALSE");
                    out.flush();
                } else {
                    out.println("TRUE");
                    out.flush();
                }
                break;
            }
            case "EPROG" : {
                //handles program exit
                return false;
            }
            case "DLTSEAT" : {
                //handles view event
                break;
            }
            case "DEVENT" : {
                Event event = eventManager.findEvent(mainmenuSplit[1]);
                String deleted = eventManager.deleteEvent(event);
                if (deleted.equals("The showing has been removed")) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }
            case "PRINTSEATS" : {
                ArrayList<Seat> seats = listSeatsAtEvent(mainmenuSplit[1]);
                String output = "";
                for (Seat seat : seats) {
                    output += seat.toDataString();
                    output += "\n";
                }
                out.println(output);
                out.println("ENDSEATS");
                out.flush();
                break;
            }
            case "CTIME" : {
                String eventStr = mainmenuSplit[1];
                String timeStr = mainmenuSplit[2];
                Event event = eventManager.findEvent(eventStr);
                boolean success = event.setFullTime(timeStr);
                out.println(success ? "TRUE" : "FALSE");
                break;
            }
            case "CEVENT" : {
                String eventName = mainmenuSplit[1];
                String theatreType = mainmenuSplit[2];
                int year = Integer.parseInt(mainmenuSplit[3]);
                int month = Integer.parseInt(mainmenuSplit[4]);
                int day = Integer.parseInt(mainmenuSplit[5]);
                int hour = Integer.parseInt(mainmenuSplit[6]);
                int minute = Integer.parseInt(mainmenuSplit[6]);

                String success = eventManager.createEvent(
                    eventName,
                    theatreType,
                    year,
                    month,
                    day,
                    hour,
                    minute);
                if (success.equals("Showing has been created successfully")) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }
            case "CMLTSEAT" : {
                boolean success = cancelMultipleSeats (mainmenuSplit[2], mainmenuSplit[1]);
                if (success) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }
            case "CSSEAT" : {
                boolean success = cancelSeat(mainmenuSplit[2], mainmenuSplit[1]);
                if (success) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }
            case "MLTSEAT" : {
                boolean success = reserveMultipleSeats(mainmenuSplit[2], mainmenuSplit[1]);
                if (success) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }

            case "SSEAT" : {
                boolean success = reserveSeat(mainmenuSplit[2], mainmenuSplit[1]);
                System.out.println("Success");
                if (success) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }

                break;
            }

            case "CPRICE" : {
                try {
                    eventManager.findEvent(mainmenuSplit[2]).setMultiplier(Double.parseDouble(mainmenuSplit[1]));
                    out.println("TRUE");
                    out.flush();
                } catch (Exception e) {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }
            case "BSEAT": {
                char row = mainmenuSplit[1].charAt(0);
                int col = Integer.parseInt(mainmenuSplit[1].substring(1,
                    mainmenuSplit[1].length()));
                try {
                    Seat seat = eventManager.findEvent(mainmenuSplit[2]).getSeat(row, col);
                    seat.setUsername("BLOCKED");
                    seat.setUser(new User(
                        "BLOCKED",
                        false,
                        "BLOCKED",
                        0.00,
                        new ArrayList<Seat>(),
                        userManager));
                    out.println("TRUE");
                    out.flush();
                } catch (Exception e) {
                    out.println("FALSE");
                    out.flush();
                }
            }
            case "BMLTSEAT": {
                boolean success = true;
                String[] seatStrings = mainmenuSplit[1].split("-");
                for (String seatStr : seatStrings) {
                    char row = seatStr.charAt(0);
                    int col = Integer.parseInt(seatStr.substring(1, 2));
                    try {
                        Seat seat = eventManager.findEvent(mainmenuSplit[2]).getSeat(row, col);
                        seat.setUsername("BLOCKED");
                        seat.setUser(new User(
                            "BLOCKED",
                            false,
                            "BLOCKED",
                            0.00,
                            new ArrayList<Seat>(),
                            userManager));
                    } catch (Exception e) {
                        success = false;
                    }
                }
                if (success) {
                    out.println("TRUE");
                    out.flush();
                } else {
                    out.println("FALSE");
                    out.flush();
                }
                break;
            }
        }
        return true;
    }
    public void run() {
        BufferedReader in;
        PrintWriter out;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        boolean running = true;
        while (running) {
            String user;
            try {
                user = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            String[] userSplit = user.split(" ");
            String userCommand = userSplit[0];

            switch (userCommand) {
                case "LOGIN": {
                    if (userSplit.length < 3) {
                        out.println("LOGIN FAILURE");
                        break;
                    }
                    String username = userSplit[1];
                    String password = userSplit[2];
                    if (loginUser(username, password)) {
                        out.println("LOGIN SUCCESS");
                        out.flush();
                        boolean next;
                        do {
                            next = mainCommandMenu(in, out);
                        } while (next);
                    } else {
                        out.println("LOGIN FAILURE");
                        out.flush();
                    }
                    break;
                }

                case "SIGNUP": {
                    if (userSplit.length < 3) {
                        out.println("SIGNUP FAILURE");
                        break;
                    }
                    String username = userSplit[1];
                    String password = userSplit[2];
                    try {
                        createUser(username, username, password);
                        out.println("SIGNUP SUCCESS");

                        boolean next;
                        do {
                            next = mainCommandMenu(in, out);
                        } while (next);

                    } catch (Exception e) {
                        out.println("SIGNUP FAILURE");
                    }
                    break;
                }

                case "EPROG":
                    // handle program exit
                    running = false;
                    return;
            }

        }
    }
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            while (true) {
                Socket client = serverSocket.accept();
                Server clientManager = new Server("usersData.txt", client);
                new Thread(clientManager).start();
            }
        } catch (IOException e) {
            System.out.println("Server Error");
        }
    }
}
