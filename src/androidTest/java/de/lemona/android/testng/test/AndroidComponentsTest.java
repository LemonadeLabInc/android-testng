package de.lemona.android.testng.test;

import static de.lemona.android.testng.AndroidTestNGSupport.getContext;
import static de.lemona.android.testng.AndroidTestNGSupport.getInstrumentation;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AndroidComponentsTest {

    @BeforeSuite
    public void beforeSuite() {
        Assert.assertNotNull(getContext(), "Non-null context in @BeforeSuite");
        Assert.assertNotNull(getInstrumentation(), "Non-null instrumentation in @BeforeSuite");
    }

    @BeforeGroups(groups="Components")
    public void beforeGroups() {
        Assert.assertNotNull(getContext(), "Null context in @BeforeGroups");
        Assert.assertNotNull(getInstrumentation(), "Null instrumentation in @BeforeGroups");
    }

    @BeforeClass
    public void beforeClass() {
        Assert.assertNotNull(getContext(), "Null context in @BeforeClass");
        Assert.assertNotNull(getInstrumentation(), "Null instrumentation in @BeforeClass");
    }

    @BeforeTest
    public void beforeTest() {
        Assert.assertNotNull(getContext(), "Null context in @BeforeTest");
        Assert.assertNotNull(getInstrumentation(), "Null instrumentation in @BeforeTest");
    }

    @Test(groups="Components")
    public void testComponents() {
        Assert.assertNotNull(getContext(), "Null context in @Test");
        Assert.assertNotNull(getInstrumentation(), "Null instrumentation in @Test");
    }

}
