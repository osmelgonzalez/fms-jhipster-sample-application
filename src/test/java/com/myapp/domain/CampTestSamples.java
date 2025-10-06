package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CampTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Camp getCampSample1() {
        return new Camp().id(1L).name("name1").additionalInfo("additionalInfo1");
    }

    public static Camp getCampSample2() {
        return new Camp().id(2L).name("name2").additionalInfo("additionalInfo2");
    }

    public static Camp getCampRandomSampleGenerator() {
        return new Camp().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).additionalInfo(UUID.randomUUID().toString());
    }
}
