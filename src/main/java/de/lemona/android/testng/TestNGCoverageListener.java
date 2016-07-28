package de.lemona.android.testng;

import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A <i>TestNG</i> {@link ITestListener} that supports Emma CodeCoverage.
 */
public class TestNGCoverageListener implements ITestListener {

    private static final String REPORT_KEY_COVERAGE_PATH = "coverageFilePath";

    private final Instrumentation instrumentation;
    private final Bundle bundle;
    private final String mCoverageFilePath;
    private boolean isFailed = false;
    private boolean isSkipped = false;

    /**
     * Create a new {@link ITestListener} instance to generates coverage output.
     *
     * @param instrumentation TThe {@link Instrumentation} running tests.
     */
    protected TestNGCoverageListener(Instrumentation instrumentation, String coverageFilePath) {
        this.instrumentation = instrumentation;
        this.bundle = new Bundle();
        this.mCoverageFilePath = coverageFilePath;
    }

    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        this.isFailed = true;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        this.isSkipped = true;
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
        if (!isFailed && !isSkipped) {
            generateCodeCoverage(bundle);
        }
    }

    private void generateCodeCoverage(Bundle results) {
        // use reflection to call emma dump coverage method, to avoid
        // always statically compiling against emma jar
        java.io.File coverageFile = new java.io.File(mCoverageFilePath);
        Class<?> emmaRTClass;
        try {
            emmaRTClass = Class.forName("com.vladium.emma.rt.RT");
            Method dumpCoverageMethod = emmaRTClass.getMethod("dumpCoverageData",
                    coverageFile.getClass(), boolean.class, boolean.class);
            dumpCoverageMethod.invoke(null, coverageFile, false, false);

            // output path to generated coverage file so it can be parsed by a test harness if
            // needed
            results.putString(REPORT_KEY_COVERAGE_PATH, mCoverageFilePath);
            // also output a more user friendly msg
            Log.d(TestNGLogger.TAG, "\nGenerated code coverage data to " + mCoverageFilePath);
        } catch (ClassNotFoundException e) {
            reportEmmaError("Is emma jar on classpath?", e);
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            reportEmmaError(e);
        }
    }

    private void reportEmmaError(Exception e) {
        reportEmmaError("", e);
    }

    private void reportEmmaError(String hint, Exception e) {
        String msg = "Failed to generate emma coverage. " + hint;
        Log.e(TestNGLogger.TAG, msg, e);
    }

}
