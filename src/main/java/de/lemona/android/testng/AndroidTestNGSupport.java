package de.lemona.android.testng;

import android.app.Instrumentation;
import android.content.Context;

/**
 * An utility class suitable for accessing the {@link Instrumentation} and
 * {@link Context} instances from tests.
 * <p>
 * NOTE: this is a bit of a hack (static), but it's the easiest way to
 * implement it without getting lost in TestNG's hooks.
 */
public abstract class AndroidTestNGSupport {

    private AndroidTestNGSupport() {
        throw new IllegalStateException("Do not construct");
    }

    /* ====================================================================== */

    private static Instrumentation instrumentation = null;

    static final void injectInstrumentation(Instrumentation instrumentation) {
        AndroidTestNGSupport.instrumentation = instrumentation;
    }

    /* ====================================================================== */

    /**
     * Return the {@link Context} associated with the tests.
     *
     * @return A <b>non-null</b> {@link Context} instance.
     * @throws IllegalStateException If no instance was available.
     */
    public static final Context getContext() {
        if (instrumentation == null) throw new IllegalStateException("No Android instrumentation available");
        final Context context = instrumentation.getTargetContext();
        if (context == null) throw new IllegalStateException("No android context available");
        return context;
    }

    /**
     * Return the {@link Instrumentation} associated with the tests.
     *
     * @return A <b>non-null</b> {@link Instrumentation} instance.
     * @throws IllegalStateException If no instance was available.
     */
    public static final Instrumentation getInstrumentation() {
        return instrumentation;
    }

}
