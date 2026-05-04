import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;

/**
 * Client
 *
 * This client class will handle the GUI
 * and displaying results; port accepted is 4242
 * For right now the program uses terminal input to log in/sign up a user
 * to enter the event database. The user can then see their seats they have
 * reserved and their balance. The user can choose to reserve or cancel seats
 * and if they are an admin they have access to altering events. It loops until
 * there is a loss of connection to the server, or the user chooses to exit.
 *
 * @author Ryan Poplar, Emma Jordan, Section L06
 *
 * @version Dec 6, 2025
 *
 */

public class Client extends JComponent implements Runnable {

    private static final String HOST = "localhost";
    private static final int PORT = 4242;
    boolean menuLoop = true;  // This variable is true if the user is still using the system
    Socket socket;  // The socker object connected to the Server
    BufferedReader reader;  // The reader to read from the server
    PrintWriter writer;  // The writer to write to the server
    Scanner scanner;  // The scanner to get user input
    JButton viewBalanceBtn;  // When clicked gets the user balance
    JButton viewSeatsBtn;  // When clicked gets the user's seats
    JButton viewEventsBtn;  // When clicked gets all events
    JButton exitBtn;  // When clicked closes the program
    JButton viewEventSeatsBtn;
    JButton changeTimeBtn;
    JButton createEventBtn;
    JButton mainMenuExitBtn;
    JButton cancelEventBtn;
    static JFrame frame;  // The frame holding all the GUI
    Container cardsContainer;  // The container to hold all the cards
    CardLayout cardLayout;  // The layout that allows you to manipulate the cards displayed
    JButton reserveSeatsBtn;  // The button for the seats screen that lets the user reserve seats
    JButton returnSeatsBtn;  // The button for the seats screen that lets the user return seats
    JButton exitSeatsBtn;  // The button for the exit screen that lets the user exit
    JComboBox<String> eventsDropDown;  // The dropdown to display all events found
    JPanel seatsContainer;  // The container to hold seats in seats view
    String userName;  // The username of the logged in user
    ArrayList<JCheckBox> seatsCheckBoxes = new ArrayList<JCheckBox>();  // All of the checkboxes in seat view
    ActionListener mainMenuListener = new ActionListener() {
        // When any of the buttons is on the main menu is clicked
        // run this  function
        @Override public void actionPerformed(ActionEvent e) {
            // If the balance btn was clicked run the view balance method
            if (e.getSource() == viewBalanceBtn) {
                try {
                    viewBalance(reader, writer);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }

                // If the seats btn was clicked run view seats method
            } else if (e.getSource() == viewSeatsBtn) {
                try {
                    viewSeats(reader, writer);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }

                // If the events btn was pressed run view events method
            } else if (e.getSource() == viewEventsBtn) {
                try {
                    updateEventsDropdown();
                } catch (IOException exception) {
                    showError("An error occured while getting movie data");
                }
                cardLayout.show(cardsContainer, "events_menu");
                // If the exit btn was clicked close the frame and then end the main menu loop
            } else if (e.getSource() == exitBtn) {
                frame.setVisible(false);
                frame.dispose();
                menuLoop = false;
                writer.println("EPROG");
                writer.flush();
            }
        }
    };  // This is the listener for any button on the main menu

