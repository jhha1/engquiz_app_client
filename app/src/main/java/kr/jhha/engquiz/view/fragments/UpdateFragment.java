package kr.jhha.engquiz.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.controller.FileManager;
import kr.jhha.engquiz.model.Const;
import kr.jhha.engquiz.view.MainActivity;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class UpdateFragment extends Fragment
{
    private LinearLayout mDownloadLayout = null;
    private LinearLayout mDownloadComplateLayout = null;

    private Button mDownloadButton = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_update, container, false);

        // 레이아웃 가져오기. 다운로드 전/후에 레이아웃 체인지 위해.
        mDownloadLayout = (LinearLayout) view.findViewById(R.id.update_layout_check_download);
        mDownloadComplateLayout = (LinearLayout) view.findViewById(R.id.update_layout_complate_download);

        // 버튼 클릭 이벤트 셋팅
        mDownloadButton = (Button) view.findViewById(R.id.update_btn_download);
        mDownloadButton.setOnClickListener(mClickListener);

        return view;
    }

    // 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update_btn_download:
                    startDownload();
                    changeLayout();
                    break;
            }
        }
    };

    private void startDownload() {
        String url = "http://cfs11.blog.daum.net/image/5/blog/2008/08/22/18/15/48ae83c8edc9d&filename=DSC04470.JPG";
        DownloadFileAsync mDownloader = new DownloadFileAsync( getActivity() );
        mDownloader.execute(url, "1", "1");
    }

    private void changeLayout() {
        mDownloadLayout.setVisibility( View.INVISIBLE );
        mDownloadComplateLayout.setVisibility( View.VISIBLE );
    }

}

class DownloadFileAsync extends AsyncTask<String, String, String> {

    private ProgressDialog mDlg;
    private Context mContext;

    public DownloadFileAsync(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mDlg = new ProgressDialog(mContext);
        mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDlg.setMessage("Start");
        mDlg.show();

        super.onPreExecute();
    }

    @Override
    // 시작후 돌아가는 중심로직이 들어가는 함수
    protected String doInBackground(String... params) {

        int count = 0;

        try {
            Thread.sleep(100);
            URL url = new URL(params[0].toString());
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());

            String tempDownloadFolder = FileManager.getInstance().getAndroidAbsolutePath(Const.KaKaoDownloadFolder_AndroidPath);
            OutputStream output = new FileOutputStream( tempDownloadFolder + "Downloadtest.jpg");

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            // 작업이 진행되면서 호출하며 화면의 업그레이드를 담당하게 된다
            //publishProgress("progress", 1, "Task " + 1 + " number");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 수행이 끝나고 리턴하는 값은 다음에 수행될 onProgressUpdate 의 파라미터가 된다
        return null;
    }

    @Override
    // 실시간으로 작업결과를 프로그레시브바에 업데이트
    protected void onProgressUpdate(String... progress) {
        if (progress[0].equals("progress")) {
            mDlg.setProgress(Integer.parseInt(progress[1]));
            mDlg.setMessage(progress[2]);
        } else if (progress[0].equals("max")) {
            mDlg.setMax(Integer.parseInt(progress[1]));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    // 종료할 때 프로그레시브바를 사라지게 하는
    protected void onPostExecute(String unused) {
        mDlg.dismiss();
        //Toast.makeText(mContext, Integer.toString(result) + " total sum",
        //Toast.LENGTH_SHORT).show();
    }
}
