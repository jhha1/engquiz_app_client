package kr.jhha.engquiz.model.remote;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;
import kr.jhha.engquiz.util.ui.MyLog;

public class Http
{
    /* 개발시에는 에뮬레이터의 호스트 컴퓨터 주소를 사용해야 한다.
    * localhost, 127.0.0.1 등은 모바일 기기에서 실행되는 웹서버를 의미하므로 안됨
    */
    final private static String ContentType = "text/html; charset=utf-8";
    //final private static String url = "http://192.168.0.6:8080/engquiz_server/servlet";
    final private static String url = "http://52.78.150.139:8080/engquiz/servlet";

    private static final int CONN_TIMEOUT = 3000;
    private static final int SO_TIMEOUT = 5000;

    public static String requestPost(String requestString )
    {
        try {
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(url);

            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            List params = new ArrayList();
            params.add(new BasicNameValuePair("json", requestString));
            UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(params, "UTF_8");
            post.setEntity( requestEntity );

            MyLog.i("[HTTP REQUEST:URL{" + post.getURI() + "},DATA{" + requestString + "}");
            HttpResponse responsePost = client.execute(post);

            HttpEntity responseEntity = responsePost.getEntity();
            if( responseEntity == null ) {
                System.out.println("ERROR response httpEntity is null");
                return null;
            }
            String responseString = EntityUtils.toString( responseEntity );
            MyLog.i("[HTTP RESPONSE:URL{" + post.getURI() + "},DATA{" + responseString +"}");

            return responseString;

        } catch (UnsupportedEncodingException e ) {
            throw new MyIllegalStateException(EResultCode.ENCODING_ERR, e);
        } catch (IOException e){
            throw new MyIllegalStateException(EResultCode.NETWORK_ERR, e);
        } catch (Exception e){
            throw new MyIllegalStateException(EResultCode.UNKNOUN_ERR, e);
        }
    }
}