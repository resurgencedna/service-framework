package sf.response;

import java.io.Serializable;

public class Response implements Serializable {

    private Object data;

    public Response() { }

    public Response(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
