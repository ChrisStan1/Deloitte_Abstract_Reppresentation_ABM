package models.market;

import simudyne.core.abm.Action;
import simudyne.core.annotations.Variable;

public class DefaultMarket extends SuperMarket {

  @Variable public double marketValue = 1;

  public BusinessCycle businessCycle;

  public static Action<DefaultMarket> updateMarketCycle =
      Action.create(DefaultMarket.class, DefaultMarket::getMarketValue);

  public static Action<DefaultMarket> updateEmploymentRate =
      Action.create(DefaultMarket.class, SuperMarket::employmentUpdateRate);

    public void getMarketValue() {

        marketValue = businessCycle.;




    }
}
