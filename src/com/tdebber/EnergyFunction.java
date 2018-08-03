package com.tdebber;

public interface EnergyFunction {
    /**
     * @param airspeedMetersPerSecond
     * @param timeSeconds
     * @return energy consumed in Wh
     */
    double energyConsumed(double airspeedMetersPerSecond, double timeSeconds);
}
