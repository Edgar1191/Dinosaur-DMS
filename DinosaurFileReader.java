
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Edgar Lopez
 * CEN 3024C - Software Development 1
 * October 19, 2026
 * DinosaurFileReader.java
 * Reads dinosaur records from a text file
 *
 * Expected format for each line:
 * name|timePeriod|diet|clade|fossilLocation|size
 */
public class DinosaurFileReader {

    /** Loads valid dinosaur records from the specified file
     * Invalid lines are skipped rather than crashing the program
     *
     * @param filePath the path to the text file
     * @return a list of valid dinosaur records, or an empty list
     */
    public List<Dinosaur> loadDinosaurs(String filePath) {
        List<Dinosaur> loadedDinosaurs = new ArrayList<>();

        if (filePath == null || filePath.isBlank()) {
            return loadedDinosaurs;
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(Path.of(filePath.trim()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                Dinosaur dinosaur = parseDinosaur(line);

                if (dinosaur != null
                        && isUniqueName(loadedDinosaurs, dinosaur.getName())) {
                    loadedDinosaurs.add(dinosaur);
                }
            }

        } catch (IOException | SecurityException exception) {
            // The caller can detect a failed load by receiving an empty list
            // Detailed error messages will be handled by the CLI
        } catch (RuntimeException exception) {
            // Handles invalid path syntax or other malformed file input
        }

        return loadedDinosaurs;
    }

    /** Converts one line of text into a dinosaur object
     *
     * @param line one line from the input file
     * @return a valid dinosaur, or null if the line is invalid
     */
    private Dinosaur parseDinosaur(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] fields = line.split("\\|", -1);

        if (fields.length != 6) {
            return null;
        }

        String name = fields[0].trim();
        String timePeriod = fields[1].trim();
        String diet = fields[2].trim();
        String clade = fields[3].trim();
        String fossilLocation = fields[4].trim();

        double size;

        try {
            size = Double.parseDouble(fields[5].trim());
        } catch (NumberFormatException exception) {
            return null;
        }

        Dinosaur dinosaur = new Dinosaur(
                name, timePeriod, diet, clade, fossilLocation, size);

        return isValidDinosaur(dinosaur) ? dinosaur : null;
    }

    /** Checks required text fields and the numeric size
     *
     * @param dinosaur the record to validate
     * @return true if the record contains valid text fields and a positive and finite size; otherwise false
     */
    private boolean isValidDinosaur(Dinosaur dinosaur) {
        return dinosaur != null
                && isValidText(dinosaur.getName())
                && isValidText(dinosaur.getTimePeriod())
                && isValidText(dinosaur.getDiet())
                && isValidText(dinosaur.getClade())
                && isValidText(dinosaur.getFossilLocation())
                && Double.isFinite(dinosaur.getSize())
                && dinosaur.getSize() > 0;
    }

    /** Checks whether a text field contains nonblank text
     *
     * @param value the text to check
     * @return true if the value is not null and not blank; otherwise false
     */
    private boolean isValidText(String value) {
        return value != null && !value.isBlank();
    }

    /** Prevents duplicate names in the records read from the file
     *
     * @param dinosaurs records already loaded
     * @param name the proposed name
     * @return true if the name is unique
     */
    private boolean isUniqueName(List<Dinosaur> dinosaurs, String name) {
        for (Dinosaur dinosaur : dinosaurs) {
            if (dinosaur.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        return true;
    }
}
