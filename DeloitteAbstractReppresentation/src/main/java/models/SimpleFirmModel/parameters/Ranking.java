/**************************
 * Ranking
 * Enum class for Ranking
 * By cas220
 **************************/

package models.SimpleFirmModel.parameters;

public enum Ranking {
  SENIOR("Senior"),
  JUNIOR("Junior");

  private final String name;

  Ranking(String s) {
    name = s;
  }

  // Check if two enums are the same
  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return name.equals(otherName);
  }

  // Conversion from enum value to string
  public String toString() {
    return this.name;
  }
}
