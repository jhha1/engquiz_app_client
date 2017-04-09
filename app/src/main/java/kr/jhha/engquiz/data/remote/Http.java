package kr.jhha.engquiz.data.remote;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class Http
{
    /* 개발시에는 에뮬레이터의 호스트 컴퓨터 주소를 사용해야 한다.
    * localhost, 127.0.0.1 등은 모바일 기기에서 실행되는 웹서버를 의미하므로 안됨
    */
    final private static String ContentType = "text/html; charset=utf-8";
    final private static String url = "http://192.168.0.6:8080/engquiz_server";
    //final private static String url = "http://172.30.51.58:8080/engquiz_server";

    public static String requestPost(String requestString )
    {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            List params = new ArrayList();
            params.add(new BasicNameValuePair("json", requestString));
            UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(params, "UTF_8");
            post.setEntity( requestEntity );

            //post.setEntity( new UrlEncodedFormEntity(params,"utf-8") );
            //requestString = "JSON="+requestString;
            //post.setEntity( new StringEntity(requestString,"utf-8") );

            System.out.println("[HTTP REQUEST:URL{" + post.getURI() + "},DATA{" + requestString + "}");
            HttpResponse responsePost = client.execute(post);

            HttpEntity responseEntity = responsePost.getEntity();
            if( responseEntity == null ) {
                System.out.println("ERROR response httpEntity is null");
                return null;
            }
            String responseString = EntityUtils.toString( responseEntity );
            System.out.println("[HTTP RESPONSE:URL{" + post.getURI() + "},ENCODE:{"+responseEntity.getContentEncoding()+"},DATA{" + responseString +"}");

            return responseString;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}