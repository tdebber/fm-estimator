package com.tdebber;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class VehicleTest {

    @Test
    public void getRemainingEnergy() throws WindException {
        List<Point> route = List.of(
                new Point(0,0),
                new Point(1,0),
                new Point(1,1),
                new Point(0, 1),
                new Point(0,0));

        Vehicle vehicle = new Vehicle(route,12, 1,
                ((airspeedMetersPerSecond, timeSeconds) -> timeSeconds), 0.3, null);
        assertEquals(vehicle.getRemainingEnergy(), 8);
    }
}