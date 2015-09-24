package de.lemona.android.testng;

import android.app.Instrumentation;

/**
 * A simple interface declaring that this test instance requires the injection
 * of the current {@link Instrumentation}.
 */
public interface AndroidInstrumentationTest {

    /**
     * Request injection of the current {@link Instrumentation} instance.
     *
     * @param instrumentation The {@link Instrumentation} to inject in this instance.
     */
    public void injectInstrumentation(Instrumentation instrumentation);

}
