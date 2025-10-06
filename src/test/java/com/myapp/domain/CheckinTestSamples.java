package com.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CheckinTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Checkin getCheckinSample1() {
        return new Checkin().id(1L);
    }

    public static Checkin getCheckinSample2() {
        return new Checkin().id(2L);
    }

    public static Checkin getCheckinRandomSampleGenerator() {
        return new Checkin().id(longCount.incrementAndGet());
    }
}
