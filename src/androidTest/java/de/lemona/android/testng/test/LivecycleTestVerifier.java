package de.lemona.android.testng.test;

import static de.lemona.android.testng.test.LifecycleTest.EVENTS;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LivecycleTestVerifier {

    @Test(dependsOnGroups="Lifecycle")
    public void verifyLifecycle() {
        Assert.assertEquals(EVENTS.size(), 12, LifecycleTest.EVENTS.toString());
        Assert.assertEquals(EVENTS.get( 0), "BEFORE_SUITE");
        Assert.assertEquals(EVENTS.get( 1), "BEFORE_TEST");
        Assert.assertEquals(EVENTS.get( 2), "BEFORE_CLASS");
        Assert.assertEquals(EVENTS.get( 3), "BEFORE_GROUPS");
        Assert.assertEquals(EVENTS.get( 4), "BEFORE_METHOD");
        Assert.assertEquals(EVENTS.get( 5), "FIRST_TEST");
        Assert.assertEquals(EVENTS.get( 6), "AFTER_METHOD");
        Assert.assertEquals(EVENTS.get( 7), "BEFORE_METHOD");
        Assert.assertEquals(EVENTS.get( 8), "SECOND_TEST");
        Assert.assertEquals(EVENTS.get( 9), "AFTER_METHOD");
        Assert.assertEquals(EVENTS.get(10), "AFTER_GROUPS");
        Assert.assertEquals(EVENTS.get(11), "AFTER_CLASS");
    }

}
