package com.elegion.tracktor.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistanceConverterTest {

    @Test
    public void formatDistanceKilometer() {
        assertEquals("1.0 км", DistanceConverter.formatDistance(1000.0, DistanceConverter.KILOMETER));
        assertNotEquals("1.0 км", DistanceConverter.formatDistance(2000.0, DistanceConverter.KILOMETER));
    }

    @Test
    public void formatDistanceKilometer2() {
        assertEquals("2.0 км", DistanceConverter.formatDistance(2000.0, DistanceConverter.KILOMETER));
        assertNotEquals("2.0 км", DistanceConverter.formatDistance(1000.0, DistanceConverter.KILOMETER));
    }
    @Test
    public void formatDistanceMeter() {
        assertEquals("1000.0 м", DistanceConverter.formatDistance(1000.0, DistanceConverter.METER));
        assertNotEquals("1000.0 м", DistanceConverter.formatDistance(2000.0, DistanceConverter.METER));
    }
    @Test
    public void formatDistanceMiles() {
        assertEquals("0.62 миль", DistanceConverter.formatDistance(1000.0, DistanceConverter.MILES));
        assertNotEquals("0.62 миль", DistanceConverter.formatDistance(2000.0, DistanceConverter.MILES));
    }

    @Test
    public void formatDistanceFoot() {
        assertEquals("3281.0 футов", DistanceConverter.formatDistance(1000.0, DistanceConverter.FOOT));
        assertNotEquals("3281.0 футов", DistanceConverter.formatDistance(2000.0, DistanceConverter.FOOT));
    }

    @Test(expected = IllegalStateException.class)
    public void formatDistanceException(){
        DistanceConverter.formatDistance(1000.0, -1);
    }
}