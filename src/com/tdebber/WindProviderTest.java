package com.tdebber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindProviderTest {

    @Test
    public void getWindOnePoint() {
        Point[] points = new Point[1];
        Velocity[] windData = new Velocity[1];
        points[0] = new Point(0, 0);
        windData[0] = new Velocity(10, 10);
        WindProvider windProvider = new WindProvider(points, windData);
        Velocity interpWind = windProvider.getWind(new Point( 1, 1));
        assertEquals(10, interpWind.getSpeedMetersPerSecond(), 0.1);
        assertEquals(10, interpWind.getCompassDegress(), 0.1);
    }
}