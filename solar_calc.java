public class solar_calc {
    //we're working in degrees for the entire thing
    //so keep that in mind
    private static double TIME_ZONE = +12;
    private static double LONGITUDE = +174.8860;
    private static double LATITUDE = -40.90060;

    //For the time being, the main method is just a test-bed
    public static void main(String[] argv) {
	for(int day = 0; day <= 360; day+= 60) {
	    //System.out.println("Declination: " + declination(day));
	    System.out.println("Equation:    " + Eqt(day));

	    double LSTM = LSTM(TIME_ZONE);
	    double TC   = TC(LONGITUDE, LSTM, Eqt(day));
	    
	    System.out.println("TC: " + TC);

	    for(int i = 6; i < 19; i+=6) {
		TC   = TC(LONGITUDE, LSTM, Eqt(day + (i / 24.0)));
		double DEC = declination(day + i/24.0);
		double TS = TSolar(i, TC);
		double HA = HourAngle(TS);
		double EA = ElevationAngle(DEC, LATITUDE, HA);
		double AA = AzimuthAngle(DEC, LATITUDE, HA, EA) * Math.signum(HA);

		
		System.out.println("EA (" + i + "): " + EA);
		System.out.println("AA (" + i + "): " + AA);
	    }
	    
	    System.out.println();
	}
    }

    
    public static double AzimuthAngle(double declination, double latitude, double HA, double EA) {
	double res = (degsin(declination) * degcos(latitude)) -
	    (degcos(declination) * degsin(latitude) * degcos(HA));
	double AA = degacos(res / degcos(EA));

	return AA;
    }

    /**
     * The zenith angle is similar to the elevation angle as it
     * describes the sun’s position in the sky, however it
     * measures from the vertical axis rather than the horizon
     * (shown in Figure 4). This is shown in equation 1.4.
     */
    public static double ZenithAngle(double declination, double latitude, double HA) {
	return 90 - ElevationAngle(declination, latitude, HA);
    }
    public static double ZenithAngle(double ElevationAngle) {
	return 90 - ElevationAngle;
    }
    
    /**
     * The elevation angle (also known as altitude angle)
     * describes the sun’s angular height above the horizon
     * (shown in Figure 4). The sun’s elevation angle is 0° at
     * sunrise and rises up to an angle depending on location
     * and time of year, then decreases back to 0° at sunset.
     * This can be calculated by equation 1.3.
     */
    public static double ElevationAngle(double declination, double latitude, double HA) {
	double res = (degsin(declination) * degsin(latitude)) +
	    (degcos(declination) * degcos(latitude) * degcos(HA));
	double EA = degasin(res);
	return EA;
    }

    /**
     * The hour angle (θhr) is the angle describing the sun’s
     * position in the sky at a specific time, with solar noon
     * being 0°, morning angle being negative and afternoon
     * angle being positive (See Figure 3). This can easily be
     * calculated by subtracting twelve from solar time, and
     * multiply by 15° since the earth spins 15° per hour. This
     * is shown in equation 1.3.
     */
    public static double HourAngle(double TSolar) {
	return 15 * (TSolar - 12);
    }

    
    /**
     * Solar time (Tsolar) at a specific local time (LT) can be
     * estimated using equation 1.2.1 (Honsberg & Bowden,
     * 2013).
     */
    public static double TSolar(double LT, double TC) {
	return LT + (TC / 60.0);
    }
    
    /** 
     * Where time correction factor (TC) is in minutes and
     * accounts for the variation from solar time at a certain
     * time zone. This variation is caused by the change of
     * longitudes within time zones and the equation of time
     * (Eqt). TC can be calculated using equation 1.2.2
     *         (Honsberg & Bowden, 2013).
     */
    public static double TC(double longitude, double LSTM, double Eqt) {
	return 4 * (longitude - LSTM) + Eqt;
    }
    
    /**
     * LSTM : Local Standard Time Meridean
     * 15 is the amount in degrees each hour that the planet rotates.
     * TIME_ZONE is gmt difference : ie +12/13 for NZ
     */
    public static double LSTM(double timeZone) {
	return 15 * timeZone;
    }

    /**
     * The equation of time (Eqt) describes the discrepancy in
     * minutes between true and mean solar time. This
     * discrepancy is caused by the eccentricity of the earth’s
     * orbit. This can be calculated using equation 1.1
     * depending on (day) the day of the year (Sandia National
     * Laboratories and PVPMC, 2014).
     */
    public static double Eqt(double day) {
	if (day < 107)
	    return -14.2 * Math.sin((Math.PI * (day + 7))   / 111.0);
	if (day < 167)
	    return  4.0  * Math.sin((Math.PI * (day - 106)) / 59.0);
	if (day < 246)
	    return -6.5  * Math.sin((Math.PI * (day - 166)) / 80.0);
	if (day < 367)
	return 16.4  * Math.sin((Math.PI * (day - 247)) / 113.0);
	
	return 0;
    }
    
    /**
     * The earth fluctuates from its axis of rotation and the
     * plane normal to a line from the center of the earth and
     * the sun by -23.45° to +23.45° (See Figure 2). This angle is
     * called the declination angle (δ) and can be calculated
     * using equation 1.0 (Cooper, 1969).
     *
     * [1.0] sin^-1(sin(23.45)sin((360/365)(day - 81)))
     */
    private static double declination(double day) {
	//let res = (360/365)(day - 81)
	//==> dec = sin^-1(sin(23.45)sin(res))
	double res = (day - 81) * (360.0 / 365.0);
	double dec = degasin(degsin(23.45) * degsin(res));

	return dec;
    }
    
    private static double degsin(double val) {
	return Math.sin(DEG2RAD * val);
    }
    private static double degcos(double val) {
	return Math.cos(DEG2RAD * val);
    }
    
    private static double degasin(double val) {
	return Math.asin(val) * RAD2DEG;
    }
    private static double degacos(double val) {
	return Math.acos(val) * RAD2DEG;
    }

    private static final double DEG2RAD = Math.PI/180;
    private static final double RAD2DEG = 180/Math.PI;
}
