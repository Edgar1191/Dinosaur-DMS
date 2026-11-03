
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Provides the graphical user interface for the Dinosaur Data Management System
 * Allows users to interact with dinosaur records through a Swing application
 */
public class DinosaurGUI {

    // Main application window
    private JFrame frame;

    // Existing application logic and file reader
    private final DinosaurManager manager;
    private final DinosaurFileReader fileReader;

    // Table that displays dinosaur records
    private JTable dinosaurTable;

    // Controls the data displayed in the table
    private DefaultTableModel tableModel;

    // Displays application status messages
    private JLabel statusLabel;

    // Main application colors
    private final Color forestGreen = new Color(24, 61, 43);
    private final Color mossGreen = new Color(49, 91, 60);
    private final Color lightSage = new Color(233, 239, 230);
    private final Color charcoal = new Color(52, 58, 52);

    /** Creates the GUI using the existing manager and file reader
     *
     * @param manager the dinosaur manager
     * @param fileReader the dinosaur file reader
     */
    public DinosaurGUI(DinosaurManager manager,
                       DinosaurFileReader fileReader) {

        this.manager = manager;
        this.fileReader = fileReader;

        createWindow();
    }

    /**
     * Creates the main application window and its components
     */
    private void createWindow() {

        frame = new JFrame("Dinosaur Data Management System");

        frame.setSize(1150, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main application layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(lightSage);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Add the main application sections
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createActionPanel(), BorderLayout.WEST);
        mainPanel.add(createRecordsPanel(), BorderLayout.CENTER);
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        frame.add(mainPanel);

        frame.setVisible(true);
    }

