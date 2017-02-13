package kr.jhha.engquiz.net;

public interface Protocol
{
    Request getRequest();
    Response getResponse();

    Object parseResponse( String responseString );
}

/**
 * Created by thyone on 2017-02-04.

public class Protocol
{
    private Request request;
    private Response response;

    public Protocol() {
        request = new Request();
        response = new Response();
    }

    public void setRequestParam( EProtocol key, Object vaule ) {
        this.request.set( key, vaule );
    }

    public String getRequestString() {
        return this.request.toJsonString();
    }

    public Object getResponseParam( EProtocol key ) {
        return this.response.get( key );
    }

    public String getResponseString() {
        return this.response.getResponseString();
    }

    public void setResponseString(String responseString ) {
        this.response.setResponseString( responseString );
    }

    public void parseResponse( String responseString ) {
        this.response.parse( responseString );
    }

    public void callbackServerResponse(){};
}
 */


