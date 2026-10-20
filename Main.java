
/**
 * Entry point for the Dinosaur Data Management System
 * Creates the application components and starts the CLI
 */
public class Main {

    /** Starts the Dinosaur DMS application
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        DinosaurManager manager = new DinosaurManager();
        DinosaurFileReader fileReader = new DinosaurFileReader();

        DinosaurCLI cli = new DinosaurCLI(manager, fileReader);

        boolean applicationExited = cli.run();

        if (applicationExited) {
            System.out.println("Application closed successfully.");
        }
    }
}
