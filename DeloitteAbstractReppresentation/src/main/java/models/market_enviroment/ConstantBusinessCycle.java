/**************************
 * ConstantBusinessCycle
 * ConstantBusinessCycle generates an exponential market value
 * By cas220
 **************************/

package models.market_enviroment;

import models.SimpleFirmModel.parameters.Globals;
import simudyne.core.abm.Agent;

public class ConstantBusinessCycle extends Agent<Globals> implements BusinessCycle {

  // Hidden variables
  public double previousMarketValue = 0;

  // Constructor:
  public ConstantBusinessCycle(double marketValueStart) {
    previousMarketValue = marketValueStart;
  }

  // Generates a new market value following an exponential curve
  @Override
  public double getMonthlyMarketValue(long tick, double marketGrowth) {
    previousMarketValue += previousMarketValue * marketGrowth;
    return previousMarketValue;
  }
}
