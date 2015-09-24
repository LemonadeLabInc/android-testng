package de.lemona.android.testng.test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class DataProviderTest {

    @DataProvider(name="simpleProvider")
    public Object[][] provideData() {
        return new Object[][] {
                new Object[] { "foo" },
                new Object[] { "bar" },
                new Object[] { "baz" },
        };
    }

    @Test(dataProvider="simpleProvider")
    public void testWithData(String data) {
        Assert.assertNotNull(data);
        if ("foo".equals(data)) return;
        if ("bar".equals(data)) return;
        if ("baz".equals(data)) return;
        Assert.fail("No \"foo\", \"bar\" or \"baz\" => \"" + data + "\"");
    }
}

