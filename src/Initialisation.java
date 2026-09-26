import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ui.*;

public class Initialisation {
    public static Login login;

    // Nimbus Set Look and Feel Initialization
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Run the actual program here
    public static void main(String[] args) {
        setLookAndFeel();
        login = new Login(); 
    }
}