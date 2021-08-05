package models.market_enviroment;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;
import simudyne.core.annotations.Variable;

import java.util.Random;

public class DefaultMarket extends SuperMarket {

  // Printed
  @Variable public double marketValue = 1;
  @Variable public double marketPerformance = 0;

  // Hidden
  public double marketValueStart = 1;

  public BusinessCycle businessCycle;

  public static Action<DefaultMarket> registerCompanies =
      Action.create(
          DefaultMarket.class,
          a -> {
            a.getMessagesOfType(Messages.MarketRegistrationHomeCompany.class)
                .forEach(a::homeCompanySetup);
            a.getMessagesOfType(Messages.MarketRegistrationClientCompany.class)
                .forEach(a::clientCompanySetup);
          });

  public static Action<DefaultMarket> updateMarketCycle =
      Action.create(DefaultMarket.class, DefaultMarket::getMarketValue);

  public static Action<DefaultMarket> updateEmploymentRate =
      Action.create(DefaultMarket.class, SuperMarket::employmentUpdateRate);

  public static Action<DefaultMarket> updateClientCompanyMarket =
      Action.create(DefaultMarket.class, DefaultMarket::clientMarketUpdate);

  private void clientMarketUpdate() {

    // If market is 50% above the mean, assign two new client company
    // If market is 20% above the mean, assign a new client company
    // Will only join 5% of market or 1% of market size:

    marketPerformance = marketValue / marketValueStart;

    if (marketPerformance > 1.50 && clientMarketWouldAllow(0.005)) {
      spawnNewClientCompany((int) Math.ceil(clientCompanyQueue.size() * 0.05));
    }
    if (marketPerformance > 1.20 && clientMarketWouldAllow(0.025)) {
      spawnNewClientCompany((int) Math.ceil(clientCompanyQueue.size() * 0.005));
    }
    if (
    /*marketPerformance < 0.20 &&*/ clientMarketWouldAllow(0.025)) {
      quitClientCompany((int) Math.ceil(clientCompanyQueue.size() * 0.005));
    }
    if (marketPerformance < 0.50 && clientMarketWouldAllow(0.005)) {
      quitClientCompany((int) Math.ceil(clientCompanyQueue.size() * 0.05));
    }
  }

  // Boolean Function Deciding the Probability of a ClientCompany being found/left.
  private boolean clientMarketWouldAllow(double probOfMarketAvailability) {
    return probOfMarketAvailability > (new Random().nextDouble());
  }

  @Override
  public void getMarketValue() {
    marketValue =
        businessCycle.getMonthlyMarketValue(getContext().getTick(), getGlobals().marketGrowth);
  }
}
