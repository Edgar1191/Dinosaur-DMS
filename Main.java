
import javax.swing.*;
import java.io.File;
import java.sql.SQLException;

/**
 * Edgar Lopez
 * CEN 3024C - Software Development 1
 * November 16, 2026
 * Main.java
 *
 * Entry point for the Dinosaur Data Management System.
 * This application addresses the need for an organized system to manage dinosaur information, including names,
 * time periods, diets, clades, fossil locations, and sizes.
 *
 * The application allows users to add, display, update, remove, search, and import dinosaur records.
 * It uses an SQLite database for persistent storage and a Java Swing graphical user interface for user interaction.
 */
public class Main {

    /**
     * Starts the Dinosaur Data Management System by allowing the user to select an existing SQLite database,
     * establishing the database connection, and launching the graphical user interface.
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
