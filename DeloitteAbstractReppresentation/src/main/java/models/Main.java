package models;

import models.SimpleFirmModel.SimpleFirmModel;
import simudyne.nexus.Server;

public class Main {
  public static void main(String[] args) {
    Server.register("SimpleFirmModel", SimpleFirmModel.class);
    Server.run();
  }
}
