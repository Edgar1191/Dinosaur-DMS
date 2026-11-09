
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** Manages the SQLite database for the Dinosaur Data Management System
 * Handles database connections and dinosaur record operations, including adding, retrieving, searching, updating,
 * and removing records
 */
public class DinosaurDatabase implements AutoCloseable{

    private Connection connection;

    /** Connects to the SQLite database at the supplied file path
     *
     * @param databasePath the location of the SQLite database file
     * @throws SQLException if the database connection fails
     */
    public DinosaurDatabase(String databasePath) throws SQLException {

        // Establish the SQLite database connection
        String url = "jdbc:sqlite:" + databasePath;

        connection = DriverManager.getConnection(url);

        // Create the dinosaurs table if it does not already exist
        createTable();
    }

    /** Creates the dinosaurs table if it does not already exist
     *
     * @throws SQLException if the table cannot be created
     */
    private void createTable() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS dinosaurs (
                    name TEXT PRIMARY KEY COLLATE NOCASE,
                    time_period TEXT NOT NULL,
                    diet TEXT NOT NULL,
                    clade TEXT NOT NULL,
                    fossil_location TEXT NOT NULL,
                    size REAL NOT NULL CHECK(size > 0)
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /** Checks whether the database connection is active
     *
     * @return true if the connection is open, otherwise false
     */
    public boolean isConnected() {

        try {
            return connection != null && !connection.isClosed();

        } catch (SQLException exception) {
            return false;
        }
    }

    /** Closes the database connection
     *
     * @throws SQLException if the connection cannot be closed
     */
    @Override
    public void close() throws SQLException {

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /** Adds a dinosaur record to the database
     *
     * @param dinosaur the dinosaur to add
     * @return true if the record was added successfully
     */
    public boolean addDinosaur(Dinosaur dinosaur) {

        String sql = """
            INSERT INTO dinosaurs
            (name, time_period, diet, clade, fossil_location, size)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, dinosaur.getName());
            statement.setString(2, dinosaur.getTimePeriod());
            statement.setString(3, dinosaur.getDiet());
            statement.setString(4, dinosaur.getClade());
            statement.setString(5, dinosaur.getFossilLocation());
            statement.setDouble(6, dinosaur.getSize());

            statement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            System.out.println("Error adding dinosaur: "
                    + exception.getMessage());
            return false;
        }
    }

    /** Retrieves all dinosaur records from the database
     *
     * @return a list containing all dinosaurs
     */
    public List<Dinosaur> getAllDinosaurs() {

        List<Dinosaur> dinosaurs = new ArrayList<>();

        String sql = "SELECT * FROM dinosaurs ORDER BY name";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {

                Dinosaur dinosaur = new Dinosaur(
                        results.getString("name"),
                        results.getString("time_period"),
                        results.getString("diet"),
                        results.getString("clade"),
                        results.getString("fossil_location"),
                        results.getDouble("size")
                );

                dinosaurs.add(dinosaur);
            }

        } catch (SQLException exception) {
            System.out.println("Error retrieving dinosaurs: "
                    + exception.getMessage());
        }

        return dinosaurs;
    }


    /** Searches dinosaur records using an exact attribute match
     * Text searches ignore capitalization, while size searches use an exact numeric comparison
     *
     * @param attribute the attribute to search
     * @param value the value to search for
     * @return a list of matching dinosaurs
     */
    public List<Dinosaur> searchDinosaurs(
            String attribute, String value) {

        List<Dinosaur> dinosaurs = new ArrayList<>();

        // Validate the search information
        if (attribute == null || value == null
                || attribute.isBlank() || value.isBlank()) {
            return dinosaurs;
        }

        // Only allow recognized database columns
        String column;

        switch (attribute.trim().toLowerCase()) {
            case "name":
                column = "name";
                break;
            case "timeperiod":
                column = "time_period";
                break;
            case "diet":
                column = "diet";
                break;
            case "clade":
                column = "clade";
                break;
            case "fossillocation":
                column = "fossil_location";
                break;
            case "size":
                column = "size";
                break;
            default:
                return dinosaurs;
        }

        // Ignore capitalization for text searches
        String sql;

        if (column.equals("size")) {
            sql = "SELECT * FROM dinosaurs WHERE size = ? ORDER BY name";
        } else {
            sql = "SELECT * FROM dinosaurs WHERE "
                    + column + " = ? COLLATE NOCASE ORDER BY name";
        }

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (column.equals("size")) {

                double requestedSize = Double.parseDouble(value.trim());

                if (!Double.isFinite(requestedSize)
                        || requestedSize <= 0) {
                    return dinosaurs;
                }

                statement.setDouble(1, requestedSize);

            } else {
                statement.setString(1, value.trim());
            }

            try (ResultSet results = statement.executeQuery()) {

                while (results.next()) {

                    dinosaurs.add(new Dinosaur(
                            results.getString("name"),
                            results.getString("time_period"),
                            results.getString("diet"),
                            results.getString("clade"),
                            results.getString("fossil_location"),
                            results.getDouble("size")
                    ));
                }
            }

        } catch (SQLException | NumberFormatException exception) {

            System.out.println("Error searching dinosaurs: "
                    + exception.getMessage());
        }

        return dinosaurs;
    }


    /** Updates an existing dinosaur record
     *
     * @param originalName the current dinosaur name
     * @param dinosaur the updated dinosaur information
     * @return true if a record was updated
     */
    public boolean updateDinosaur(
            String originalName, Dinosaur dinosaur) {

        String sql = """
            UPDATE dinosaurs
            SET name = ?,
                time_period = ?,
                diet = ?,
                clade = ?,
                fossil_location = ?,
                size = ?
            WHERE name = ?
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, dinosaur.getName());
            statement.setString(2, dinosaur.getTimePeriod());
            statement.setString(3, dinosaur.getDiet());
            statement.setString(4, dinosaur.getClade());
            statement.setString(5, dinosaur.getFossilLocation());
            statement.setDouble(6, dinosaur.getSize());
            statement.setString(7, originalName);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error updating dinosaur: "
                    + exception.getMessage());
            return false;
        }
    }

    /** Removes a dinosaur record from the database
     *
     * @param name the name of the dinosaur to remove
     * @return true if a record was removed
     */
    public boolean removeDinosaur(String name) {

        String sql = "DELETE FROM dinosaurs WHERE name = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error removing dinosaur: "
                    + exception.getMessage());
            return false;
        }
    }

    /** Returns the total number of dinosaur records
     *
     * @return the number of records in the database
     */
    public int getDinosaurCount() {

        String sql = "SELECT COUNT(*) FROM dinosaurs";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            if (results.next()) {
                return results.getInt(1);
            }

        } catch (SQLException exception) {
            System.out.println("Error counting dinosaurs: "
                    + exception.getMessage());
        }

        return 0;
    }

}
