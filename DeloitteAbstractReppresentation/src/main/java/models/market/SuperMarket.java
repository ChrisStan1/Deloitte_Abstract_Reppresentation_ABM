package models.market;

import models.SimpleFirmModel.parameters.Globals;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.Random;

public abstract class SuperMarket extends Agent<Globals> implements Market {

  @Override
  public void employmentUpdateRate() {

    updateEmploymentRate(true);
    updateEmploymentRate(false);
  }

  @Variable
  public double srEmploymentMean = 1.0;
  @Variable
  public double jrEmploymentMean = 1.0;


  @Override
  public void updateEmploymentRate(boolean isSrCons) {

    double deviation = (new Random().nextGaussian() / 100); // Used for %
    if (isSrCons) {
      srEmploymentMean += deviation;
    } else {
      jrEmploymentMean += deviation;
    }
  }


}
