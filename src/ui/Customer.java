package ui;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import objcls.FileHandler;
import objcls.FileHandler2;
import objcls.UserManagement;

public class Customer extends JFrame {
    private UserManagement userManagement;
    public String username;
    private Map<String, Double> hallFees; // Store hall fees for each type of hall
    private double customerBalance = 1000.00; // Example initial balance
    private JComboBox<String> bookingComboBox; // Declare bookingComboBox
    private Map<String, String> bookingHallTypeMap = new HashMap<>();
    private FileHandler fileHandler;

    public String generateBookingID() {
    int randomNumber = new Random().nextInt(1000); // Random number between 0 and 999
    return "BOOK" + randomNumber;
    }

public Customer(String username) {
    this.username = username;
    loadBookingData("bookings.txt");
    this.fileHandler = new FileHandler("fileHandler2");
    userManagement = new UserManagement(fileHandler, username);
    loadHallFees(); // Load hall fees if necessary

    setTitle("Customer Dashboard");
    setSize(600, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Initialize hall fees (example data)
    hallFees = new HashMap<>();
    hallFees.put("Auditorium", 500.00);
    hallFees.put("Banquet Hall", 300.00);
    hallFees.put("Meeting Room", 150.00);

    // Initialize bookingComboBox
    bookingComboBox = new JComboBox<>(); // Ensure this is initialized

    // Create a tabbed pane and add panels
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Update Profile", createUpdateProfilePanel());
    tabbedPane.addTab("Make a Booking", createBookingPanel(bookingComboBox));
    tabbedPane.addTab("View Bookings", createViewBookingsPanel());
    tabbedPane.addTab("Cancel Booking", createCancelPanel(bookingComboBox));
    tabbedPane.addTab("Logout", createLogoutPanel());

    // Add the tabbed pane to the frame
    add(tabbedPane);
    displayUserBookings(); // Ensure this method is correctly implemented
    setVisible(true);
}    
    

    
   // Panel for updating profile
    private JPanel createUpdateProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Update Button
        JButton updateButton = new JButton("Update");
        updateButton.setBounds(150, 250, 100, 30);
        updateButton.addActionListener(this::openEditUserDialog);

        // Add components to the panel
        panel.add(updateButton);

        return panel;
    }

    private void openEditUserDialog(ActionEvent e){
        FileHandler fileHandler1 = new FileHandler("Login.txt");
        DialogConstructor dialogConstructor = new DialogConstructor(fileHandler1, username);

        dialogConstructor.openEditProfileDialog(username, success -> {}); //Do nothing if success
    }

    // Panel for making a booking
    private JPanel createBookingPanel(JComboBox<String> bookingComboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Hall selection
        JLabel hallLabel = new JLabel("Select Hall:");
        hallLabel.setBounds(50, 50, 100, 30);
        JComboBox<String> hallComboBox = new JComboBox<>(hallFees.keySet().toArray(new String[0]));
        hallComboBox.setBounds(150, 50, 150, 30);

        // Hall fee display
        JLabel hallfeeLabel = new JLabel("Hall Fee: ");
        hallfeeLabel.setBounds(50, 100, 100, 30);
        JLabel feeAmountLabel = new JLabel("0.00");
        feeAmountLabel.setBounds(150, 100, 100, 30);
        panel.add(feeAmountLabel);

        // Date selection
        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setBounds(50, 150, 100, 30);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(150, 150, 150, 30);

        // Time selection
        JLabel timeLabel = new JLabel("Select Time:");
        timeLabel.setBounds(50, 200, 100, 30);
        String[] times = {"09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"};
        JComboBox<String> timeComboBox = new JComboBox<>(times);
        timeComboBox.setBounds(150, 200, 150, 30);

        // Proceed to Payment Button
        JButton paymentButton = new JButton("Proceed to Payment");
        paymentButton.setBounds(100, 250, 200, 30);
        paymentButton.addActionListener(e -> {
            String selectedHall = (String) hallComboBox.getSelectedItem();
            Date selectedDate = (Date) dateSpinner.getValue();
            String selectedTime = (String) timeComboBox.getSelectedItem();

            // Proceed to payment logic
            proceedToPayment(selectedHall, selectedDate, selectedTime, bookingComboBox);
        });

        hallComboBox.addActionListener(e -> {
            String selectedHall = (String) hallComboBox.getSelectedItem();
            double hallFee = hallFees.get(selectedHall);
            feeAmountLabel.setText(String.format("%.2f", hallFee));
        });

        panel.add(hallLabel);
        panel.add(hallComboBox);
        panel.add(hallfeeLabel);
        panel.add(dateLabel);
        panel.add(dateSpinner);
        panel.add(timeLabel);
        panel.add(timeComboBox);
        panel.add(paymentButton);

        return panel;
    }

