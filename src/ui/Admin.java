package ui;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import objcls.BookingManagement;
import objcls.FileHandler;
import objcls.UserManagement;

public class Admin extends JFrame implements ActionListener {
    private final JTabbedPane tabbedPane;
    private FileHandler fileHandler, fileHandler1;
    private String currentAdmin;
    private UserManagement userManagement;
    private BookingManagement bookingManagement;
    private JTable userTable;
    private DefaultTableModel tableModel, bookingTableModel;

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
    
    // Getter for userManagement
    public UserManagement getUserManagement() {
        return userManagement;
    }
    
    // Setter for userManagement
    public void setUserManagement(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public FileHandler getFileHandler1() {
        return fileHandler1;
    }

    public void setFileHandler1(FileHandler fileHandler1) {
        this.fileHandler1 = fileHandler1;
    }

    public BookingManagement getBookingManagement() {
        return bookingManagement;
    }

    public void setBookingManagement(BookingManagement bookingManagement) {
        this.bookingManagement = bookingManagement;
    }

    // Constructor
    public Admin(String adminUsername) {
        super("Admin Menu");
        fileHandler = new FileHandler("Login.txt");
        fileHandler1 = new FileHandler("Bookings.txt");
        this.currentAdmin = adminUsername;
        this.userManagement = new UserManagement(fileHandler, currentAdmin);
        this.bookingManagement = new BookingManagement(fileHandler, currentAdmin);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 800, 600);
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Booking Management", createBookingManagementPanel());
        tabbedPane.addTab("Logout", createLogoutPanel());
        add(tabbedPane);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogout();
            }
        });
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
    
        // Search Field
        JTextField searchField = new JTextField();
        searchField.setBounds(50, 30, 400, 30);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ArrayList<String[]> searcheduser= userManagement.searchUsers(searchField.getText());
                updateTable(tableModel, searcheduser);
            }
        });
        panel.add(searchField);
    
        // Filter Users Button
        JButton filterButton = new JButton("Filter Users");
        filterButton.setBounds(470, 30, 150, 30);
        filterButton.addActionListener(e -> openFilterDialog(e, true));
        panel.add(filterButton);
    
        // Initialize table model and JTable
        String[] columnNames = {"Username", "Password", "Role", "Blocked?"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        userTable.setDefaultEditor(Object.class, null);
        userTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBounds(50, 70, 650, 300);
        panel.add(scrollPane);
    
        // Add User Button
        JButton addUserButton = new JButton("Add User");
        addUserButton.setBounds(50, 380, 150, 30);
        addUserButton.addActionListener(this::openAddUserDialog);
        panel.add(addUserButton);
    
        // Edit User Button
        JButton editUserButton = new JButton("Edit User");
        editUserButton.setEnabled(false);
        editUserButton.setBounds(210, 380, 150, 30);
        editUserButton.addActionListener(this::openEditUserDialog);
        panel.add(editUserButton);
    
        // Delete User Button
        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.setEnabled(false);
        deleteUserButton.setBounds(370, 380, 150, 30);
        deleteUserButton.addActionListener(this::openDeleteUserDialog);
        panel.add(deleteUserButton);
    
        // Block/Unblock User Button
        JButton blockUnblockButton = new JButton("Block/Unblock User");
        blockUnblockButton.setEnabled(false);
        blockUnblockButton.setBounds(530, 380, 150, 30);
        blockUnblockButton.addActionListener(this::openBlockUserDialog);
        panel.add(blockUnblockButton);

        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean isSelected = userTable.getSelectedRow() != -1;
            editUserButton.setEnabled(isSelected);
            deleteUserButton.setEnabled(isSelected);
            blockUnblockButton.setEnabled(isSelected);
        });
    
        // Update table initially
        updateTable(tableModel, fileHandler.getDataMasked());// Load all users initially

        return panel;
    }

        

    
    private void openAddUserDialog(ActionEvent e) {
        DialogConstructor dialogConstructor = new DialogConstructor(fileHandler, currentAdmin);
    
        dialogConstructor.openAddUserDialog(success -> {
            if (success) {
                updateTable(tableModel, fileHandler.getDataMasked());
            }
        });
    }
    
    private void openEditUserDialog(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String currentUsername = (String) userTable.getValueAt(selectedRow, 0);
            String currentPassword = (String) userTable.getValueAt(selectedRow, 1);
            String currentRole = (String) userTable.getValueAt(selectedRow, 2);
    
            DialogConstructor dialogConstructor = new DialogConstructor(fileHandler, currentAdmin);
    
            dialogConstructor.openEditUserDialog(currentUsername, currentPassword, currentRole, success -> {
                if (success) {
                    updateTable(tableModel, fileHandler.getDataMasked());
                }
            });
        }
    }
    
    private void openDeleteUserDialog(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
    
            DialogConstructor dialogConstructor = new DialogConstructor(fileHandler, currentAdmin);
    
            dialogConstructor.openDeleteUserDialog(username, success -> {
                if (success) {
                    updateTable(tableModel,fileHandler.getDataMasked());
                }
            });
        }
    }
    
    private void openBlockUserDialog(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
            boolean isBlocked = "TRUE".equals(userTable.getValueAt(selectedRow, 3));
    
            DialogConstructor dialogConstructor = new DialogConstructor(fileHandler, currentAdmin);
    
            dialogConstructor.openBlockUserDialog(username, isBlocked, success -> {
                if (success) {
                    updateTable(tableModel,fileHandler.getDataMasked());
                }
            });
        }
    }
    
    private JPanel createBookingManagementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
    
        // Filter Bookings Button
        JButton filterButton = new JButton("Filter Bookings");
        filterButton.setBounds(470, 30, 150, 30);
        filterButton.addActionListener(e -> openFilterDialog(e, false));
        panel.add(filterButton);
    
        // Initialize table model and JTable
        String[] columnNames = {"Customer name", "Booking ID", "Hall Name", "Hall Type","Booking Date", "Start Time","Period per hour","Booking Amount"};
        bookingTableModel = new DefaultTableModel(columnNames, 0);
        JTable bookingTable = new JTable(bookingTableModel);
        bookingTable.setDefaultEditor(Object.class, null);
        bookingTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBounds(50, 70, 650, 300);
        panel.add(scrollPane);

        // Search Field
        JTextField searchField = new JTextField();
        searchField.setBounds(50, 30, 400, 30);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ArrayList<String[]> searchedBookings = bookingManagement.searchBookings(searchField.getText());
                updateTable(bookingTableModel,searchedBookings);
            }
        });
        panel.add(searchField);
    
        // Update table initially
        updateTable(bookingTableModel, fileHandler1.getData()); // Load all bookings initially
    
        return panel;
    }

    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.addActionListener(this::openEditProfileDialog);
        panel.add(editProfileButton).setBounds(50,30,150,30);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 70, 150, 30);
        logoutButton.addActionListener(e -> handleLogout());
        panel.add(logoutButton);

        return panel;
    }

    private void openEditProfileDialog(ActionEvent e) {
        DialogConstructor dialogConstructor = new DialogConstructor(fileHandler, currentAdmin);
    
        dialogConstructor.openEditProfileDialog(currentAdmin, success -> {
            if (success) {
                updateTable(tableModel, fileHandler.getDataMasked());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.");
            }
        });
    }
    

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            Login login = new Login();  // Return to login screen or perform other logout actions
            login.setVisible(true);
        }
    }

    private void updateTable(DefaultTableModel tableModel, ArrayList<String[]> data) {
        tableModel.setRowCount(0); // Clear existing rows
        for (String[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void openFilterDialog(ActionEvent e, boolean users) {
        if (users){
            DialogConstructor dialogConstructor = new DialogConstructor(fileHandler, currentAdmin);
    
            // Call the openFilterDialog method with a Consumer
            dialogConstructor.openFilterDialog(filteredUsers -> {
                // Update the table with the filtered users
                updateTable(tableModel, filteredUsers);
            });
        }
        else{
            DialogConstructor dialogConstructor = new DialogConstructor(fileHandler1, currentAdmin);
    
            // Call the openBookingFilterDialog method with a Consumer
            dialogConstructor.openBookingFilterDialog(filteredBookings -> {
                // Update the booking table with the filtered bookings
                updateTable(bookingTableModel, filteredBookings);
            });
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Not implemented yet");// Handle action events for buttons
    }
 
    //For Testing purposes only
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Admin("admin"));
    }
}
