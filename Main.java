import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        Connection connection = null;
        String url = "jdbc:mariadb://localhost:3306/playground";
        String user = "root";
        System.out.print("Please enter mariadb password: ");
        String pwd = scan.next();

        try {
             connection = DriverManager.getConnection(url, user, pwd);
            String query = "select holder_id, username, first_name, last_name from bankAccountHolders";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

             System.out.println("ID, Username, First, Last");

            while(rs.next())//returns true if there is a remaining subsequent row in the table, moves pointer to subsequent row
              {
      
                int id = rs.getInt("holder_id");
                String username = rs.getString("username");
                String fn = rs.getString("first_name");
                String ln = rs.getString("last_name");
               
               
                System.out.format("%s, %s, %s, %s %n", id, username, fn, ln);
               }


    int answer = 1;
//While loop that will run through if statements corresponding to different user inputs for the variable 'answer'; will return the user to the main menu if answer does not equal zero(0).
    while(answer!=0)
    {

            System.out.println("\nWelcome to the bank!\nPlease enter the number that corresponds to your request: \n\n 0: Exit\n 1: Create a holder profile \n 2: Create a savings account \n 3: Withdraw money \n 4: Deposit money");
            answer = scan.nextInt();
              if(answer==0)
              {
                System.out.println("Bye!");
              }
              

              if(answer == 1)
              {
                System.out.println("You want to create an account holder profile! This is required to set up savings accounts.\nPlease enter your first name:");
                String firstName = scan.next();
                System.out.println("\nPlease enter your last name:");
                String lastName = scan.next();
               

              String createdUsername = "";
              int count = 1; //to count if a username is already used in the table

              while(count>=1)
              {
               int count2 = 0;
               rs.first();

                 while(rs.next())//main while loop
                  {
                  createdUsername = firstName.substring(0,1).toLowerCase() + lastName + 
                  String.valueOf((int)(Math.random() * 4) + 1);//generates random int from 1-100
                  
                   if(rs.getString("username").equals(createdUsername))
                    {
                        count2++;//this means the created username already exists in the table;this counter will increase, which means the next if statement will not be satisfied
                    }
                  }

                if(count2<=0)
                //if username is not found in table, we decrease the main counter to 0, so that the main while loop ends
                 {
                  count--;
                 }
                         
                }//end of main while loop
               

                System.out.println("\nSuccesfully created username: " + createdUsername);
                System.out.println("\nPlease enter a password:");
                String password = scan.next();
                System.out.println("Re-enter password to confirm: ");
                String password2 = scan.next();

                while(!(password2.equals(password)))
                {
                 System.out.println("Incorrect password. Please re-enter password to confirm:");
                 password2 = scan.next();
                }


                //adds the createdUsername, first name, last name, and password to the table using SQL
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO bankAccountHolders(username, first_name, last_name, password) VALUES (?, ?, ?, ?)");
                pstmt.setString(1, createdUsername);
                pstmt.setString(2, firstName.toUpperCase());
                pstmt.setString(3, lastName.toUpperCase());
                pstmt.setString(4, password);
                pstmt.executeUpdate();
                System.out.println("\n" + createdUsername + " added to database");
                  }
              

                if(answer == 2)
                {
                    System.out.println("You want to set up a savings account!\nHave you created an account holder profile yet?: Y/n");
                    String ans = scan.next().toLowerCase();
                    if(ans.equals("yes"))
                    {
                     System.out.println("Great! Please enter the username of your account holder profile: ");
                     String username2 = scan.next();
                    

                     String query2 = "select username from bankAccountHolders";// Retrieving the usernames from the bankAccounts table
                     ResultSet rs2 = st.executeQuery(query2);
                     int count3 = 0;
                      while(rs2.next())//loops through rowns of bankAccountHolders, will increase the variable count3 if the entered username is found in the database.
                       {
                       if(rs2.getString("username").equals(username2))
                       {
                        count3++;
                       }
                       }

                       if(count3>0)//If this executes, that means the username the user entered DOES exist in the bankAccountHolders database.
                       {
                        System.out.println("Please enter the password for the username " + username2 + ":");
                        String password2 = scan.next();
                        PreparedStatement pstmt2 = connection.prepareStatement("SELECT username, first_name, last_name, password FROM bankAccountHolders WHERE username = ?");//this selects the rows that ONLY contain usernames that match the one entered by the user
                        pstmt2.setString(1, username2);
                        ResultSet rsInfo = pstmt2.executeQuery();//creates a result set of only rows with the user-specified username.
                        
                        rsInfo.next();//this moves the pointer to the first row of the result set
                        
                          while((rsInfo.getString("password").equals(password2))==false)//will loop through until entered password matches the password in database
                          {
                            System.out.println("Incorrect password. Please re-enter the correct password: ");
                            password2 = scan.next();
                          }
                          System.out.println("Perfect! Please enter a starting deposit amount: ");
                          float balance = scan.nextFloat();

                          PreparedStatement pstmt3 = connection.prepareStatement("INSERT INTO bankAccounts(username, firstName, lastName, balance, lastTransaction) VALUES (?, ?, ?, ?, ?)");
                          pstmt3.setString(1, username2);
                          pstmt3.setString(2, rsInfo.getString("first_name"));
                          pstmt3.setString(3, rsInfo.getString("last_name"));
                          pstmt3.setFloat(4, balance);
                          pstmt3.setString(5, Float.toString(balance));//turns the entered balance into a string, which is then entered into the database as the last transaction of the account.
                          pstmt3.executeUpdate();
                          System.out.println("Great! Your savings account is active!");
                       }
                       else//if the entered username is not found in the database, the user will be directed back to the main menu to create a user profile.
                       {
                        System.out.println("That username does not exist in our database. You must create an account holder profile before you set up a savings account. At the main menu, enter 1 to begin creating your profile. Once completed, you can begin setting up a savings account.");
                       }

                    }
                    else//will run if the answer to "do you have an account holder profile" is "no".
                    {
                     System.out.println("You must create an account holder profile before you set up a savings account. At the main menu, enter 1 to begin creating your profile. Once completed, you can begin setting up a savings account.");

                    }
                 
           
                 }

                 if(answer == 3)
                 {
                  System.out.println("You want to withdraw money! ");
                  String username3 = "";

                     String query3 = "select username from bankAccountHolders";// Retrieving the usernames from the bankAccounts table
                     ResultSet rs3 = st.executeQuery(query3);
                     int count3 = 0;
                     while(count3 == 0)
                      {
                      System.out.println("Please enter a valid username: ");
                      rs3.beforeFirst();
                      username3 = scan.next();
                       while(rs3.next())//loops through rowns of bankAccountHolders, will increase the variable count3 if the entered username is found in the database.
                        {
                         if(rs3.getString("username").equals(username3))
                          {
                          count3++;
                          }
                        }
                        }

                        System.out.println("Please enter the password for the username " + username3 + ":");
                        String password3 = scan.next();
                        PreparedStatement pstmt3 = connection.prepareStatement("SELECT username, password FROM bankAccountHolders WHERE username = ?");//this selects the rows that ONLY contain usernames that match the one entered by the user
                        pstmt3.setString(1, username3);
                        ResultSet rsInfo2 = pstmt3.executeQuery();//creates a result set of only rows with the user-specified username.
                        
                        rsInfo2.next();//this moves the pointer to the first row of the result set
                        
                          while((rsInfo2.getString("password").equals(password3)) == false)//will loop through until entered password matches the password in database
                          {
                            System.out.println("Incorrect password. Please re-enter the correct password: ");
                            password3 = scan.next();
                          }
                       
                      
                       System.out.println("Please enter an amount to withdraw: ");
                       float withdrawAmnt = scan.nextFloat();
                       PreparedStatement pstmtBalance = connection.prepareStatement("SELECT balance FROM bankAccounts WHERE username = ?");
                       pstmtBalance.setString(1, username3);
                       ResultSet rsBalance = pstmtBalance.executeQuery();//creates a result set of 1 row, 1 column containing the balance of the savings account
                       rsBalance.next();
                       float currentBalance = rsBalance.getFloat("balance");//sets this variable to the current balnce of the savings account

                        while(withdrawAmnt > currentBalance)//will not let user enter a value greater than their current balance
                        {
                          System.out.println("You do not have enough funds. Please enter a new amount to withdraw: ");
                          withdrawAmnt = scan.nextFloat();
                        }
                        PreparedStatement pstmnt4 = connection.prepareStatement("UPDATE bankAccounts set balance = ? WHERE username =?");
                        pstmnt4.setFloat(1, (currentBalance - withdrawAmnt));
                        pstmnt4.setString(2, username3);
                        pstmnt4.executeQuery();
                        System.out.println("Your new account balance is " + (currentBalance-withdrawAmnt));

                 
                   }

                   if(answer ==4)
                   {
                    
                  System.out.println("You want to deposit money! ");
                  String username4 = "";

                     String query4 = "select username from bankAccountHolders";// Retrieving the usernames from the bankAccounts table
                     ResultSet rs4 = st.executeQuery(query4);
                     int count4 = 0;

                     while(count4 == 0)//will loop through until a valid username is entered
                       {
                      rs4.beforeFirst();//places pointer right before first row of result set, this needs to happen every time this loop checks a new username
                      System.out.println("Please enter a valid username: ");
                      username4 = scan.next();
                       while(rs4.next())//loops through rowns of bankAccountHolders, will increase the variable count3 if the entered username is found in the database.
                        {
                         if(rs4.getString("username").equals(username4))
                          {
                          count4++;
                          }
                        }
                        }

                        System.out.println("Please enter the password for the username " + username4 + ":");
                        String password4 = scan.next();
                        PreparedStatement pstmt4 = connection.prepareStatement("SELECT password FROM bankAccountHolders WHERE username = ?");//this selects the rows that ONLY contain usernames that match the one entered by the user
                        pstmt4.setString(1, username4);
                        ResultSet rsInfo2 = pstmt4.executeQuery();//creates a result set of 1 row, 1 column containing the password connected to the user-specified username.
                        
                        rsInfo2.next();//this moves the pointer to the first row of the result set
                        
                          while((rsInfo2.getString("password").equals(password4)) == false)//will loop through until entered password matches the password in database
                          {
                            System.out.println("Incorrect password. Please re-enter the correct password: ");
                            password4 = scan.next();
                          }
                       
                      
                       System.out.println("Please enter an amount to deposit: ");
                       float withdrawAmnt = scan.nextFloat();
                       PreparedStatement pstmtBalance = connection.prepareStatement("SELECT balance FROM bankAccounts WHERE username = ?");
                       pstmtBalance.setString(1, username4);
                       ResultSet rsBalance = pstmt4.executeQuery();//creates a result set of 1 row, 1 column containing the balance of the savings account

                       float currentBalance = rsBalance.getFloat("balance");//sets this variable to the current balnce of the savings account

                      
                        PreparedStatement pstmnt4 = connection.prepareStatement("UPDATE bankAccounts set balance = ? WHERE username =?");//A query that will update the new balance of the account
                        pstmnt4.setFloat(1, (currentBalance + withdrawAmnt));
                        pstmnt4.setString(2, username4);
                        pstmnt4.executeQuery();

                        System.out.println("Your new account balance is " + (currentBalance + withdrawAmnt));

                   }
            st.close();
      
        }
      
    }

            
         catch (SQLException e) {
            e.printStackTrace();
        }
     }
        
}
    


        
}
    

