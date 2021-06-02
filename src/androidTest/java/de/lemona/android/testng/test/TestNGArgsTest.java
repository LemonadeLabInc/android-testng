package de.lemona.android.testng.test;

import android.app.Instrumentation;
import android.os.Bundle;

import com.google.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import de.lemona.android.testng.AndroidTestNGModule;
import de.lemona.android.testng.TestNGArgs;

/**
 *
 */
@Guice(modules=AndroidTestNGModule.class)
public class TestNGArgsTest {

    private final Instrumentation instrumentation;

    @Inject
    public TestNGArgsTest(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Test
    public void testParseArguments() {
        Map<String,String> args = new HashMap<String, String>(){{
            put(TestNGArgs.ARGUMENT_DEBUG,"true");
            put(TestNGArgs.ARGUMENT_COVERAGE,"true");
            put(TestNGArgs.ARGUMENT_COVERAGE_PATH, "somewhere");
        }};

        Bundle bundle = getTestArguments(args);

        TestNGArgs.Builder builder = new TestNGArgs.Builder(instrumentation);
        builder = builder.fromBundle(bundle);
        TestNGArgs testNGArgs = builder.build();
        Assert.assertTrue(testNGArgs.debug, "argument should be true");
        Assert.assertTrue(testNGArgs.codeCoverage, "argument should be true");
        Assert.assertEquals("somewhere", testNGArgs.codeCoveragePath, "argument should be received");

        args.put(TestNGArgs.ARGUMENT_DEBUG, "false");
        args.put(TestNGArgs.ARGUMENT_COVERAGE,"false");
        args.remove(TestNGArgs.ARGUMENT_COVERAGE_PATH);

        bundle = getTestArguments(args);
        builder = builder.fromBundle(bundle);
        TestNGArgs testNGArgs2 = builder.build();
        Assert.assertFalse(testNGArgs2.debug, "argument should be false");
        Assert.assertFalse(testNGArgs2.codeCoverage,"argument should be false");
        Assert.assertNull(testNGArgs2.codeCoveragePath, "argument should be null");
    }


    private Bundle getTestArguments(Map<String, String> args) {
        Bundle bundle = new Bundle();
        for (String key : args.keySet()) {
            bundle.putString(key, args.get(key));
        }
        return bundle;
    }
}
