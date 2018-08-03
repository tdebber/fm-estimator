# Flight Margin Estimator

Addresses the Flight Margin Estimator problem.<br>
The arguments
and functionality are as presented in the Problem statement and
not provided separately in Javadoc.<br>
The `Vehicle` class does contain implementation notes. <p>
The one addition to the problem statement is a timestep
parameter to indicate the granularity to when determining
wind speeds.<P>

A complete sample invocation is provided in the `FlightEstimator` class.<P>
Unit tests are provided for `Vehicle` and `KrigingWindProvider`.

## Testing
1. More thorough unit tests, especially for the KrigingWindProvider.

## Productionization Steps
1. Convert coordinate system from arbitrary x,y to latitude, longitude.
1. Provide better way to input wind data
    1. Retrieve wind from online METARs
    1. Input via text file
1. Provide a better way to input the waypoints
    1. GUI Course Editor
    1. Input via Text File
    1. Input via Vehicle file format
