import javax.swing.UIManager;
import javax.swing.SwingUtilities;

public class WhatsUTClient {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}