    ActionListener eventMenuListener = new ActionListener() {
        @Override public void actionPerformed(ActionEvent e) {
            if (e.getSource() == viewEventSeatsBtn) {
                try {
                    cardLayout.show(cardsContainer, "seats_menu");
                    viewEventSeats(((String) eventsDropDown.getSelectedItem()).split(",")[0]);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (e.getSource() == mainMenuExitBtn) {
                cardLayout.show(cardsContainer, "main_menu");

            } else if (e.getSource() == changeTimeBtn) {
                try {
                    changeTime(reader, writer);
                    updateEventsDropdown();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            } else if (e.getSource() == cancelEventBtn) {
                try {
                    cancelEvent(reader, writer);
                    updateEventsDropdown();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (e.getSource() == createEventBtn) {
                try {
                    createEvent(reader, writer);
                    updateEventsDropdown();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    };

    ActionListener seatsMenuListener = new ActionListener() {
        @Override public void actionPerformed(ActionEvent e) {
            if (e.getSource() == reserveSeatsBtn) {
                getCreditCard();
                for (JCheckBox box : seatsCheckBoxes) {
                    if (box.isSelected()) {
                        String[] params = box.getText().split(":");
                        try {
                            reserveSingleSeat(reader, writer, scanner, params[0], 
                                              ((String) eventsDropDown.getSelectedItem()).split(",")[0]);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                cardLayout.show(cardsContainer, "events_menu");
            } else if (e.getSource() == returnSeatsBtn) {
                for (JCheckBox box : seatsCheckBoxes) {
                    if (box.isSelected()) {
                        String[] params = box.getText().split(":");
                        try {
                            cancelSingleSeat(reader, writer, scanner, params[0],  
                                             ((String) eventsDropDown.getSelectedItem()).split(",")[0]);
                            cardLayout.show(cardsContainer, "events_menu");
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            } else if (e.getSource() == exitSeatsBtn) {
                cardLayout.show(cardsContainer, "events_menu");
            }
        }
    };  // This is the ActionListener for any button within the seats menu
    /**
     * Constructor that connects the client to the server and handles exceptions as well as starts
     * the login process.
     */
    public Client() {
        //open up socket and reader/writer connection to server
        try {
            socket = new Socket(HOST, PORT);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            showError("Could not connect to server: " + e.getMessage());
        }
    }

    /**
     * Thread safe Error messages
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * This main method is run when the Client is first started.
     * It creates a instance of the Client in the EDT to be used to create the GUI
     * for the user to use.
     *
     * @param args The traditional arguments passed into the main method
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Client());
    }

    /**
     * This method connects to the server, logs the user in or signs them up.
     * It then creates the main menu GUI and takes in user interaction to
     * preform the correct associated action by passing data into the server
     * and displaying the response. This method is called when an instance
     * of the Client is created in a new thread on the EDT.
     */
    public void run() {
        scanner = new Scanner(System.in);

        loginMenu();
    }
    /**
     * This method creates the main window for the program to run on and sets the values of
     * the contianers and layouts so that the subscreens can be manipulate.
     */
    private void createMainWindow() {
        frame = new JFrame("VRADE Events");  // The main window frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Set the main window to exit when closed
        cardLayout = new CardLayout();
        cardsContainer = frame.getContentPane();  // This is the container that holds all the cards
        cardsContainer.setLayout(cardLayout);  // Set the card container to have the card layout
        createMainMenu();
        createEventMenu();
        createSeatingMenu();
    }
    /**
     * This method creates the Swing objects that form the card to be used to display
     * the seat options and allow the user to reserve or return a seat.
     */
    private void createSeatingMenu() {
        JPanel seatMenuCard = new JPanel();  // The container for the card
        seatMenuCard.setLayout(new BorderLayout());

        seatsContainer = new JPanel();
        JLabel titleLabel = new JLabel("Select a seat and an option");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel options = new JPanel();
        reserveSeatsBtn = new JButton("Reserve selected");
        returnSeatsBtn = new JButton("Cancel selected");
        exitSeatsBtn = new JButton("Exit");

        reserveSeatsBtn.addActionListener(seatsMenuListener);
        returnSeatsBtn.addActionListener(seatsMenuListener);
        exitSeatsBtn.addActionListener(seatsMenuListener);

        options.add(reserveSeatsBtn);
        options.add(returnSeatsBtn);
        options.add(exitSeatsBtn);

        seatMenuCard.add(titleLabel, BorderLayout.NORTH);
        seatMenuCard.add(seatsContainer, BorderLayout.CENTER);
        seatMenuCard.add(options, BorderLayout.SOUTH);

        cardsContainer.add(seatMenuCard, "seats_menu");


    }
    /**
     * This method creates the Swing objects needed to display a main menu of options.
     * It then parents those objects in the correct format to the root object and displays it.
     * In addition, this method also adds the listeners to the buttons
     * so they can begin to track for user interaction and run their
     * corresponding methods.
     */
    private void createMainMenu() {

        JPanel mainMenuCard = new JPanel();  // This is the card for the main menu
        mainMenuCard.setLayout(new BorderLayout());  // Set the main menu to be a border layout

        JLabel welcomeLbl = new JLabel("Welcome to VRADE Events!", SwingConstants.CENTER); // Welcome label
        welcomeLbl.setFont(new Font("Arial", Font.BOLD, 20));  // Set the font to look nice
        mainMenuCard.add(welcomeLbl, BorderLayout.NORTH);  // Add the welcome label to the main menu card

        JPanel options = new JPanel();  // The panel to hold all of the option buttons
        options.setLayout(new GridLayout(4, 1, 10, 10));  // Set the layout
        options.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        // Create the buttons
        viewBalanceBtn = new JButton("View My Balance");
        viewSeatsBtn = new JButton("My Reserved Seats");
        viewEventsBtn = new JButton("View All Movies");
        exitBtn = new JButton("Exit");

        // Add the buttons' listeners
        viewSeatsBtn.addActionListener(mainMenuListener);
        viewEventsBtn.addActionListener(mainMenuListener);
        viewBalanceBtn.addActionListener(mainMenuListener);
        exitBtn.addActionListener(mainMenuListener);

        // Add the buttons to the panel
        options.add(viewSeatsBtn);
        options.add(viewEventsBtn);
        options.add(viewBalanceBtn);
        options.add(exitBtn);

        mainMenuCard.add(options, BorderLayout.CENTER);  // Add the buttons panel to the main menu card

        cardsContainer.add(mainMenuCard, "main_menu");
        frame.setSize(1050, 700);
        frame.setVisible(true);
    }


    public void createEventMenu() {
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());

        JLabel welcomeLbl = new JLabel("Current VRADE Events", SwingConstants.CENTER);
        welcomeLbl.setFont(new Font("Arial", Font.BOLD, 20));
        content.add(welcomeLbl, BorderLayout.NORTH);

        JPanel options = new JPanel();
        options.setLayout(new GridLayout(5, 1, 10, 10));
        options.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        viewEventSeatsBtn = new JButton("View the seats of a selected event");
        changeTimeBtn = new JButton("Change the time of a selected event (ADMIN)");
        cancelEventBtn = new JButton("Cancel a selected event (ADMIN)");
        createEventBtn = new JButton("Create a new event (ADMIN)");
        mainMenuExitBtn = new JButton("Return to main menu");


        viewEventSeatsBtn.addActionListener(eventMenuListener);
        changeTimeBtn.addActionListener(eventMenuListener);
        cancelEventBtn.addActionListener(eventMenuListener);
        createEventBtn.addActionListener(eventMenuListener);
        mainMenuExitBtn.addActionListener(eventMenuListener);

        options.add(viewEventSeatsBtn);
        options.add(changeTimeBtn);
        options.add(cancelEventBtn);
        options.add(createEventBtn);
        options.add(mainMenuExitBtn);


        JPanel eventsList = new JPanel();
        eventsDropDown = new JComboBox<String>();

        eventsList.add(eventsDropDown);
        content.add(eventsList, BorderLayout.EAST);
        content.add(options, BorderLayout.WEST);
        cardsContainer.add(content, "events_menu");
    }

    /**
     * the login menu is the first to pop-up when running the program. This function specifically is just
     * to process whether the client would like to sign in or sign up. It then calls loginResponse()
     * to parse their login info and can be called again if an error occured.
     */
    private void loginMenu() {
        String[] options = {"Login", "Sign Up"};
        int choice = JOptionPane.showOptionDialog(frame, "Would you like to log in or sign up?",
                "Login", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                String username = JOptionPane.showInputDialog("Enter your username: ");
                userName = username;
                String password = JOptionPane.showInputDialog("Enter your password: ");
                if (username != null && password != null) {
                    writer.println("LOGIN " + username + " " + password);
                    writer.flush();
                    loginResponse();
                }
                break;
            case 1:
                String usernameCreated = JOptionPane.showInputDialog("Enter your username: ");
                userName = usernameCreated;
                String passwordCreated = JOptionPane.showInputDialog("Enter your password: ");
                if (usernameCreated != null && passwordCreated != null) {
                    writer.println("SIGNUP " +  usernameCreated + " " + passwordCreated);
                    writer.flush();
                    loginResponse();
                }
                break;
        }
    }

    /**
     * loginResponse() is called after the client chooses whether to login or sign up. This method takes that
     * response and then, dependent on the login info, either shows an error messages and calls loginMenu()
     * again or shows a success pane and calls the showGUI() function to show the main menu for the program.
     */
    private void loginResponse() {
        try {
            String loginOrSignUpResponse = reader.readLine();
            String temp = loginOrSignUpResponse.split(" ")[0];

            if (temp.equals("LOGIN")) {
                String loginResponse = loginOrSignUpResponse.split(" ")[1];
                if (loginResponse.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(frame, "logged in successfully!", "Success!", 
                                                  JOptionPane.INFORMATION_MESSAGE);
                    // This creates a main menu GUI and displays it
                    // It then also begins to track for main menu input through the buttons
                    createMainWindow();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Error!", 
                                                  JOptionPane.ERROR_MESSAGE);
                    loginMenu(); // to restart again
                }
            } else if (temp.equals("SIGNUP")) {
                String signUpResponse = loginOrSignUpResponse.split(" ")[1];
                if (signUpResponse.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "signed up successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    // This creates a main menu GUI and displays it
                    // It then also begins to track for main menu input through the buttons
                    createMainWindow();
                } else {
                    JOptionPane.showMessageDialog(
                        frame,
                        "There is another user with that name! Please choose a different Username.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    loginMenu();
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error processing server response.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method communicate with the server and the user to determine output. It sends the VSEAT command
     * to the server and parses its response; If admin, it will bring them back to the main menu as they have
     * no reserved seats. If Customer, it will either say the have not reserved anything or will show their seats.
     * After, it will prompt the user if they want to delete their reservation-- and if aplicable-- it will do so.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     */
    public void viewSeats(BufferedReader bf, PrintWriter pw) throws IOException {
        pw.println("VSEAT");
        pw.flush();
        frame.setVisible(false);

        // this assumes response is one of the following:
        // Customer - CUSTOMER: NONE || CUSTOMER: [AB],[BC],...
        // Admin - ADMIN
        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }

        // Admin path
        if (response.contains("ADMIN")) {
            JOptionPane.showMessageDialog(frame, "You have not reserved any seats! We are bringing you back " +
                    "the main menu.", "VSEAT", JOptionPane.INFORMATION_MESSAGE);
            frame.setVisible(true);
        }

        // Customer path
        if (response.startsWith("CUSTOMER")) {
            String str = "";
            if (response.length() > 8) {
                str = response.substring(8).trim(); // everything after "CUSTOMER"
            }

            if (str.equals("NONE") || str.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "You have not reserved any seats! We are bringing you back " +
                        "the main menu.", "VSEAT", JOptionPane.INFORMATION_MESSAGE);
                frame.setVisible(true);
                return;
            }

            // ---------------------PopUp GUI----------------------------------
            String[] seats = str.split(",\\s*");

            JDialog dialog = new JDialog(frame, "My Reserved Seats", true);
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(frame);
            dialog.setLayout(new BorderLayout(10, 10));

            JLabel title = new JLabel("Your Reserved Seats", SwingConstants.CENTER);
            dialog.add(title, BorderLayout.NORTH);
            JList<String> seatList = new JList<>(seats);
            seatList.setEnabled(false);
            dialog.add(new JScrollPane(seatList), BorderLayout.CENTER);

            // bottom panel
            JPanel bottomPanel = new JPanel();

    

            // Buttons
            JPanel buttonPanel = new JPanel();
            JButton returnBtn = new JButton("Main Menu");
            buttonPanel.add(returnBtn);

            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
            dialog.add(bottomPanel, BorderLayout.SOUTH);

            returnBtn.addActionListener(e -> {
                dialog.dispose(); // closes popup
                frame.setVisible(true);
            });
           
            dialog.setVisible(true);

        }

    }

    /**
     * This method is called if the user chooses to see the upcoming events that are in
     * the VRADE database in the next 3 months. It gives the server "VEVENTS" which
     * tells the server to return a formatted string of the events in the next 3 months.
     * These events are then added to the events drop down so that the user can select an
     * event they want to interact with.
     */

    public void updateEventsDropdown() throws IOException {
        eventsDropDown.removeAllItems();  // Remove all of the previous options from the drop down


        // Request the data from the server
        writer.println("VEVENT");
        writer.flush();

        //Response should be a formatted string with the events listed out on separate lines
        ArrayList<String> eventList = new ArrayList<>();
        String response = "";
        String serverLine = reader.readLine();
        while (!serverLine.equals("ENDEVENT")) {
            response += serverLine + "\n";
            eventList.add(serverLine);
            serverLine = reader.readLine();
        }

        // Add all of the events to the drop down
        for (String event : eventList) {
            eventsDropDown.addItem(event);
        }
    }

    /**
     * This method is called if the user chooses to see the seats of an event
     * in the event menu loop. It asks the user what event they would like to view,
     * and then the server returns a formatted string of the seats in the event.
     * Then this starts what we have been calling the event loop. It gives the user
     * 8 options on what to do once they are looking at the seats in an event. This
     * method loops until a user chooses to exit to the event menu.
     *
     * @param eventName, a string of the event name
     */

    public void viewEventSeats(String eventName) throws IOException {
        //ask user what event they want to look at/alter
        writer.println("PICKEVENT " + eventName);
        writer.flush();
        //return true if event exists
        String response = reader.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            writer.println("EPROG");
            writer.flush();
            return;
        }
        if (!response.contains("TRUE")) {
            showError("That event no longer exits!");
            return;
        }

        writer.println("PRINTSEATS " + eventName);
        writer.flush();

        ArrayList<String> seatDisplays = new ArrayList<String>();
        char maxRow = 0;
        int maxCol = 0;
        response = reader.readLine();
        while (!response.equals("ENDSEATS")) {
            String[] params = response.split(",");
            if (params.length < 5) {
                response = reader.readLine();
                continue;
            }
            String event = params[0];
            String username = params[1];
            char row = params[2].charAt(0);
            int col = Integer.valueOf(params[3]);
            String cost = params[4];
            String tempDisplay = String.format(
                    "%C%d: $%s",
                    row,
                    col,
                    cost
            );
            System.out.println(username);
            if (username.equals(userName)) {
                seatDisplays.add(
                        String.format("%C%d: %s",
                                row,
                                col,
                                "Purchased by you"));
            } else if (!username.equals("null")) {
                seatDisplays.add("Reserved");
            } else if (username.equals("BLOCKED")) {
                seatDisplays.add("Blocked");
            } else {
                seatDisplays.add(tempDisplay);
            }
            if (row > maxRow) {
                maxRow = row;
            }
            if (col > maxCol) {
                maxCol = col;
            }
            response = reader.readLine();
        }
        GridLayout grid = new GridLayout(maxRow - 64, maxCol);
        seatsContainer.setLayout(grid);
        seatsCheckBoxes = new ArrayList<JCheckBox>();
        for (Component c : seatsContainer.getComponents()) {
            seatsContainer.remove(c);
        }
        for (String display : seatDisplays) {
            if (display.equals("Blocked") || display.equals("Reserved")) {
                JLabel tempLbl = new JLabel(display);
                seatsContainer.add(tempLbl);
            } else {
                JCheckBox tempBox = new JCheckBox(display);
                seatsContainer.add(tempBox);
                seatsCheckBoxes.add(tempBox);
            }
        }
    }

    /**
     * This method is called if the user chooses to change the time of an event
     * from the event menu loop. This method is an admin only method so the method
     * first checks to see if the user is an admin before executing anything. Then
     * the method asks the user for the event they would like to change, then the
     * time they would like to change the event to. The server returns a confirmation
     * string that the method will then interpret to tell the user if their time
     * change was successful or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     */


    public static void changeTime(BufferedReader bf, PrintWriter pw) throws IOException {
        //check if the user is an admin
        pw.println("ADMIN");
        pw.flush();

        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (!response.contains("TRUE")) {
            JOptionPane.showMessageDialog(frame, "You are not an admin!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // immediately exit, like createEvent
        }

        //ask user what event they want to look at/alter
        String eventName;
        while (true) {
            eventName = JOptionPane.showInputDialog(
                "Which event would you like to change the time for? (type EXIT to exit)");
            if (eventName == null) {
                return;
            }
            if (eventName.equals("EXIT")) {
                return;
            } else if (eventName.contains(",")) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid event name! (No commas)", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            pw.println("PICKEVENT " + eventName);
            pw.flush();
            //return true if event exists
            String response2 = bf.readLine();
            if (response2 == null) {
                JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
                pw.println("EPROG");
                pw.flush();
                return;
            }
            if (response2.contains("TRUE")) {
                break;
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid event name!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        String time = JOptionPane.showInputDialog(
            "What time would you like " + eventName + " to be? (Enter in String ISO8601 format)");

        pw.println("CTIME " + eventName + " " + time);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println();
            JOptionPane.showMessageDialog(
                frame,
                "SUCCESSFULLY CHANGED THE TIME OF " + eventName + " TO " + time, "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "TIME CHANGE FAILED!", "Failure", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * This method is called if the user chooses to cancel an event
     * from the event menu loop. This method is an admin only method so the method
     * first checks to see if the user is an admin before executing anything. Then
     * the method asks the user for the event they would like to cancel. It sends
     * the event to delete to the server, and then the server returns a confirmation
     * message if the event was deleted or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     */

    public static void cancelEvent(BufferedReader bf, PrintWriter pw) throws IOException {

        //check if the user is an admin
        pw.println("ADMIN");
        pw.flush();

        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (!response.contains("TRUE")) {
            JOptionPane.showMessageDialog(frame, "You are not an admin!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // immediately exit, like createEvent
        }

        //ask user what event they want to delete
        String eventName;
        while (true) {
            eventName = JOptionPane.showInputDialog("Which event would you like to delete? (type EXIT to exit)");
            if (eventName == null) {
                return;
            }
            if (eventName.equals("EXIT")) {
                return;
            } else if (eventName.contains(",") ||  eventName.contains(" ")) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid event name! (No commas or Spaces)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            pw.println("PICKEVENT " + eventName);
            pw.flush();
            //return true if event exists
            String response2 = bf.readLine();
            if (response2 == null) {
                JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
                pw.println("EPROG");
                pw.flush();
                return;
            }
            if (response2.contains("TRUE")) {
                break;
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid event name!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        pw.println("DEVENT " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println();
            JOptionPane.showMessageDialog(
                frame,
                "EVENT SUCCESSFULLY DELETED!",
                "Success!",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "EVENT DELETION FAILED!", "Failure!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called if the user chooses to create an event
     * from the event menu loop. This method is an admin only method so the method
     * first checks to see if the user is an admin before executing anything. Then
     * the method asks the user for the name of the event they would like to create, then the
     * parameters for the event they are creating. The server returns a confirmation
     * string that the method will then interpret to tell the user if their creation
     * was successful or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     */

    public void createEvent(BufferedReader bf, PrintWriter pw) throws IOException {
        //check if the user is an admin
        pw.println("ADMIN");
        pw.flush();

        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (!response.contains("TRUE")) {
            JOptionPane.showMessageDialog(frame, "You are not an admin!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // immediately exit, like createEvent
        }


        //get variables for new event
        String eventName;
        String theatreType;
        int year;
        int month;
        int day;
        int hour;
        int minute;

        //getEventName
        while (true) {
            eventName = JOptionPane.showInputDialog("What is the name of the event you would like to create?");
            if (eventName.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Event Name",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else if (eventName.contains(",")) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Event Name",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }
        //getTheatreType
        while (true) {
            theatreType = JOptionPane.showInputDialog(
                "What is the theatre type of the event you would like to create?");
            theatreType.trim();
            if (theatreType.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Theatre Type!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (!(theatreType.equals("S") || theatreType.equals("M") || theatreType.equals("L"))) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Theatre Type!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }
        //getYear
        int currentYear = LocalDate.now().getYear();
        while (true) {
            String line = JOptionPane.showInputDialog("What is the year of the event you would like to create?");
            try {
                year = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Year!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (year < currentYear) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Year!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }
        //getMonth
        int currentMonth = LocalDateTime.now().getMonthValue();
        while (true) {
            String line = JOptionPane.showInputDialog("What is the month of the event you would like to create?");
            try {
                month = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Month!",
                    "Failure!", 
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (month < 1 || month > 12) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Month!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (year == currentYear && month < currentMonth) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Month!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }
        //getDay
        int currentDay = LocalDateTime.now().getDayOfMonth();
        while (true) {
            String line = JOptionPane.showInputDialog("What is the day of the event you would like to create?");
            try {
                day = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Day!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (day < 1 || day > 31) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Day!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (month == 2 && day > 28) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Day!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if ((month == 9 || month == 4 || month == 6 || month == 11) && day > 30) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Day!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (year == currentYear && month == currentMonth && day < currentDay) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Day!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }
        //getHour
        int currentHour = LocalDateTime.now().getHour();
        while (true) {
            String line = JOptionPane.showInputDialog("What is the hour of the event you would like to create?");
            try {
                hour = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Hour!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (hour < 0 || hour > 23) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Hour!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (year == currentYear && month == currentMonth && day == currentDay &&  hour < currentHour) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Hour!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }
        //getMinute
        int currentMinute = LocalDateTime.now().getMinute();
        while (true) {
            String line = JOptionPane.showInputDialog("What is the minute of the event you would like to create?");
            try {
                minute = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Minute!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (minute < 0 || minute > 59) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Minute!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (
                year == currentYear &&
                    month == currentMonth &&
                    day == currentDay &&
                    hour == currentHour
                    && minute < currentMinute) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please Enter a Valid Minute!",
                    "Failure!",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        pw.println(
            "CEVENT " +
            eventName +
            " " +
            theatreType +
            " " +
            year +
            " " +
            month +
            " " +
            day +
            " " +
            hour +
            " " +
            minute);
        pw.flush();

        String response2 = bf.readLine().trim();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println();
            JOptionPane.showMessageDialog(
                frame,
                "EVENT SUCCESSFULLY CREATED!",
                "Success!",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "EVENT DELETION FAILED!", "Failure!", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * This method asks the user to enter credit card information
     * to validate that they can spend money.
     */
    private void getCreditCard() {
        String credit;
        while (true) {
            try {
                credit = (String) JOptionPane.showInputDialog(
                        null,
                        "Please enter a credit card number",
                        "Credit Card",
                        JOptionPane.QUESTION_MESSAGE
                );
            } catch (InputMismatchException e) {
                showError("Please enter a valid credit card number (between 13 and 19 digits)");
                continue;
            }
            try {
                new BigInteger(credit);
            } catch (NumberFormatException e) {
                showError("Please enter a valid credit card number (between 13 and 19 digits)");
                continue;
            }
            if (credit.length() < 13 || credit.length() > 19) {
                showError("Please enter a valid credit card number (between 13 and 19 digits)");
            } else {
                break;
            }
        }
    }
    /**
     * This method is called if the user chooses to reserve a single seat
     * from the event loop. The method asks the user what seat they would
     * like to reserve until they enter a valid seat. It then sends the
     * seat and eventName to the server and the server returns a confirmation
     * if the seat was able to be reserved or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param seat, a string of the seat name
     * @param eventName, a string of the eventName of the selected event
     */

    public void reserveSingleSeat(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String seat,
        String eventName) throws IOException {

        pw.println("SSEAT " + seat + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            showError("Server disconnected!");
            return;
        }
        if (response2.contains("TRUE")) {
            JOptionPane.showMessageDialog(
                    null,
                    "SEAT: " + seat + " SUCCESSFULLY RESERVED!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            showError("Failure in reserving seat");
        }

    }

    /**
     * This method is called if the user chooses to reserve multiple seats
     * from the event loop. The method asks the user what seats they would
     * like to reserve until they enter a valid set of seats. It then sends the
     * seats and eventName to the server and the server returns a confirmation
     * if the seats were able to be reserved or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param seatList, a string of all the seats in the selected event
     * @param eventName, a string of the eventName of the selected event
     */

    public void reserveMultipleSeats(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String seatList,
        String eventName) throws IOException {
        System.out.println("Please enter the seats you would like to reserve: ");
        String seats;
        while (true) {
            try {
                seats = scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid set of seats!");
                continue;
            }
            if (seats.isEmpty()) {
                System.out.println("Please enter a valid set of seats!");
            } else {
                break;
            }
        }
        String credit;
        System.out.println("Please enter a credit card number");
        while (true) {
            try {
                credit = scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid credit card number!");
                continue;
            }
            if (credit.length() < 13 || credit.length() > 19) {
                System.out.println("Please enter a valid credit card number!");
            } else {
                break;
            }
        }

        pw.println("MLTSEAT " + seats + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println("SEATS " + seats + " SUCCESSFULLY RESERVED!");
        } else {
            System.out.println("SEAT RESERVATION FAILED!");
        }
    }

    /**
     * This method is called if the user chooses to cancel a single seat
     * from the event loop. The method asks the user what seat they would
     * like to cancel until they enter a valid seat. It then sends the
     * seat and eventName to the server and the server returns a confirmation
     * if the seat was able to be canceled or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param seat, a string of the seat name
     * @param eventName, a string of the eventName of the selected event
     */

    public void cancelSingleSeat(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String seat,
        String eventName) throws IOException {

        pw.println("CSSEAT " + seat + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            showError("Server disconnected!");
            return;
        }
        if (response2.contains("TRUE")) {
            JOptionPane.showMessageDialog(
                    null,
                    "SEAT: " + seat + " SUCCESSFULLY CANCLED!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            showError("SEAT CANCELLATION FAILED!");
        }
    }

    /**
     * This method is called if the user chooses to cancel multiple seats
     * from the event loop. The method asks the user what seats they would
     * like to cancel until they enter a valid set of seats. It then sends the
     * seats and eventName to the server and the server returns a confirmation
     * if the seats were able to be canceled or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param seatList, a string of all the seats in the selected event
     * @param eventName, a string of the eventName of the selected event
     */

    public void cancelMultipleSeats(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String seatList,
        String eventName) throws IOException {
        System.out.println("Please enter the seats you would like to cancel: ");
        String seats;
        while (true) {
            try {
                seats = scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid set of seats!");
                continue;
            }
            if (seats.isEmpty()) {
                System.out.println("Please enter a valid set of seats!");
            } else {
                break;
            }
        }

        pw.println("CMLTSEAT " + seats + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println("SEATS " + seats + " SUCCESSFULLY CANCELED!");
        } else {
            System.out.println("SEAT CANCELLATION FAILED!");
        }
    }

    /**
     * This method is called if the user chooses to block a single seat
     * from the event loop. This method is admin only so the method checks
     * to see if the user is an admin or not first. The method then asks the
     * user what seat they would like to block until they enter a valid seat.
     * It then sends the seats and eventName to the server and the server
     * returns a confirmation if the seat was able to be blocked or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param seatList, a string of all the seats in the selected event
     * @param eventName, a string of the eventName of the selected event
     */

    public void blockSingleSeat(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String seatList,
        String eventName) throws IOException {
        //check if the user is an admin
        pw.println("ADMIN");
        pw.flush();

        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response.contains("TRUE")) {
            System.out.println("ADMIN CHECK SUCCESSFUL");
        } else {
            System.out.println("You are not an admin!");
            return;
        }

        System.out.println("Please enter the seat you would like to block: ");
        String seat;
        while (true) {
            try {
                seat = scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid seat!");
                continue;
            }
            if (seat.isEmpty()) {
                System.out.println("Please enter a valid seat!");
            } else {
                break;
            }
        }

        pw.println("BSEAT " + seat + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println("SEAT " + seat + " SUCCESSFULLY BLOCKED!");
        } else {
            System.out.println("SEAT BLOCK FAILED!");
        }


    }

    /**
     * This method is called if the user chooses to block multiple seats
     * from the event loop. This method is admin only so the method checks
     * to see if the user is an admin or not first. The method then asks the
     * user what seats they would like to block until they enter a valid set
     * of seats. It then sends the seats and eventName to the server and the
     * server returns a confirmation if the seats were able to be blocked or
     * not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param seatList, a string of all the seats in the selected event
     * @param eventName, a string of the eventName of the selected event
     */

    public void blockMultipleSeats(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String seatList,
        String eventName) throws IOException {
        //check if the user is an admin
        pw.println("ADMIN");
        pw.flush();

        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response.contains("TRUE")) {
            System.out.println("ADMIN CHECK SUCCESSFUL");
        } else {
            System.out.println("You are not an admin!");
            return;
        }

        System.out.println("Please enter the seats you would like to block: ");
        String seats;
        String seatsFormatted;
        while (true) {
            try {
                seats = scanner.nextLine();
                String[] seatsSeparate = seats.split("(?=[A-Z])");
                seatsFormatted = String.join("-", seatsSeparate);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid set of seats!");
                continue;
            }
            if (seats.isEmpty()) {
                System.out.println("Please enter a valid set of seats!");
            } else {
                break;
            }
        }

        pw.println("BMLTSEAT " + seatsFormatted + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println("SEATS " + seatsFormatted + " SUCCESSFULLY BLOCKED!");
        } else {
            System.out.println("SEAT BLOCK FAILED!");
        }
    }

    /**
     * This method is called if the user chooses to change the prices of seats
     * from the event loop. This method is admin only so the method checks
     * to see if the user is an admin or not first. The method then asks the
     * user what their multiplier of the price of the seats will be. The
     * method then sends the multiplier and eventName to the server, and then
     * the server will send back a confirmation on if the multiplier was put
     * into effect or not.
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     * @param scanner, the scanner used to get user input for the questions
     * @param eventName, a string of the eventName of the selected event
     */

    public void changeSeatPrices(
        BufferedReader bf,
        PrintWriter pw,
        Scanner scan,
        String eventName) throws IOException {
        //check if the user is an admin
        pw.println("ADMIN");
        pw.flush();

        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response.contains("TRUE")) {
            System.out.println("ADMIN CHECK SUCCESSFUL");
        } else {
            System.out.println("You are not an admin!");
            return;
        }

        System.out.println("Please enter multiplier for the price of the seats (0.1 to 5):");
        double multiplier;
        while (true) {
            try {
                multiplier = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid multiplier for the price of the seats!");
                continue;
            }
            if (multiplier < 0.1 || multiplier > 5) {
                System.out.println("Please enter a valid multiplier for the price of the seats!");
            } else {
                break;
            }
        }

        pw.println("CPRICE " + multiplier + " " + eventName);
        pw.flush();

        String response2 = bf.readLine();
        if (response2 == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }
        if (response2.contains("TRUE")) {
            System.out.println("SEAT MULTIPLIER SUCCESSFULLY APPLIED!");
        } else {
            System.out.println("SEAT MULTIPLIER FAILED!");
        }
    }

    /**
     * This method reads the Users.txt files to find the users balance. If admin, its total sales, and if customer its
     * the amount of money that they have spent. The terminal will then prompt them if they are ready to return to
     * the main menu, and will continue to do so until they answer affirmatively.\
     *
     * @param bf, the buffered reader used to read in data from the server
     * @param pw, the print writer used to write data to the server
     */
    public void viewBalance(BufferedReader bf, PrintWriter pw) throws IOException {
        pw.println("VBALANCE");
        pw.flush();

        // this assumes response is one of the following:
        // Customer - CUSTOMER: XX.XX
        // Admin - ADMIN: XX.XX
        String response = bf.readLine();
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "Server disconnected!", "Error", JOptionPane.ERROR_MESSAGE);
            pw.println("EPROG");
            pw.flush();
            return;
        }

        // Customer or Admin paths
        if (response.contains("CUSTOMER")) {
            response = response.substring(response.indexOf("R") + 1);
            JOptionPane.showMessageDialog(
                frame,
                "Your current balance is: " + response, "Account Balance",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (response.contains("ADMIN")) {
            //response = response.substring(response.indexOf(" "));
            JOptionPane.showMessageDialog(
                frame,
                "Your total sales are: " + response, "Account Balance",
                JOptionPane.INFORMATION_MESSAGE);
        }

        // Main menu prompt automatically is set up once code has completed
    }

}

