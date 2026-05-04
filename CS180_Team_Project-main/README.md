# CS 180 Group Project, L06
We couldn't figure out how to format our code with folders correctly...
## Running the program
### Phase one backend testing
To test the backend component from phase 1 compile and run the main methods in the following files while ensuring that JUnit is installed. You can also just run each main method of the following files through Intellij while making sure the JUnit package is installed.
- EventManagerTestCases.java
- EventTestCases.java
- SeatTestCases.java
- UserTestCases.java
- UserManagerTestCases.java
After compiling and running each of these files individually you will receive ouput that states the ammount of tests that ran, alongth with the number passed. In addition there will also be potential messages printed to the console indicated bad data that was caught and handled by the program.
### Phase two server and client test
To test the server and client that were written in phase two first run the main method in Server.java and then the main method in Client.java. Into each client you run type in the socket, 4242, and then follow through with the rest of the on screen prompts to use the program.
### Phase three GUI
To test the GUI from phase three follow the below steps:
1. Run the main method of the Server.java file (either through the `java` and `javac` commands, or clicking he play button in Intellij).
2. Run the main method of the Client.java file using the same process.
3. Navigate the GUI to test the functionalities. (The program will have a default user account with username `test` and password `password`. It will also have a default admin account with username `admin` and password `password`.)

You test open multiple windows for each instance of the Client you want, but to switch users within the same instance you must close and reopen it.
# Submissions
## Vocareum
David submitted this phase on Vocareum.
## Report
Emma submited the report on Brightspace.
## Presentation
David submited the presentation video via link on Brightspace. 
# Classes
## Phase one
### EventManager
Event manager is a class that can create, save, load, and delete events. It is thread safe and modifies files to save and draw from device memory rather than local variables. Testcases verify that all of EventManager's methods run and compile independently of each other. This primarily relates to Event and Seat, as it writes out Events and Seats to seperate .txt Files.
### Event
Event is in charge of the making of all new movie showings as well as what seating and time slot each movie will have. Testcases have been built to make sure all methods do as they should and we've tested its relation to EventManager in its testcases as well. This class related to Seats and EventManager to store all information of a showing.
### Seat
Seat exists to save all seat information about an event, like if its reserved and where it is in the theatrer itself, as well as what different seating arrangments there are (Small, Medium, and Large theaters). We've created Tests to verify that it creates/ gets the correct positions, names, etc... And this relates to Event and Event Manager in that it is basically like the blueprint for Event and is being saved into a file in EventManager.
### UserManager
UserManager does many things, in addition to saving users and writing it to a file, it can validate users and can check to see whether or not a password/ username is incorrect. There have been extensive testcases made that make sure users can only be created with validated data.
### User
User is a class that can create a new user, but it only has to be validated in UserManager. So, it creates their username, password, wallet, etc... We have created multiple testcases to make sure each method can work independently and has been used in the UserManager testcases to make sure it works with UserManager in the way it should.
## Phase Two
### Server
This class creates a socket for a client to connect to. It will then constantly loop and look for a connected client. When a new client connects the Server will create a new instance of itself for that specific client. It will then create a new thread of that Server and run `.start()` on the thread. Once the thread begins in opens a new UserManager and EventManager and has them read in their respective files.

Once the files are read in this new Server begins its main loop. This entails of the Server looking for new data from the client and then executing the appropriate database function corresponding to the client. After the funciton is executed it will return the data in the correct format to the client. These functions include things as login, get events, get seats, and purchase seat. The server also handles all authentication for administrator privillages to the system.

### Client
 The Client class logs the user into a new socket that then connects to teh Server. The server will add the client to a new thread to be handled. Once in this waiting the client will ask the user to sign up or login. Once this method is handled it will give the user options as to what they want to do in the movie theater. All data collected from the user pertaining to these options and the option selected itself will be forwarded to the Server for processing. The server will then respond with data that the Client will handle.

 ## Phase Three
 ### GUI
The project was reformed so that our client now implements a Java Swing GUI, using JOptionPane and JFrame. The JOptionPane dialog requires minimal resources to load and can be used to authorize a user’s identity on the application. Once authenticated the system switches to the JFrame, which provides the complex navigation (buttons, panels, seating maps, etc…) required for the system’s core functionality.

