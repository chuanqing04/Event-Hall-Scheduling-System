package objcls;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

public class UserManagement {
    private FileHandler fileHandler, fileHandler1;
    private String currentAdmin;

    // Getter for fileHandler
    public FileHandler getFileHandler() {
        return fileHandler;
    }
        
    // Setter for fileHandler
    public void setFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }
        
    // Getter for currentAdmin
    public String getCurrentAdmin() {
        return currentAdmin;
    }
        
    // Setter for currentAdmin
    public void setCurrentAdmin(String currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    public UserManagement(FileHandler fileHandler, String currentAdmin) {
        this.fileHandler = fileHandler;
        fileHandler1 = new FileHandler2("bookings.txt");
        this.currentAdmin = currentAdmin;
    }

    public boolean addUser(String username, String password, String confirmPassword, String role) {
        if (!password.equals(confirmPassword)){
            showError("Passwords do not match");
            return false;
        }

        if (!isValidUsername(username) || !isValidPassword(password)) {
            showError("Username or Password contains null or illegal characters.");
            return false;
        }
        
        if (isUsernameTaken(username)) {
            showError("Username has been taken. Please select another username");
            return false;
        }
        
        boolean success = fileHandler.addData(username, password, role);
        if (success) {
            showMessage("User added successfully.");
            logAction("Added user: " + username);
        }
        return success;
    }

    public boolean editUser(String currentUsername, String newUsername, String newPassword, String newRole, boolean changeUsername) {
        if (currentUsername.equals(currentAdmin)) {
            showError("Failed to edit own user details: " + currentUsername);
            return false;
        }
    
        if (!isValidUsername(newUsername) || !isValidPassword(newPassword)) {
            showError("Username or Password contains null or illegal characters.");
            return false;
        }
    
        if (changeUsername && !currentUsername.equals(newUsername) && isUsernameTaken(newUsername)) {
            showError("Username has been taken. Please select another username.");
            return false;
        }
    
        boolean success = fileHandler.updateData(currentUsername, newUsername, newPassword, newRole);
        if (success) {
            showMessage("User Edited successfully");
            logAction("Edited user: " + currentUsername + " to " + newUsername);
        }
        return success;
    }

    public boolean editUser(String username, String currentPassword, String newPassword, String confirmPassword) {
        if (!fileHandler.validateData(username, currentPassword)) {
            System.out.println(username + currentPassword);
            showError("Unable to change password: Wrong password.");
            return false;
        }
    
        if (!isValidPassword(newPassword)) {
            showError("Username or Password contains null or illegal characters.");
            return false;
        }
    
        if (newPassword == null ? confirmPassword != null : !newPassword.equals(confirmPassword)) {
            showError("Password cannot be null/New password and confirm password are not the same.");
            return false;
        }
    
        boolean success = fileHandler.updateData(username, newPassword);
        if (success) {
            showMessage("User Edited successfully");
            logAction("Edited user: " + username + " Password changed");
        }
        return success;
    }

    public boolean deleteUser(String username) {
        if (username.equals(currentAdmin)) {
            showError("Failed to delete own user details: " + username);
            return false;
        }

        boolean success = fileHandler.deleteData(username);
        if (success) {
            showMessage("User successfully deleted.");
            logAction("Deleted user: " + username);
        }
        return success;
    }

    public ArrayList<String[]> filterUsers(String role, String sortOrder) {
        boolean ascending = "Ascending".equals(sortOrder);
        ArrayList<String[]> filteredUsers = fileHandler.filterData(role, ascending);
        return filteredUsers;
    }

    public ArrayList<String[]> searchUsers(String query) {
        ArrayList<String[]> users;
        
        if (query.isEmpty()) {
            users = fileHandler.getDataMasked(); // If query is empty, show all users
        } else {
            users = fileHandler.searchData(query); // Search for users with usernames starting with the query
        }
        
        return users;
    }

    public boolean blockUser(String username, boolean block) {
        if (username.equals(currentAdmin)) {
            showError("Cannot block/unblock own user: " + username);
            return false;
        }
    
        boolean success = fileHandler.blockUserData(username, block);
        if (success) {
            showMessage("User " + (block ? "blocked" : "unblocked") + " successfully.");
            logAction((block ? "Blocked" : "Unblocked") + " user: " + username);
        }
        return success;
    }

    private boolean isUsernameTaken(String username) {
        return fileHandler.getData().stream().anyMatch(data -> data[0].equals(username));
    }

    private boolean isValidUsername(String username) {
        return !(username == null || username.trim().isEmpty() || username.matches(".*[.,/?!].*"));
    }
    
    private boolean isValidPassword(String password) {
        return !(password == null || password.trim().isEmpty() || password.matches(".*[,].*"));
    }

    private void logAction(String action) {
        try (FileWriter fw = new FileWriter("Adminlog.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            out.println("[" + timestamp + "] " + currentAdmin + " - " + action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public ArrayList<String[]> getBookingsByUsername(String username) {
        return ((FileHandler2) fileHandler1).getBookingsByUsername(username); // Delegate to FileHandler
    }
}