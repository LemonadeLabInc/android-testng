TestNG runner for Android
=========================

This is a minimal implementation of an Android
[Instrumentation](http://developer.android.com/reference/android/app/Instrumentation.html)
executing unit tests based on [TestNG](http://testng.org/) (the best testing framework for Java).

[![Download](https://api.bintray.com/packages/lemonade/maven/android-testng/images/download.svg) ](https://bintray.com/lemonade/maven/android-testng/_latestVersion)

Usage
-----

Depending on your build system, your mileage might vary, but with
[Gradle](https://gradle.org/) the only required changes to your build files
should be limited to adding our [repository](https://bintray.com/lemonade/maven),
then declaring the dependency and modifying your `testInstrumentationRunner`:

```groovy
// Our Bintray repository
repositories {
  maven {
    url 'http://dl.bintray.com/lemonade/maven'
  }
}

// TestNG dependency, remember to update to the latest version
dependencies {
  androidTestCompile 'de.lemona.android:testng:X.Y.Z'
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

In order to have access to the Android's application
[Context](http://developer.android.com/reference/android/content/Context.html)
please refer to the [`AndroidTestNGSupport`](src/main/java/de/lemona/android/testng/AndroidTestNGSupport.java)
utility class. The two static `getContext()` and `getInstrumentation()` methods
allow retrieval of the instances.

[Google Guice](https://github.com/google/guice) injection is also supported. Take a look at the 
[GuiceInjectionTest](src/androidTest/java/de/lemona/android/testng/test/GuiceInjectionTest.java)
for an example of how to configure your tests.


License
-------

Licensed under the [Apache License version 2](LICENSE.md)
