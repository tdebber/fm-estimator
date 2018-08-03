package com.tdebber;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;

public class Vehicle {

    protected List<Point> route;
    protected double initialBatteryEnergy;
    protected double cruiseSpeed;
    protected EnergyFunction energyFunction;
    protected OptionalDouble remainingEnergy;
    protected double timestep;
    protected WindProvider windProvider;

    public Vehicle(List<Point> route, double initialBatteryEnergy,
                   double cruiseSpeed, EnergyFunction energyFunction,
                   double timestep, WindProvider windProvider) {
        if (timestep <= 0) {
            throw new IllegalArgumentException("Timestep must be greater than 0.");
        }
        if (initialBatteryEnergy <= 0) {
            throw new IllegalArgumentException("initialBatteryEnergy must be greater than 0.");
        }
        if (cruiseSpeed <= 0) {
            throw new IllegalArgumentException("cruiseSpeed must be greater than 0");
        }
        if (route.size() <= 1) {
            remainingEnergy = OptionalDouble.of(initialBatteryEnergy);
            return;
        }
        this.route = route;
        this.initialBatteryEnergy = initialBatteryEnergy;
        this.cruiseSpeed = cruiseSpeed;
        this.energyFunction = energyFunction;
        this.timestep = timestep;
        this.windProvider = windProvider;

        remainingEnergy = OptionalDouble.empty();
    }

    public double getRemainingEnergy() throws WindException {
        if (remainingEnergy.isPresent()) {
            return remainingEnergy.getAsDouble();
        }
        /*
         * Iterate through each leg, determining the time to traverse the leg.
         * Accumulate the time for all legs.
         * Apply the provided energyFunction given the cruise speed and accumulated time.
         */
        double time = 0;
        Iterator<Point> routePoints = route.iterator();
        Point currentWaypoint;
        Point nextWaypoint = routePoints.next();
        while (routePoints.hasNext()) {
            currentWaypoint = nextWaypoint;
            nextWaypoint = routePoints.next();
            time += timeToWaypoint(currentWaypoint, nextWaypoint);
        }

        double energyUsed = energyFunction.energyConsumed(cruiseSpeed, time);
        remainingEnergy = OptionalDouble.of(initialBatteryEnergy - energyUsed);
        return remainingEnergy.getAsDouble();
    }

    // @VisibleForTesting
    public static double computeGroundSpeed(double airspeed, double course, Velocity windVelocity) {
        // Formulas from http://delphiforfun.org/Programs/math_topics/WindTriangle.htm
        double wtAngle = Math.toRadians(course - windVelocity.getCompassDegress());
        double sinWca = windVelocity.getSpeedMetersPerSecond() *
                Math.sin(wtAngle) / airspeed;
        double wca = Math.asin(sinWca);
        return (airspeed * Math.cos(wca)) +
                (windVelocity.getSpeedMetersPerSecond() * Math.cos(wtAngle));
    }

    private double timeToWaypoint(Point currentWaypoint, Point nextWaypoint) throws WindException {
        /*
         * 1. Determine the course from currentWaypoint to nextWaypoint
         * 2. At each timestep, determine the wind at the current position and
         *    determine the vehicle ground speed to be used for the timestep.
         * 3. Move along the line between the two waypoints the distance that
         *    is traveled in one timestep at the determined groundspeed
         * 4. Continue until the next waypoint is reached, checking to ensure
         *    that the Vehicle does not pass the nextWaypoint.  At each
         *    step, the time to move the step is accumulated.
         * 5. Return the accumulated time.
         */
        double bearingToNextDegrees = currentWaypoint.compassBearingTo(nextWaypoint);
        Point currentPosition = currentWaypoint;
        double totalDistance = currentPosition.distanceTo(nextWaypoint);
        double accumulatedDistance = 0;
        double time = 0;
        while (accumulatedDistance < totalDistance) {
            Velocity windAtPosition = new Velocity(0, 0);
            if (windProvider != null) {
                windAtPosition = windProvider.getWind(currentPosition);
            }
            double groundSpeed = computeGroundSpeed(cruiseSpeed, bearingToNextDegrees, windAtPosition);
            if (groundSpeed <= 0) {
                throw new WindException("Wind speed exceeds vehicle cruise speed.");
            }
            double distanceThisTimestep = groundSpeed * timestep;
            double remainingDistance = totalDistance - accumulatedDistance;
            if (distanceThisTimestep > remainingDistance) {
                double remainingTime = remainingDistance / groundSpeed;
                time += remainingTime;
                break;
            } else {
                currentPosition = currentPosition.plusDistanceToward(nextWaypoint, distanceThisTimestep);
                accumulatedDistance += distanceThisTimestep;
                time += timestep;
            }
        }
        return time;
    }

}
