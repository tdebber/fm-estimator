package com.tdebber;

public class TestWindProvider implements WindProvider {
    public final static double WIND_SPEED = 10;

    public TestWindProvider() {

    }

    @Override
    public Velocity getWind(Point point) {
        if (point.getX() < 0) {
            return new Velocity(10, 0);
        }
        return new Velocity(10, 180);
    }
}
