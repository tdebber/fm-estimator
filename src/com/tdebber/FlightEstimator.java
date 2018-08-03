package com.tdebber;

import java.util.List;

public class FlightEstimator {
    /*
     * Demonstration using the KrigingWindProvider.
     * See the VehicleTest for verified test cases.
     */
    public static void main(String[] args) {
        EnergyFunction simpleEnergyFunction =
                ((airspeedMetersPerSecond, timeSeconds) -> airspeedMetersPerSecond * timeSeconds);

        List<Point> route = List.of(
                new Point(0, -1),
                new Point(1, -1),
                new Point(1, 1),
                new Point(-1, 1),
                new Point(-1, -1),
                new Point(0, -1));

        Point[] samplePoints = {
                new Point(-2.1, 2),
                new Point(2, 2),
                new Point(-2, -2),
                new Point(-2, 2)};

        Velocity[] velocities = {
                new Velocity(10, 170),
                new Velocity(5, 190),
                new Velocity(15, 180),
                new Velocity(10, 200)
        };
        KrigingWindProvider windProvider =
                new KrigingWindProvider(samplePoints, velocities);
        double startingEnergy = 15;
        Vehicle vehicle = new Vehicle(route, startingEnergy, 20,
                simpleEnergyFunction, 0.2, windProvider);

        try {
            double energyRemaining = vehicle.getRemainingEnergy();
            System.out.println("Starting with energy " + startingEnergy +
                    ", ended with energy " + energyRemaining);
        } catch (WindException e) {
            System.out.println("Got WindException.");
        }

    }
}
