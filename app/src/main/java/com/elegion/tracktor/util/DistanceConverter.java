package com.elegion.tracktor.util;

public class DistanceConverter {

    public static final int KILOMETER = 1;
    public static final int MILES = 2;
    public static final int METER = 3;
    public static final int FOOT = 4;

    public static String formatDistance(double meter, int unit) {
        switch (unit) {
            case KILOMETER:
                return MathUtils.round((meter / 1000), 2) + " км";
            case MILES:
                return MathUtils.round((meter * 0.000621371), 2) + " миль";
            case METER:
                return MathUtils.round(meter, 2) + " м";
            case FOOT:
                return MathUtils.round(meter * 3.281, 2) + " футов";
            default:
                throw new IllegalStateException("Неизвестная единица измерения длины: " + unit);
        }
    }
}
