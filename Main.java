
import javax.swing.*;
import java.io.File;
import java.sql.SQLException;

/** Edgar Lopez
 * CEN 3024C - Software Development 1
 * November 9, 2026
 * Main.java
 *
 * Entry point for the Dinosaur Data Management System
 * Connects to the SQLite Database and starts the GUI
 */
public class Main {

    /** Starts the Dinosaur DMS application
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Allow the user to select an SQLite database
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle(
                    "Select Dinosaur Database"
            );

            fileChooser.setFileFilter(
                    new javax.swing.filechooser.FileNameExtensionFilter(
                            "SQLite Database (*.db)",
                            "db"
                    )
            );

            int result = fileChooser.showOpenDialog(null);

            // Stop if the user cancels
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File databaseFile = fileChooser.getSelectedFile();

            // Verify that the selected file exists
            if (!databaseFile.isFile()) {

                JOptionPane.showMessageDialog(
                        null,
                        "Please select an existing database file.",
                        "Invalid Database",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // Connect to the selected database
            try {

                DinosaurDatabase database =
                        new DinosaurDatabase(
                                databaseFile.getAbsolutePath()
                        );

                DinosaurManager manager = new DinosaurManager();

                DinosaurFileReader fileReader =
                        new DinosaurFileReader();

                // Launch the GUI with the database connection
                new DinosaurGUI(manager, fileReader, database);

            } catch (SQLException exception) {

                JOptionPane.showMessageDialog(
                        null,
                        "Unable to connect to the selected database:\n"
                                + exception.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
