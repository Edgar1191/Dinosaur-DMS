
/**
 * Edgar Lopez
 * CEN 3024C - Software Development 1
 * November 16, 2026
 * Dinosaur.java
 *
 * Represents a dinosaur in the Dinosaur Data Management System
 * Stores the dinosaur's identifying information and physical attributes
 */
public class Dinosaur {

    private String name;

    private String timePeriod;

    private String diet;

    private String clade;

    private String fossilLocation;

    private double size;

    /**
     * Creates a dinosaur using the six required attributes
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

    public String getName() {
        return name;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public String getDiet() {
        return diet;
    }

    public String getClade() {
        return clade;
    }

    public String getFossilLocation() {
        return fossilLocation;
    }

    public double getSize() {
        return size;
    }

    public String setName(String name) {
        this.name = name;
        return this.name;
    }

    public String setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
        return this.timePeriod;
    }

    public String setDiet(String diet) {
        this.diet = diet;
        return this.diet;
    }

    public String setClade(String clade) {
        this.clade = clade;
        return this.clade;
    }

    public String setFossilLocation(String fossilLocation) {
        this.fossilLocation = fossilLocation;
        return this.fossilLocation;
    }

    public double setSize(double size) {
        this.size = size;
        return this.size;
    }

    /**
     * method: toString
     * parameters: none
     * return: String
     * purpose: creates a readable text representation of the dinosaur's identifying information and physical attributes
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
