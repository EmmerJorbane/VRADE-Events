import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * ClientInterface
 *
 * This client interface outlines all the static methods used in
 * our client implementation. In our client we did not use any methods
 * that were not static, so all the method headers in here are
 * commented out.
 *
 * @author Ryan Poplar, Emma Jordan, Section L06
 *
 * @version Nov 24, 2025
 *
 */

public interface ClientInterface {

    void showError();

    void run();

    void createMainWindow();

    void createSeatingMenu();

    void createMainMenu();

    void createEventMenu();

    void loginMenu();

    void loginResponse();

    void viewSeats(BufferedReader bf, PrintWriter pw) throws IOException;

    void updateEventsDropdown() throws IOException;

    void viewEventSeats(String eventName) throws IOException;

    static void changeTime(BufferedReader bf, PrintWriter pw) throws IOException {

    }

    static void cancelEvent(BufferedReader bf, PrintWriter pw) throws IOException {

    }

    void createEvent(BufferedReader bf, PrintWriter pw) throws IOException;

    void getCreditCard();

    void reserveSingleSeat(BufferedReader bf,
                           PrintWriter pw,
                           Scanner scanner,
                           String seat,
                           String eventName) throws IOException;

    void reserveMultipleSeats(BufferedReader bf,
                              PrintWriter pw,
                              Scanner scanner,
                              String seatList,
                              String eventName) throws IOException;

    void cancelSingleSeat(BufferedReader bf,
                          PrintWriter pw,
                          Scanner scanner,
                          String seat,
                          String eventName) throws IOException;

    void cancelMultipleSeats(BufferedReader bf,
                             PrintWriter pw,
                             Scanner scanner,
                             String seatList,
                             String eventName) throws IOException;

    void blockSingleSeat(BufferedReader bf,
                         PrintWriter pw,
                         Scanner scanner,
                         String seatList,
                         String eventName) throws IOException;

    void blockMultipleSeats(BufferedReader bf,
                            PrintWriter pw,
                            Scanner scanner,
                            String seatList,
                            String eventName) throws IOException;

    void changeSeatPrices(BufferedReader bf,
                          PrintWriter pw,
                          Scanner scanner,
                          String eventName) throws IOException;

    void viewBalance(BufferedReader bf, PrintWriter pw) throws IOException;
    
    

}

