package kr.jhha.engquiz.presenter_view.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.FragmentHandler;

/**
 * Created by jhha on 2016-12-16.
 */

public class WebViewFragment extends Fragment
{
    WebView mWebView;

    private FragmentHandler.EFRAGMENT mWhichUrlShow;

    private static String URL_ROOT = "http://52.78.150.139:8080/engquiz/help/";
    public static String URL_QUICK_GUIDE = URL_ROOT + "quick_guide.htm";

    public WebViewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_webview, container, false);
        mWebView = (WebView)view.findViewById(R.id.webview);

        mWebView.setBackgroundColor(0); //배경색
        mWebView.setHorizontalScrollBarEnabled(false); //가로 스크롤
        mWebView.setVerticalScrollBarEnabled(false); //세로 스크롤
        //mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //스크롤 노출타입

        //HTML을 파싱하여 웹뷰에서 보여주거나 하는 작업에서
        //width , height 가 화면 크기와 맞지 않는 현상이 발생한다
        //이를 잡아주기 위한 코드
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //캐시파일 사용 금지(운영중엔 주석처리 할 것)
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //zoom 허용
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);

        //javascript의 window.open 허용
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //javascript 허용
        mWebView.getSettings().setJavaScriptEnabled(true);

        //meta태그의 viewport사용 가능
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.loadUrl(getUrl());
        mWebView.setWebViewClient(new MyWebViewClient());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String getUrl(){
        String url = URL_QUICK_GUIDE;
        switch (mWhichUrlShow){
            case PLAYQUIZ:
                url = URL_QUICK_GUIDE;
            break;
        }
        return url;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }

    // 뒤로 가기 버튼 클릭시, 앱뷰를 닫도록 한다.
    public boolean canGoBack(){
        if( mWebView == null )
            return false;

        return mWebView.canGoBack();
    }

    // 뒤로 가기 버튼 클릭시, 앱뷰를 닫도록 한다.
    public void goBack(){
        if( mWebView != null )
            mWebView.goBack();
    }

    public void setHelpWhat(FragmentHandler.EFRAGMENT enumFragment){
        mWhichUrlShow = enumFragment;
    }
}

