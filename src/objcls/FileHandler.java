package objcls;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileHandler {
    private String filePath;
    

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    // Getter and Setter for filePath
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    //Public Methods
    //Validates Email and Password
    public boolean validateData(String username, String password) {
        return loadData().stream()
                .anyMatch(data -> data[0].equals(username) && data[1].equals(password));
    }
    
    //Gets a single row of Data
    public String[] getUserData(String username) {
        return loadData().stream()
                .filter(data -> data[0].equals(username))
                .findFirst()
                .orElse(null);
    }
    
    //Gets all data
    public ArrayList<String[]> getData() {
        return loadData();
    }
    
    //Gets all data with passwords hashed
    public ArrayList<String[]> getDataMasked() {
        return loadData().stream()
                .map(user -> new String[]{user[0], user[1].replaceAll(".", "*"), user[2], user[3]})
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    //Add. Edit, Delete, Block
    public boolean addData(String username, String password, String userType) {
        return writeData(username, password, userType, true);
    }

    public boolean updateData(String oldUsername, String newUsername, String newPassword, String role) {
        ArrayList<String[]> data = loadData();
        boolean updated = false;

        for (String[] credential : data) {
            if (credential[0].equals(oldUsername)) {
                credential[0] = newUsername;
                credential[1] = newPassword;
                credential[2] = role;
                updated = true;
            }
        }

        if (updated) {
            return saveData(data);
        }
        return false;
    }

    public boolean updateData(String username, String newPassword) {
        ArrayList<String[]> data = loadData();
        boolean updated = false;

        for (String[] credential : data) {
            if (credential[0].equals(username)) {
                credential[1] = newPassword;
                updated = true;
            }
        }

        if (updated) {
            return saveData(data);
        }
        return false;
    }

    public boolean deleteData(String username) {
        ArrayList<String[]> data = loadData();
        boolean found = data.removeIf(credential -> credential[0].equals(username));

        if (found) {
            return saveData(data);
        }
        return false;
    }

    public boolean blockUserData(String username, boolean block) {
        ArrayList<String[]> users = loadData();
        boolean userFound = false;

        for (String[] user : users) {
            if (user[0].equals(username)) {
                user[3] = block ? "TRUE" : "FALSE"; // Update the Blocked? status
                userFound = true;
                break;
            }
        }

        if (userFound) {
            return saveData(users);
        }
        return false; // User not found
    }

    //Data Manipulation (Search and Filtering)
    public ArrayList<String[]> filterData(String role, boolean ascending) {
        ArrayList<String[]> users = getDataMasked().stream()
                .filter(user -> role.equalsIgnoreCase("All") || user[2].equalsIgnoreCase(role))
                .collect(Collectors.toCollection(ArrayList::new));
    
        users.sort((user1, user2) -> {
            if (ascending) {
                return user1[0].compareToIgnoreCase(user2[0]);
            } else {
                return user2[0].compareToIgnoreCase(user1[0]);
            }
        });
    
        return users;
    }

    public ArrayList<String[]> filterBookingsData(String hallType, boolean ascending) {
        ArrayList<String[]> bookings = getData().stream()
                .filter(booking -> hallType.equalsIgnoreCase("All") || booking[2].equalsIgnoreCase(hallType)) // Assuming index 2 is Hall Type
                .collect(Collectors.toCollection(ArrayList::new));
    
        bookings.sort((booking1, booking2) -> {
            if (ascending) {
                return booking1[0].compareToIgnoreCase(booking2[0]); // Assuming index 0 is Username
            } else {
                return booking2[0].compareToIgnoreCase(booking1[0]);
            }
        });
    
        return bookings;
    }

    public ArrayList<String[]> searchData(String username) {
        return getDataMasked().stream()
                .filter(user -> user[0].toLowerCase().startsWith(username.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    // All Private File Handling methods
    private ArrayList<String[]> loadData() {
        ArrayList<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                data.add(parts);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        return data;
    }

    private boolean writeData(String username, String password, String userType, boolean append) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, append))) {
            writer.println(String.join(",", username, password, userType, "FALSE"));
            return true;
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            return false;
        }
    }

    private boolean saveData(ArrayList<String[]> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (String[] user : users) {
                writer.println(String.join(",", user));
            }
            return true;
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            return false;
        }
    }
    
//    public class HallRate {
//        private static final Map<String, Double> hallRates = new HashMap<>();
    
//        static {
//            hallRates.put("Auditorium", 300.00);
//            hallRates.put("Banquet Hall", 100.00);
//            hallRates.put("Meeting Room", 50.00);
//        }
    
//        public static double getRateByHallType(String hallType) {
//            if (hallRates.containsKey(hallType)) {
//                return hallRates.get(hallType);
//            } else {
//                throw new IllegalArgumentException("Invalid hall type: " + hallType);
//            }
//        }
//    }
    
    // Save a new booking

    // Load all bookings
    public List<String[]> loadBookings() {
        List<String[]> bookings = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Split the line by commas and add to bookings list
                String[] bookingDetails = line.split(",");
                bookings.add(bookingDetails);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        
        return bookings;
    }
    
    // Load all maintenance issues from the maintenance.txt file
    public List<String[]> loadMaintenanceIssues() {
        List<String[]> issues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("maintenance.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] issueData = line.split(",");
                issues.add(issueData);
            }
        } catch (IOException e) {
            System.out.println("IO Exception while loading issues: " + e.getMessage());
        }
        return issues;
    }

    // Method to update a maintenance issue in the file
    public boolean updateMaintenanceIssue(int issueId, String customerName, String description, String hallName, 
                                          String hallType, String assignedStaff, String status, String response, 
                                          String remarks, String startDate, String startTime, 
                                          String endDate, String endTime) {
        List<String[]> issues = loadMaintenanceIssues();
        boolean updated = false;

        try (PrintWriter writer = new PrintWriter(new FileWriter("maintenance.txt"))) {
            // Write header
            writer.println("Issue ID,Customer Name,Description,Hall Name,Hall Type,Assigned Staff,Status,Response," +
                    "Remarks,Maintenance Start Date,Maintenance Start Time,Maintenance End Date,Maintenance End Time");

            for (String[] issue : issues) {
                // Check if the current issue matches the issue ID
                if (Integer.parseInt(issue[0]) == issueId) {
                    // Update the current issue with new values
                    if (issue.length >= 13) { // Ensure there are enough elements
                        issue[1] = customerName; // Customer Name
                        issue[2] = description;   // Description
                        issue[3] = hallName;      // Hall Name
                        issue[4] = hallType;      // Hall Type
                        issue[5] = assignedStaff;  // Assigned Staff
                        issue[6] = status;        // Status
                        issue[7] = response;      // Response
                        issue[8] = remarks;       // Remarks
                        issue[9] = startDate;     // Maintenance Start Date
                        issue[10] = startTime;    // Maintenance Start Time
                        issue[11] = endDate;      // Maintenance End Date
                        issue[12] = endTime;      // Maintenance End Time

                        updated = true;           // Mark as updated
                        System.out.println("Updated Issue ID: " + issueId); // Debugging statement
                    } else {
                        System.out.println("Error: Issue array length is insufficient for Issue ID: " + issueId);
                    }
                }
                writer.println(String.join(",", issue)); // Write each issue back to the file
            }
        } catch (IOException e) {
            System.out.println("IO Exception while updating issues: " + e.getMessage());
            return false;
        }

        return updated;
    }
    
    public List<String[]> loadHallData() {
        return loadData();
    }
}


   


