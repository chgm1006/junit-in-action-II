package master;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Forrest on 2016. 12. 13..
 */
public class DefaultController implements Controller {
    private Map requestHandlers = new HashMap();

    protected RequestHandler getHandler(Request request) {

        if (!this.requestHandlers.containsKey(request.getName())) {
            String message = "Cannot find handler for request name " + "[" + request.getName() + "]";
            throw new RuntimeException(message);
        }

        return (RequestHandler) this.requestHandlers.get(request.getName());
    }

    public Response processRequest(Request request) {
        Response response;
        try {
            response = getHandler(request).process(request);
        } catch (Exception ex) {
            response = new ErrorResponse(request, ex);

        }
        return response;
    }

    public void addHandler(Request request, RequestHandler requestHandler) {

        if (this.requestHandlers.containsKey(request.getName())) {
            throw new RuntimeException("A request hanlder has " + "already been registerd for request name " + "[" + request.getName() + "])");
        } else {
            this.requestHandlers.put(request.getName(), requestHandler);
        }

    }
}
