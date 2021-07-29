package models.market;

import models.SimpleFirmModel.parameters.Globals;
import simudyne.core.abm.Agent;

import java.util.Random;

public abstract class SuperMarket extends Agent<Globals> {

  public static double srEmploymentRate = 1.0;
  public static double jrEmploymentRate = 1.0;

  // Constructor:
  void DefaultMarket() {
    updateEmploymentRate(getGlobals().srEmploymentMean, true);
    updateEmploymentRate(getGlobals().jrEmploymentMean, false);
  }

  // Todo: Add the Employment Mean !!!
  public static void updateEmploymentRate(double employmentMean, boolean isSrCons) {

    double deviation = (new Random().nextGaussian() / 100); // Used for %
    if (isSrCons) {
      srEmploymentRate += deviation;
    } else {
      jrEmploymentRate += deviation;
    }
  }

  public static double getSrEmploymentRate() {
    return srEmploymentRate;
  }

  public static double getJrEmploymentRate() {
    return jrEmploymentRate;
  }
}
