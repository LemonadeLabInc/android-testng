package de.lemona.android.testng;

import static android.app.Instrumentation.REPORT_KEY_IDENTIFIER;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.IConfigurationListener;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;

/**
 * A <i>TestNG</i> {@link ITestListener} sending status reports.
 */
public class TestNGListener implements ITestListener,
                                       IExecutionListener,
                                       IConfigurationListener,
                                       Closeable {

    /** Value for {@link Instrumentation#REPORT_KEY_IDENTIFIER} */
    private static final String REPORT_VALUE_ID = "TestNGRunner";

    /** Total number of tests being run (sent with all status messages). */
    private static final String REPORT_KEY_NUM_TOTAL = "numtests";
    /** Sequence number of the current test. */
    private static final String REPORT_KEY_NUM_CURRENT = "current";
    /** Name of the current test class. */
    private static final String REPORT_KEY_NAME_CLASS = "class";
    /** The name of the current test. */
    private static final String REPORT_KEY_NAME_TEST = "test";
    /** Stack trace describing an error or failure. */
    private static final String REPORT_KEY_STACK = "stack";

    /** Test is starting. */
    private static final int REPORT_VALUE_RESULT_START = 1;
    /** Test completed successfully. */
    private static final int REPORT_VALUE_RESULT_OK = 0;
    /** Test completed with an error.*/
    //private static final int REPORT_VALUE_RESULT_ERROR = -1;
    /** Test completed with a failure. */
    private static final int REPORT_VALUE_RESULT_FAILURE = -2;
    /** Test was skipped. */
    private static final int REPORT_VALUE_RESULT_SKIPPED = -3;
    /** Test completed with an assumption failure. */
    //private static final int REPORT_VALUE_RESULT_ASSUMPTION_FAILURE = -4;

    /* ====================================================================== */

    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final AtomicInteger testNumber = new AtomicInteger();
    private final ConcurrentHashMap<String, AtomicInteger> tests;
    private final Instrumentation instrumentation;
    private final Bundle bundle;
    private boolean started;

    /**
     * Create a new {@link TestNGListener} instance.
     *
     * @param instrumentation The {@link Instrumentation} running tests.
     */
    protected TestNGListener(Instrumentation instrumentation) {
        if (instrumentation == null) throw new NullPointerException();
        this.tests = new ConcurrentHashMap<String, AtomicInteger>();
        this.instrumentation = instrumentation;
        this.bundle = new Bundle();
        this.started = false;

        // Always set...
        bundle.putString(REPORT_KEY_IDENTIFIER, REPORT_VALUE_ID);
    }

    /* ====================================================================== */

    private final void sendStatus(int status) {
        if (isClosed.compareAndSet(false, false)) {
            Log.d("TestNG", "Sending " + status + ": " + bundle);
            this.instrumentation.sendStatus(status, bundle);
        }
    }

    private final void sendStatus(int status, ITestResult result) {
        sendStatus(status, result.getThrowable());
    }

    private final void sendStatus(int status, Throwable throwable) {
        if (throwable != null) {
            final StringWriter writer = new StringWriter();
            final PrintWriter printer = new PrintWriter(writer);
            throwable.printStackTrace(printer);
            printer.flush();
            writer.flush();
            final String trace = writer.toString()
                                       .replaceAll("(?m)^[ \t]*\r?\n", "")
                                       .trim();
            bundle.putString(REPORT_KEY_STACK, trace);
        } else {
            bundle.remove(REPORT_KEY_STACK);
        }

        sendStatus(status);
    }

    /* ====================================================================== */

    /**
     * Close ths instance, notifying the {@link Instrumentation} that all
     * tests have been executed.
     *
     * This method will call {@link Instrumentation#finish(int, Bundle)} only
     * once and once called will prevent further notifications.
     */
    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            bundle.remove(REPORT_KEY_NUM_CURRENT);
            bundle.remove(REPORT_KEY_NAME_CLASS);
            bundle.remove(REPORT_KEY_NAME_TEST);
            bundle.remove(REPORT_KEY_STACK);
            instrumentation.finish(Activity.RESULT_OK, bundle);
        }
    }

    /** Report an unexpected failure in the tests */
    void fail(String className, String testName, Throwable throwable) {
        if (! started) bundle.putInt(REPORT_KEY_NUM_TOTAL, 1);

        bundle.putInt(REPORT_KEY_NUM_CURRENT, testNumber.incrementAndGet());
        bundle.putString(REPORT_KEY_NAME_CLASS, className);
        bundle.putString(REPORT_KEY_NAME_TEST, testName);

        sendStatus(REPORT_VALUE_RESULT_START);
        sendStatus(REPORT_VALUE_RESULT_FAILURE, throwable);
    }

    /* ====================================================================== */

    /**
     * Notify that we are about to start testing.
     *
     * This method will setup the initial {@link Bundle} for notifications.
     */
    @Override
    public void onStart(ITestContext context) {
        this.started = true;

        final ITestNGMethod[] methods = context.getAllTestMethods();

        if ((methods == null) || (methods.length < 1)) {
            bundle.putInt(REPORT_KEY_NUM_TOTAL, 0);
        } else {
            bundle.putInt(REPORT_KEY_NUM_TOTAL, methods.length);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // Let this be closed by the instrumentation runner for when
        // there are multiple suites (e.g. from "testng.xml" in assets)
    }

    /* ====================================================================== */

    @Override
    public void onTestStart(ITestResult result) {
        final String className = result.getInstanceName();
        final String resultName = result.getName();
        final String name = className + '.' + resultName;

        // Test methods can be invoked mutiple times, with data providers!
        if (! tests.contains(name)) tests.putIfAbsent(name, new AtomicInteger(0));
        final AtomicInteger count = tests.get(name);

        final int num = count.getAndIncrement();
        final String testName = num == 0 ? resultName : resultName + '#' + num;

        bundle.putInt(REPORT_KEY_NUM_CURRENT, testNumber.incrementAndGet());
        bundle.putString(REPORT_KEY_NAME_CLASS, className);
        bundle.putString(REPORT_KEY_NAME_TEST, testName);

        sendStatus(REPORT_VALUE_RESULT_START);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        sendStatus(REPORT_VALUE_RESULT_OK, result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        sendStatus(REPORT_VALUE_RESULT_SKIPPED, result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        sendStatus(REPORT_VALUE_RESULT_FAILURE, result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        sendStatus(REPORT_VALUE_RESULT_FAILURE, result);
    }

    /* ====================================================================== */

    @Override
    public void onExecutionStart() {
        // Nothing to do really...
    }

    @Override
    public void onExecutionFinish() {
        this.close();
    }

    /* ====================================================================== */

    @Override
    public void onConfigurationSuccess(ITestResult result) {
        // We don't report any configuration success...
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        // Emulate test failure
        this.onTestStart(result);
        this.onTestFailure(result);
    }

    @Override
    public void onConfigurationSkip(ITestResult result) {
        // Emulate test skipped
        this.onTestStart(result);
        this.onTestSkipped(result);
    }
}
