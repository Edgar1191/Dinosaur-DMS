
import java.util.ArrayList;
import java.util.List;

/** Manages the dinosaur collection and provides operations for creating, reading, updating, deleting,
 * and searching records
 */
public class DinosaurManager {

    // Stores dinosaur records in memory for Phase 1
    private final ArrayList<Dinosaur> dinosaurs;

    //Creates an empty dinosaur collection
    public DinosaurManager() {
        dinosaurs = new ArrayList<>();
    }

    /** Checks whether a dinosaur contains valid attribute values
     *
     * @param dinosaur the dinosaur to validate
     * @return true if all attributes are valid; otherwise false
     */
    public boolean validateDinosaur(Dinosaur dinosaur) {
        return dinosaur != null
                && isValidText(dinosaur.getName())
                && isValidText(dinosaur.getTimePeriod())
                && isValidText(dinosaur.getDiet())
                && isValidText(dinosaur.getClade())
                && isValidText(dinosaur.getFossilLocation())
                && Double.isFinite(dinosaur.getSize())
                && dinosaur.getSize() > 0;
    }

    /** Checks whether a text value is present and not blank
     *
     * @param value the text to check
     * @return true if the value is not null and not blank; otherwise false
     */
    private boolean isValidText(String value) {
        return value != null && !value.isBlank();
    }

    /** Adds a valid dinosaur if its name is not already in use
     *
     * @param dinosaur the dinosaur to add
     * @return true if added; otherwise false
     */
    public boolean addDinosaur(Dinosaur dinosaur) {
        if (!validateDinosaur(dinosaur)
                || findDinosaur(dinosaur.getName()) != null) {
            return false;
        }

        dinosaurs.add(dinosaur);
        return true;
    }

    /** Returns a copy of the current dinosaur collection
     *
     * @return a new list containing the stored dinosaur references
     */
    public List<Dinosaur> getAllDinosaurs() {
        return new ArrayList<>(dinosaurs);
    }

    /** Finds a dinosaur by its unique name
     *
     * @param name the name to find
     * @return the matching dinosaur, or null if not found
     */
    public Dinosaur findDinosaur(String name) {
        if (!isValidText(name)) {
            return null;
        }

        for (Dinosaur dinosaur : dinosaurs) {
            if (dinosaur.getName().equalsIgnoreCase(name.trim())) {
                return dinosaur;
            }
        }

        return null;
    }

    /** Removes a dinosaur by name
     *
     * @param name the name of the dinosaur to remove
     * @return true if removed; otherwise false
     */
    public boolean removeDinosaur(String name) {
        Dinosaur dinosaur = findDinosaur(name);

        if (dinosaur == null) {
            return false;
        }

        return dinosaurs.remove(dinosaur);
    }

    /** Updates a dinosaur while preserving the original record if the replacement data is invalid
     *
     * @param name the existing dinosaur's name
     * @param updatedDinosaur the replacement dinosaur data
     * @return true if updated; otherwise false
     */
    public boolean updateDinosaur(
            String name, Dinosaur updatedDinosaur) {

        Dinosaur existingDinosaur = findDinosaur(name);

        if (existingDinosaur == null
                || !validateDinosaur(updatedDinosaur)) {
            return false;
        }

        // A name must remain unique, even after an update.
        Dinosaur duplicate = findDinosaur(updatedDinosaur.getName());

        if (duplicate != null && duplicate != existingDinosaur) {
            return false;
        }

        // Update only after all validation checks have passed.
        existingDinosaur.setName(updatedDinosaur.getName());
        existingDinosaur.setTimePeriod(updatedDinosaur.getTimePeriod());
        existingDinosaur.setDiet(updatedDinosaur.getDiet());
        existingDinosaur.setClade(updatedDinosaur.getClade());
        existingDinosaur.setFossilLocation(
                updatedDinosaur.getFossilLocation());
        existingDinosaur.setSize(updatedDinosaur.getSize());

        return true;
    }

    /** Searches the collection using a supported attribute
     * Text searches ignore capitalization; size searches use an exact numeric comparison
     *
     * @param attribute the attribute to search
     * @param value the value to match
     * @return matching dinosaurs, or an empty list for invalid criteria
     */
    public List<Dinosaur> searchDinosaurs(
            String attribute, String value) {

        List<Dinosaur> results = new ArrayList<>();

        if (!isValidText(attribute) || !isValidText(value)) {
            return results;
        }

        String searchAttribute = attribute.trim().toLowerCase();
        String searchValue = value.trim();

        // Reject unsupported attributes and invalid size searches.
        if (!isSupportedAttribute(searchAttribute)) {
            return results;
        }

        double requestedSize = 0;

        if (searchAttribute.equals("size")) {
            try {
                requestedSize = Double.parseDouble(searchValue);

                if (!Double.isFinite(requestedSize)
                        || requestedSize <= 0) {
                    return results;
                }
            } catch (NumberFormatException exception) {
                return results;
            }
        }

        for (Dinosaur dinosaur : dinosaurs) {
            boolean matches = switch (searchAttribute) {
                case "name" ->
                        dinosaur.getName().equalsIgnoreCase(searchValue);
                case "timeperiod" ->
                        dinosaur.getTimePeriod().equalsIgnoreCase(searchValue);
                case "diet" ->
                        dinosaur.getDiet().equalsIgnoreCase(searchValue);
                case "clade" ->
                        dinosaur.getClade().equalsIgnoreCase(searchValue);
                case "fossillocation" ->
                        dinosaur.getFossilLocation()
                                .equalsIgnoreCase(searchValue);
                case "size" ->
                        Double.compare(dinosaur.getSize(), requestedSize) == 0;
                default -> false;
            };

            if (matches) {
                results.add(dinosaur);
            }
        }

        return results;
    }

    /** Checks whether a search attribute is supported
     *
     * @param attribute the normalized attribute name
     * @return true if supported; otherwise false
     */
    private boolean isSupportedAttribute(String attribute) {
        return attribute.equals("name")
                || attribute.equals("timeperiod")
                || attribute.equals("diet")
                || attribute.equals("clade")
                || attribute.equals("fossillocation")
                || attribute.equals("size");
    }

    /** Returns the number of dinosaur records
     *
     * @return the current collection size
     */
    public int getDinosaurCount() {
        return dinosaurs.size();
    }
}
