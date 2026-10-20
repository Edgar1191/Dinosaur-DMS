
import java.util.List;
import java.util.Scanner;

/** Edgar Lopez
 * CEN 3024C - Software Development 1
 * October 20, 2026
 * DinosaurCLI.java
 * Provides the command-line interface for the Dinosaur DMS
 * Displays the menu, collects input, and presents results
 */
public class DinosaurCLI {

    private final Scanner scanner;
    private final DinosaurManager manager;
    private final DinosaurFileReader fileReader;

    /** Creates the CLI and connects it to the manager and file reader
     *
     * @param manager the dinosaur data manager
     * @param fileReader the file reader
     */
    public DinosaurCLI(DinosaurManager manager,
                       DinosaurFileReader fileReader) {
        this.scanner = new Scanner(System.in);
        this.manager = manager;
        this.fileReader = fileReader;
    }

    /** Runs the menu until the user chooses to exit
     *
     * @return true when the application exits normally
     */
    public boolean run() {
        boolean running = true;

        while (running) {
            System.out.println("\nDinosaur Data Management System");
            System.out.println("1. Load dinosaurs from a text file");
            System.out.println("2. Display all dinosaurs");
            System.out.println("3. Add a dinosaur");
            System.out.println("4. Remove a dinosaur");
            System.out.println("5. Update a dinosaur");
            System.out.println("6. Search/filter dinosaurs");
            System.out.println("7. Exit");
            System.out.print("Choose an option (1-7): ");

            String choice = scanner.nextLine().trim();
            // Routes each valid menu selection to the corresponding DMS operation
            switch (choice) {
                case "1" -> System.out.println(loadFromFile());
                case "2" -> System.out.println(displayDinosaurs());
                case "3" -> System.out.println(addDinosaur());
                case "4" -> System.out.println(removeDinosaur());
                case "5" -> System.out.println(updateDinosaur());
                case "6" -> System.out.println(searchDinosaurs());
                case "7" -> {
                    System.out.println("Thank you for using the Dinosaur DMS.");
                    running = false;
                }
                default -> System.out.println(
                        "Invalid menu option. Please enter a number from 1 to 7.");
            }
        }

        return !running;
    }

    /** Loads dinosaur records from a user-specified text file
     *
     * @return a message describing the loading result
     */
    private String loadFromFile() {
        System.out.print("Enter the path to the dinosaur text file: ");
        String filePath = scanner.nextLine().trim();

        if (filePath.isBlank()) {
            return "File path cannot be blank.";
        }

        List<Dinosaur> loadedDinosaurs =
                fileReader.loadDinosaurs(filePath);

        if (loadedDinosaurs.isEmpty()) {
            return "No valid records were loaded. Check the file path, "
                    + "file contents, and required format.";
        }

        int added = 0;
        int rejected = 0;

        for (Dinosaur dinosaur : loadedDinosaurs) {
            if (manager.addDinosaur(dinosaur)) {
                added++;
            } else {
                rejected++;
            }
        }

        return "File loading finished. Added " + added
                + " dinosaur record(s). Rejected " + rejected
                + " duplicate or invalid record(s).";
    }

    /** Displays every stored dinosaur
     *
     * @return a formatted list of records or an empty-collection message
     */
    private String displayDinosaurs() {
        List<Dinosaur> dinosaurs = manager.getAllDinosaurs();

        if (dinosaurs.isEmpty()) {
            return "No dinosaur records are currently stored.";
        }

        StringBuilder output = new StringBuilder("\n--- Dinosaur Records ---\n");

        for (Dinosaur dinosaur : dinosaurs) {
            output.append(dinosaur).append("\n");
        }

        output.append("Total records: ").append(dinosaurs.size());

        return output.toString();
    }

    /** Collects and validates the information for a new dinosaur
     *
     * @return a message describing the result
     */
    private String addDinosaur() {
        Dinosaur dinosaur = promptForDinosaur();

        if (dinosaur == null) {
            return "Dinosaur was not added because the input was invalid.";
        }

        if (manager.addDinosaur(dinosaur)) {
            return "Dinosaur added successfully:\n" + dinosaur;
        }

        return "Dinosaur could not be added. Check the information "
                + "and make sure the name is unique.";
    }

