/** Edgar Lopez
 * CEN 3024C - Software Development 1
 * October 20, 2026
 * Main.java
 *
 * Entry point for the Dinosaur Data Management System
 * Creates the application components and starts the GUI
 */
public class Main {

    /** Starts the Dinosaur DMS application
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        // Create the application components
        DinosaurManager manager = new DinosaurManager();
        DinosaurFileReader fileReader = new DinosaurFileReader();

        // Launch the graphical user interface
        javax.swing.SwingUtilities.invokeLater(() ->
                new DinosaurGUI(manager, fileReader)
        );
    }
}