package de.lemona.android.testng.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FailingBeforeHookTest {

    @BeforeClass
    public void beforeClass() {
        Assert.fail("This is an expected failure for an @BeforeClass hook");
    }

    @Test
    public void shouldBeSkipped() {
        Assert.fail("This should never be called");
    }

}
