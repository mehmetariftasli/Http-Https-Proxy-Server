package networkProject;

public class HttpResponse {
    int statusCode;
    String reasonPhrase;
    MimeHeader mh;

    public HttpResponse(String request) {
        String[] myRequest = request.split("\n", 2);
        String theRequest = myRequest [0];
        String[] finalRequest = theRequest.split(" ", 3);
        statusCode = Integer.parseInt(finalRequest[1]);
        reasonPhrase = finalRequest[2];
        String raw_mime_header = finalRequest[1];
        mh = new MimeHeader(raw_mime_header);
    }

    public HttpResponse(int code, String reason, MimeHeader m) {
        statusCode = code;
        reasonPhrase = reason;
        mh = m;
        mh.put("Connection", "close");
    }

    public String toString() {
        return "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n" + mh + "\r\n";
    }
}