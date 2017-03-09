package kr.jhha.engquiz.net;

public abstract class Protocol
{
    protected Request request = null;
    protected Response response = null;

    public Protocol( Integer pid ) {
        request = new Request( pid );
        response = new Response();
    }

    public Request getRequest() {
        return request;
    }
    public Response getResponse() {
        return response;
    }

    public Response callServer() {
        request.serialize();
        String responseString = new Http().httpRequestPost( request.getRequestString() );
        response.unserialize( responseString );
        return parseResponseMore();
    }
    abstract protected Response parseResponseMore();
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


