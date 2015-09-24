package de.lemona.android.testng.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups="Lifecycle")
public class LifecycleTest {

    static final List<String> EVENTS = new ArrayList<>();

    /* ====================================================================== */

    @BeforeSuite
    public void beforeSuite() {
        EVENTS.add("BEFORE_SUITE");
    }

//    @AfterSuite
//    public void afterSuite() {
//        EVENTS.add("AFTER_SUITE");
//    }

    /* ====================================================================== */

    @BeforeGroups(groups="Lifecycle")
    public void beforeGroups() {
        EVENTS.add("BEFORE_GROUPS");
    }

    @AfterGroups(groups="Lifecycle")
    public void afterGroups() {
        EVENTS.add("AFTER_GROUPS");
    }

    /* ====================================================================== */

    @BeforeClass
    public void beforeClass() {
        EVENTS.add("BEFORE_CLASS");
    }


    @AfterClass
    public void afterClass() {
        EVENTS.add("AFTER_CLASS");
    }

    /* ====================================================================== */

    @BeforeTest
    public void beforeTest() {
        EVENTS.add("BEFORE_TEST");
    }

//    @AfterTest
//    public void afterTest() {
//        EVENTS.add("AFTER_TEST");
//    }

    /* ====================================================================== */

    @BeforeMethod
    public void beforeMethod() {
        EVENTS.add("BEFORE_METHOD");
    }

    @AfterMethod
    public void afterMethod() {
        EVENTS.add("AFTER_METHOD");
    }

    /* ====================================================================== */

    @Test
    public void firstTest() {
        EVENTS.add("FIRST_TEST");
    }

    @Test(dependsOnMethods="firstTest")
    public void secondTest() {
        EVENTS.add("SECOND_TEST");
    }

}
