package mgbolotto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MgboImplementation implements MgboInterface{
    Scanner scanner = new Scanner(System.in);
    
    @Override
    public void createPlayer() {
        //collect player name
        System.out.println("Enter your name: \n");
        String player_name = scanner.nextLine();
        //collect contact information
        System.out.println("Enter your phone number: \n");
        String contactInformation = scanner.nextLine();
        //create passcode
        System.out.println("Create a four digit passcode: \n");
        String passcod = scanner.nextLine();
        while(true){
            if(passcod.length() != 4){
                print("Pin must have 4 digits, try again");
            }
            //confirm passcode
            System.out.println("Enter passcode again to confirm: \n");
            String confirmPassCode = scanner.nextLine();
            if(passcod.equals(confirmPassCode)){
                print("Pin updated successully");
                break;
            } else{
                print("Incorrect passcode!");
            }
        }
        //add information to database
        try(Connection conn = getDBConnection()){
            int passcode = Integer.parseInt(passcod);
            String query = "INSERT INTO players (player_name,contact_information,passcode) VALUES (?,?,?)";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, player_name);
            stmt.setString(2, contactInformation);
            stmt.setInt(3, passcode);
            stmt.execute();
            if(stmt.execute()){
               print("User created successfully"); 
            }
            //boolean state = stmt.execute();
            
//             ResultSet generatedKeys = stmt.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                int playerId = generatedKeys.getInt(1);
//                System.out.println("Player created successfully with ID: " + playerId);
//            } else {
//                System.out.println("Failed to create player.");
//            }
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addBank() {
        System.out.println("Enter your name: \n");
        String pName = scanner.nextLine();
        System.out.println("Enter your passcode: \n");
        String passcode = scanner.nextLine();
        System.out.println("Enter the Bank name: \n");
        String bank = scanner.nextLine();
        System.out.println("Enter your account number: \n");
        String acc_num = scanner.nextLine();
        //check if num is equal to 10 digits
        while(true){
            if(acc_num.length() != 10){
                System.out.println("Invalid Account Number!");
            }
            break;
        }
        //database query to add bank to database
        try(Connection conn = getDBConnection()){
            String query = "UPDATE players SET bank = ?, acc_number = ? WHERE passcode = ? ";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, bank);
            stmt.setString(2, acc_num);
            stmt.setString(3, passcode);
            stmt.execute();
            if(stmt.execute()){
                print("Account updated successfully");
            }

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     @Override
    public void playLottery() {
        //identify the user
        System.out.println("Enter your correct phone number: ");
        String phoneNumber = scanner.nextLine();
        //collect users lucky numbers
        System.out.println("Enter your 5 lucky numbers (1-90, separated by commas: \n"
                + " eg: 10,12,29,19,30");
        String luckyNumbers = scanner.nextLine();
        //store lucky numbers in a string array
        String[] numberStr = luckyNumbers.split(",");
        //check to ensure that the user enters exactly 5 numbers
        if (numberStr.length != 5){
            System.out.println("Please Enter exactly 5 numbers");
        }
        //convert the string array of lucky numbers to an int array
        int[] numbers = new int[5];
        for(int i = 0; i < 5; i++){
            numbers[i] = Integer.parseInt(numberStr[i].trim());
            //check to ensure that the numbers are within the specified range
            if(numbers[i] < 1 || numbers[i] > 90){
                print(numbers[i] + " is an invalid number");
                return;
            }
            //return;
        }
        System.out.println("Your lucky numbers: " + luckyNumbers);
        try(Connection conn = getDBConnection()){
            String query = "INSERT INTO tickets (lucky_numbers) VALUES (?)";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, luckyNumbers);
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
 
       saveLuckyNumbers(luckyNumbers);
       createTickect( phoneNumber, luckyNumbers);
       
    }

    @Override
    public void winning() {
        System.out.println("Enter your passcode: \n");
        String passcode = scanner.nextLine();
        System.out.println("Check winnings by date; enter date in the format yyyy-mm-dd: \n");
        String date = scanner.nextLine();
        while(true){
            try(Connection conn = getDBConnection()){
            String query = "SELECT winning_numbers FROM lottery_drawings WHERE drawings_date = ? ";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, date);
            ResultSet result = stmt.executeQuery();
            if(result.next()){
                System.out.println(result.getString("winning_numbers"));
            }else{
                print("An error occured!");
                break;
            }
//            if (result.next()) {
//                int totalMatched = result.getInt("total_matched");
//                int winnings = totalMatched == 5 ? 1000 : 0;
//
//                System.out.println("Your winnings: N" + winnings);
//            } else {
//                System.out.println("Your winnings: N0");
//            }
           
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        try(Connection conn = getDBConnection()){
            String query = "SELECT lucky_numbers FROM tickets WHERE passcode = ? ";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, passcode);
            ResultSet result = stmt.executeQuery();
            if(result.next()){
                System.out.println(result.getString("lucky_numbers"));
            }else{
                print("An error occured!");
                break;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }

    @Override
    public void checkWalletBalance() {
        System.out.println("Enter your passcode: ");
        String passcode = scanner.nextLine();
        try(Connection conn = getDBConnection()){
            String query = "SELECT account_balance FROM players WHERE passcode = ? ";
            
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, passcode);
            ResultSet result = stmt.executeQuery();
            
            if (result.next()) {
                double balance = result.getDouble("account_balance");
                System.out.println("Your wallet balance: N" + balance);
            } else {
                System.out.println("An error occurred.");
            }
          
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void history() {
        print("History:");
        print("Enter Date in this format: 'yyyy-mm-dd'");
        String date = scanner.nextLine();
         try(Connection conn = getDBConnection()){
            String query = "SELECT winning_numbers FROM lottery_drawings WHERE drawings_date = ? ";
            
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, date);
            stmt.executeQuery();
          
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void topUp() {
        System.out.println("How much do you want to topUp: \n");
        int amnt = scanner.nextInt();
        System.out.println("Enter your account number");
        String acc_num = scanner.nextLine();
        try(Connection conn = getDBConnection()){
            String query = "UPDATE players SET account_balance = account_balance + ? WHERE account_number = ?";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setDouble(1, amnt);
            stmt.setString(2, acc_num);
            boolean state = stmt.execute();
            
            if(state == true){
                print("account balance has been updated");
            }
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
     public static Connection getDBConnection() throws ClassNotFoundException, SQLException {
            String username = "root";
            String password = "Dhnonny23";
            String url = "jdbc:mysql://127.0.0.1:3306/mgbolotto_db?useSSL=false&serverTimezone=UTC";
            Class.forName("com.mysql.cj.jdbc.Driver");
            return (Connection) DriverManager.getConnection(url, username, password);
    }
    private void print(String value) {
        System.out.println(value);
    }


    private void createTickect(String phoneNumber, String luckyNumbers) {
        try(Connection conn = getDBConnection()){
            String query = "INSERT INTO tickets (phone_number, lucky_numbers) VALUES (?, ?)";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, phoneNumber);
            stmt.setString(2, luckyNumbers);
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveLuckyNumbers(String numbers) {
        try(Connection conn = getDBConnection()){
            String query = "INSERT INTO tickets (lucky_numbers) VALUES (?)";
            PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
            stmt.setString(1, numbers);
            stmt.execute();
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(MgboImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}