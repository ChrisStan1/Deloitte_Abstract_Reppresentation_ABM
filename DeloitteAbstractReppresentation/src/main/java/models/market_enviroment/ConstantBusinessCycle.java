package models.market_enviroment;

public class ConstantBusinessCycle implements BusinessCycle {

  public double previousMarketValue = 0;

  public ConstantBusinessCycle(double marketValueStart) {
    previousMarketValue = marketValueStart;
  }

  // Todo: This is exponential...
  @Override
  public double getMonthlyMarketValue(long tick, double marketGrowth) {
    previousMarketValue += previousMarketValue * marketGrowth;
    return previousMarketValue;
  }
}
