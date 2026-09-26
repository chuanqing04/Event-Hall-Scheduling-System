package ui;

import com.toedter.calendar.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import objcls.FileHandler;



public class Manager extends JFrame implements ActionListener{
    private String currentManager;
    private FileHandler fileHandler,fileHandler1,fileHandler2; 
    private final JTabbedPane tabbedPane;
    private JRadioButton weeklyButton, monthlyButton, yearlyButton;
    private JDateChooser startDateChooser, endDateChooser;
    private JComboBox<String> hallTypeComboBox;
    private JTable salesTable;
    private JLabel totalSalesLabel;
    private JTable issuesTable;
    private JButton assignStaffButton, changeStatusButton;
    private JButton respondButton;

    // Getter for fileHandler
    public FileHandler getFileHandler() {
        return fileHandler;
    }
    
    // Setter for fileHandler
    public void setFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

     // Getter for currentManager
     public String getCurrentManager() {
        return currentManager;
    }
    
    // Setter for currentManager
    public void setCurrentManager(String currentManager) {
        this.currentManager = currentManager;
    }
    
    // Constructor
    public Manager(String managerUsername){
        super("Manager Menu");
        fileHandler = new FileHandler("bookings.txt");
        fileHandler1 = new FileHandler("maintenance.txt");
        fileHandler2 = new FileHandler("Login.txt");
        this.currentManager = managerUsername;
    
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setLayout(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 1000, 700);
        tabbedPane.addTab("Sales Dashboard", createSalesDashboardPanel());
        tabbedPane.addTab("Maintenance Operation", createMaintenanceOperationPanel());
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

    // Sales Dashboard
    private JPanel createSalesDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // View Sales panel
        JPanel viewSalesPanel = new JPanel(new GridBagLayout());
        viewSalesPanel.setBorder(BorderFactory.createTitledBorder("View Sales"));
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
    
        // Time Frame selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        viewSalesPanel.add(new JLabel("Time Frame:"), gbc);
    
        gbc.gridx = 1;
        weeklyButton = new JRadioButton("Weekly");
        viewSalesPanel.add(weeklyButton, gbc);
    
        gbc.gridx = 2;
        monthlyButton = new JRadioButton("Monthly");
        viewSalesPanel.add(monthlyButton, gbc);
    
        gbc.gridx = 3;
        yearlyButton = new JRadioButton("Yearly");
        viewSalesPanel.add(yearlyButton, gbc);
    
        ButtonGroup timeFrameGroup = new ButtonGroup();
        timeFrameGroup.add(weeklyButton);
        timeFrameGroup.add(monthlyButton);
        timeFrameGroup.add(yearlyButton);
    
        // Total Sales label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4; // Span across columns
        totalSalesLabel = new JLabel("Total Sales: RM 0.00");
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font style
        totalSalesLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the label
        viewSalesPanel.add(totalSalesLabel, gbc);
    
        mainPanel.add(viewSalesPanel, BorderLayout.NORTH);
    
        // Filter Sales panel
        JPanel filterSalesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterSalesPanel.setBorder(BorderFactory.createTitledBorder("Filter Sales"));
    
        filterSalesPanel.add(new JLabel("Start Date:"));
        
        startDateChooser = new JDateChooser(); // Ensure this is imported correctly
        startDateChooser.setPreferredSize(new Dimension(150, 30)); // Set preferred size for better visibility
        filterSalesPanel.add(startDateChooser);
    
        filterSalesPanel.add(new JLabel("End Date:"));
        
        endDateChooser = new JDateChooser(); // Ensure this is imported correctly
        endDateChooser.setPreferredSize(new Dimension(150, 30)); // Set preferred size for better visibility
        filterSalesPanel.add(endDateChooser);
    
        filterSalesPanel.add(new JLabel("Hall Type:"));
        
        String[] hallTypes = {"All", "Banquet Hall", "Auditorium", "Meeting Room"};
        
        hallTypeComboBox = new JComboBox<>(hallTypes);
        
        filterSalesPanel.add(hallTypeComboBox);
    
        JButton filterSalesButton = new JButton("Filter Sales");
        
        filterSalesButton.addActionListener(e -> {
            System.out.println("Filter Sales button clicked."); // Debugging line
            filterSales();
            System.out.println("Filter sales executed."); // Debugging line
        });
        
        filterSalesPanel.add(filterSalesButton);
    
        mainPanel.add(filterSalesPanel, BorderLayout.CENTER);
    
        // Sales table panel
        JPanel salesTablePanel = new JPanel(new BorderLayout());
        
       String[] columnNames = {"Sale Date", "Hall Type", "Sale Amount"}; 
       DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
       
       salesTable = new JTable(tableModel);
       salesTable.setDefaultEditor(Object.class, null); // Set table to read-only
       salesTable.setRowHeight(30); // Set row height for better visibility
       
       JScrollPane scrollPane = new JScrollPane(salesTable);
       salesTablePanel.add(scrollPane, BorderLayout.CENTER);
       mainPanel.add(salesTablePanel, BorderLayout.SOUTH);
    
       // Add action listeners for radio buttons
       weeklyButton.addActionListener(e -> {
           System.out.println("Weekly button selected."); // Debugging line
           viewSales();
       });
       
       monthlyButton.addActionListener(e -> {
           System.out.println("Monthly button selected."); // Debugging line
           viewSales();
       });
       
       yearlyButton.addActionListener(e -> {
           System.out.println("Yearly button selected."); // Debugging line
           viewSales();
       });
    
       return mainPanel; 
    }

