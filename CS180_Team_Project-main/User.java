import java.util.ArrayList;
/**
 * User
 *
 * This class instantiates and toggles a User
 * object that represents a user on our management system.
 *
 * @author Arnav Nayak, L06
 *
 * @version Nov 10, 2025
 *
 */
public class User implements UserInterface {
    private String username;
    private String password;
    private double balance;
    private String name;
    private ArrayList<Seat> seatList;
    private UserManager um;
    private boolean isAdmin;

    public User(
        String username,
        boolean admin,
        String password,
        double balance,
        ArrayList<Seat> seatList,
        UserManager um) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.name = "DEPRECIATED";
        this.seatList = seatList;
        this.um = um;
        this.isAdmin = admin;
    }

    // getters

    public String getUsername() {
        return username;
    }
    public boolean getAdmin() {
        return isAdmin;
    }
    public String getPassword() {
        return password;
    }

    public void setAdmin(boolean newIsAdmin) {
        this.isAdmin = newIsAdmin;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Seat> getSeatList() {
        return seatList;
    }

    // setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeatList(ArrayList<Seat> seatList) {
        this.seatList = seatList;
    }

    public void subtractBalance(double cost) {
        balance -= cost;
        um.writeUsers();
    }

    public void addBalance(double refund) {
        balance += refund;
        um.writeUsers();
    }

    public void addSeat(Seat seat) {
        if (seat != null) {
            seatList.add(seat);
            seat.setUser(this);
        }
    }

    public void removeSeat(Seat seat) {
        seatList.remove(seat);
        seat.setUser(null);
    }
}
