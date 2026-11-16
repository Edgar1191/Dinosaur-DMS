
import java.util.List;
import java.util.Scanner;

/**
 * Edgar Lopez
 * CEN 3024C - Software Development 1
 * November 16, 2026
 * DinosaurCLI.java
 *
 * Provides a command-line interface for the Dinosaur Data Management System.
 * Allows users to interact with dinosaur records through a text-based menu.
 *
 * The interface supports importing dinosaur records from a text file, displaying records, adding new dinosaurs,
 * removing records, updating attributes, and searching or filtering dinosaur information.
 *
 * Connects the user interface to the DinosaurManager and DinosaurFileReader classes to support the application's
 * data management functionality.
 */
public class DinosaurCLI {

    private final Scanner scanner;
    private final DinosaurManager manager;
    private final DinosaurFileReader fileReader;

    /**
     * Creates the command-line interface and connects it to the dinosaur manager and file reader
     *
     * @param manager the component responsible for managing dinosaur records
     * @param fileReader the component responsible for loading dinosaur data
     */
    public DinosaurCLI(DinosaurManager manager,
                       DinosaurFileReader fileReader) {
        this.scanner = new Scanner(System.in);
        this.manager = manager;
        this.fileReader = fileReader;
    }

    /**
     * Displays the command-line menu and processes user selections until the user chooses to exit the application.
     *
     * @return true when the user exits the menu normally
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

    /**
     * Loads dinosaur records from a user-specified text file and attempts to add valid records to the manager.
     * Reports the number of records added and rejected.
     *
     * @return a message describing the file loading results
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

    /**
     * Retrieves and formats all currently stored dinosaur records for display in the command-line interface.
     *
     * @return a formatted list of records or a message if none exist
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

    /**
     * Collects dinosaur information, validates the new record, and attempts to add it to the manager.
     *
     * @return a message describing the result of the operation
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

    /**
     * Collects all six dinosaur attributes from the user and validates the information before creating a dinosaur
     * object.
     *
     * @return a valid dinosaur object, or null if input is invalid
     */
    private Dinosaur promptForDinosaur() {
        String name = promptForText("Dinosaur name: ");
        if (name == null) {
            return null;
        }

        if (manager.findDinosaur(name) != null) {
            System.out.println("A dinosaur with that name already exists.");
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

    private String promptForText(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        if (value.isBlank()) {
            System.out.println("Input cannot be blank.");
            return null;
        }

        return value;
    }

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


    /**
     * Finds a dinosaur by name, requests user confirmation, and attempts to remove the selected record.
     *
     * @return a message describing the result of the removal
     */
    private String removeDinosaur() {
        System.out.print("Enter the name of the dinosaur to remove: ");
        String name = scanner.nextLine().trim();

        if (name.isBlank()) {
            return "Dinosaur name cannot be blank.";
        }

        Dinosaur dinosaur = manager.findDinosaur(name);

        if (dinosaur == null) {
            return "No dinosaur named '" + name + "' was found.";
        }

        System.out.println("You are about to remove:");
        System.out.println(dinosaur);

        System.out.print("Are you sure you want to delete this dinosaur? (y/n): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("y")) {
            return "Delete cancelled. The dinosaur was not removed.";
        }

        if (manager.removeDinosaur(name)) {
            return "Dinosaur removed successfully.";
        }

        return "The dinosaur could not be removed.";
    }


    /**
     * Allows the user to select an existing dinosaur and update one of its attributes after validating the new value.
     *
     * @return a message describing the result of the update
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

        System.out.println("\nCurrent record:");
        System.out.println(existing);

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Time Period");
        System.out.println("3. Diet");
        System.out.println("4. Clade");
        System.out.println("5. Fossil Location");
        System.out.println("6. Size");
        System.out.println("7. Cancel");
        System.out.print("Choose an option (1-7): ");

        String choice = scanner.nextLine().trim();

        String field;
        String newValue;

        switch (choice) {
            case "1":
                field = "name";
                newValue = promptForText("Enter the new name: ");
                break;

            case "2":
                field = "timePeriod";
                newValue = promptForText("Enter the new time period: ");
                break;

            case "3":
                field = "diet";
                newValue = promptForText("Enter the new diet: ");
                break;

            case "4":
                field = "clade";
                newValue = promptForText("Enter the new clade: ");
                break;

            case "5":
                field = "fossilLocation";
                newValue = promptForText("Enter the new fossil location: ");
                break;

            case "6":
                field = "size";
                Double newSize = promptForSize("Enter the new size: ");

                if (newSize == null) {
                    return "Update cancelled. The original record was not changed.";
                }

                newValue = String.valueOf(newSize);
                break;

            case "7":
                return "Update cancelled. The original record was not changed.";

            default:
                return "Invalid update option. Please select a number from 1 to 7.";
        }

        if (newValue == null) {
            return "Update cancelled. The original record was not changed.";
        }

        if (manager.updateDinosaurField(name, field, newValue)) {
            Dinosaur updated = manager.findDinosaur(newValue);

            if (field.equals("name")) {
                updated = manager.findDinosaur(newValue);
            } else {
                updated = manager.findDinosaur(name);
            }

            return "Dinosaur updated successfully:\n" + updated;
        }

        return "Update rejected. Check the input and make sure the value is valid "
                + "and the name is not already in use.";
    }
    /**
     * Allows the user to select a dinosaur attribute and search the manager for records matching the specified value.
     *
     * @return matching records or a message describing the search result
     */
    private String searchDinosaurs() {
        System.out.println("\nWhat would you like to search by?");
        System.out.println("1. Name");
        System.out.println("2. Time Period");
        System.out.println("3. Diet");
        System.out.println("4. Clade");
        System.out.println("5. Fossil Location");
        System.out.println("6. Size");
        System.out.println("7. Cancel");
        System.out.print("Choose an option (1-7): ");

        String choice = scanner.nextLine().trim();

        String attribute;

        switch (choice) {
            case "1":
                attribute = "name";
                break;

            case "2":
                attribute = "timePeriod";
                break;

            case "3":
                attribute = "diet";
                break;

            case "4":
                attribute = "clade";
                break;

            case "5":
                attribute = "fossilLocation";
                break;

            case "6":
                attribute = "size";
                break;

            case "7":
                return "Search cancelled.";

            default:
                return "Invalid search option. Please select a number from 1 to 7.";
        }

        System.out.print("Enter the value to search for: ");
        String value = scanner.nextLine().trim();

        if (value.isBlank()) {
            return "Search value cannot be blank.";
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
