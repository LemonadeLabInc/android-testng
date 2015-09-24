package de.lemona.android.testng;

import org.testng.IConfigurationListener;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import android.util.Log;

/**
 * A <i>TestNG</i> {@link ITestListener} logging TestNG events to Android's
 * {@link Log} with <code>TestNG</code> tag.
 *
 * You can view what's being logged using the Android SDK's
 * <a href="http://developer.android.com/tools/help/logcat.html">logcat</a>
 * tool.
 */
public class TestNGLogger implements ITestListener, IConfigurationListener {

    public static final String TAG = "TestNG";

    @Override
    public void onStart(ITestContext context) {
        final ISuite suite = context.getSuite();
        final String suiteName = suite == null ? "[UNKNOWN]" : suite.getName();

        final ITestNGMethod[] methods = context.getAllTestMethods();
        if (methods == null) {
            Log.w(TAG, "No test methods provided by " + suiteName + " (null methods)");
        } else if (methods.length < 1) {
            Log.w(TAG, "No test methods provided by " + suiteName + " (length=" + methods.length + ")");
        } else {
            Log.i(TAG, "Starting test run \"" + suiteName + "\" with " + methods.length + " tests");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        final ISuite suite = context.getSuite();
        final String suiteName = suite == null ? "[UNKNOWN]" : suite.getName();

        final IResultMap passed = context.getPassedTests();
        final IResultMap failed = context.getFailedTests();
        final IResultMap skipped = context.getSkippedTests();

        final int passedCount = passed == null ? -1 : passed.size();
        final int failedCount = failed == null ? -1 : failed.size();
        final int skippedCount = skipped == null ? -1 : skipped.size();

        Log.i(TAG, "Finished test run \"" + suiteName + "\" with "
                   + passedCount + " successful tests, "
                   + failedCount + " failures and "
                   + skippedCount + " tests skipped");
    }

    /* ====================================================================== */

    @Override
    public void onTestStart(ITestResult result) {
        final String name = result.getInstanceName() + "." + result.getName();
        Log.d(TAG, "Test \"" + name + "\" starting");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        final Throwable throwable = result.getThrowable();
        final long ms = result.getEndMillis() - result.getStartMillis();
        final String name = result.getInstanceName() + "." + result.getName();
        Log.d(TAG, "Test \"" + name + "\" successful (" + ms + " ms)", throwable);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        final Throwable throwable = result.getThrowable();
        final long ms = result.getEndMillis() - result.getStartMillis();
        final String name = result.getInstanceName() + "." + result.getName();
        Log.w(TAG, "Test \"" + name + "\" failed (" + ms + " ms)", throwable);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        final Throwable throwable = result.getThrowable();
        final long ms = result.getEndMillis() - result.getStartMillis();
        final String name = result.getInstanceName() + "." + result.getName();
        Log.i(TAG, "Test \"" + name + "\" skipped (" + ms + " ms)", throwable);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        this.onTestFailure(result);
    }

    /* ====================================================================== */

    @Override
    public void onConfigurationSuccess(ITestResult result) {
        final String name = result.getInstanceName() + "." + result.getName();
        Log.i(TAG, "Configuration success: " + name);
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        final String name = result.getInstanceName() + "." + result.getName();
        Log.i(TAG, "Configuration failure: " + name);
    }

    @Override
    public void onConfigurationSkip(ITestResult result) {
        final String name = result.getInstanceName() + "." + result.getName();
        Log.i(TAG, "Configuration skipped: " + name);
    }

}
