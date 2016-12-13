package master;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Forrest on 2016. 12. 13..
 */
public class TestDefaultController {
    private class SampleRequest implements Request {
        private static final String DEFAULT_NAME = "Test";
        private String name;

        public SampleRequest(String name) {
            this.name = name;
        }

        public SampleRequest() {
            this(DEFAULT_NAME);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    private class SampleHandler implements RequestHandler {
        public Response process(Request request) throws Exception {
            return new SampleResponse();
        }
    }

    private class SampleResponse implements Response {
        private static final String NAME = "Test";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = false;
            if (obj instanceof SampleResponse) {
                result = ((SampleResponse) obj).getName().equals(getName());
            }
            return result;
        }

        @Override
        public int hashCode() {
            return NAME.hashCode();
        }
    }

    private class SampleExceptionHandler implements RequestHandler {
        public Response process(Request request) throws Exception {
            throw new Exception("error processing request");
        }
    }


    private DefaultController controller;
    private Request request;
    private RequestHandler handler;

    @Test
    public void testGetHandler() throws Exception {

    }


    @Test
    public void testProcessRequest() throws Exception {
        Response response = controller.processRequest(request);
        assertNotNull("Must not return a null response", response);
        assertEquals("Response should be of type SampleResponse", new SampleResponse(), response);

    }

    @Test
    public void testAddHandler() throws Exception {
        RequestHandler handler2 = controller.getHandler(request);
        assertSame("Handler we set in controller should be the same handler we get", handler2, handler);
    }

    @Before
    public void initialize() throws Exception {

        controller = new DefaultController();
        request = new SampleRequest();
        handler = new SampleHandler();
        controller.addHandler(request, handler);

    }

    @Test(expected = RuntimeException.class)
    public void testMethod() {
        throw new RuntimeException("implement me");
    }

    @Test
    public void testProcessRequestAnswersErrorResponse() {
        SampleRequest request = new SampleRequest("testError");
        SampleExceptionHandler handler = new SampleExceptionHandler();
        controller.addHandler(request, handler);
        Response response = controller.processRequest(request);
        assertNotNull("Must not return a null response", response);
        assertEquals(ErrorResponse.class, response.getClass());

    }

    @Test(expected = RuntimeException.class)
    public void testGetHandlerNotDefined() {
        SampleRequest request = new SampleRequest("testNotDefined");

        controller.getHandler(request);
    }

    @Test(expected = RuntimeException.class)
    public void testAddRequestDuplicateName() {
        SampleRequest request = new SampleRequest();
        SampleHandler handler = new SampleHandler();

        controller.addHandler(request, handler);
    }

    @Test(timeout = 130)
    @Ignore(value = "Ignore for now untildecent time-limit")
    public void testProcessMultipleRequestsTimeout() {
        Request request;
        Response response = new SampleResponse();
        RequestHandler handler = new SampleHandler();

        for (int i = 0; i < 99999; i++) {
            request = new SampleRequest(String.valueOf(i));
            controller.addHandler(request, handler);

            response = controller.processRequest(request);
            assertNotNull(response);
            assertNotSame(ErrorResponse.class, response.getClass());
        }
    }
}