    /** Creates the application header
     *
     * @return the header panel
     */
    private JPanel createHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(forestGreen);
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel titleLabel = new JLabel(
                "Dinosaur Data Management System"
        );

        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));

        JLabel subtitleLabel = new JLabel(
                "Manage, explore, and organize dinosaur records"
        );

        subtitleLabel.setForeground(lightSage);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel titleContainer = new JPanel();
        titleContainer.setBackground(forestGreen);
        titleContainer.setLayout(new BoxLayout(
                titleContainer, BoxLayout.Y_AXIS
        ));

        titleContainer.add(titleLabel);
        titleContainer.add(Box.createVerticalStrut(5));
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);

        return headerPanel;
    }

    /** Creates the panel containing application operation buttons
     *
     * @return the action panel
     */
    private JPanel createActionPanel() {

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(4, 2, 5, 10));
        actionPanel.setBackground(lightSage);
        actionPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0)
        );

        JLabel actionTitle = new JLabel("OPERATIONS");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        actionTitle.setForeground(charcoal);

        actionPanel.add(actionTitle);

        // Load dinosaur records from a text file
        JButton loadButton = createButton("Load File");
        loadButton.addActionListener(e -> loadDinosaurFile());
        actionPanel.add(loadButton);

        // Display all dinosaur records
        JButton displayButton = createButton("Display All");
        displayButton.addActionListener(e ->
                refreshTable(manager.getAllDinosaurs()));
        actionPanel.add(displayButton);

        // Open the form to add a dinosaur
        JButton addButton = createButton("Add Dinosaur");
        addButton.addActionListener(e -> addDinosaur());
        actionPanel.add(addButton);

        // Open the form to update a dinosaur
        JButton updateButton = createButton("Update Dinosaur");
        updateButton.addActionListener(e -> updateDinosaur());
        actionPanel.add(updateButton);

        // Remove dinosaur by entering its name
        JButton removeButton = createButton("Remove Dinosaur");
        removeButton.addActionListener(e -> removeDinosaur());
        actionPanel.add(removeButton);

        // Search dinosaur records using the selected attribute and value
        JButton searchButton = createButton("Search / Filter");
        searchButton.addActionListener(e -> searchDinosaurs());
        actionPanel.add(searchButton);

        // Exit application
        JButton exitButton = createButton("Exit");
        exitButton.setBackground(new Color(130, 55, 48));
        exitButton.addActionListener(e -> frame.dispose());

        actionPanel.add(exitButton);

        return actionPanel;
    }

    /** Creates consistently styled application buttons
     *
     * @param text the button label
     * @return the styled button
     */
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(mossGreen);
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(12, 10, 12, 10)
        );
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /** Creates the panel containing the dinosaur records table
     *
     * @return the records panel
     */
    private JPanel createRecordsPanel() {

        JPanel recordsPanel = new JPanel(new BorderLayout(5, 10));
        recordsPanel.setBackground(Color.WHITE);
        recordsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(190, 200, 190)
                        ),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel recordsTitle = new JLabel("Dinosaur Records");
        recordsTitle.setFont(new Font("Arial", Font.BOLD, 20));
        recordsTitle.setForeground(forestGreen);

        // Six required dinosaur attributes
        String[] columnNames = {
                "Name",
                "Time Period",
                "Diet",
                "Clade",
                "Fossil Location",
                "Size"
        };

        // Prevent users from editing table cells directly
        tableModel = new DefaultTableModel(columnNames, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dinosaurTable = new JTable(tableModel);
        dinosaurTable.setFont(new Font("Arial", Font.PLAIN, 13));
        dinosaurTable.setRowHeight(28);
        dinosaurTable.setSelectionBackground(lightSage);
        dinosaurTable.setSelectionForeground(charcoal);
        dinosaurTable.setGridColor(new Color(220, 225, 220));

        dinosaurTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        dinosaurTable.getTableHeader().setBackground(forestGreen);
        dinosaurTable.getTableHeader().setForeground(Color.WHITE);
        dinosaurTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(dinosaurTable);

        recordsPanel.add(recordsTitle, BorderLayout.NORTH);
        recordsPanel.add(scrollPane, BorderLayout.CENTER);

        return recordsPanel;
    }

    /** Opens a form that allows users to manually add a dinosaur
     * Validates the information before adding the record
     */
    private void addDinosaur() {

        // Create input fields for the six dinosaur attributes
        JTextField nameField = new JTextField();
        JTextField timePeriodField = new JTextField();
        JTextField dietField = new JTextField();
        JTextField cladeField = new JTextField();
        JTextField fossilLocationField = new JTextField();
        JTextField sizeField = new JTextField();

        // Create the input form
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));

        inputPanel.add(new JLabel("Dinosaur Name:"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Time Period:"));
        inputPanel.add(timePeriodField);

        inputPanel.add(new JLabel("Diet:"));
        inputPanel.add(dietField);

        inputPanel.add(new JLabel("Clade:"));
        inputPanel.add(cladeField);

        inputPanel.add(new JLabel("Fossil Location:"));
        inputPanel.add(fossilLocationField);

        inputPanel.add(new JLabel("Size:"));
        inputPanel.add(sizeField);

        // Display the form
        int result = JOptionPane.showConfirmDialog(
                frame,
                inputPanel,
                "Add New Dinosaur",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Stop if the user cancels
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Read the user's input
        String name = nameField.getText().trim();
        String timePeriod = timePeriodField.getText().trim();
        String diet = dietField.getText().trim();
        String clade = cladeField.getText().trim();
        String fossilLocation = fossilLocationField.getText().trim();

        // Convert the size into a numeric value
        double size;

        try {
            size = Double.parseDouble(sizeField.getText().trim());

        } catch (NumberFormatException exception) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Size must be a valid number.",
                    "Invalid Size",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Create a new dinosaur object
        Dinosaur newDinosaur = new Dinosaur(
                name,
                timePeriod,
                diet,
                clade,
                fossilLocation,
                size
        );

        // Validate and add the dinosaur through the manager
        if (manager.addDinosaur(newDinosaur)) {

            // Refresh the table with the updated collection
            refreshTable(manager.getAllDinosaurs());

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur successfully added!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            statusLabel.setText(
                    "Dinosaur added successfully. Total records: "
                            + manager.getDinosaurCount()
            );

        } else {

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur could not be added.\n\n"
                            + "Please check that:\n"
                            + "- All fields contain information.\n"
                            + "- The dinosaur name is unique.\n"
                            + "- Size is a positive, finite number.",
                    "Invalid Dinosaur Information",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Removes a dinosaur by asking the user to enter its name
     * Requests confirmation before deleting the record
     */
    private void removeDinosaur() {

        // Ask the user for the dinosaur's name
        String name = JOptionPane.showInputDialog(
                frame,
                "Enter the name of the dinosaur you want to remove:",
                "Remove Dinosaur",
                JOptionPane.QUESTION_MESSAGE
        );

        // Stop if the user cancels
        if (name == null) {
            return;
        }

        // Reject blank input
        if (name.isBlank()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur name cannot be blank.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        name = name.trim();

        // Check whether the dinosaur exists
        Dinosaur dinosaur = manager.findDinosaur(name);

        if (dinosaur == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "No dinosaur was found with the name: " + name,
                    "Dinosaur Not Found",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Ask the user to confirm the deletion
        int confirmation = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to remove "
                        + dinosaur.getName() + "?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // Only remove the dinosaur if the user confirms
        if (confirmation == JOptionPane.YES_OPTION) {

            if (manager.removeDinosaur(name)) {

                // Refresh the table after deletion
                refreshTable(manager.getAllDinosaurs());

                JOptionPane.showMessageDialog(
                        frame,
                        dinosaur.getName() + " was successfully removed.",
                        "Removal Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                statusLabel.setText(
                        "Dinosaur removed. Total records: "
                                + manager.getDinosaurCount()
                );
            }
        }
    }

    // Searches for dinosaur records using a selected attribute and value
    private void searchDinosaurs() {

        // Available attributes users can search
        String[] attributes = {
                "Name",
                "Time Period",
                "Diet",
                "Clade",
                "Fossil Location",
                "Size"
        };

        // Let the user choose an attribute
        JComboBox<String> attributeBox =
                new JComboBox<>(attributes);

        // Field where the user enters a search value
        JTextField searchField = new JTextField(15);

        // Arrange the search controls
        JPanel searchPanel = new JPanel(new GridLayout(2, 2, 8, 8));

        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(attributeBox);

        searchPanel.add(new JLabel("Search value:"));
        searchPanel.add(searchField);

        // Display the search dialog
        int result = JOptionPane.showConfirmDialog(
                frame,
                searchPanel,
                "Search Dinosaur Records",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Stop if the user cancels
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedAttribute =
                (String) attributeBox.getSelectedItem();

        String searchValue = searchField.getText().trim();

        // Validate the search value
        if (searchValue.isBlank()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a value to search for.",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Convert the selected attribute to the manager's expected format
        String attribute = selectedAttribute.toLowerCase()
                .replace(" ", "");

        // Validate numeric searches
        if (attribute.equals("size")) {

            try {
                double size = Double.parseDouble(searchValue);

                if (!Double.isFinite(size) || size <= 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException exception) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a valid positive number for size.",
                        "Invalid Search Value",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }
        }

        // Perform the search using the existing manager method
        List<Dinosaur> results =
                manager.searchDinosaurs(attribute, searchValue);

        // Display matching records
        refreshTable(results);

        // Provide feedback
        if (results.isEmpty()) {

            statusLabel.setText("No matching dinosaur records found.");

            JOptionPane.showMessageDialog(
                    frame,
                    "No dinosaur records matched your search.",
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            statusLabel.setText(
                    "Found " + results.size() + " matching dinosaur records."
            );
        }
    }

    /** Updates an existing dinosaur using a form containing its current values
     * Allows users to change one or more of the six attributes
     */
    private void updateDinosaur() {

        // Ask the user which dinosaur they want to update
        String name = JOptionPane.showInputDialog(
                frame,
                "Enter the name of the dinosaur you want to update:",
                "Update Dinosaur",
                JOptionPane.QUESTION_MESSAGE
        );

        // Stop if the user cancels
        if (name == null) {
            return;
        }

        // Reject blank input
        if (name.isBlank()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur name cannot be blank.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        name = name.trim();

        // Find the existing dinosaur
        Dinosaur existingDinosaur = manager.findDinosaur(name);

        if (existingDinosaur == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "No dinosaur was found with the name: " + name,
                    "Dinosaur Not Found",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Create input fields containing the current dinosaur information
        JTextField nameField =
                new JTextField(existingDinosaur.getName());

        JTextField timePeriodField =
                new JTextField(existingDinosaur.getTimePeriod());

        JTextField dietField =
                new JTextField(existingDinosaur.getDiet());

        JTextField cladeField =
                new JTextField(existingDinosaur.getClade());

        JTextField fossilLocationField =
                new JTextField(existingDinosaur.getFossilLocation());

        JTextField sizeField =
                new JTextField(String.valueOf(existingDinosaur.getSize()));

        // Create the update form
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));

        inputPanel.add(new JLabel("Dinosaur Name:"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Time Period:"));
        inputPanel.add(timePeriodField);

        inputPanel.add(new JLabel("Diet:"));
        inputPanel.add(dietField);

        inputPanel.add(new JLabel("Clade:"));
        inputPanel.add(cladeField);

        inputPanel.add(new JLabel("Fossil Location:"));
        inputPanel.add(fossilLocationField);

        inputPanel.add(new JLabel("Size:"));
        inputPanel.add(sizeField);

        // Display the form
        int result = JOptionPane.showConfirmDialog(
                frame,
                inputPanel,
                "Update Dinosaur Information",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Stop if the user cancels
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Read the updated information
        String updatedName = nameField.getText().trim();
        String updatedTimePeriod = timePeriodField.getText().trim();
        String updatedDiet = dietField.getText().trim();
        String updatedClade = cladeField.getText().trim();
        String updatedFossilLocation =
                fossilLocationField.getText().trim();

        // Convert the updated size into a numeric value
        double updatedSize;

        try {

            updatedSize = Double.parseDouble(
                    sizeField.getText().trim()
            );

        } catch (NumberFormatException exception) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Size must be a valid number.",
                    "Invalid Size",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Create a replacement dinosaur with the updated information
        Dinosaur updatedDinosaur = new Dinosaur(
                updatedName,
                updatedTimePeriod,
                updatedDiet,
                updatedClade,
                updatedFossilLocation,
                updatedSize
        );

        // Update the existing record through DinosaurManager
        if (manager.updateDinosaur(name, updatedDinosaur)) {

            // Refresh the table with the updated information
            refreshTable(manager.getAllDinosaurs());

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur information was successfully updated!",
                    "Update Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            statusLabel.setText(
                    "Dinosaur updated successfully. Total records: "
                            + manager.getDinosaurCount()
            );

        } else {

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur information could not be updated.\n\n"
                            + "Please check that:\n"
                            + "- All fields contain information.\n"
                            + "- The dinosaur name is unique.\n"
                            + "- Size is a positive, finite number.",
                    "Invalid Dinosaur Information",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Opens a file chooser and loads dinosaur records
    private void loadDinosaurFile() {

        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {

            String filePath = fileChooser.getSelectedFile()
                    .getAbsolutePath();

            // Read records using the existing file reader
            List<Dinosaur> loadedDinosaurs =
                    fileReader.loadDinosaurs(filePath);

            if (loadedDinosaurs.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "No valid dinosaur records were loaded. "
                                + "Check the file and its contents.",
                        "File Loading Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            int addedCount = 0;

            // Add records through the existing manager
            for (Dinosaur dinosaur : loadedDinosaurs) {

                if (manager.addDinosaur(dinosaur)) {
                    addedCount++;
                }
            }

            // Refresh the table with the current records
            refreshTable(manager.getAllDinosaurs());

            statusLabel.setText(
                    "Successfully loaded " + addedCount
                            + " dinosaur records."
            );

            JOptionPane.showMessageDialog(
                    frame,
                    addedCount + " dinosaur records were successfully loaded.",
                    "File Loading Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /** Refreshes the table with the current dinosaur records
     *
     * @param dinosaurs the records to display
     */
    private void refreshTable(List<Dinosaur> dinosaurs) {

        // Remove previously displayed records
        tableModel.setRowCount(0);

        // Add the current records to the table
        for (Dinosaur dinosaur : dinosaurs) {

            Object[] row = {
                    dinosaur.getName(),
                    dinosaur.getTimePeriod(),
                    dinosaur.getDiet(),
                    dinosaur.getClade(),
                    dinosaur.getFossilLocation(),
                    dinosaur.getSize()
            };

            tableModel.addRow(row);
        }

        statusLabel.setText(
                "Currently displaying " + dinosaurs.size()
                        + " dinosaur records."
        );
    }

    /** Creates the bottom status bar
     *
     * @return the status panel
     */
    private JPanel createStatusPanel() {

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(lightSage);

        statusLabel = new JLabel(
                "Ready | Dinosaur Data Management System"
        );

        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(charcoal);

        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }


}
