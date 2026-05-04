import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * UserManagerTestCases
 *
 * This class contains all of the JUnit test cases
 * for UserManager to ensure all of its functions handle bad
 * data nicely and run as intended.
 *
 * @author David Goldfus, L06
 * @version November 9th, 2025
 */

@RunWith(Enclosed.class)
public class UserManagerTestCases {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(UnitTest.class);
        System.out.printf("Test count: %d\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Wonderful - All tests passed");
        } else {
            System.out.printf("Tests failed: %d\n", result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
    /**
     * UserManagerTestCases
     *
     * This class contains all of the JUnit test cases
     * for UserManager to ensure all of its functions handle bad
     * data nicely and run as intended.
     *
     * @author David Goldfus, L06
     * @version November 9th, 2025
     */
    public static class UnitTest {
        private String username = "username";  // The username for most test cases
        private String password = "password";  // The password for most test cases
        private boolean admin = false;  // The admin boolean for most test cases
        private double balance = 100.5;  // The balance for most test cases
        private ArrayList<Seat> seatList = new ArrayList<Seat>();  // The default seat list for most test cases


        // A helper method to write to the data file
        public void writeFile(String fileData) {
            try (BufferedWriter bfw = new BufferedWriter(new FileWriter("JUnitTestFile.txt"))) {
                bfw.write(fileData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // A helper method to write out good data to the data file
        public void writeGoodData() {
            String testString = "username,password,100.5,false,";
            writeFile(testString);
        }

        // A helper method to read in the user file
        public ArrayList<String> readUserFile() {
            ArrayList<String> fileLines = new ArrayList<String>();
            try (BufferedReader bfr = new BufferedReader(new FileReader("JUnitTestFile.txt"))) {
                String line = bfr.readLine();
                while (line != null) {
                    fileLines.add(line);
                    line = bfr.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fileLines;
        }

        // Test if the user data file can be read
        @Test
        public void readTest() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = um.getUserList().get(0);
            assertEquals(testUser.getUsername(), username);
            assertEquals(testUser.getAdmin(), admin);
            assertEquals(testUser.getPassword(), password);
            //assertEquals(testUser.getBalance(), balance);
        }

        // Tests if a user can be found via the user search function
        @Test
        public void queryUserTest() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = um.queryUsers("username");
            assertEquals(testUser.getUsername(), username);
            assertEquals(testUser.getAdmin(), admin);
            assertEquals(testUser.getPassword(), password);
            //assertEquals(testUser.getBalance(), balance);
        }

        // Tests for the case that there is no user with the set username
        @Test
        public void queryUserNoUserTest() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = um.queryUsers("ThisIsNotAUser");
            assertEquals(testUser, null);
        }

        // Tests deleting a user from the database class and file
        @Test
        public void deleteUserTest() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = um.getUserList().get(0);
            um.deleteUser(testUser);
            assertEquals(um.getUserList().size(), 0);
            ArrayList<String> fileLines = readUserFile();
            assertEquals(fileLines.size(), 0);

        }

        // Tests the case where a user that does not exist is atempted to be deleted
        @Test
        public void deleteUserTestFalseUser() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = new User("FalseUser", false, "password2", 
                                     100.00, new ArrayList<Seat>(), new UserManager("falseUser.txt"));
            um.deleteUser(testUser);
            assertEquals(um.getUserList().size(), 1);
        }

        // Tests making a user
        @Test
        public void makeUserTest() {
            UserManager um = new UserManager("JUnitTestFile.txt");

            try {
                um.makeUser(username, false, password, balance, seatList);
            } catch (UserCreationException e) {
                System.out.println(e.getMessage());
            }
            String userString = String.format("%s,%s,%s,%s,",
                    username,
                    password,
                    balance,
                    false);
            assertEquals(readUserFile().get(0), userString);
        }

        // Tests the case where an atempt to make a user with a duplicate name is made
        @Test
        public void makeUserTestDuplicateUsername() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            try {
                um.makeUser(username, false, password, balance, seatList);
            } catch (UserCreationException e) {
                System.out.println(e.getMessage());
            }
            assertEquals(readUserFile().size(), 1);
        }

        // Tests making a user with null data
        @Test
        public void makeUserTestNullData() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            try {
                um.makeUser(null, false, null, 0, null);
            } catch (UserCreationException e) {
                System.out.println(e.getMessage());
            }
            assertEquals(readUserFile().size(), 1);
        }

        // Tests validating a user's password
        @Test
        public void validateUserTest() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = um.getUserList().get(0);
            boolean validPass = um.validateUser(testUser, password);
            assertEquals(validPass, true);
        }

        // Tests writting the users to the data file
        @Test
        public void writeUsersTest() {
            UserManager um = new UserManager("JUnitTestfile.txt");
            try {
                um.makeUser(username, false, password, balance, seatList);
            } catch (UserCreationException e) {
                System.out.println(e.getMessage());
            }
            um.writeUsers();
            String userString = String.format("%s,%s,%s,%s,",
                    username,
                    password,
                    balance,
                    false
                    );
            assertEquals(readUserFile().get(0), userString);
        }

        // Tests writting users to the file when there is no user data availible
        @Test
        public void writeUsersTestNoData() {
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.writeUsers();
            assertEquals(readUserFile().size(), 0);
        }

        // Tests reading the user data when there are not seat ids
        @Test
        public void readTestNoSeatIds() {
            String testString = "username,password,100.5,First Last";
            writeFile(testString);
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            User testUser = um.getUserList().get(0);
            assertEquals(testUser.getUsername(), username);
            assertEquals(testUser.getAdmin(), admin);
            assertEquals(testUser.getPassword(), password);
            //assertEquals(testUser.getBalance(),balance);
            assertEquals(testUser.getSeatList().size(), 0);
        }

        // Tests reading user data when the balance is a string not a number
        @Test
        public void readTestInvalidBalance() {
            String testString = "username,password,FAIL,First Last,123:1234:12345";
            writeFile(testString);
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            assertEquals(um.getUserList().size(), 0);
        }

        // Tests reading in data from an invalid file name
        @Test
        public void readTestInvalidFileName() {
            writeGoodData();
            UserManager um = new UserManager("MadeUpFile.txt");
            um.readUsers();
            assertEquals(um.getUserList().size(), 0);
        }

        // Tests reading in invalid seat names
        @Test
        public void readTestInvalidSeatIds() {
            String testString = "username,password,1oo.5,First Last,123:inavalid:12345";
            writeFile(testString);
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            assertEquals(um.getUserList().size(), 0);
        }

        // Tests getting the user list
        @Test
        public void getUserListTest() {
            writeGoodData();
            UserManager um = new UserManager("JUnitTestFile.txt");
            um.readUsers();
            assertEquals(um.getUserList().size(), 1);
        }


    }
}
