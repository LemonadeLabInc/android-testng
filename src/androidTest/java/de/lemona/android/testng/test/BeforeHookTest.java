package de.lemona.android.testng.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

public class BeforeHookTest {

    private AtomicReference<Object> ref = new AtomicReference<>(null);
    @BeforeClass
    public void beforeClass() {
        // processing before test
        ref.set(new Object());
    }

    @Test
    public void verifyBeforeWorked() {
        Assert.assertNotNull(ref.get(), "This should never be null");
    }

}
