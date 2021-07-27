package models.SimpleFirmModel.parameters;

public enum Ranking {
  SENIOR("Senior"),
  JUNIOR("Junior");

  private final String name;

  Ranking(String s) {
    name = s;
  }

  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return name.equals(otherName);
  }

  public String toString() {
    return this.name;
  }
}
