package com.tdebber;

public final class Point {

    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Point pt2) {
        double xDiff = pt2.x - x;
        double yDiff = pt2.y - y;
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public double compassBearingTo(Point pt2) {
        double thetaRad = bearingInRad(pt2);
        double thetaDeg = rad2deg(thetaRad);
        if (thetaDeg > 90) {
            return 450 - thetaDeg;
        }
        return -thetaDeg + 90;
    }

    protected double bearingInRad(Point pt2) {
        double xDiff = pt2.x - x;
        double yDiff = pt2.y - y;
        return Math.atan2(yDiff, xDiff);
    }

    public Point plusDistanceToward(Point p2, double distance) {
        double bearingRad = bearingInRad(p2);
        double diffX = distance * Math.cos(bearingRad);
        double diffY = distance * Math.sin(bearingRad);
        return new Point(x + diffX, y + diffY);
    }
}

