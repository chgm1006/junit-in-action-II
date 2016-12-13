package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author ggregory
 */
public class AllTests extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("com.google.gwt.sample.stockwatcher.client.AllTests");
        // $JUnit-BEGIN$
        suite.addTestSuite(StockPriceTest.class);
        suite.addTestSuite(StockWatcherTest.class);
        // $JUnit-END$
        return suite;
    }

}
