/**************************
 * Specialization
 * Enum class for Specialization
 * By cas220
 **************************/

package models.SimpleFirmModel.parameters;

import java.util.Random;

public enum Specialization {
  FINANCE("Finance"),
  INDUSTRIAL("Industry"),
  TECHNOLOGY("Technology");

  private final String name;

  private static Specialization[] vals = values();

  Specialization(String s) {
    name = s;
  }

  // Function if two specializations are the same
  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return name.equals(otherName);
  }

  // Converting an enum to string
  public String toString() {
    return this.name;
  }

  // Specialization random generator
  public static Specialization generateNewRandomSpecialization() {
    return Specialization.values()[new Random().nextInt(Specialization.values().length)];
  }

  // Loop through specialization by the desired amount
  public Specialization skip(int amount) {
    return vals[(this.ordinal() + amount) % vals.length];
  }

  public static Specialization assignNumeric(int pos) {
    return vals[pos];
  }
}