        // Modify the `proceedToPayment` method to accept date and time
    private void proceedToPayment(String selectedHall, Date selectedDate, String selectedTime, JComboBox<String> bookingComboBox) {
        String bookingId = generateBookingID();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(selectedDate);

        JFrame paymentFrame = new JFrame("Proceed to Payment");
        paymentFrame.setSize(400, 400);
        paymentFrame.setLayout(null);
        paymentFrame.setLocationRelativeTo(null);

        JLabel selectHallLabel = new JLabel("Selected Hall: " + selectedHall);
        selectHallLabel.setBounds(50, 30, 300, 30);
        paymentFrame.add(selectHallLabel);

        JLabel dateTimeLabel = new JLabel("Booking Date & Time: " + formattedDate + " " + selectedTime);
        dateTimeLabel.setBounds(50, 80, 300, 30);
        paymentFrame.add(dateTimeLabel);

        JLabel hallFeeLabel = new JLabel("Hall Fee:");
        hallFeeLabel.setBounds(50, 130, 100, 30);
        paymentFrame.add(hallFeeLabel);

        JLabel feeAmountLabel = new JLabel(String.format("%.2f", hallFees.get(selectedHall)));
        feeAmountLabel.setBounds(150, 130, 100, 30);
        paymentFrame.add(feeAmountLabel);

        JLabel balanceLabel = new JLabel("Your Balance:");
        balanceLabel.setBounds(50, 180, 100, 30);
        paymentFrame.add(balanceLabel);

        JLabel balanceAmountLabel = new JLabel(String.format("%.2f", customerBalance));
        balanceAmountLabel.setBounds(150, 180, 100, 30);
        paymentFrame.add(balanceAmountLabel);

        JButton confirmPaymentButton = new JButton("Confirm Payment");
        confirmPaymentButton.setBounds(120, 230, 150, 30);
        paymentFrame.add(confirmPaymentButton);

        confirmPaymentButton.addActionListener(e -> {
            double hallFee = hallFees.get(selectedHall);

            if (customerBalance >= hallFee) {
                customerBalance -= hallFee; // Deduct hall fee from customer's balance
                balanceAmountLabel.setText(String.format("%.2f", customerBalance));
                displayReceipt(selectedHall, hallFee, bookingId, formattedDate, selectedTime);

                // Save the booking to the file
                saveBooking(bookingId, selectedHall, formattedDate, selectedTime, hallFee);
                populateBookingComboBox(bookingComboBox, username); // Refresh the combo box
                reloadBookings();
                paymentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(paymentFrame, "Insufficient balance. Please add funds.");
            }
        });

    paymentFrame.setVisible(true);
}

    // Update displayReceipt method to show date and time
    private void displayReceipt(String hall, double hallFee, String bookingId, String date, String time) {
        JFrame receiptFrame = new JFrame("Receipt");
        receiptFrame.setSize(400, 400);
        receiptFrame.setLayout(null);
        receiptFrame.setLocationRelativeTo(null);

        JLabel successLabel = new JLabel("Payment Successful!");
        successLabel.setBounds(50, 20, 300, 30);
        successLabel.setFont(successLabel.getFont().deriveFont(16f).deriveFont(java.awt.Font.BOLD));

        JLabel hallLabel = new JLabel("Hall Booked: " + hall);
        hallLabel.setBounds(50, 50, 300, 30);

        JLabel bookingIdLabel = new JLabel("Booking ID: " + bookingId);
        bookingIdLabel.setBounds(50, 80, 300, 30);

        JLabel dateTimeLabel = new JLabel("Booking Date & Time: " + date + " " + time);
        dateTimeLabel.setBounds(50, 110, 300, 30);

        JLabel feeLabel = new JLabel("Amount Paid: " + String.format("%.2f", hallFee));
        feeLabel.setBounds(50, 140, 300, 30);

        JLabel balanceLabel = new JLabel("Remaining Balance: " + String.format("%.2f", customerBalance));
        balanceLabel.setBounds(50, 170, 300, 30);

        JButton closeButton = new JButton("Close");
        closeButton.setBounds(150, 250, 100, 30);
        closeButton.addActionListener(e -> receiptFrame.dispose());

        receiptFrame.add(successLabel);
        receiptFrame.add(hallLabel);
        receiptFrame.add(bookingIdLabel);
        receiptFrame.add(dateTimeLabel);
        receiptFrame.add(feeLabel);
        receiptFrame.add(balanceLabel);
        receiptFrame.add(closeButton);

        receiptFrame.setVisible(true);
    }

