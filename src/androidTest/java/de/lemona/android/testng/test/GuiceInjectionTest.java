package de.lemona.android.testng.test;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import android.app.Instrumentation;
import android.content.Context;
import de.lemona.android.testng.AndroidTestNGModule;

@Guice(modules=AndroidTestNGModule.class)
public class GuiceInjectionTest {

    private final Context context;
    private final Instrumentation instrumentation;

    @Inject
    private GuiceInjectionTest(Context context, Instrumentation instrumentation) {
        this.context = context;
        this.instrumentation = instrumentation;
    }

    @Test
    public void testWasConstructed() {
        Assert.assertNotNull(this.context, "Null context");
        Assert.assertNotNull(this.instrumentation, "Null instrumentation");
    }
}
