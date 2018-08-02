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

    private double timeToWaypoint(Point currentWaypoint, Point nextWaypoint) throws WindException {
       double bearingToNextDegrees = currentWaypoint.compassBearingTo(nextWaypoint);
       Point currentPosition = currentWaypoint;
       double totalDistance = currentPosition.distanceTo(nextWaypoint);
       double accumulatedDistance = 0;
       double time = 0;
       while (accumulatedDistance < totalDistance) {
           // add wind vector from velocity vector to get groundspeed
           Velocity windAtPosition = new Velocity(0,0);
           if (windProvider != null) {
               windAtPosition = windProvider.getWind(currentPosition);
           }
           Velocity vehicleAirVelocity = new Velocity(cruiseSpeed, bearingToNextDegrees);
           Velocity groundVelocity = vehicleAirVelocity.add(windAtPosition);
           double groundSpeed = groundVelocity.getSpeedMetersPerSecond();
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
