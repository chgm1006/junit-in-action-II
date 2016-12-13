package master;

/**
 * Created by Forrest on 2016. 12. 13..
 */
public interface RequestHandler {
    Response process(Request request) throws Exception;
}
