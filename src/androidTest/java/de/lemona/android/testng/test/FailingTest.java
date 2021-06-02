package de.lemona.android.testng.test;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FailingTest {

    @Test
    public void failingTest() {
        try {
            Assert.fail("This is an expected failure for an @Test method");
            throw new RuntimeException("This should not be thrown."); // if assertion is failed, NullPointerException caused a failure of test.
        } catch (AssertionError ae) {
            // success
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void expectedFailingTest() {
        throw new NullPointerException("This is expected");
    }

    @Test(enabled=false)
    public void shouldBeSkippedAgain() {
        Assert.fail("This test should be skipped");
    }

}
