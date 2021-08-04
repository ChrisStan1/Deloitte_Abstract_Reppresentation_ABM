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

  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return name.equals(otherName);
  }

  public String toString() {
    return this.name;
  }

  public static Specialization generateNewRandomSpecialization() {
    return Specialization.values()[new Random().nextInt(Specialization.values().length)];
  }

  public Specialization skip(int amount) {
    return vals[(this.ordinal() + amount) % vals.length];
  }

  public static Specialization assignNumeric(int pos) {
    return vals[pos];
  }
}
