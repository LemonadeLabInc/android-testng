package de.lemona.android.testng;

import android.app.Instrumentation;
import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import org.testng.TestNG;
import org.testng.collections.Lists;
import org.testng.xml.Parser;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumSet;

import dalvik.system.DexFile;

import static de.lemona.android.testng.TestNGLogger.TAG;

/**
 * The root of all evil, creating a TestNG {@link XmlSuite} and running it.
 */
public class TestNGRunner extends Instrumentation {

    private String targetPackage = null;
    private ActivityLifecycleMonitorImpl mLifecycleMonitor = new ActivityLifecycleMonitorImpl();
    private TestNGArgs args;

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        InstrumentationRegistry.registerInstance(this, arguments);
        ActivityLifecycleMonitorRegistry.registerInstance(mLifecycleMonitor);

        args = parseRunnerArgument(arguments);
        targetPackage = this.getTargetContext().getPackageName();
        this.start();
    }

    private TestNGArgs parseRunnerArgument(Bundle arguments) {
        Log.d(TAG, "DEBUG arguments");
        for (String key : arguments.keySet()) {
            Log.d(TAG, "key " + key + " = " + arguments.get(key));
        }
        Log.d(TAG, "DEBUG argumetns END");
        TestNGArgs.Builder builder = new TestNGArgs.Builder(this).fromBundle(arguments);
        return builder.build();
    }

    @Override
    public void onStart() {
        final TestNGListener listener = new TestNGListener(this);
        AndroidTestNGSupport.injectInstrumentation(this);

        if (args.debug) {
            // waitForDebugger
            Log.d(TAG, "waiting for debugger...");
            Debug.waitForDebugger();
            Log.d(TAG, "debugger was connected.");
        }

        setupDexmakerClassloader();

        final TestNG ng = new TestNG(false);
        ng.setDefaultSuiteName("Android TestNG Suite");
        ng.setDefaultTestName("Android TestNG Test");

        // Try to load "testng.xml" from the assets directory...
        try {
            final InputStream input = this.getContext().getAssets().open("testng.xml");
            if (input != null) ng.setXmlSuites(new Parser(input).parseToList());
        } catch (final FileNotFoundException exception) {
            Log.d(TAG, "The \"testng.xml\" file was not found in assets");
        } catch (final Throwable throwable) {
            Log.e(TAG, "An unexpected error occurred parsing \"testng.xml\"", throwable);
            listener.fail(this.getClass().getName(), "onStart", throwable);
        }

        try {
            // Our XML suite for running tests
            final XmlSuite xmlSuite = new XmlSuite();

            xmlSuite.setVerbose(0);
            xmlSuite.setJUnit(false);
            xmlSuite.setName(targetPackage);

            // Open up the DEX file associated with our APK
            final String apk = this.getContext().getPackageCodePath();
            final DexFile dex = new DexFile(apk);
            final Enumeration<String> e = dex.entries();

            // Prepare our XML test and list of classes
            final XmlTest xmlTest = new XmlTest(xmlSuite);
            final List<XmlClass> xmlClasses = Lists.newArrayList();

            // Process every element of the DEX file
            while (e.hasMoreElements()) {
                final String cls = e.nextElement();
                if (! cls.startsWith(targetPackage)) continue;
                Log.d(TAG, "Adding potential test class " + cls);

                try {
                    xmlClasses.add(new XmlClass(cls, true));
                } catch (final Throwable throwable) {
                    // Likely NoClassDefException for missing dependencies
                    Log.w(TAG, "Ignoring class " + cls, throwable);
                }
            }

            // Remember our classes if we have to
            if (! xmlClasses.isEmpty()) {
                Log.i(TAG, "Adding suite from package \"" + targetPackage + "\"");
                xmlTest.setXmlClasses(xmlClasses);
                ng.setCommandLineSuite(xmlSuite);
            }

        } catch (final Throwable throwable) {
            Log.e(TAG, "An unexpected error occurred analysing package \"" + targetPackage + "\"", throwable);
            listener.fail(this.getClass().getName(), "onStart", throwable);
        }

        // Run tests!
        try {
            ng.addListener(new TestNGLogger());
            if (args.codeCoverage) {
                ng.addListener(new TestNGCoverageListener(this, args.codeCoveragePath));
            }
            ng.addListener((Object) listener);
            ng.runSuitesLocally();

        } catch (final Throwable throwable) {
            Log.e(TAG, "An unexpected error occurred running tests", throwable);
            listener.fail(this.getClass().getName(), "onStart", throwable);

        } finally {
            // Close our listener
            listener.close();
        }
    }

    @Override
    public void finish(int resultCode, Bundle results)  {
        finishActivities();
        ActivityLifecycleMonitorRegistry.registerInstance(null);
        super.finish(resultCode, results);
    }

    private void setupDexmakerClassloader() {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        // must set the context classloader for apps that use a shared uid, see
        // frameworks/base/core/java/android/app/LoadedApk.java
        ClassLoader newClassLoader = this.getClass().getClassLoader();
        //Log.i(LOG_TAG, String.format("Setting context classloader to '%s', Original: '%s'",
        //        newClassLoader.toString(), originalClassLoader.toString()));
        Thread.currentThread().setContextClassLoader(newClassLoader);
    }
    @Override
    public void callActivityOnDestroy(Activity activity) {
        super.callActivityOnDestroy(activity);
        mLifecycleMonitor.signalLifecycleChange(Stage.DESTROYED, activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        super.callActivityOnRestart(activity);
        mLifecycleMonitor.signalLifecycleChange(Stage.RESTARTED, activity);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        mLifecycleMonitor.signalLifecycleChange(Stage.PRE_ON_CREATE, activity);
        super.callActivityOnCreate(activity, bundle);
        mLifecycleMonitor.signalLifecycleChange(Stage.CREATED, activity);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        try {
            super.callActivityOnStart(activity);
            mLifecycleMonitor.signalLifecycleChange(Stage.STARTED, activity);
        } catch (RuntimeException re) {
            throw re;
        }
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        super.callActivityOnStop(activity);
        mLifecycleMonitor.signalLifecycleChange(Stage.STOPPED, activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        super.callActivityOnResume(activity);
        mLifecycleMonitor.signalLifecycleChange(Stage.RESUMED, activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        super.callActivityOnPause(activity);
        mLifecycleMonitor.signalLifecycleChange(Stage.PAUSED, activity);
    }

    private void finishActivities() {
        List<Activity> activities = new ArrayList<Activity>();

        for (Stage s : EnumSet.range(Stage.CREATED, Stage.PAUSED)) {
            activities.addAll(mLifecycleMonitor.getActivitiesInStage(s));
        }

        Log.i(TAG, "Activities that are still in CREATED to PAUSED: " + activities.size());
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                try {
                    Log.i(TAG, "Stopping activity: " + activity);
                    activity.finish();
                } catch (RuntimeException e) {
                    Log.e(TAG, "Failed to stop activity.", e);
                }
            }
        }
    }
}
