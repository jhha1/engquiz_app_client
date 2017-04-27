package kr.jhha.engquiz.model.remote;

import android.os.AsyncTask;
import android.util.Log;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;
import kr.jhha.engquiz.util.exception.system.SystemException;
import kr.jhha.engquiz.util.ui.MyLog;

public class AsyncNet extends AsyncTask<Void, Void, Void>
{
    public interface Callback<T> {
        void onResponse( Response response );
    }

    private Callback callback;
    private Request request;
    private Response response;
    private String requestString;
    private String responseString;
    private SystemException exception;

    public AsyncNet( Request request, Callback callback ){
        super();
        this.request = request;
        this.response = new Response();
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.request.serialize();
        requestString = request.getRequestString();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            this.responseString = Net.call(requestString);
        } catch (MyIllegalStateException e) {
            this.exception = e;
        } catch(Exception e){
            this.exception = new MyIllegalStateException(EResultCode.UNKNOUN_ERR, e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if( exception != null ){
            MyLog.e("Net Exception. code:" + exception.getErrorCode() + ", msg:"+exception.getMessage());
            this.responseString = response.makeErrResponseString(exception);
            MyLog.e("ResponseString by Client:" + responseString);
        }
        response.unserialize(responseString);
        callback.onResponse( response );
    }

    static class Net {
        static String call( final String requestString )
        {
            return Http.requestPost( requestString );
        }
    }
}

