package models.market_enviroment;

public class ConstantBusinessCycle implements BusinessCycle {

  public double previousMarketValue = 0;

  // Constructor:
  public ConstantBusinessCycle(double marketValueStart) {
    previousMarketValue = marketValueStart;
  }

  // Todo: At the moment it is setup to be exponential
  @Override
  public double getMonthlyMarketValue(long tick, double marketGrowth) {
    previousMarketValue += previousMarketValue * marketGrowth;
    return previousMarketValue;
  }
}
