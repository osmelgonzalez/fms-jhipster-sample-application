package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GuardianTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Guardian getGuardianSample1() {
        return new Guardian()
            .id(1L)
            .firstName("firstName1")
            .middleInitial("middleInitial1")
            .lastName("lastName1")
            .relationshipToPlayer("relationshipToPlayer1")
            .testField("testField1");
    }

    public static Guardian getGuardianSample2() {
        return new Guardian()
            .id(2L)
            .firstName("firstName2")
            .middleInitial("middleInitial2")
            .lastName("lastName2")
            .relationshipToPlayer("relationshipToPlayer2")
            .testField("testField2");
    }

    public static Guardian getGuardianRandomSampleGenerator() {
        return new Guardian()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .middleInitial(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .relationshipToPlayer(UUID.randomUUID().toString())
            .testField(UUID.randomUUID().toString());
    }
}