    private void viewSales() {
        String timeFrame = getSelectedTimeFrame(); // Get selected time frame
    
        // Load sales data from the file
        List<String[]> salesData = fileHandler.loadBookings(); // Assuming this method exists in FileHandler
    
        // Clear existing rows in the sales table
        DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
        model.setRowCount(0); // Clear existing rows
    
        // Create a map to sum payment amounts by booking date
        Map<String, Double> salesMap = new HashMap<>();
    
        // Process each sale entry
        for (String[] sale : salesData) {
            try {
                String date = sale[4]; // Assuming booking date is in the fifth column
                double paymentAmount = Double.parseDouble(sale[7]); // Assuming payment amount is in the eighth column
    
                // Check if the sale date matches the selected time frame
                if (isDateInTimeFrame(date, timeFrame)) {
                    salesMap.put(date, salesMap.getOrDefault(date, 0.0) + paymentAmount);
                }
            } catch (Exception e) {
                System.out.println("Error processing sale entry: " + Arrays.toString(sale));
                e.printStackTrace(); // Print stack trace for debugging
            }
        }
    
        // Populate a list to sort and display
        List<String[]> sortedSales = new ArrayList<>();
        for (Map.Entry<String, Double> entry : salesMap.entrySet()) {
            String saleDate = entry.getKey();
            double totalAmountForDate = entry.getValue();
            sortedSales.add(new String[]{saleDate, "All", String.format("%.2f", totalAmountForDate)}); 
        }
    
        // Sort the sales data in descending order based on sale date
        sortedSales.sort((a, b) -> {
            LocalDate date1 = LocalDate.parse(a[0], DateTimeFormatter.ISO_DATE);
            LocalDate date2 = LocalDate.parse(b[0], DateTimeFormatter.ISO_DATE);
            return date2.compareTo(date1);
        });
    
        // Add sorted sales data to the table model
        for (String[] sale : sortedSales) {
            model.addRow(sale);
        }
    
        // Update total sales label
        double totalSales = sortedSales.stream()
                                        .mapToDouble(s -> Double.parseDouble(s[2])) // Assuming amount is at index 2
                                        .sum();
        
        totalSalesLabel.setText("Total Sales: RM " + String.format("%.2f", totalSales));
    }
    
    private void filterSales() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        String hallType = (String) hallTypeComboBox.getSelectedItem();
    
