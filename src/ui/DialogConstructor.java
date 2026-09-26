package ui;

import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.*;
import objcls.BookingManagement;
import objcls.FileHandler;
import objcls.UserManagement;

//Global class for Dialog Based User Interfaces
public class DialogConstructor {
    private UserManagement userManagement;
    private BookingManagement bookingManagement;

    //Getters and Setters
    public UserManagement getUserManagement() {
        return userManagement;
    }

    public void setUserManagement(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public BookingManagement getBookingManagement() {
        return bookingManagement;
    }

    public void setBookingManagement(BookingManagement bookingManagement) {
        this.bookingManagement = bookingManagement;
    }

    //Constructor
    public DialogConstructor(FileHandler fileHandler, String currentAdmin) {
        this.userManagement = new UserManagement(fileHandler, currentAdmin); // Passing null for Admin UI if not needed
        this.bookingManagement = new BookingManagement(fileHandler, currentAdmin);
    }

    // Public methods
    // Method that creates registration dialog
    public void openRegistrationDialog(Consumer<Boolean> onRegistration) {
        String[] labels = {"Username:", "Password:", "Confirm Password:"};
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JComponent[] components = {usernameField, passwordField, confirmPasswordField};
    
        createDialog("Register", 400, 300, labels, components, dialog -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String role = "customer";
    
            boolean success = userManagement.addUser(username, password, confirmPassword, role);
            onRegistration.accept(success);
            if (success) {
                dialog.dispose();
            }
        });
    }

    // Method that creates filter dialog
    public void openFilterDialog(Consumer<ArrayList<String[]>> onFilterApplied) {
        String[] labels = {"Role:", "Sort By Username:"};
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"All", "Admin", "Scheduler", "Manager", "Customer"});
        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{"Ascending", "Descending"});
        JComponent[] components = {roleComboBox, sortComboBox};

        createDialog("Filter Users", 400, 250, labels, components, dialog -> {
            String role = (String) roleComboBox.getSelectedItem();
            String sortOrder = (String) sortComboBox.getSelectedItem();
            
            ArrayList<String[]> filteredUsers = userManagement.filterUsers(role, sortOrder);
            if (filteredUsers != null) {
                onFilterApplied.accept(filteredUsers);
            }
        });
    }

    public void openBookingFilterDialog(Consumer<ArrayList<String[]>> onFilterApplied) {
        String[] labels = {"Hall Type:", "Sort By Username:"};
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"All", "Auditorium", "Banquet Hall", "Meeting Room"});
        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{"Ascending", "Descending"});
        JComponent[] components = {roleComboBox, sortComboBox};

        createDialog("Filter Bookings", 400, 250, labels, components, dialog -> {
            String role = (String) roleComboBox.getSelectedItem();
            String sortOrder = (String) sortComboBox.getSelectedItem();
            
            ArrayList<String[]> filteredBookings = bookingManagement.filterBookings(role, sortOrder);
            if (filteredBookings != null) {
                onFilterApplied.accept(filteredBookings);
            }
        });
    }

    // Method that creates add user dialog
    public void openAddUserDialog(Consumer<Boolean> onUserAdded) {
        String[] labels = {"Username:", "Password:", "Confirm Password:","Role:"};
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"admin", "scheduler", "manager"});
        JComponent[] components = {usernameField, passwordField, confirmPasswordField,roleComboBox};

        createDialog("Add User", 400, 350, labels, components, dialog -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmpassword = new String(confirmPasswordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            boolean success = userManagement.addUser(username, password, confirmpassword, role);
            onUserAdded.accept(success);
            if (success) {
                dialog.dispose();
            }
        });
    }

    // Method that creates the edit user dialog
    public void openEditUserDialog(String currentUsername, String currentPassword, String currentRole, Consumer<Boolean> onUserEdited) {
        String[] labels = {"Username:", "New Password:", "Change Username?", "User Type:"};
        JTextField usernameField = new JTextField(currentUsername);
        usernameField.setEditable(false);
        JPasswordField passwordField = new JPasswordField(currentPassword);
        JCheckBox changeUsernameCheckbox = new JCheckBox("Change Username?");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"admin", "scheduler", "manager"});
        roleComboBox.setSelectedItem(currentRole);

        JComponent[] components = {usernameField, passwordField, changeUsernameCheckbox, roleComboBox};

        // Set up ItemListener to toggle usernameField's editability
        changeUsernameCheckbox.addItemListener(e1 -> {
            if (changeUsernameCheckbox.isSelected()) {
                usernameField.setEditable(true);
            } else {
                usernameField.setEditable(false);
                usernameField.setText(currentUsername);
            }
        });

        createDialog("Edit User", 400, 350, labels, components, dialog -> {
            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());
            String newRole = (String) roleComboBox.getSelectedItem();

            boolean success = userManagement.editUser(currentUsername, newUsername, newPassword, newRole, changeUsernameCheckbox.isSelected());
            onUserEdited.accept(success);
            if (success) {
                dialog.dispose();
            }
        });
    }

    // Method that creates delete user dialog
    public void openDeleteUserDialog(String username, Consumer<Boolean> onUserDeleted) {
        int option = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            boolean success = userManagement.deleteUser(username);
            onUserDeleted.accept(success);
        }
    }

    // Method that creates block user dialog
    public void openBlockUserDialog(String username, boolean isBlocked, Consumer<Boolean> onUserBlocked) {
        String[] labels = {"Username:", "Block User?"};
        JTextField usernameField = new JTextField(username);
        usernameField.setEditable(false);
        JCheckBox blockCheckbox = new JCheckBox("Block User", isBlocked);

        JComponent[] components = {usernameField, blockCheckbox};

        createDialog("Block/Unblock User", 400, 250, labels, components, dialog -> {
            boolean block = blockCheckbox.isSelected();
            boolean success = userManagement.blockUser(username, block);
            onUserBlocked.accept(success);
            dialog.dispose();
        });
    }

    // Method that creates edit profile dialog
    public void openEditProfileDialog(String currentAdmin, Consumer<Boolean> onProfileEdited) {
        String[] labels = {"Current Password:", "New Password:", "Confirm Password:"};
        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JComponent[] components = {currentPasswordField, newPasswordField, confirmPasswordField};

        createDialog("Edit Profile", 400, 300, labels, components, dialog -> {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            boolean success = userManagement.editUser(currentAdmin, currentPassword, newPassword, confirmPassword);
            onProfileEdited.accept(success);
            if (success) {
                dialog.dispose();
            }
        });
    }

    // Private Methods
    // Creates the base dialog frame
    private void createDialog(String title, int width, int height, String[] labels, JComponent[] components, Consumer<JDialog> onApply) {
        JDialog dialog = new JDialog((JFrame) null, title, true);
        dialog.setSize(width, height);
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(null);

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setBounds(50, 30 + i * 50, 150, 30);
            dialog.add(label);

            JComponent component = components[i];
            component.setBounds(200, 30 + i * 50, 150, 30);
            dialog.add(component);
        }

        JButton applyButton = new JButton("Apply");
        applyButton.setBounds(150, 50 + labels.length * 50, 100, 30);
        dialog.add(applyButton);

        applyButton.addActionListener(e -> onApply.accept(dialog));

        dialog.setVisible(true);
    }
}


