package kr.jhha.engquiz.data.remote;

import android.os.AsyncTask;

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
        this.responseString = Net.call( requestString );
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        response.unserialize( responseString );
        callback.onResponse( response );
    }

    static class Net {
        static String call( final String requestString ) {
            return Http.requestPost( requestString );
        }
    }
}

