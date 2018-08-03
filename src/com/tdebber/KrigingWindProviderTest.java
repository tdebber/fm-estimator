package com.tdebber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KrigingWindProviderTest {

    @Test
    public void getWindOnePoint() {
        Point[] points = new Point[1];
        Velocity[] windData = new Velocity[1];
        points[0] = new Point(0, 0);
        windData[0] = new Velocity(10, 10);
        KrigingWindProvider windProvider = new KrigingWindProvider(points, windData);
        Velocity interpWind = windProvider.getWind(new Point( 1, 1));
        assertEquals(10, interpWind.getSpeedMetersPerSecond(), 0.1);
        assertEquals(10, interpWind.getCompassDegress(), 0.1);
    }

    @Test
    public void getWindTwoPointsOppositeDirections() {
        Point[] points = new Point[2];
        Velocity[] windData = new Velocity[2];
        points[0] = new Point(0, 0);
        points[1] = new Point(2, 2);
        windData[0] = new Velocity(10, 10);
        windData[1] = new Velocity(10, 190);
        KrigingWindProvider windProvider = new KrigingWindProvider(points, windData);
        Velocity interpWind = windProvider.getWind(new Point(1, 1));
        assertEquals(0, interpWind.getSpeedMetersPerSecond(), 0.1);
    }

    @Test
    public void getWindTwoPointsSameDirections() {
        Point[] points = new Point[2];
        Velocity[] windData = new Velocity[2];
        points[0] = new Point(0, 0);
        points[1] = new Point(2, 2);
        windData[0] = new Velocity(20, 190);
        windData[1] = new Velocity(10, 190);
        KrigingWindProvider windProvider = new KrigingWindProvider(points, windData);
        Velocity interpWind = windProvider.getWind(new Point(1, 1));
        assertEquals(15, interpWind.getSpeedMetersPerSecond(), 0.1);
        assertEquals(190, interpWind.getCompassDegress(), 0.1);
    }

}