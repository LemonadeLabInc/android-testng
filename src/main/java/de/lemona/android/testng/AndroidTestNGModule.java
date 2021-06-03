package de.lemona.android.testng;

import android.app.Instrumentation;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * A basic {@link Module} which can be used to inject {@link Context} and
 * {@link Instrumentation} with Google Guice.
 * <p>
 * For example:
 * <pre>
 * {@literal @}Guice(modules=AndroidTestNGModule.class)
 *  public class GuiceInjectionTest {
 *    {@literal @}Inject private InjectedTest(Context context) {
 *       ...
 *    }
 *  }
 * </pre>
 */
public class AndroidTestNGModule extends AbstractModule {

    /**
     * Create a new {@link Module} binding {@link Context} and
     * {@link Instrumentation} instances.
     */
    public AndroidTestNGModule() {
        // Nothing to do, javadoc only
    }

    /**
     * Bind the {@link Context} and {@link Instrumentation} instances.
     */
    @Override
    public void configure() {
        Binder binder = binder();
        binder.bind(Context.class).toInstance(AndroidTestNGSupport.getContext());
        binder.bind(Instrumentation.class).toInstance(AndroidTestNGSupport.getInstrumentation());
    }

}
