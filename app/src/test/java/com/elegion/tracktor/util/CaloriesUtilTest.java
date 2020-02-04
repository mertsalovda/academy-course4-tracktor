package com.elegion.tracktor.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class CaloriesUtilTest {
    private String[] activeTypes = {"Ходьба", "Бег", "Велосипед", "Самокат"};

    @Test
    public void execute() {
        int height = 184;
        int weight = 62;
        long duration = 14;
        float averageSpeed = 194.038f;
        int activeType = 0; // ходьба
        float result = CaloriesUtil.execute(activeType, averageSpeed, duration, weight, height, activeTypes);
        assertEquals(result, 662.9157f, 0.05f);

        height = 171;
        weight = 70;
        duration = 60;
        averageSpeed = 194.038f;
        activeType = 0; // ходьба
        result = CaloriesUtil.execute(activeType, averageSpeed, duration, weight, height, activeTypes);
        assertEquals(result, 3451.265386066f, 0.05f);
    }
}