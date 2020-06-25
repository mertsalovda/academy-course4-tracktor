package com.elegion.tracktor.util;

public class DistanceConverter {

    public static final int KILOMETER = 1;
    public static final int MILES = 2;
    public static final int METER = 3;
    public static final int FOOT = 4;

    public static String formatDistance(Double meter, int unit) {
        String result = "";
        switch (unit) {
            case KILOMETER:
                result = MathUtils.round((meter / 1000), 2) + " км";
                break;
            case MILES:
                result = MathUtils.round((meter * 0.000621371), 2) + " миль";
                break;
            case METER:
                result = MathUtils.round(meter, 2) + " м";
                break;
            case FOOT:
                result = MathUtils.round(meter * 3.281, 2) + " футов";
                break;
            default:
                throw new IllegalStateException("Неизвестная единица измерения длины: " + unit);
        }
        return result;
    }
}
