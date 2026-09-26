package objcls;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileHandler2 extends FileHandler {
   private String filePath;

   public FileHandler2(String filePath) {
      super(filePath); // Call the constructor of the parent class (if applicable)
      this.filePath = filePath; // Specific to FileHandler2
   }

   @Override
   public String getFilePath() {
      return this.filePath;
   }

   @Override
   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   // Validates username and password
   @Override
   public boolean validateData(String username, String password) {
      return this.loadData().stream().anyMatch(data -> data[0].equals(username) && data[1].equals(password));
   }

   // Get specific user data by username
   @Override
   public String[] getUserData(String username) {
      for (String[] data : this.loadData()) {
         if (data[0].equals(username)) {
            return data;
         }
      }
      return null;
   }

   // Get all data
   @Override
   public ArrayList<String[]> getData() {
      return this.loadData();
      
   }

   // Get all data with passwords masked
   @Override
   public ArrayList<String[]> getDataMasked() {
      ArrayList<String[]> maskedData = new ArrayList<>();
      for (String[] user : this.loadData()) {
         maskedData.add(new String[]{user[0], user[1].replaceAll(".", "*"), user[2], user[3]});
      }
      return maskedData;
   }

   // Add new data (append mode)
   @Override
   public boolean addData(String username, String password, String userType) {
      return this.writeData(username, password, userType, true);
   }

   // Update user data (username, password, role)
   @Override
   public boolean updateData(String oldUsername, String newUsername, String newPassword, String role) {
      ArrayList<String[]> data = this.loadData();
      boolean updated = false;

      for (String[] credential : data) {
         if (!credential[0].equals(oldUsername)) {
         } else {
             credential[0] = newUsername;
             credential[1] = newPassword;
             credential[2] = role;
             updated = true;
         }
      }

      if (updated) {
         return this.saveData(data);
      }
      return false;
   }

   // Update user password
   @Override
   public boolean updateData(String username, String newPassword) {
      ArrayList<String[]> data = this.loadData();
      boolean updated = false;

      for (String[] credential : data) {
         if (credential[0].equals(username)) {
            credential[1] = newPassword;
            updated = true;
         }
      }

      if (updated) {
         return this.saveData(data);
      }
      return false;
   }

   // Delete user data
   @Override
   public boolean deleteData(String username) {
      ArrayList<String[]> data = this.loadData();
      boolean found = data.removeIf(credential -> credential[0].equals(username));
      return found ? this.saveData(data) : false;
   }

   // Block or unblock user
   @Override
   public boolean blockUserData(String username, boolean block) {
      ArrayList<String[]> users = this.loadData();
      boolean userFound = false;

      for (String[] user : users) {
         if (user[0].equals(username)) {
            user[3] = block ? "TRUE" : "FALSE";
            userFound = true;
            break;
         }
      }

      return userFound ? this.saveData(users) : false;
   }

   // Filter data based on role
   @Override
   public ArrayList<String[]> filterData(String role, boolean ascending) {
      ArrayList<String[]> users = (ArrayList<String[]>) this.getDataMasked().stream()
              .filter(user -> role.equalsIgnoreCase("All") || user[2].equalsIgnoreCase(role))
              .collect(Collectors.toList());

      users.sort((user1, user2) -> ascending ? user1[0].compareToIgnoreCase(user2[0]) : user2[0].compareToIgnoreCase(user1[0]));
      return users;
   }

   // Search user by username
   @Override
   public ArrayList<String[]> searchData(String username) {
      return (ArrayList<String[]>) this.getDataMasked().stream()
              .filter(user -> user[0].toLowerCase().startsWith(username.toLowerCase()))
              .collect(Collectors.toList());
   }

   // Load data from file
   private ArrayList<String[]> loadData() {
      ArrayList<String[]> data = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
         String line;
         while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
               data.add(parts);
            }
         }
      } catch (IOException e) {
         System.out.println("IO Exception: " + e.getMessage());
      }
      return data;
   }


   // Write data to file
   private boolean writeData(String username, String password, String userType, boolean append) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath, append))) {
         writer.write(String.join(",", username, password, userType, "FALSE"));
         writer.newLine();
         return true;
      } catch (IOException e) {
         System.out.println("IO Exception: " + e.getMessage());
         return false;
      }
   }

   // Save updated data to file
   private boolean saveData(ArrayList<String[]> users) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath, false))) {
         for (String[] user : users) {
            writer.write(String.join(",", user));
            writer.newLine();
         }
         return true;
      } catch (IOException e) {
         System.out.println("IO Exception: " + e.getMessage());
         return false;
      }
   }

    public void loadData(String username) {
     try {
         BufferedReader reader = new BufferedReader(new FileReader("bookings.txt"));
         String line;
         boolean showBooking = false;

         while ((line = reader.readLine()) != null) {
             // Check if this line contains the CustomerID
             if (line.startsWith("CustomerID:")) {
                 String bookingCustomerID = line.split(":")[1].trim();

                 // If the CustomerID matches the logged-in customer, show booking details
                 showBooking = bookingCustomerID.equals(username);
             }

             // If it's a booking for the current customer, print it
             if (showBooking) {
                 System.out.println(line);
             }

             // Reset when a booking ends (e.g., encountering the separator line)
             if (line.equals("--------------")) {
                 showBooking = false;
             }
         }
         reader.close();
     } catch (IOException e) {
         e.printStackTrace();
     }
 }

    // Fetch the currently logged-in username from login.txt
    public String getLoggedInUsername() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Login.txt"))) {
            String line = reader.readLine();  // Only read the first line (current session)
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    return parts[0].trim(); // Return the username from the first part of the line
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        return null; // Return null if no username is found
    }

    // Save booking details in a comma-separated format
    public void saveBooking(String bookingID, String hallType, String date, String time, double hallFee) {
        FileHandler2 fileHandler = new FileHandler2("login.txt");
        String username = fileHandler.getLoggedInUsername();

        if (username != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("bookings.txt", true))) {
                // Write booking details in a single line, comma-separated
                writer.write(username + "," + bookingID + "," + hallType + "," + date + "," + time + "," + hallFee);
                writer.newLine(); // Move to the next line after each booking
                System.out.println("Booking saved successfully for " + username);
            } catch (IOException e) {
                System.out.println("An error occurred while saving the booking.");
                e.printStackTrace();
            }
        } else {
            System.out.println("No user is logged in.");
        }
    }

    
        // Read bookings data from the bookings.txt file
      public List<String> loadBookingsRaw() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("bookings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    
    public void login(String username, String password) {
    if (validateData(username, password)) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("login.txt"))) {
            String[] userData = getUserData(username);
            if (userData != null) {
                // Write the actual username, password, and role to the file
                writer.write(username + "," + userData[1] + "," + userData[2] + ",FALSE");
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
     }
    }
    // Method to get bookings for a specific user
    public ArrayList<String[]> getBookingsByUsername(String username) {
        ArrayList<String[]> userBookings = new ArrayList<>();
        List<String> allBookings = loadBookingsRaw(); // Fetch all bookings as raw lines

        for (String bookingLine : allBookings) {
            String[] bookingDetails = bookingLine.split(","); // Split the line by commas
            if (bookingDetails.length == 6 && bookingDetails[0].equals(username)) { // Check if it's the right user
                userBookings.add(bookingDetails); // Add the booking details to the user's bookings
            }
        }
        return userBookings;
      }
}
