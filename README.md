TestNG runner for Android
=========================

This is a minimal implementation of an Android
[Instrumentation](http://developer.android.com/reference/android/app/Instrumentation.html)
executing unit tests based on [TestNG](http://testng.org/) (the best testing framework for Java).


Usage
-----

Depending on your build system, your mileage might vary, but with
[Gradle](https://gradle.org/) the only required changes to your build files
should be limited to adding our repository, and dependency and modifying
your `testInstrumentationRunner`:

```groovy
// Our Bintray repository
repositories {
  maven {
    url 'http://dl.bintray.com/lemonade/maven'
  }
}

// TestNG dependency
dependencies {
  androidTestCompile 'de.lemona.android:testng:1.0.0'
}

// Android setup
android {
  defaultConfig {
    testInstrumentationRunner 'de.lemona.android.testng.TestNGRunner'
  }
}
```


Packages
--------

The runner will *ONLY* look for classes in the package specified by the
`targetPackage` entry in your `AndroidManifest.xml` file.

In [Gradle](https://gradle.org/) this defaults to your application package
plus `....test`.

If no tests can be found, verify the parameter in the manifest of your APK.

For example in our [manifest](src/main/AndroidManifest.xml) the declared
package is `de.lemona.android.testng`, henceforth after the build processes
it, all our tests will be automatically searched for in the
[`de.lemona.android.testng.test`](https://github.com/LemonadeLabInc/android-testng/tree/master/src/androidTest/java/de/lemona/android/testng/test)
package.


Contexts
--------

Simply implement the
[AndroidContextTest](src/main/java/de/lemona/android/testng/AndroidContextTest.java)
interface or extend the
[AbstractAndroidTest](src/main/java/de/lemona/android/testng/AbstractAndroidTest.java)
abstract class in order to access the Android's
[Context](http://developer.android.com/reference/android/content/Context.html)
instance for the running app.


License
-------

Licensed under the [Apache License version 2](LICENSE.md)
