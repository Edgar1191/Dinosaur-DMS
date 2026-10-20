
/** Edgar Lopez
 * CEN 3024C - Software Development 1
 * October 19, 2026
 * Dinosaur.java
 * Represents a dinosaur in the Dinosaur Data Management System
 * Stores the dinosaur's identifying information and physical attributes
 */
public class Dinosaur {

    // Stores the dinosaur's name, must be unique
    private String name;

    // Stores the geological time period in which the dinosaur lived
    private String timePeriod;

    // Stores the dinosaur's diet
    private String diet;

    // Stores the dinosaur's scientific clade
    private String clade;

    // Stores the location where the dinosaur's fossils were found
    private String fossilLocation;

    // Stores the dinosaur's size as a positive numeric value
    private double size;

    /** Creates a dinosaur using the six required attributes
     *
     * @param name the dinosaur's unique name
     * @param timePeriod the geological time period
     * @param diet the dinosaur's diet
     * @param clade the dinosaur's clade
     * @param fossilLocation the fossil discovery location
     * @param size the dinosaur's size
     */
    public Dinosaur(String name, String timePeriod, String diet,
                    String clade, String fossilLocation, double size) {
        this.name = name;
        this.timePeriod = timePeriod;
        this.diet = diet;
        this.clade = clade;
        this.fossilLocation = fossilLocation;
        this.size = size;
    }

    /** Gets the dinosaur's name
     *
     * @return the dinosaur's name
     */
    public String getName() {
        return name;
    }

    /** Gets the dinosaur's geological time period
     *
     * @return the dinosaur's time period
     */
    public String getTimePeriod() {
        return timePeriod;
    }

    /** Gets the dinosaur's diet
     *
     * @return the dinosaur's diet
     */
    public String getDiet() {
        return diet;
    }

    /** Gets the dinosaur's clade
     *
     * @return the dinosaur's clade
     */
    public String getClade() {
        return clade;
    }

    /** Gets the location where the dinosaur's fossils were found
     *
     * @return the fossil discovery location
     */
    public String getFossilLocation() {
        return fossilLocation;
    }

    /** Gets the dinosaur's size
     *
     * @return the dinosaur's size
     */
    public double getSize() {
        return size;
    }

    /** Sets the dinosaur's name
     *
     * @param name the new dinosaur name
     * @return the updated dinosaur name
     */
    public String setName(String name) {
        this.name = name;
        return this.name;
    }

    /** Sets the dinosaur's geological time period
     *
     * @param timePeriod the new time period
     * @return the updated time period
     */
    public String setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
        return this.timePeriod;
    }

    /** Sets the dinosaur's diet
     *
     * @param diet the new diet
     * @return the updated diet
     */
    public String setDiet(String diet) {
        this.diet = diet;
        return this.diet;
    }

    /** Sets the dinosaur's clade
     *
     * @param clade the new clade
     * @return the updated clade
     */
    public String setClade(String clade) {
        this.clade = clade;
        return this.clade;
    }

    /** Sets the dinosaur's fossil discovery location
     *
     * @param fossilLocation the new fossil location
     * @return the updated fossil location
     */
    public String setFossilLocation(String fossilLocation) {
        this.fossilLocation = fossilLocation;
        return this.fossilLocation;
    }

    /** Sets the dinosaur's size
     *
     * @param size the new dinosaur size
     * @return the updated dinosaur size
     */
    public double setSize(double size) {
        this.size = size;
        return this.size;
    }

    /** Returns a readable representation of the dinosaur
     *
     * @return the dinosaur's attributes formatted as text
     */
    @Override
    public String toString() {
        return "Name: " + name
                + ", Time Period: " + timePeriod
                + ", Diet: " + diet
                + ", Clade: " + clade
                + ", Fossil Location: " + fossilLocation
                + ", Size: " + size;
    }
}
