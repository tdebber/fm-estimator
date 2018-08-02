package com.tdebber;

public class Velocity {
    final double speedMetersPerSecond;
    final double compassDegress;
    public Velocity(double speedMetersPerSecond, double compassDegress) {
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.compassDegress = compassDegress;
    }

    public static Velocity fromComponents(double xComp, double yComp) {
        Point origin = new Point(0,0);
        Point vector = new Point(xComp, yComp);
        return new Velocity(origin.distanceTo(vector), origin.compassBearingTo(vector));
    }

    public Velocity add(Velocity v2) {
        return Velocity.fromComponents(getXComponent() + v2.getXComponent(),
                getYComponent() + v2.getYComponent());
    }

    public double getCompassDegress() {
        return compassDegress;
    }

    public double getSpeedMetersPerSecond() {
        return speedMetersPerSecond;
    }

    private double getRadians() {
        if (compassDegress > 90) {
            return Math.toRadians(450 - compassDegress);
        }
        return Math.toRadians(90 - compassDegress);
    }

    public double getXComponent() {
        return speedMetersPerSecond * Math.cos(getRadians());
    }

    public double getYComponent() {
        return speedMetersPerSecond * Math.sin(getRadians());
    }
}