        // Validate date selection
        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.");
            return;
        }
    
        // Convert dates to LocalDate
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    
        // Debugging: Print selected dates
        System.out.println("Start Date: " + startLocalDate);
        System.out.println("End Date: " + endLocalDate);
        
        // Load sales data from the file
        List<String[]> salesData = fileHandler.loadBookings(); // Assuming this method exists in FileHandler
    
        // Clear existing rows in the sales table
        DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
        model.setRowCount(0); // Clear existing rows
    
        // Create a map to sum payment amounts by booking date
        Map<String, Double> salesMap = new HashMap<>();
    
        // Process each sale entry
        for (String[] sale : salesData) {
            try {
                LocalDate saleDate = LocalDate.parse(sale[4], DateTimeFormatter.ISO_DATE); // Assuming booking date is in the fifth column
                double paymentAmount = Double.parseDouble(sale[7]); // Assuming payment amount is in the eighth column
    
                // Debugging: Print each sale entry being processed
                System.out.println("Processing Sale - Date: " + saleDate + ", Payment Amount: RM " + paymentAmount);
    
                // Check if the sale date is within the specified range and hall type matches
                boolean isWithinDateRange = (saleDate.isEqual(startLocalDate) || saleDate.isAfter(startLocalDate)) &&
                                             (saleDate.isEqual(endLocalDate) || saleDate.isBefore(endLocalDate));
                boolean isHallTypeMatch = hallType.equals("All") || sale[3].equalsIgnoreCase(hallType); // Assuming hall type is in the fourth column
    
                if (isWithinDateRange && isHallTypeMatch) {
                    salesMap.put(sale[4], salesMap.getOrDefault(sale[4], 0.0) + paymentAmount);
                    System.out.println("Added to Sales Map - Date: " + sale[4] + ", Total Amount: RM " + salesMap.get(sale[4]));
                }
            } catch (Exception e) {
                System.out.println("Error processing sale entry: " + Arrays.toString(sale));
                e.printStackTrace(); // Print stack trace for debugging
            }
        }
    
        // Populate the table model with summed sales amounts
        double totalSales = 0.0;
        List<String[]> sortedSales = new ArrayList<>();

        for (Map.Entry<String, Double> entry : salesMap.entrySet()) {
            String saleDate = entry.getKey();
            double totalAmountForDate = entry.getValue();
            sortedSales.add(new String[]{saleDate, hallType, String.format("%.2f", totalAmountForDate)});
            totalSales += totalAmountForDate;
        }

        // Sort the sales data in descending order based on sale date
        sortedSales.sort((a, b) -> {
            LocalDate date1 = LocalDate.parse(a[0], DateTimeFormatter.ISO_DATE);
            LocalDate date2 = LocalDate.parse(b[0], DateTimeFormatter.ISO_DATE);
            return date2.compareTo(date1);
        });

        // Add sorted sales data to the table model
        for (String[] sale : sortedSales) {
            model.addRow(sale);
        }

        // Update total sales label
        totalSalesLabel.setText("Total Sales: RM " + String.format("%.2f", totalSales));
        }

        // Method to get the selected time frame from radio buttons
        private String getSelectedTimeFrame() {
            if (weeklyButton.isSelected()) {
                return "Weekly";
            } else if (monthlyButton.isSelected()) {
                return "Monthly";
            } else if (yearlyButton.isSelected()) {
                return "Yearly";
            }
            return "Not selected"; 
        }

        // Method to check if a date is within the specified time frame
        private boolean isDateInTimeFrame(String date, String timeFrame) {
            LocalDate bookingDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            LocalDate currentDate = LocalDate.now();

            switch (timeFrame) {
                case "Weekly":
                    return !bookingDate.isBefore(currentDate.minusDays(7)) && !bookingDate.isAfter(currentDate);
                case "Monthly":
                    return !bookingDate.isBefore(currentDate.minusMonths(1)) && !bookingDate.isAfter(currentDate);
                case "Yearly":
                    return !bookingDate.isBefore(currentDate.minusYears(1)) && !bookingDate.isAfter(currentDate);
                default:
                    return false;
            }
        }

        private JPanel createMaintenanceOperationPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
            JLabel titleLabel = new JLabel("Maintenance Operations", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding around the title
            panel.add(titleLabel, BorderLayout.NORTH);
        
            // Define column names based on your requirements
            String[] columnNames = {
                "Issue ID", 
                "Customer Name", 
                "Description", 
                "Hall Name", 
                "Hall Type", 
                "Assigned Scheduler", 
                "Status", 
                "Response", 
                "Scheduler Remarks", 
                "Maintenance Start Date", 
                "Maintenance Start Time", 
                "Maintenance End Date", 
                "Maintenance End Time"
            };
        
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            issuesTable = new JTable(tableModel);
            issuesTable.setDefaultEditor(Object.class, null); // Make table read-only
            issuesTable.getTableHeader().setReorderingAllowed(false);
            
            // Set the table to not auto-resize columns to enable horizontal scrolling
            issuesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            // Set preferred widths for each column (optional)
            int[] columnWidths = {50, 150, 250, 150, 100, 150, 100, 200, 200, 150, 150, 150, 150};
            
            for (int i = 0; i < columnWidths.length; i++) {
                TableColumn column = issuesTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidths[i]);
            }
        
            // Enable row selection
            issuesTable.setRowSelectionAllowed(true);
            
            // Create a JScrollPane and add the table to it
            JScrollPane tableScrollPane = new JScrollPane(issuesTable,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            
            panel.add(tableScrollPane, BorderLayout.CENTER);
        
            loadMaintenanceIssues(tableModel); // Load issues from file
        
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
            // Assign Staff Button
            assignStaffButton = new JButton("Assign Staff");
            assignStaffButton.setEnabled(false); // Initially disabled
            assignStaffButton.addActionListener(e -> assignStaff());
            actionPanel.add(assignStaffButton);
        
            // Change Status Button
            changeStatusButton = new JButton("Change Status");
            changeStatusButton.setEnabled(false); // Initially disabled
            changeStatusButton.addActionListener(e -> changeStatus());
            actionPanel.add(changeStatusButton);
        
            // Respond Button
            respondButton = new JButton("Respond to Issue");
            respondButton.setEnabled(false); // Initially disabled
            respondButton.addActionListener(e -> respondToIssue());
            actionPanel.add(respondButton);
        
            // Add a listener to enable/disable buttons based on selection
            issuesTable.getSelectionModel().addListSelectionListener(e -> {
                boolean rowSelected = issuesTable.getSelectedRow() != -1; // Check if a row is selected
                assignStaffButton.setEnabled(rowSelected);
                changeStatusButton.setEnabled(rowSelected);
                respondButton.setEnabled(rowSelected);
            });
        
            panel.add(actionPanel, BorderLayout.SOUTH);
        
            return panel;
        }
        
        // Function to assign staff to an issue
        private void assignStaff() {
            int selectedRow = issuesTable.getSelectedRow();
            
            if (selectedRow != -1) {
                String[] staffMembers = {"Select Staff", "Staff A", "Staff B", "Staff C"};
                String selectedStaff = (String) JOptionPane.showInputDialog(
                        null,
                        "Select staff member to assign:",
                        "Assign Staff",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        staffMembers,
                        staffMembers[0]);
                
                if (selectedStaff != null && !selectedStaff.equals("Select Staff")) {
                    issuesTable.setValueAt(selectedStaff, selectedRow, 5); // Update assigned staff in the table
                    updateMaintenanceIssue(selectedRow); // Update the file with changes
                    JOptionPane.showMessageDialog(null, "Staff member assigned: " + selectedStaff);
                }
                
                issuesTable.clearSelection(); // Ensure selection is cleared
            }
        }
        
        // Function to change the status of an issue
        private void changeStatus() {
            int selectedRow = issuesTable.getSelectedRow();
            
            if (selectedRow != -1) {
                String currentStatus = (String) issuesTable.getValueAt(selectedRow, 6);
                
                String newStatus = (String) JOptionPane.showInputDialog(
                        null,
                        "Select new status:",
                        "Change Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"In Progress", "Done", "Closed", "Cancelled"},
                        currentStatus);
                
                if (newStatus != null) {
                    issuesTable.setValueAt(newStatus, selectedRow, 6); // Update status in the table
                    updateMaintenanceIssue(selectedRow); // Update the file with changes
                    JOptionPane.showMessageDialog(null, "Status updated to: " + newStatus);
                }
                
                issuesTable.clearSelection(); // Ensure selection is cleared
            }
        }
        
        // Function to respond to an issue
        private void respondToIssue() {
            int selectedRow = issuesTable.getSelectedRow();
            
            if (selectedRow != -1) {
                String response = JOptionPane.showInputDialog(
                        null,
                        "Respond to the issue:",
                        "Respond to Issue",
                        JOptionPane.PLAIN_MESSAGE);
                
                if (response != null && !response.trim().isEmpty()) {
                    issuesTable.setValueAt(response, selectedRow, 7); // Update response in the table
                    updateMaintenanceIssue(selectedRow); // Update the file with changes
                    JOptionPane.showMessageDialog(null, "Response recorded: " + response);
                } else if (response == null || response.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Response cannot be empty.");
                }
                
                issuesTable.clearSelection(); // Ensure selection is cleared
            }
        }

        private void loadMaintenanceIssues(DefaultTableModel tableModel) {
            try {
                // Get the file path where the maintenance log is stored
                String filePath = "maintenance.txt";
                
                // Open the file for reading
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    // Clear any existing data in the table model
                    tableModel.setRowCount(0);
                    
                    String line;
                    
                    // Read each line from the file
                    while ((line = reader.readLine()) != null) {
                        // Split the line into columns
                        String[] columns = line.split(",");
                        
                        // Add the row data to the table model directly
                        tableModel.addRow(columns);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading maintenance issues: " + e.getMessage());
            }
        }
        
        private void updateMaintenanceIssue(int rowIndex) {
            try {
                // Get the file path where you want to save the maintenance log
                String filePath = "maintenance.txt";
                
                // Create a temporary file to write the updated data
                File tempFile = new File("temp_maintenance.txt");
                
                // Open the original file for reading
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                     
                    String line;
                    int currentRow = 0;
                    
                    // Read each line from the original file
                    while ((line = reader.readLine()) != null) {
                        // Split the line into columns
                        String[] columns = line.split(",");
                        
                        // If the current row matches the selected row index
                        if (currentRow == rowIndex) {
                            // Update the values in the columns array based on the changes made in the JTable
                            columns[5] = (String) issuesTable.getValueAt(rowIndex, 5); // Assigned Staff
                            columns[6] = (String) issuesTable.getValueAt(rowIndex, 6); // Status
                            columns[7] = (String) issuesTable.getValueAt(rowIndex, 7); // Response
                            
                            // Join the updated columns back into a line
                            line = String.join(",", columns);
                        }
                        
                        // Write the line (original or updated) to the temporary file
                        writer.write(line);
                        writer.newLine();
                        
                        currentRow++;
                    }
                }
                
                // Delete the original file and rename the temporary file to original file name
                File originalFile = new File(filePath);
                if (originalFile.delete()) {
                    tempFile.renameTo(originalFile);
                    JOptionPane.showMessageDialog(null, "Maintenance issue updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete original maintenance log.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating maintenance issue: " + e.getMessage());
            }
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
        DialogConstructor dialogConstructor = new DialogConstructor(fileHandler2, currentManager);
    
        dialogConstructor.openEditProfileDialog(currentManager, success -> {}); //Do nothing if success
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            Login login = new Login();  // Return to login screen or perform other logout actions
            login.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Not implemented yet");// Handle action events for buttons
    }
 
    //For Testing purposes only
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Manager("manager"));
    }

    public FileHandler getFileHandler1() {
        return fileHandler1;
    }

    public void setFileHandler1(FileHandler fileHandler1) {
        this.fileHandler1 = fileHandler1;
    }

    public FileHandler getFileHandler2() {
        return fileHandler2;
    }

    public void setFileHandler2(FileHandler fileHandler2) {
        this.fileHandler2 = fileHandler2;
    }

}