        // Panel for viewing bookings
        private JPanel createViewBookingsPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(null);

            // Label to indicate booking filter
            JLabel filterLabel = new JLabel("Filter by:");
            filterLabel.setBounds(50, 20, 80, 30);
            panel.add(filterLabel);

            // ComboBox for filtering options (Upcoming or Past)
            JComboBox<String> filterComboBox = new JComboBox<>(new String[]{"Upcoming", "Past"});
            filterComboBox.setBounds(130, 20, 150, 30);
            panel.add(filterComboBox);

            // Table to display bookings
            String[] columnNames = {"Booking ID", "Hall", "Date", "Time", "Hall Fee"};
            JTable bookingsTable = new JTable(new Object[][]{}, columnNames); // Initialize with empty data
            JScrollPane scrollPane = new JScrollPane(bookingsTable);
            scrollPane.setBounds(50, 70, 500, 250);
            panel.add(scrollPane);

            // Button to apply filter
            JButton applyFilterButton = new JButton("Apply Filter");
            applyFilterButton.setBounds(300, 20, 120, 30);
            panel.add(applyFilterButton);

            // Action listener for the filter button
            applyFilterButton.addActionListener(e -> {
                String filter = (String) filterComboBox.getSelectedItem();
                FileHandler2 fileHandler = new FileHandler2("bookings.txt");
                String username = fileHandler.getLoggedInUsername(); // Fetch the logged-in username

                if (username != null) {
                    List<String[]> bookings = getBookingsFromFile(); // Fetch bookings for the user
                    if (!bookings.isEmpty()) {
                        String[][] tableData = filterBookingsToTableData(bookings, filter); // Filter based on "Upcoming" or "Past"

                        // Update the JTable with the filtered data
                        bookingsTable.setModel(new DefaultTableModel(tableData, columnNames));
                        bookingsTable.repaint(); // Repaint to ensure the table updates
                    } else {
                        JOptionPane.showMessageDialog(panel, "No bookings found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "No user is logged in.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            return panel;
        }
        
    private String[][] filterBookingsToTableData(List<String[]> bookings, String filter) {
        List<String[]> filteredList = new ArrayList<>();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (String[] booking : bookings) {
            try {
                // Parse the booking date
                Date bookingDate = dateFormat.parse(booking[3]); // Date is in the 4th field

                if ((filter.equals("Upcoming") && bookingDate.after(currentDate)) ||
                    (filter.equals("Past") && bookingDate.before(currentDate))) {

                    // Add the booking data (Booking ID, Hall, Date, Time, Hall Fee) to the filtered list
                    filteredList.add(new String[]{booking[1], booking[2], booking[3], booking[4], booking[5]});
                }
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parsing errors
            }
        }

        // Convert the filtered list to a 2D array
        return filteredList.toArray(new String[0][0]);
    }


        // Fetch bookings from the file in a comma-separated format
    private List<String[]> getBookingsFromFile() {
        FileHandler2 fileHandler = new FileHandler2("bookings.txt");
        ArrayList<String[]> bookings = new ArrayList<>();

        // Load raw booking data from the file
        List<String> lines = fileHandler.loadBookingsRaw(); // Assuming this returns lines from the file

        // Parse each line into a booking array
        for (String line : lines) {
            String[] bookingDetails = line.split(","); // Split by comma
            if (bookingDetails.length == 6) { // Ensure it has all the necessary details
                if (username.equals(bookingDetails[0])) { // Check if the booking belongs to the logged-in user
                    bookings.add(bookingDetails); // Add to the list of user bookings
                }
            }
        }

        return bookings;
    }


    // Method to save booking details into a text file in a comma-separated format
    public void saveBooking(String bookingID, String hallType, String date, String time, double hallFee) {
        if (username != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("bookings.txt", true))) {
                // Write booking details in a comma-separated format
                writer.write(username + "," + bookingID + "," + hallType + "," + date + "," + time + "," + hallFee);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No user is logged in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
        private void displayUserBookings() {
        // Fetch user-specific bookings using userManagement
        ArrayList<String[]> userBookings = userManagement.getBookingsByUsername(username);
        
        // Example code to display bookings in the UI (adjust based on your actual GUI code)
        for (String[] booking : userBookings) {
            System.out.println(Arrays.toString(booking)); // Replace with actual UI code
        }
    }

    
    // Step 1: Create Cancel Booking Panel
    private JPanel createCancelPanel(JComboBox<String> bookingComboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Booking ID input or dropdown to select specific booking
        JLabel bookingLabel = new JLabel("Select Booking to Cancel:");
        bookingLabel.setBounds(50, 50, 200, 30);
        populateBookingComboBox(bookingComboBox, username);  // Call method to populate the combo box
        bookingComboBox.setBounds(220, 50, 150, 30);

        // Proceed to Cancel Button
        JButton cancelButton = new JButton("Proceed to Cancel");
        cancelButton.setBounds(100, 100, 200, 30);
        cancelButton.addActionListener(e -> {
            String selectedBooking = (String) bookingComboBox.getSelectedItem();
            proceedToCancel(selectedBooking,bookingComboBox);
        });

        panel.add(bookingLabel);
        panel.add(bookingComboBox);
        panel.add(cancelButton);

        return panel;
    }
    // Step 2: Proceed to Cancel Method
    private void proceedToCancel(String selectedBooking, JComboBox<String> bookingComboBox) {
        JFrame cancelFrame = new JFrame("Proceed to Cancel");
        cancelFrame.setSize(400, 300);
        cancelFrame.setLayout(null);
        cancelFrame.setLocationRelativeTo(null);

        // Confirmation message
        JLabel confirmationLabel = new JLabel("Are you sure you want to cancel Booking: " + selectedBooking + "?");
        confirmationLabel.setBounds(50, 50, 300, 30);
        cancelFrame.add(confirmationLabel);

        // Confirm and Cancel Buttons
        JButton confirmButton = new JButton("Confirm Cancellation");
        confirmButton.setBounds(50, 150, 150, 30);
        cancelFrame.add(confirmButton);

        JButton closeButton = new JButton("Close");
        closeButton.setBounds(220, 150, 100, 30);
        cancelFrame.add(closeButton);

        closeButton.addActionListener(e -> cancelFrame.dispose());

        confirmButton.addActionListener(e -> {
            refundHallFee(selectedBooking); // Refund the hall fee to customer
            cancelBooking(selectedBooking); // Call method to cancel booking

            // Re-populate the JComboBox with updated bookings
            populateBookingComboBox(bookingComboBox, username);

            cancelFrame.dispose(); // Close the confirmation dialog
        });

        cancelFrame.setVisible(true);
    }

    // Step 3: Refund Method and Panel
    private void refundHallFee(String selectedBooking) {
        // Assuming you fetch the hallFee and other details for the selected booking
        double hallFee = getHallFeeForBooking(selectedBooking);

        customerBalance += hallFee; // Refund the hall fee to the customer balance

        JFrame refundFrame = new JFrame("Refund");
        refundFrame.setSize(400, 300);
        refundFrame.setLayout(null);
        refundFrame.setLocationRelativeTo(null);

        JLabel refundLabel = new JLabel("Refund Successful! Hall Fee: " + String.format("%.2f", hallFee) + " has been refunded.");
        refundLabel.setBounds(50, 50, 300, 30);
        refundFrame.add(refundLabel);

        JLabel balanceLabel = new JLabel("Updated Balance: " + String.format("%.2f", customerBalance));
        balanceLabel.setBounds(50, 100, 300, 30);
        refundFrame.add(balanceLabel);

        JButton closeButton = new JButton("Close");
        closeButton.setBounds(150, 150, 100, 30);
        closeButton.addActionListener(e -> refundFrame.dispose());

        refundFrame.add(closeButton);
        refundFrame.setVisible(true);
    }

    // Method to cancel the booking (remove from file or database)
    private void cancelBooking(String bookingId) {
        // Logic to remove or update the booking entry in the system (e.g., remove from file or database)
        removeBookingFromFile(bookingId); // Assuming this method handles booking removal
    }
    
    private void removeBookingFromFile(String bookingId) {
        File inputFile = new File("bookings.txt");
        File tempFile = new File("bookings_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean bookingFound = false;

            // Read the input file line by line
            while ((line = reader.readLine()) != null) {
                String[] bookingDetails = line.split(",");

                // Check if the booking ID matches the one to be removed
                if (bookingDetails.length > 1 && bookingDetails[1].equals(bookingId)) {
                    bookingFound = true; // Mark that the booking ID was found
                    continue; // Skip writing this line to the temp file
                }

                // Write the line to the temp file if it doesn't match the booking ID
                writer.write(line);
                writer.newLine();
            }

            // Optionally, inform the user if the booking ID was not found
            if (!bookingFound) {
                System.out.println("Booking ID " + bookingId + " not found.");
            }

        } catch (IOException e) {
            System.out.println("Error processing the file.");
            e.printStackTrace();
        }

        // Delete the original file and rename the temp file to original
        if (!inputFile.delete()) {
            System.out.println("Could not delete the original file.");
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename the temporary file.");
        }
    }

private void loadBookingData(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String bookingId = parts[1].trim(); // Booking ID
                    String hallType = parts[2].trim(); // Hall Type
                    bookingHallTypeMap.put(bookingId, hallType);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHallFees() {
        // Ensure hallFees is initialized
        if (hallFees == null) {
            hallFees = new HashMap<>(); // Initialize if null
        }

        // Example of loading hall fees
        hallFees.put("Auditorium", 500.00);
        hallFees.put("Banquet Hall", 300.00);
        hallFees.put("Meeting Room", 150.00);

        // Add more hall fees as needed
        System.out.println("Hall fees loaded: " + hallFees);
    }

    private double getHallFeeForBooking(String bookingId) {
        // Check if bookingId is null or empty
        if (bookingId == null || bookingId.isEmpty()) {
            System.out.println("Booking ID is null or empty.");
            return 0.0;
        }

        // Fetch the hall type for the given booking ID
        String hallType = bookingHallTypeMap.get(bookingId);
        if (hallType == null) {
            System.out.println("No hall type found for booking ID: " + bookingId);
            System.out.println("Available booking IDs: " + bookingHallTypeMap.keySet());
            return 0.0;
        }

        // Fetch the hall fee associated with the hall type
        Double hallFee = hallFees.get(hallType);
        if (hallFee == null) {
            System.out.println("No hall fee found for hall type: " + hallType);
            return 0.0;
        }

        return hallFee;
    }
    
    private void reloadBookings() {
    bookingHallTypeMap.clear(); // Clear existing data
    loadBookingData("bookings.txt"); // Reload from file
}

    // Example refund method
    private void populateBookingComboBox(JComboBox<String> comboBox, String username) {
            List<String> bookingList = getBookingList(username);  // Fetch list of booking IDs for the user

            comboBox.removeAllItems();  // Clear any existing items in the combo box

            if (bookingList.isEmpty()) {
                comboBox.addItem("No bookings available");
            } else {
                for (String bookingID : bookingList) {
                    comboBox.addItem(bookingID);  // Add each booking ID to the combo box
                }
            }
        }

    private List<String> getBookingList(String username) {
        List<String> bookingList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();  // Get the current date

        try (BufferedReader reader = new BufferedReader(new FileReader("bookings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookingDetails = line.split(",");

                // Assuming the format is username, bookingID, halltype, date, time, hallFee
                if (bookingDetails.length >= 6 && bookingDetails[0].equals(username)) {
                    String bookingID = bookingDetails[1];  // Booking ID is the second field
                    String bookingDateStr = bookingDetails[3];  // Date is the fourth field

                    try {
                        Date bookingDate = dateFormat.parse(bookingDateStr);  // Parse the date from the string

                        // Calculate the difference between the current date and the booking date
                        long diffInMillis = bookingDate.getTime() - currentDate.getTime() ;
                        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);  // Convert milliseconds to days

                        // Only add booking ID if the booking date is 3 or more days before the current date
                        if (diffInDays >= 3) {
                            bookingList.add(bookingID);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookingList;
    }



        // Panel for logging out
    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(150, 80, 150, 30);
        logoutButton.addActionListener(e -> {
            // Logic to logout and return to the login screen
            logout();
        });

        panel.add(logoutButton);
        return panel;
    }
    
    private void logout(){
        this.dispose();
        Login login = new Login();
        login.setVisible(true);
    }

    public UserManagement getUserManagement() {
        return userManagement;
    }

    public void setUserManagement(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Customer("customer"));
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public void setFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }
}

