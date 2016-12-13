package master;

/**
 * Created by Forrest on 2016. 12. 13..
 */
public interface Controller {
    Response processRequest(Request request);

    void addHandler(Request request, RequestHandler requestHandler);
}
