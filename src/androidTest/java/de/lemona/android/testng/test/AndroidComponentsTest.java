package de.lemona.android.testng.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import android.app.Instrumentation;
import android.content.Context;
import de.lemona.android.testng.AndroidContextTest;
import de.lemona.android.testng.AndroidInstrumentationTest;

public class AndroidComponentsTest implements AndroidContextTest, AndroidInstrumentationTest {

    private Instrumentation instrumentation;
    private Context context;

    @Override
    public final void injectInstrumentation(Instrumentation instrumentation) {
        if (instrumentation != null) this.instrumentation = instrumentation;
    }

    @Override
    public final void injectContext(Context context) {
        if (context != null) this.context = context;
    }

    /* ====================================================================== */

    @BeforeSuite
    public void beforeSuite() {
        // TODO: How can we inject this right after construction???
        Assert.assertNull(context, "Non-null context in @BeforeSuite");
        Assert.assertNull(instrumentation, "Non-null instrumentation in @BeforeSuite");
    }

    @BeforeGroups(groups="Components")
    public void beforeGroups() {
        Assert.assertNotNull(context, "Null context in @BeforeGroups");
        Assert.assertNotNull(instrumentation, "Null instrumentation in @BeforeGroups");
    }

    @BeforeClass
    public void beforeClass() {
        Assert.assertNotNull(context, "Null context in @BeforeClass");
        Assert.assertNotNull(instrumentation, "Null instrumentation in @BeforeClass");
    }

    @BeforeTest
    public void beforeTest() {
        Assert.assertNotNull(context, "Null context in @BeforeTest");
        Assert.assertNotNull(instrumentation, "Null instrumentation in @BeforeTest");
    }

    @Test(groups="Components")
    public void testComponents() {
        Assert.assertNotNull(context, "Null context in @Test");
        Assert.assertNotNull(instrumentation, "Null instrumentation in @Test");
    }

}
