
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/** Provides the graphical user interface for the Dinosaur Data Management System
 * Allows users to interact with dinosaur records through a Swing application
 */
public class DinosaurGUI {

    // Main application window
    private JFrame frame;

    // Provides validation, file importing, and database access
    private final DinosaurManager manager;
    private final DinosaurFileReader fileReader;
    private final DinosaurDatabase database;

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

    /** Creates the GUI using the manager, file reader, and database
     *
     * @param manager the dinosaur manager
     * @param fileReader the dinosaur file reader
     * @param database the SQLite database connection
     */
    public DinosaurGUI(
            DinosaurManager manager,
            DinosaurFileReader fileReader,
            DinosaurDatabase database) {

        this.manager = manager;
        this.fileReader = fileReader;
        this.database = database;

        createWindow();
    }

    // Creates the main application window and its components
    private void createWindow() {

        frame = new JFrame("Dinosaur Data Management System");

        frame.setSize(1150, 750);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {

                try {

                    database.close();
                    frame.dispose();

                } catch (java.sql.SQLException exception) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Unable to close the database connection:\n"
                                    + exception.getMessage(),
                            "Database Closing Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
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

        // Display all dinosaur records from the database
        JButton displayButton = createButton("Display All");
        displayButton.addActionListener(e ->
                refreshTable(database.getAllDinosaurs()));
        actionPanel.add(displayButton);

        // Open the form to add a dinosaur
        JButton addButton = createButton("Add Dinosaur");
        addButton.addActionListener(e -> addDinosaur());
        actionPanel.add(addButton);

        // Open the form to update a dinosaur
        JButton updateButton = createButton("Update Dinosaur");
        updateButton.addActionListener(e -> updateDinosaur());
        actionPanel.add(updateButton);

        // Remove the dinosaur selected in the records table
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
        exitButton.addActionListener(e -> {

            try {

                database.close();
                frame.dispose();

            } catch (java.sql.SQLException exception) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Unable to close the database connection:\n"
                                + exception.getMessage(),
                        "Database Closing Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

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
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
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

        // Configure the table to highlight complete selected rows
        dinosaurTable = new JTable(tableModel) {

            @Override
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer,
                    int row,
                    int column) {

                Component component =
                        super.prepareRenderer(renderer, row, column);

                // Apply the same selection colors to every cell in the row
                if (isRowSelected(row)) {
                    component.setBackground(mossGreen);
                    component.setForeground(Color.WHITE);
                } else {
                    component.setBackground(Color.WHITE);
                    component.setForeground(charcoal);
                }

                return component;
            }
        };

        dinosaurTable.setFont(new Font("Arial", Font.PLAIN, 13));
        dinosaurTable.setRowHeight(28);

        // Allow users to select one complete dinosaur record
        dinosaurTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        dinosaurTable.setRowSelectionAllowed(true);
        dinosaurTable.setColumnSelectionAllowed(false);
        dinosaurTable.setCellSelectionEnabled(false);

        // Set the selected row colors
        dinosaurTable.setSelectionBackground(mossGreen);
        dinosaurTable.setSelectionForeground(Color.WHITE);

        dinosaurTable.setGridColor(new Color(220, 225, 220));

        // Configure table headers
        dinosaurTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        dinosaurTable.getTableHeader().setBackground(forestGreen);
        dinosaurTable.getTableHeader().setForeground(Color.WHITE);
        dinosaurTable.getTableHeader().setReorderingAllowed(false);

        // Place the table inside a scrollable panel
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

        // Validate and add the dinosaur through the database
        if (manager.validateDinosaur(newDinosaur)
                && database.addDinosaur(newDinosaur)) {

            // Refresh the table with the updated collection
            refreshTable(database.getAllDinosaurs());

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur successfully added!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            statusLabel.setText(
                    "Dinosaur added successfully. Total records: "
                            + database.getDinosaurCount()
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


    /** Removes the dinosaur record selected in the table
     * Requests confirmation before deleting the record
     */
    private void removeDinosaur() {

        // Check whether a dinosaur row has been selected
        int selectedRow = dinosaurTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please select a dinosaur from the table first.",
                    "No Dinosaur Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get the selected dinosaur's name from the table
        int modelRow = dinosaurTable.convertRowIndexToModel(selectedRow);

        String name = tableModel.getValueAt(modelRow, 0).toString();

        // Find the selected dinosaur
        Dinosaur dinosaur = database.getAllDinosaurs()
                .stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (dinosaur == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "The selected dinosaur could not be found.",
                    "Removal Error",
                    JOptionPane.ERROR_MESSAGE
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

            if (database.removeDinosaur(name)) {

                // Refresh the table after deletion
                refreshTable(database.getAllDinosaurs());

                JOptionPane.showMessageDialog(
                        frame,
                        dinosaur.getName() + " was successfully removed.",
                        "Removal Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                statusLabel.setText(
                        "Dinosaur removed. Total records: "
                                + database.getDinosaurCount()
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

        // Perform the search using the database
        List<Dinosaur> results =
                database.searchDinosaurs(attribute, searchValue);

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


    // Updates the dinosaur record selected in the table
    private void updateDinosaur() {

        // Check whether a dinosaur row has been selected
        int selectedRow = dinosaurTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please select a dinosaur from the table first.",
                    "No Dinosaur Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get the selected dinosaur's name from the table
        int modelRow = dinosaurTable.convertRowIndexToModel(selectedRow);

        String name = tableModel.getValueAt(modelRow, 0).toString();

        Dinosaur existingDinosaur = database.getAllDinosaurs()
                .stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (existingDinosaur == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "The selected dinosaur could not be found.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Create fields containing the existing dinosaur information
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

        // Arrange the input fields in a form
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));

        inputPanel.add(new JLabel("Name:"));
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

        // Display the update form
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

        double updatedSize;

        // Validate the size input
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

        // Create the updated dinosaur object
        Dinosaur updatedDinosaur = new Dinosaur(
                updatedName,
                updatedTimePeriod,
                updatedDiet,
                updatedClade,
                updatedFossilLocation,
                updatedSize
        );

        // Save the changes through the manager
        if (manager.validateDinosaur(updatedDinosaur)
                && database.updateDinosaur(name, updatedDinosaur)) {

            refreshTable(database.getAllDinosaurs());

            JOptionPane.showMessageDialog(
                    frame,
                    "Dinosaur information updated successfully.",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    frame,
                    "The update could not be completed. "
                            + "Check that all fields contain valid information, "
                            + "the name is unique, and the size is positive.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }



    /**
     * Opens a file chooser and imports dinosaur records from a text file into the SQLite database
     */
    private void loadDinosaurFile() {

        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showOpenDialog(frame);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String filePath = fileChooser.getSelectedFile()
                .getAbsolutePath();

        // Read records from the selected text file
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

        // Validate and save each record to SQLite
        for (Dinosaur dinosaur : loadedDinosaurs) {

            if (manager.validateDinosaur(dinosaur)
                    && database.addDinosaur(dinosaur)) {

                addedCount++;
            }
        }

        // Refresh the table using database records
        refreshTable(database.getAllDinosaurs());

        statusLabel.setText(
                "Successfully imported " + addedCount
                        + " dinosaur records."
        );

        JOptionPane.showMessageDialog(
                frame,
                addedCount + " dinosaur records were successfully "
                        + "saved to the database.",
                "File Loading Complete",
                JOptionPane.INFORMATION_MESSAGE
        );
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
