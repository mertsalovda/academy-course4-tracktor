package com.elegion.tracktor.util;

public class SpeedUtil {

    public static float speedInKmS(Double distance, long duration) {
        return ((distance.floatValue() / duration) * 18.0f) / 5.0f;
    }

    public static float convertKmSToMs(float speed) {
        return speed * 0.277778f;
    }
}
