package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FileDataTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FileData getFileDataSample1() {
        return new FileData().id(1L).uid("uid1").fileName("fileName1");
    }

    public static FileData getFileDataSample2() {
        return new FileData().id(2L).uid("uid2").fileName("fileName2");
    }

    public static FileData getFileDataRandomSampleGenerator() {
        return new FileData().id(longCount.incrementAndGet()).uid(UUID.randomUUID().toString()).fileName(UUID.randomUUID().toString());
    }
}
