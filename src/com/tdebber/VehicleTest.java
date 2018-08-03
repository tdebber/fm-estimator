package com.tdebber;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class VehicleTest {

    static final EnergyFunction SIMPLE_ENERGY_FUNCTION =
            ((airspeedMetersPerSecond, timeSeconds) -> airspeedMetersPerSecond * timeSeconds);

    @Test
    public void groundspeedTest() {
        double airspeed = 120;
        double course = 90;
        Velocity wind = new Velocity(30, 180);
        double groundspeed = Vehicle.computeGroundSpeed(airspeed, course, wind);
        assertEquals(116, groundspeed, 1);
    }

    @Test
    public void getRemainingEnergyNoWind() throws WindException {
        List<Point> route = List.of(
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, 1),
                new Point(0, 0));

        Vehicle vehicle = new Vehicle(route, 12, 2,
                SIMPLE_ENERGY_FUNCTION, 0.3, null);
        assertEquals(4, vehicle.getRemainingEnergy());
    }

    @Test
    public void getRemainingEnergyWindTooStrong() throws WindException {
        /*
         * Verify that when the wind cannot be overcome by the Vehucle velocity
         * to reach a given waypoint, a WindException is thrown.
         */
        TestWindProvider testWindProvider = new TestWindProvider();
        List<Point> route = List.of(
                new Point(1, 0), new Point(1, 1));
        Vehicle vehicle = new Vehicle(route, 12,
                TestWindProvider.WIND_SPEED - 2, SIMPLE_ENERGY_FUNCTION, 0.1, testWindProvider);
        assertThrows(WindException.class, () -> vehicle.getRemainingEnergy());
    }

    @Test
    public void getRemainingEnergyNegative() throws WindException {
        List<Point> route = List.of(
                new Point(0,0),
                new Point(1,0),
                new Point(1,1),
                new Point(0, 1),
                new Point(0,0));

        Vehicle vehicle = new Vehicle(route, 2, 1,
                SIMPLE_ENERGY_FUNCTION, 0.3, null);
        assertEquals(-2, vehicle.getRemainingEnergy());
    }

    @Test
    public void getRemainingEnergyWithWind() throws WindException {
        /* Flies a square with legs 2 meters long.  On the x > 0 side, wind
         * is blowing toward 180 at 10 m/s, on the x < 0 side, wind is blowing toward
         * the north at 10 m/s  Expected groundspeeds for each leg are noted with
         * Vehicle speed at 20 m/s
         */
        List<Point> route = List.of(
                new Point(0, -1),
                new Point(1, -1), // 17.32 m/s -> 0.0577s
                new Point(1, 1), // 10 m/s -> 0.2s
                new Point(-1, 1), // 17.32 m/s -> 0.1154s
                new Point(-1, -1), // 10 m/s -> 0.2s
                new Point(0, -1)); // 17.32 m/s -> 0.0577s
        // Total time: 0.6308

        Vehicle vehicle = new Vehicle(route, 15, 20,
                SIMPLE_ENERGY_FUNCTION, 0.2, new TestWindProvider());
        // Energy consumed: airspeed * time = 20 * 0.6308 = 12.616
        // Initial energy - energy consumed = 15 - 12.616 = 2.384
        double energyRemaining = vehicle.getRemainingEnergy();
        assertEquals(2.384, energyRemaining, 0.01);
    }
}