package com.manning.junitbook.ch12.htmlunit;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.After;
import org.junit.Before;

/**
 * Manages an HtmlUnit WebClient on behalf of subclasses. The point of the class
 * is to make sure closeAllWindows() is called when a test is done with a
 * WebClient instance.
 *
 * @author ggregory
 * @version $Id: JavadocPageTest.java 392 2009-04-28 23:38:33Z garydgregory $
 */
public abstract class ManagedWebClient {
    protected WebClient webClient;

    protected WebClient getWebClient() {
        return this.webClient;
    }

    protected void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Before
    public void setUp() {
        this.setWebClient(new WebClient());
    }

    @After
    public void tearDown() {
        this.getWebClient().closeAllWindows();
    }
}
