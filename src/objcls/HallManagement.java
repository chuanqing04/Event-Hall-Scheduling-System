package objcls;

import com.toedter.calendar.JDateChooser;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class HallManagement {
    private List<String[]> hallData, maintenanceData;
    private DefaultTableModel tableModel;
    private JTable hallTable;
    private int selectedRow;
    private JDateChooser startDateChooser, endDateChooser;
    private FileHandler fileHandler1,fileHandler2;
    

    public HallManagement(JTable hallTable, DefaultTableModel tableModel) {
        this.hallTable = hallTable;
        this.tableModel = tableModel;
        
        this.fileHandler1 = new FileHandler("Hall.txt");
        this.hallData = fileHandler1.loadHallData();
        this.fileHandler2 = new FileHandler("maintenance.txt");
        this.maintenanceData = fileHandler2.loadMaintenanceIssues();
    }
    
    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public void showAddHallDialog() {
        JTextField hallNameField = new JTextField();
        JComboBox<String> hallSizeComboBox = new JComboBox<>(new String[]{"Banquet Hall", "Auditorium", "Meeting Room"});
        Object[] message = {
            "Hall Name:", hallNameField,
            "Hall Type:", hallSizeComboBox
        };

        int option;
        do {
            option = JOptionPane.showConfirmDialog(null, message, "Add Hall", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                String hallName = hallNameField.getText();
                String hallSize = (String) hallSizeComboBox.getSelectedItem();
                if (hallName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a hall name.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (hallSize == null) {
                    JOptionPane.showMessageDialog(null, "Please select a hall type.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!isHallNameUnique(hallName)) {
                    JOptionPane.showMessageDialog(null, "Hall name already exists. Please choose a different name.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String[] newHall = {hallName, hallSize, "-", "-", "-", "-", "-"};
                    hallData.add(newHall);
                    tableModel.addRow(newHall);
                    saveHallData();
                    break;
                }
            } else {
                break;
            }
        } while (true);
    }

    public void showEditHallDialog() {
        if (selectedRow != -1) {
            String currentHallName = (String) tableModel.getValueAt(selectedRow, 0);
            String currentHallSize = (String) tableModel.getValueAt(selectedRow, 1);

            JTextField hallNameField = new JTextField(currentHallName);
            JComboBox<String> hallSizeComboBox = new JComboBox<>(new String[]{"Banquet Hall", "Auditorium", "Meeting Room"});
            hallSizeComboBox.setSelectedItem(currentHallSize);
            Object[] message = {
                "Hall Name:", hallNameField,
                "Hall Type:", hallSizeComboBox
            };

            int option;
            do {
                option = JOptionPane.showConfirmDialog(null, message, "Edit Hall", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {
                    String newHallName = hallNameField.getText();
                    String newHallSize = (String) hallSizeComboBox.getSelectedItem();
                    if (newHallName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a hall name.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (newHallSize == null) {
                        JOptionPane.showMessageDialog(null, "Please select a hall type.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (!isHallNameUnique(newHallName) && !newHallName.equalsIgnoreCase(currentHallName)) {
                        JOptionPane.showMessageDialog(null, "Hall name already exists. Please choose a different name.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        hallData.get(selectedRow)[0] = newHallName;
                        hallData.get(selectedRow)[1] = newHallSize;
                        tableModel.setValueAt(newHallName, selectedRow, 0);
                        tableModel.setValueAt(newHallSize, selectedRow, 1);
                        saveHallData();
                        break;
                    }
                    } else {
                    break;
                }
            } while (true);
        }
    }

    public void deleteHall() {
        if (selectedRow != -1) {
            int modelRow = hallTable.convertRowIndexToModel(selectedRow);
            hallData.remove(modelRow);
            tableModel.removeRow(modelRow);
            saveHallData();
        }else {
            System.out.println("No row selected for deletion.");
        }
    }

    private boolean isHallNameUnique(String hallName) {
        for (String[] hall : hallData) {
            if (hall[0].equalsIgnoreCase(hallName)) {
                return false;
            }
        }
        return true;
    }
    
    public void setAvailabilityDialog(JFrame parentFrame){
        if (selectedRow != -1) {
            // Convert the view index to model index
            int modelRow = hallTable.convertRowIndexToModel(selectedRow);

            String hallName = (String) tableModel.getValueAt(modelRow, 0);
        
            while (true) {
                // Create input fields
                JTextField startTimeField = new JTextField();
                JTextField endTimeField = new JTextField();
                JTextField remarksField = new JTextField();
                remarksField.setPreferredSize(new Dimension(300, 20));
            
                // Create a panel to hold the components
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Start Date:"));
                startDateChooser = new JDateChooser();
                startDateChooser.setPreferredSize(new Dimension(150, 30));
                panel.add(startDateChooser);
                panel.add(new JLabel("Start Time (HH:mm):"));
                panel.add(startTimeField);
                panel.add(new JLabel("End Date:"));
                endDateChooser = new JDateChooser();
                endDateChooser.setPreferredSize(new Dimension(150, 30));
                panel.add(endDateChooser);
                panel.add(new JLabel("End Time (HH:mm):"));
                panel.add(endTimeField);
                panel.add(new JLabel("Remarks:"));
                panel.add(remarksField);

                int option = JOptionPane.showConfirmDialog(parentFrame, panel,
                    "Set Availability for " + hallName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    // Convert date choosers to String
                    String startDateText = formatDate(startDateChooser.getDate());
                    String startTimeText = startTimeField.getText();
                    String endDateText = formatDate(endDateChooser.getDate());
                    String endTimeText = endTimeField.getText();
                    String remarksText = remarksField.getText();
                    
                    if (startDateChooser.getDate() == null || startTimeField.getText().isEmpty() || endDateChooser.getDate() == null || endTimeField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, "Please fill out all necessary information!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Validation: Check if end date is earlier than start date
                    else if (endDateChooser.getDate().before(startDateChooser.getDate()) || (endDateChooser.getDate().equals(startDateChooser.getDate()) && endTimeField.getText().compareTo(startTimeField.getText()) < 0)) {
                        JOptionPane.showMessageDialog(parentFrame, "End date cannot be earlier than start date!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Validation: Check date format
                    else if (!isValidDate(startDateText) || !isValidDate(endDateText)) {
                        JOptionPane.showMessageDialog(parentFrame, "Invalid date format! Please use 'yyyy-MM-dd'.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    else if (!isValidTime(startTimeText) || !isValidTime(endTimeText)) {
                        JOptionPane.showMessageDialog(parentFrame, "Invalid time format! Please use 'HH:mm'.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    else if (remarksText.isEmpty()) {
                    remarksText = "-";
                    }

                    // Validate date format
                    else if (isValidDate(startDateText) && isValidDate(endDateText) && isValidTime(startTimeText) && isValidTime(endTimeText)) {
                        // Update table and data
                        tableModel.setValueAt(startDateText, modelRow, 1);
                        tableModel.setValueAt(startTimeText, modelRow, 2);
                        tableModel.setValueAt(endDateText, modelRow, 3);
                        tableModel.setValueAt(endTimeText, modelRow, 4);
                        tableModel.setValueAt(remarksText, modelRow, 5);

                        // Assuming hallData is a List<String[]> or similar
                        hallData.get(modelRow)[2] = startDateText;
                        hallData.get(modelRow)[3] = startTimeText;
                        hallData.get(modelRow)[4] = endDateText;
                        hallData.get(modelRow)[5] = endTimeText;
                        hallData.get(modelRow)[6] = remarksText;

                        saveHallData();
                        break;
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "Invalid time format! Please use 'HH:mm'.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    break;
                }
            }
        }
    }
    
    public void setMaintenanceDialog(JFrame parentFrame){
        if (selectedRow != -1) {
            // Convert the view index to model index
            int modelRow = hallTable.convertRowIndexToModel(selectedRow);

            String issueID = (String) tableModel.getValueAt(modelRow, 0);
        
            while (true) {
                // Create input fields
                JTextField startTimeField = new JTextField();
                JTextField endTimeField = new JTextField();
                JTextField remarksField = new JTextField();
                remarksField.setPreferredSize(new Dimension(300, 20));
            
                // Create a panel to hold the components
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Start Date:"));
                startDateChooser = new JDateChooser();
                startDateChooser.setPreferredSize(new Dimension(150, 30));
                panel.add(startDateChooser);
                panel.add(new JLabel("Start Time (HH:mm):"));
                panel.add(startTimeField);
                panel.add(new JLabel("End Date:"));
                endDateChooser = new JDateChooser();
                endDateChooser.setPreferredSize(new Dimension(150, 30));
                panel.add(endDateChooser);
                panel.add(new JLabel("End Time (HH:mm):"));
                panel.add(endTimeField);
                panel.add(new JLabel("Remarks:"));
                panel.add(remarksField);

                int option = JOptionPane.showConfirmDialog(parentFrame, panel,
                    "Set Maintenance for issue " + issueID, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    // Convert date choosers to String
                    String startDateText = formatDate(startDateChooser.getDate());
                    String startTimeText = startTimeField.getText();
                    String endDateText = formatDate(endDateChooser.getDate());
                    String endTimeText = endTimeField.getText();
                    String remarksText = remarksField.getText();
                    
                    
                    if (startDateChooser.getDate() == null || startTimeField.getText().isEmpty() || endDateChooser.getDate() == null || endTimeField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, "Please fill out all necessary information!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Validation: Check if end date is earlier than start date
                    else if (endDateChooser.getDate().before(startDateChooser.getDate()) || (endDateChooser.getDate().equals(startDateChooser.getDate()) && endTimeField.getText().compareTo(startTimeField.getText()) < 0)) {
                        JOptionPane.showMessageDialog(parentFrame, "End date cannot be earlier than start date!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Validation: Check date format
                    else if (!isValidDate(startDateText) || !isValidDate(endDateText)) {
                        JOptionPane.showMessageDialog(parentFrame, "Invalid date format! Please use 'yyyy-MM-dd'.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    else if (!isValidTime(startTimeText) || !isValidTime(endTimeText)) {
                        JOptionPane.showMessageDialog(parentFrame, "Invalid time format! Please use 'HH:mm'.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    else if (remarksText.isEmpty()) {
                    remarksText = "-";
                    }

                    // Validate date format
                    else if (isValidDate(startDateText) && isValidDate(endDateText) && isValidTime(startTimeText) && isValidTime(endTimeText)) {
                        // Update table and data
                        
                        tableModel.setValueAt(startDateText, modelRow, 5);
                        tableModel.setValueAt(startTimeText, modelRow, 6);
                        tableModel.setValueAt(endDateText, modelRow, 7);
                        tableModel.setValueAt(startTimeText, modelRow, 8);
                        tableModel.setValueAt(remarksText, modelRow, 4);

                        // Assuming hallData is a List<String[]> or similar
                        maintenanceData.get(modelRow)[9] = startDateText;
                        maintenanceData.get(modelRow)[10] = startTimeText;
                        maintenanceData.get(modelRow)[11] = endDateText;
                        maintenanceData.get(modelRow)[12] = endTimeText;
                        maintenanceData.get(modelRow)[8] = remarksText;

                        saveMaintenanceData();
                        break;
                    } else {
                        JOptionPane.showMessageDialog(parentFrame,
                            "Error", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    break;
                }
            }
        }
    }
    
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private boolean isValidDate(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateTime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        try {
            sdf.parse(dateTime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void saveHallData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("hall.txt"))) {
            for (String[] hall : hallData) {
                writer.write(String.join(",", hall));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMaintenanceData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("maintenance.txt"))) {
            for (String[] hall : maintenanceData) {
                writer.write(String.join(",", hall));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

