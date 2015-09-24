package de.lemona.android.testng;

import android.app.Instrumentation;
import android.content.Context;

/**
 * An abstract implementation of the {@link AndroidContextTest} and
 * {@link AndroidInstrumentationTest} interface with a couple of convenience
 * <i>getter</i> methods.
 */
public class AbstractAndroidTest implements AndroidContextTest, AndroidInstrumentationTest {

    private Instrumentation instrumentation;
    private Context context;

    @Override
    public final void injectInstrumentation(Instrumentation instrumentation) {
        if (instrumentation != null) this.instrumentation = instrumentation;
    }

    @Override
    public final void injectContext(Context context) {
        if (context != null) this.context = context;
    }

    /**
     * Return the injected {@link Instrumentation}.
     *
     * @throws IllegalStateException If the {@link Instrumentation} was not injected.
     * @return The {@link Instrumentation} associated with this instance.
     */
    protected final Instrumentation getInstrumentation() {
        if (instrumentation == null) throw new IllegalStateException("Instrumentation not available");
        return instrumentation;
    }

    /**
     * Return the injected {@link Context}.
     *
     * @throws IllegalStateException If the {@link Context} was not injected.
     * @return The {@link Context} associated with this instance.
     */
    protected final Context getContext() {
        if (context == null) throw new IllegalStateException("Context not available");
        return context;
    }

}
