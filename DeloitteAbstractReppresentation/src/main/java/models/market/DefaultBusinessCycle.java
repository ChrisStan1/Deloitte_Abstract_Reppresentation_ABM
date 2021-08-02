package models.market;

import java.util.Random;

public class DefaultBusinessCycle implements BusinessCycle{

    public double amplitude = 1.2;
    public double period = 0.06;
    public double slope = 0.005;


    @Override
    public double getMonthlyMarketValue(double marketValueStart, long tick) {
        return (marketValueStart + slope*tick + amplitude *Math.sin(period*tick)) + (new Random().nextDouble());
    }
}
