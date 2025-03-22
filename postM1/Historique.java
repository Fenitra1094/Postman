package historique;
import java.io.Serializable;

public class Historique implements Serializable {
    private static final long serialVersionUID = 1L;

    private String method;
    private String url;
    private String headers;
    private String body;
    private String response;

    public Historique(String method, String url, String headers, String body, String response) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.response = response;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getResponse() {
        return response;
    }
}

