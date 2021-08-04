package models.market_enviroment;

import java.util.Random;

public class DefaultBusinessCycle implements BusinessCycle{

    public double marketValueStart;
    public double amplitude = 1.2;
    public double period = 60;

    public DefaultBusinessCycle(double marketValueStart){
        this.marketValueStart = marketValueStart;
    }

    @Override
    public double getMonthlyMarketValue(long tick, double marketGrowth) {
        return (marketValueStart + marketGrowth*tick + amplitude *Math.sin((tick/period)*(2*Math.PI) + doubleOffset())) + (new Random().nextDouble());
    }

    private double doubleOffset() {
        return new Random().nextDouble() * 2*Math.PI;
    }
}
