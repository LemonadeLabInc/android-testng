package de.lemona.android.testng;

import static de.lemona.android.testng.TestNGLogger.TAG;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.testng.TestNG;
import org.testng.collections.Lists;
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
                } catch (Throwable throwable) {
                    // Likely NoClassDefException for missing dependencies
                    Log.w(TAG, "Ignoring class " + cls, throwable);
                }
            }

            // Remember our classes
            xmlTest.setXmlClasses(xmlClasses);

            // Create our TestNG runner
            final TestNG ng = new TestNG(false);
            ng.setDefaultSuiteName("Android TestNG Suite");
            ng.setDefaultTestName("Android TestNG Test");
            ng.setCommandLineSuite(xmlSuite);
            ng.addListener(new TestNGLogger());
            ng.addListener(listener); // reporter

            // Run tests!
            ng.runSuitesLocally();

        } catch (IOException exception) {
            // Wrap the IOException into something we can throw
            throw new IllegalStateException("I/O error", exception);

        } finally {
            // Close our listener
            listener.close();
        }
    }
}
