package com.tdebber;

import smile.interpolation.KrigingInterpolation;

import java.util.InputMismatchException;

/**
 *  This site discusses methods for interpolating wind direction.
 *  https://www.researchgate.net/post/Which_interpolation_method_is_sound_for_interpolating_wind_direction_data
 *  A primary recommendation is to decompose the wind speed direction and velocity into their vector components,
 *  interpolate each of those, and then recombine for wind speed and direction.
 */

public class WindProvider {

    protected KrigingInterpolation xInterpolator;
    protected KrigingInterpolation yInterpolator;

    public WindProvider(Point[] samplePoints, Velocity[] windData) {
        if (samplePoints.length != windData.length) {
            throw new InputMismatchException("samplePoints and windData must be the same length.");
        }
        if (samplePoints.length == 0) {
            throw new IllegalArgumentException("input must include at least one sample");
        }
        double[][] x0 = new double[samplePoints.length][2];
        double[] xCompData = new double[samplePoints.length];
        double[] yCompData = new double[samplePoints.length];
        for (int i =  0; i < windData.length; i++) {
            x0[i][0] = samplePoints[i].getX();
            x0[i][1] = samplePoints[i].getY();
            Velocity curVelocity = windData[i];
            double xWind = curVelocity.getXComponent();
            double yWind = curVelocity.getYComponent();
            xCompData[i] = xWind;
            yCompData[i] = yWind;
        }
        xInterpolator = new KrigingInterpolation(x0, xCompData);
        yInterpolator = new KrigingInterpolation(x0, yCompData);
    }

    public Velocity getWind(Point point) {
        return Velocity.fromComponents(xInterpolator.interpolate(point.getX(), point.getY()),
                yInterpolator.interpolate(point.getX(), point.getY()));
    }
}
