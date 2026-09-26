package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import objcls.FileHandler;
import objcls.HallManagement;

public class Scheduler extends JFrame {
    private JLabel welcomeLabel;
    private String username;
    private JTabbedPane tabbedPane;
    private JTextField hallDNameField, hallINameField, hallANameField, issueIDField;
    private JButton addHallButton, editHallButton, deleteHallButton, setAvailabilityButton, setMaintenanceButton;
    private FileHandler fileHandler1,fileHandler2;
    private JTable hallTable, infoTable, availabilityTable, maintenanceTable;
    private DefaultTableModel tableModelD, tableModelI, tableModelA, tableModelM;
    private TableRowSorter<DefaultTableModel> rowSorterD, rowSorterI, rowSorterA, rowSorterM;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Scheduler(String username) {
        super("Scheduler Main Menu");  // This must be the first statement
        this.username = username;  // Store the username
        
        this.fileHandler1 = new FileHandler("Hall.txt");
        this.fileHandler2 = new FileHandler("maintenance.txt");
        
        // Set up the frame
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Set layout as BorderLayout to place components
        setLayout(new BorderLayout());

        // Create welcome label with the username
        add(Box.createVerticalStrut(20));
        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, 48));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the label
        add(welcomeLabel, BorderLayout.NORTH);

        // Create a JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("View Hall Details", createHallDetailsPanel());
        tabbedPane.addTab("Hall Information Management", createInformationPanel());
        tabbedPane.addTab("Hall Availability Management", createAvailabilityPanel());
        tabbedPane.addTab("Hall Maintenance Management", createMaintenancePanel());
        tabbedPane.addTab("Logout", createLogoutPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Confirm before closing the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogout();
            }
        });
    }

    private JPanel createHallDetailsPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        // Create the table model and the table for Hall Name and Hall Size
        String[] columnNames = {"Hall Name", "Hall Type", "Hall Availability Start Date", "Hall Availability Start Time", "Hall Availability End Date", "Hall Availability End Time", "Availability Remarks"};
    
        // Initialize the table model with column names
        tableModelD = new DefaultTableModel(columnNames, 0);
        hallTable = new JTable(tableModelD);
        rowSorterD = new TableRowSorter<>(tableModelD);
        hallTable.setRowSorter(rowSorterD);
        hallTable.setDefaultEditor(Object.class, null); // Make table read-only
        hallTable.setRowHeight(30); // Set row height

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(hallTable);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate the table with data from Hall.txt
        loadHallTable();

        // Create input panel for filtering
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Hall Name Filtering:"));
        hallDNameField = new JTextField();
        inputPanel.add(hallDNameField);
        
        JPanel paddedInputPanel = new JPanel(new BorderLayout());
        paddedInputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Add padding
        paddedInputPanel.add(inputPanel, BorderLayout.CENTER);
        panel.add(paddedInputPanel, BorderLayout.NORTH);
        
        hallDNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterD();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterD();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterD();
            }
        });
        
        return panel;
    }
    
    private void filterD() {
        String text = hallDNameField.getText();
        if (text.trim().isEmpty()) {
            rowSorterD.setRowFilter(null);
        } else {
            rowSorterD.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
        }
    }

    private void loadHallTable() {
        List<String[]> hallData = fileHandler1.loadHallData();
    
        // Clear existing rows in the hall table
        tableModelD.setRowCount(0);

        // Add new rows from hallData
        for (String[] rowData : hallData) {
            tableModelD.addRow(rowData);
        }
    }

    // Placeholder methods for other panels
    private JPanel createInformationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create the table model and the table for Hall Name and Hall Size
        String[] columnNames = {"Hall Name", "Hall Type"};
    
        // Initialize the table model with column names
        tableModelI = new DefaultTableModel(columnNames, 0);
        infoTable = new JTable(tableModelI);
        rowSorterI = new TableRowSorter<>(tableModelI);
        infoTable.setRowSorter(rowSorterI);
        infoTable.setDefaultEditor(Object.class, null); // Make table read-only
        infoTable.setRowHeight(30); // Set row height

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(infoTable);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate the table with data from Hall.txt
        loadInformationTable();

        // Create input panel for filtering
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Hall Name Filtering:"));
        hallINameField = new JTextField();
        inputPanel.add(hallINameField);
        
        JPanel paddedInputPanel = new JPanel(new BorderLayout());
        paddedInputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Add padding
        paddedInputPanel.add(inputPanel, BorderLayout.CENTER);
        panel.add(paddedInputPanel, BorderLayout.NORTH);

        // Create button panel
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        addHallButton = new JButton("Add Hall");
        addHallButton.addActionListener(this::showAddHallDialog);
        editHallButton = new JButton("Edit Hall");
        editHallButton.addActionListener(this::showEditHallDialog);
        deleteHallButton = new JButton("Delete Hall");
        deleteHallButton.addActionListener(this::showDeleteHallDialog);

        editHallButton.setEnabled(false);
        deleteHallButton.setEnabled(false);

        buttonPanel.add(addHallButton);
        buttonPanel.add(editHallButton);
        buttonPanel.add(deleteHallButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        infoTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean rowSelected = infoTable.getSelectedRow() != -1;
                editHallButton.setEnabled(rowSelected);
                deleteHallButton.setEnabled(rowSelected);
            }
        });

        // Add button panel to the main panel
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        hallINameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterI();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterI();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterI();
            }
        });
        
        
        return panel;
    }
    
    // Print debug information
    private void filterI() {
        String text = hallINameField.getText();
        if (text.trim().isEmpty()) {
            rowSorterI.setRowFilter(null);
        } else {
            rowSorterI.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
        }
    }

    private void loadInformationTable() {
        List<String[]> hallData = fileHandler1.loadHallData();
    
        // Clear existing rows in the table
        tableModelI.setRowCount(0);

        // Add new rows from hallData
        for (String[] rowData : hallData) {
            // Only add Hall Name and Hall Size columns
            tableModelI.addRow(new Object[]{rowData[0], rowData[1]});
        }
    }
    
    private void showAddHallDialog(ActionEvent e) {
        HallManagement hallManagement = new HallManagement(infoTable, tableModelI);
        hallManagement.showAddHallDialog();
        refreshTable();
    }

    private void showEditHallDialog(ActionEvent e) {
        int selectedRow = infoTable.getSelectedRow();
        HallManagement hallManagement = new HallManagement(infoTable, tableModelI);
        hallManagement.setSelectedRow(selectedRow);
        hallManagement.showEditHallDialog();
        refreshTable();
    }

    private void showDeleteHallDialog(ActionEvent e) {
        int selectedRow = infoTable.getSelectedRow();
        HallManagement hallManagement = new HallManagement(infoTable, tableModelI);
        hallManagement.setSelectedRow(selectedRow);
        hallManagement.deleteHall();
        refreshTable();
    }
    

    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create the table model and the table for Hall Name and Hall Size
        String[] columnNames = {"Hall Name", "Availability Start Date", "Availabiltiy Start Time", "Availability End Date", "Availability End Time", "Availability Remarks"};
    
        // Initialize the table model with column names
        tableModelA = new DefaultTableModel(columnNames, 0);
        availabilityTable = new JTable(tableModelA);
        rowSorterA = new TableRowSorter<>(tableModelA);
        availabilityTable.setRowSorter(rowSorterA);
        availabilityTable.setDefaultEditor(Object.class, null); // Make table read-only
        availabilityTable.setRowHeight(30); // Set row height

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(availabilityTable);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate the table with data from Hall.txt
        loadAvailabilityTable();

        // Create input panel for filtering
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Hall Name Filtering:"));
        hallANameField = new JTextField();
        inputPanel.add(hallANameField);
        
        JPanel paddedInputPanel = new JPanel(new BorderLayout());
        paddedInputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Add padding
        paddedInputPanel.add(inputPanel, BorderLayout.CENTER);
        panel.add(paddedInputPanel, BorderLayout.NORTH);

        // Create button panel
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        setAvailabilityButton = new JButton("Set Availability");
        setAvailabilityButton.addActionListener(this::setAvailabilityDialog);

        setAvailabilityButton.setEnabled(false);

        buttonPanel.add(setAvailabilityButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        availabilityTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean rowSelected = availabilityTable.getSelectedRow() != -1;
                setAvailabilityButton.setEnabled(rowSelected);
            }
        });

        // Add button panel to the main panel
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        hallANameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterA();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterA();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterA();
            }
        });
        
        
        return panel; // Implement as needed
    }
    
    private void filterA() {
        String text = hallANameField.getText();
        if (text.trim().isEmpty()) {
            rowSorterA.setRowFilter(null);
        } else {
            rowSorterA.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
        }
    }
    
    private void setAvailabilityDialog(ActionEvent e) {
        int selectedRow = availabilityTable.getSelectedRow();
        HallManagement hallManagement = new HallManagement(availabilityTable, tableModelA);
        hallManagement.setSelectedRow(selectedRow);
        hallManagement.setAvailabilityDialog(this);
        refreshTable();
    }

    
    private void loadAvailabilityTable() {
        List<String[]> hallData = fileHandler1.loadHallData();
    
        // Clear existing rows in the table
        tableModelA.setRowCount(0);

        // Add new rows from hallData
        for (String[] rowData : hallData) {
            // Only add Hall Name and Hall Size columns
            tableModelA.addRow(new Object[]{rowData[0], rowData[2], rowData[3], rowData[4], rowData[5], rowData[6]});
        }
    }
    
    private JPanel createMaintenancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create the table model and the table for Hall Name and Hall Size
        String[] columnNames = {"Issue ID", "Description", "Hall Name", "Hall Type", "Scheduler Remarks", "Maintenance Start Date", "Maintenance Start Time", "Maintenance End Date", "Maintenance End Time"};
    
        // Initialize the table model with column names
        tableModelM = new DefaultTableModel(columnNames, 0);
        maintenanceTable = new JTable(tableModelM);
        rowSorterM = new TableRowSorter<>(tableModelM);
        maintenanceTable.setRowSorter(rowSorterM);
        maintenanceTable.setDefaultEditor(Object.class, null); // Make table read-only
        maintenanceTable.setRowHeight(30); // Set row height

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate the table with data from Hall.txt
        loadMaintenanceTable();

        // Create input panel for filtering
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Issue ID Filtering:"));
        issueIDField = new JTextField();
        inputPanel.add(issueIDField);
        
        JPanel paddedInputPanel = new JPanel(new BorderLayout());
        paddedInputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Add padding
        paddedInputPanel.add(inputPanel, BorderLayout.CENTER);
        panel.add(paddedInputPanel, BorderLayout.NORTH);

        // Create button panel
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        setMaintenanceButton = new JButton("Set Maintenance");
        setMaintenanceButton.addActionListener(this::setMaintenanceDialog);

        setMaintenanceButton.setEnabled(false);

        buttonPanel.add(setMaintenanceButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        maintenanceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean rowSelected = maintenanceTable.getSelectedRow() != -1;
                setMaintenanceButton.setEnabled(rowSelected);
            }
        });

        // Add button panel to the main panel
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        issueIDField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterM();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterM();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterM();
            }
        });
        
        
        return panel; // Implement as needed
    }
    
    private void filterM() {
        String text = issueIDField.getText();
        if (text.trim().isEmpty()) {
            rowSorterM.setRowFilter(null);
        } else {
            rowSorterM.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
        }
    }
    
    private void setMaintenanceDialog(ActionEvent e) {
        int selectedRow = maintenanceTable.getSelectedRow();
        HallManagement hallManagement = new HallManagement(maintenanceTable, tableModelM);
        hallManagement.setSelectedRow(selectedRow);
        hallManagement.setMaintenanceDialog(this);
        refreshTable();
    }

    
    private void loadMaintenanceTable() {
        List<String[]> hallData = fileHandler2.getData();
    
        // Clear existing rows in the table
        tableModelM.setRowCount(0);

        // Add new rows from hallData
        for (String[] rowData : hallData) {
            // Only add Hall Name and Hall Size columns
            tableModelM.addRow(new Object[]{rowData[0], rowData[2], rowData[3], rowData[4], rowData[8], rowData[9], rowData[10], rowData[11], rowData[12]});
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
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.dispose();
                Login login = new Login();  // Return to login screen or perform other logout actions
                login.setVisible(true);
            }
        });
        panel.add(logoutButton);

        return panel;
    }
    
    
    private void openEditProfileDialog(ActionEvent e) {
        FileHandler fileHandler1 = new FileHandler("Login.txt");
        DialogConstructor dialogConstructor = new DialogConstructor(fileHandler1, username);
    
        dialogConstructor.openEditProfileDialog(username, success -> {
            if (success) {

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
    
    private void refreshTable(){
        loadHallTable();
        loadInformationTable();
        loadAvailabilityTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Scheduler("scheduler"));
    }
}
