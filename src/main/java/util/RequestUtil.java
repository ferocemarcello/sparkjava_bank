package util;

import spark.Request;

public class RequestUtil {
    /**
     * @param request
     * @return true if the clients accepts html
     */
    public static boolean clientAcceptsHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    /**
     * not used actually, might be useful
     *
     * @param request
     * @return true if the clients accepts json
     */
    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

}
