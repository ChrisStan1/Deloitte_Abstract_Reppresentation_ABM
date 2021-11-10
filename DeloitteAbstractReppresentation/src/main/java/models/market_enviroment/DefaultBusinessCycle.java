/**************************
 * DefaultBusinessCycle
 * This is the default business cycle, follows a randomized sin wave
 * By cas220
 **************************/

package models.market_enviroment;

import models.SimpleFirmModel.parameters.Globals;
import simudyne.core.abm.Agent;

import java.util.Random;

public class DefaultBusinessCycle extends Agent<Globals> implements BusinessCycle {

  // Hidden variables
  public double marketValueStart;
  public double noiseAmplitude = 0.05;
  public double offset = 0;
  public double period = 60;
  public double amplitude = 0.2;

  // Constructor
  public DefaultBusinessCycle(double marketValueStart) {
    this.marketValueStart = marketValueStart;
    this.offset = doubleOffset();
  }

  // Sign Wave Setup As a business Cycle (even out the randomization)
  @Override
  public double getMonthlyMarketValue(long tick, double marketGrowth) {
    return marketValueStart
        + marketGrowth * tick
        + amplitude * Math.sin(((tick / period) * (2 * Math.PI)) + offset)
        + randomizeNoise();
  }

  // This noise between -1 - 1
  private double randomizeNoise() {
    return ((new Random().nextDouble() - 0.5) * 2) * noiseAmplitude;
  }

  // Generates an initialization offset in the business cycle
  private double doubleOffset() {
    return (new Random().nextDouble() * (2 * Math.PI));
  }
}
