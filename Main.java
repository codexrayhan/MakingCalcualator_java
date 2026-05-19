import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Run on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            // Use system look-and-feel as a base, then override with custom styling
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
               
            }

            // Anti-aliased text system-wide
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}