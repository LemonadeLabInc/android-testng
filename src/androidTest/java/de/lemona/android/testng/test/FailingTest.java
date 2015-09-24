package de.lemona.android.testng.test;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FailingTest {

    @Test
    public void failingTest() {
        Assert.fail("This is an expected failure for an @Test method");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void expectedFailingTest() {
        throw new NullPointerException();
    }

    @Test(enabled=false)
    public void shouldBeSkipped() {
        Assert.fail("This test should be skipped");
    }

}