    /** Prompts for all six dinosaur attributes
     *
     * @return a valid dinosaur object, or null if input is invalid
     */
    private Dinosaur promptForDinosaur() {
        String name = promptForText("Dinosaur name: ");
        if (name == null) {
            return null;
        }

        String timePeriod = promptForText("Time period: ");
        if (timePeriod == null) {
            return null;
        }

        String diet = promptForText("Diet: ");
        if (diet == null) {
            return null;
        }

        String clade = promptForText("Clade: ");
        if (clade == null) {
            return null;
        }

        String fossilLocation = promptForText("Fossil location: ");
        if (fossilLocation == null) {
            return null;
        }

        Double size = promptForSize("Size (must be greater than 0): ");
        if (size == null) {
            return null;
        }

        Dinosaur dinosaur = new Dinosaur(
                name, timePeriod, diet, clade, fossilLocation, size);

        if (!manager.validateDinosaur(dinosaur)) {
            return null;
        }

        return dinosaur;
    }

    /** Prompts for nonblank text
     *
     * @param prompt the message shown to the user
     * @return trimmed text, or null if the input is blank
     */
    private String promptForText(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        if (value.isBlank()) {
            System.out.println("Input cannot be blank.");
            return null;
        }

        return value;
    }

    /** Prompts for a finite and positive numeric size
     *
     * @param prompt the message shown to the user
     * @return the valid size, or null if the input is invalid
     */
    private Double promptForSize(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            double size = Double.parseDouble(input);

            if (!Double.isFinite(size) || size <= 0) {
                System.out.println(
                        "Size must be a finite number greater than zero.");
                return null;
            }

            return size;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid size. Please enter a number.");
            return null;
        }
    }

    /** Removes a dinosaur selected by name
     *
     * @return a message describing the result
     */
    private String removeDinosaur() {
        System.out.print("Enter the name of the dinosaur to remove: ");
        String name = scanner.nextLine().trim();

        if (name.isBlank()) {
            return "Dinosaur name cannot be blank.";
        }

        if (manager.removeDinosaur(name)) {
            return "Dinosaur removed successfully.";
        }

        return "No dinosaur named '" + name + "' was found.";
    }

    /** Updates an existing dinosaur's attributes
     *
     * @return a message describing the result
     */
    private String updateDinosaur() {
        System.out.print("Enter the name of the dinosaur to update: ");
        String name = scanner.nextLine().trim();

        if (name.isBlank()) {
            return "Dinosaur name cannot be blank.";
        }

        Dinosaur existing = manager.findDinosaur(name);

        if (existing == null) {
            return "No dinosaur named '" + name + "' was found.";
        }

        System.out.println("Current record:\n" + existing);
        System.out.println("Enter the replacement information.");

        Dinosaur replacement = promptForDinosaur();

        if (replacement == null) {
            return "Update cancelled. The original record was not changed.";
        }

        if (manager.updateDinosaur(name, replacement)) {
            return "Dinosaur updated successfully:\n"
                    + manager.findDinosaur(replacement.getName());
        }

        return "Update rejected. Check the input and ensure the name "
                + "is not already in use. The original record is unchanged.";
    }

    /** Searches dinosaur records using a supported attribute
     *
     * @return matching records or an appropriate message
     */
    private String searchDinosaurs() {
        System.out.println("Supported attributes: name, timePeriod, diet, "
                + "clade, fossilLocation, size");

        System.out.print("Enter the attribute to search: ");
        String attribute = scanner.nextLine().trim();

        if (attribute.isBlank()) {
            return "Search attribute cannot be blank.";
        }

        System.out.print("Enter the value to search for: ");
        String value = scanner.nextLine().trim();

        if (value.isBlank()) {
            return "Search value cannot be blank.";
        }

        if (!isSupportedSearchAttribute(attribute)) {
            return "Unsupported attribute. Please select one of the "
                    + "six supported attributes.";
        }

        List<Dinosaur> results =
                manager.searchDinosaurs(attribute, value);

        if (results.isEmpty()) {
            return "No matching dinosaurs were found.";
        }

        StringBuilder output = new StringBuilder("\nSearch Results\n");

        for (Dinosaur dinosaur : results) {
            output.append(dinosaur).append("\n");
        }

        output.append("Matches found: ").append(results.size());

        return output.toString();
    }

    /** Checks whether the user selected a supported search attribute
     *
     * @param attribute the attribute supplied by the user
     * @return true if supported; otherwise false
     */
    private boolean isSupportedSearchAttribute(String attribute) {
        return attribute.equalsIgnoreCase("name")
                || attribute.equalsIgnoreCase("timePeriod")
                || attribute.equalsIgnoreCase("diet")
                || attribute.equalsIgnoreCase("clade")
                || attribute.equalsIgnoreCase("fossilLocation")
                || attribute.equalsIgnoreCase("size");
    }
}
