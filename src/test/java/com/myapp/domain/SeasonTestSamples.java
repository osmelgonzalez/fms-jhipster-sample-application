package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SeasonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Season getSeasonSample1() {
        return new Season().id(1L).name("name1").additionalInfo("additionalInfo1");
    }

    public static Season getSeasonSample2() {
        return new Season().id(2L).name("name2").additionalInfo("additionalInfo2");
    }

    public static Season getSeasonRandomSampleGenerator() {
        return new Season().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).additionalInfo(UUID.randomUUID().toString());
    }
}
