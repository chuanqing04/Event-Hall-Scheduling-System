package ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import objcls.FileHandler;

public class Login extends JFrame implements ActionListener {
    private final String currentAdmin;
    private JLabel title;
    private JLabel unamelabel;
    private JLabel passwordlabel;
    private JTextField unametb;
    private JPasswordField passwordtb;
    private JButton loginb;
    private JButton registerb;
    private FileHandler filehandler;

    //Getters and Setters
    public FileHandler getFilehandler() {
        return filehandler;
    }
    
    public void setFilehandler(FileHandler filehandler) {
        this.filehandler = filehandler;
    }

    //User Interface
    public Login() {
        super("Login");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        JPanel panel = createLoginPanel();
        add(panel);
        setVisible(true);
        filehandler = new FileHandler("Login.txt");
        currentAdmin = "System";
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 300, 400);

        title = new JLabel("Login");
        title.setFont(new Font("Times New Roman",Font.BOLD, 24));
        unamelabel = new JLabel("Username: ");
        passwordlabel = new JLabel("Password: ");
        unametb = new JTextField();
        passwordtb = new JPasswordField();
        loginb = new JButton("Login");
        registerb = new JButton("SIgn Up");

        loginb.addActionListener(this::loginbclick);
        registerb.addActionListener(this::registerbclick);

        panel.add(title).setBounds(100, 50, 100, 30);
        panel.add(unamelabel).setBounds(50, 100, 70, 30);
        panel.add(passwordlabel).setBounds(50, 150, 70, 30);
        panel.add(unametb).setBounds(130, 100, 100, 30);
        panel.add(passwordtb).setBounds(130, 150, 100, 30);
        panel.add(loginb).setBounds(80, 200, 100, 30);
        panel.add(registerb).setBounds(80, 240, 100, 30);

        return panel;
    }

    //Action Events
    //Login Button clicked 
    private void loginbclick(ActionEvent e) {
        String username = unametb.getText().trim();
        String password = new String(passwordtb.getPassword()).trim();
    
        if (filehandler.validateData(username, password)) {
            String[] userDetails = filehandler.getUserData(username);
            if (userDetails != null) {
                handleUserLogin(userDetails);
            } else {
                showError("Invalid username or password.");
            }
        } else {
            showError("Invalid username or password.");
        }
    
        clearTextFields();
    }

    //Register Button Clicked
    private void registerbclick(ActionEvent e) {
        DialogConstructor dialogConstructor = new DialogConstructor(filehandler, currentAdmin);
    
        dialogConstructor.openRegistrationDialog(success -> {
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
            }
        });
    }

   //Supplementary Methods
   //Handling login requests 
    private void handleUserLogin(String[] userDetails) {
        if (isUserBlocked(userDetails)) {
            showError("Your account is blocked. Please contact support.");
        } else {
            JOptionPane.showMessageDialog(this, "Login successful!");
            openUserWindow(userDetails[2]); // userDetails[2] is the usertype
        }
    }
    
    private boolean isUserBlocked(String[] userDetails) {
        return userDetails[3].equalsIgnoreCase("TRUE");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    private void clearTextFields() {
        unametb.setText("");
        passwordtb.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //All action events are handled
    }

    //Opens Corresponding Windows based on userType
    private void openUserWindow(String userType) {
        switch (userType.toLowerCase()) {
            case "admin":  {
                Admin adminWindow = new Admin(unametb.getText());
                adminWindow.setVisible(true);
                break;
            }
            case "scheduler":  {
                Scheduler schedulerWindow = new Scheduler(unametb.getText());
                schedulerWindow.setVisible(true);
                break;
                }
            case "manager":  {
                Manager managerWindow = new Manager(unametb.getText());
                managerWindow.setVisible(true);
                break;
                }
            case "customer":  {
                Customer customerWindow = new Customer(unametb.getText());
                customerWindow.setVisible(true);
                break;
                }
            default: JOptionPane.showMessageDialog(this, "Unknown user type.");
        }
        this.dispose();
    }

    //For Testing purposes only
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}
