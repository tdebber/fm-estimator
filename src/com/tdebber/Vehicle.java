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
   protected double distancePerTimestep;

   public Vehicle(List<Point> route, double initialBatteryEnergy,
                  double cruiseSpeed, EnergyFunction energyFunction,
                  double timestep) {
       this.route = route;
       this.initialBatteryEnergy = initialBatteryEnergy;
       this.cruiseSpeed = cruiseSpeed;
       this.energyFunction = energyFunction;
       this.timestep = timestep;

       remainingEnergy = OptionalDouble.empty();
       distancePerTimestep = cruiseSpeed * timestep;
   }

   public double getRemainingEnergy() {
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

    private double timeToWaypoint(Point currentWaypoint, Point nextWaypoint) {
       double bearingToNextDegrees = currentWaypoint.compassBearingTo(nextWaypoint);
       Point currentPosition = currentWaypoint;
       double remainingDistance = currentPosition.distanceTo(nextWaypoint);
       double time = 0;
       for ( ;
            remainingDistance > distancePerTimestep;
            remainingDistance -= distancePerTimestep) {
            currentPosition = currentPosition.plusDistanceToward(nextWaypoint, distancePerTimestep);
            time += timestep;
            System.out.println("Remaining distance: " + remainingDistance);
       }
       time += remainingDistance / cruiseSpeed;
       return time;
    }

}
