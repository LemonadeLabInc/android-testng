package de.lemona.android.testng;

import android.content.Context;

/**
 * A simple interface declaring that this test instance requires the injection
 * of the Android {@link Context}.
 */
public interface AndroidContextTest {

    /**
     * Request injection of the current {@link Context} instance.
     *
     * @param context The {@link Context} to inject in this instance.
     */
    public void injectContext(Context context);

}
