package kr.jhha.engquiz.presenter_view.sync;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SYNC;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class SyncFragment extends Fragment implements SyncContract.View
{
    private SyncContract.UserActionsListener mActionListener;

    private TextView mSyncReadyTextView;
    private TextView mSubTextView;
    private TextView mSubTextView2;
    private Button mDownloadButton = null;

    private ProgressDialog mDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new SyncPresenter( getActivity(), this, ScriptRepository.getInstance() );
        initDialog();
    }

    private void initDialog(){
        mDlg = new ProgressDialog(getActivity());
        mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDlg.setMessage("Start");
       // mDlg.setToolbarBackground();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_sync, container, false);

        setUpToolBar();

        // 텍스트 뷰
        mSyncReadyTextView = (TextView) view.findViewById(R.id.sync_textview);
        mSubTextView = (TextView) view.findViewById(R.id.update_text_description2);
        mSubTextView2 = (TextView) view.findViewById(R.id.update_text_description3);

        // 버튼 클릭 이벤트 셋팅
        mDownloadButton = (Button) view.findViewById(R.id.sync_btn_download);
        mDownloadButton.setOnClickListener(mClickListener);

        mActionListener.initView();

        return view;
    }

    // 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sync_btn_download:
                    mActionListener.sync();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(SYNC);
    }

    @Override
    public void onSyncReady(float sizeMB) {
        String msg = "약 " + sizeMB + "MB의 데이터를 다운로드 "
                     +"할 예정이에요. WIFI 접속이 아닐경우 "
                    +"데이터 요금이 부과되어요. 데이터가 큰 경우, " +
                    "WIFI 접속 환경에서 다운로드 해주세요.";
        mSyncReadyTextView.setText(msg);
        mDownloadButton.setClickable(true);
    }

    @Override
    public void onSynced() {
        String msg = "넹~ 모든 문장들이 최신이에요.";
        mSubTextView.setText(msg);
        mSubTextView2.setVisibility(View.INVISIBLE);
        mSyncReadyTextView.setVisibility(View.INVISIBLE);
        mDownloadButton.setClickable(false);
    }

    @Override
    public void onFailedSync(String msg) {
        mSubTextView.setText(msg);
    }
}
