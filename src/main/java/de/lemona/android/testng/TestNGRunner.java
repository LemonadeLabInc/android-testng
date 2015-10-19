package de.lemona.android.testng;

import static de.lemona.android.testng.TestNGLogger.TAG;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

import org.testng.TestNG;
import org.testng.collections.Lists;
import org.testng.xml.Parser;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;
import dalvik.system.DexFile;

/**
 * The root of all evil, creating a TestNG {@link XmlSuite} and running it.
 */
public class TestNGRunner extends Instrumentation {

    private String targetPackage = null;

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        targetPackage = this.getTargetContext().getPackageName();
        this.start();
    }

    @Override
    public void onStart() {
        final TestNGListener listener = new TestNGListener(this);
        AndroidTestNGSupport.injectInstrumentation(this);

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
}
