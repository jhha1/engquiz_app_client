package kr.jhha.engquiz.net.protocols;

import kr.jhha.engquiz.net.AsyncNet;
import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

/**
 * Created by thyone on 2017-03-16.
 */

public class Protocol  {

    protected Request request = null;
    protected Response response = null;

    interface CallbackResponse {
        void onResponse( Response response );
    }
    private AsyncNet.Callback mContentsCallback;

    public Protocol(Integer pid ) {
        request = new Request( pid );
        response = new Response();
    }

    public void setRequest(EProtocol name, Object value ) {
        request.set( name, value );
    }

    public Response getResponse() {
        return response;
    }

    public void request(final AsyncNet.Callback callback ) {
        request.serialize();
        mContentsCallback = callback;
    }

    public void onResponse( Object responseString ) {
        if( responseString == null ) {

        }

        if( ! (responseString instanceof String) ) {

        }

        response.unserialize( (String) responseString );
        mContentsCallback.onResponse( response );
    }
}
