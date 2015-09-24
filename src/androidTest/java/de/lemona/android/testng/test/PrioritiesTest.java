package de.lemona.android.testng.test;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PrioritiesTest {

    private boolean firstCalled = false;
    private boolean secondCalled = false;

    @Test(priority=1)
    public void myFirstTest() {
        firstCalled = true;
    }

    @Test(priority=2)
    public void mySecondTest() {
        secondCalled = true;
    }

    @Test(priority=3)
    public void verifySimpleTest() {
        Assert.assertTrue(firstCalled, "SimpleTest.myFirstTest() not called");
        Assert.assertTrue(secondCalled, "SimpleTest.mySecondTest() not called");
    }

}
