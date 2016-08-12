package de.lemona.android.testng;

import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

/**
 * A <i>TestNG</i> runner arguments.
 */
public class TestNGArgs {

    /* ARGUMENT KEYS */
    static final String ARGUMENT_DEBUG = "debug";
    static final String ARGUMENT_COVERAGE = "coverage";
    static final String ARGUMENT_COVERAGE_PATH = "coverageFile";

    /* Default Values */

    private static final String DEFAULT_COVERAGE_FILE_NAME = "coverage.ec";


    public final boolean debug;
    public final boolean codeCoverage;
    public final String codeCoveragePath;

    private TestNGArgs(Builder builder) {
        this.debug = builder.debug;
        this.codeCoverage = builder.codeCoverage;
        this.codeCoveragePath = builder.codeCoveragePath;

        Log.d(TestNGLogger.TAG, this.toString());
    }

    public static class Builder {
        private final Instrumentation instrumentation;
        private boolean debug = false;
        private boolean codeCoverage = false;
        private String codeCoveragePath = null;

        public Builder(Instrumentation instrumentation) {
            this.instrumentation = instrumentation;
        }

        public Builder fromBundle(Bundle bundle) {
            debug = parseBoolean(bundle.getString(ARGUMENT_DEBUG));
            codeCoverage = parseBoolean(bundle.getString(ARGUMENT_COVERAGE));
            codeCoveragePath = bundle.getString(ARGUMENT_COVERAGE_PATH);
            if (codeCoverage && codeCoveragePath == null) {
                codeCoveragePath = instrumentation.getTargetContext().getFilesDir().getAbsolutePath() +
                        File.separator + DEFAULT_COVERAGE_FILE_NAME;
            }

            return this;
        }

        public TestNGArgs build() {
            return new TestNGArgs(this);
        }

        /**
         * Parse boolean value from a String
         *
         * @return the boolean value, false on null input
         */
        private boolean parseBoolean(String booleanValue) {
            return booleanValue != null && Boolean.parseBoolean(booleanValue);
        }

    }

    public String toString() {
        return "[" + TestNGArgs.class.getSimpleName() + "]\n" +
                "\tdebug = " + debug + "\n" +
                "\tcodeCoverage = " + codeCoverage + "\n" +
                "\tcodeCoveragePath = " + codeCoveragePath;
    }

}
