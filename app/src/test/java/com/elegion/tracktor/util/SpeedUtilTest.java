package com.elegion.tracktor.util;

import org.junit.Assert;
import org.junit.Test;

public class SpeedUtilTest {

    @Test
    public void speedInKmS() {
        Double distance = 20.0;
        long duration = 1;
        float result = SpeedUtil.speedInKmS(distance, duration);
        Assert.assertEquals(result, 72.0f, 0.05f);
        distance = 754.5922033770271;
        duration = 14;
        result = SpeedUtil.speedInKmS(distance, duration);
        Assert.assertEquals(result, 194.037994285184, 0.05f);
    }

    @Test
    public void convertKmSToMs(){
        float speedInKmS = 72;
        float speedInMs = 20;
        float result = SpeedUtil.convertKmSToMs(speedInKmS);
        Assert.assertEquals(result, speedInMs, 0.05f);
        speedInKmS = 100;
        speedInMs = 27.7778f;
        result = SpeedUtil.convertKmSToMs(speedInKmS);
        Assert.assertEquals(result, speedInMs, 0.05f);
    }
}