package endpoint.backend.model;

/**
 * Created by bradenhart on 7/09/15.
 */
/**
 * The object model for the data we are sending through endpoints
 */
public class MyBean {

    private String myData;

    public String getData() {
        return myData;
    }

    public void setData(String data) {
        myData = data;
    }
}
