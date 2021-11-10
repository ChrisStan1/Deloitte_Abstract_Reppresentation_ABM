/**************************
 * BusinessCycle interface
 * By cas220
 **************************/

package models.market_enviroment;

public interface BusinessCycle {
  double getMonthlyMarketValue(long tick, double marketGrowth);
}
