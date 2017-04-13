package kr.jhha.engquiz.presenter_view.sync;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class SyncFragment extends Fragment implements SyncContract.View
{
    private SyncContract.UserActionsListener mActionListener;

    private LinearLayout mDownloadLayout = null;
    private LinearLayout mDownloadComplateLayout = null;

    private TextView mTextView;

    private Button mDownloadButton = null;

    private ProgressDialog mDlg;

    private final String mTITLE = "Sync Quizs";

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
       // mDlg.switchBackground();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_sync, container, false);

        setUpToolBar();

        // 레이아웃 가져오기. 다운로드 전/후에 레이아웃 체인지 위해.
        mDownloadLayout = (LinearLayout) view.findViewById(R.id.update_layout_check_download);
        mDownloadComplateLayout = (LinearLayout) view.findViewById(R.id.update_layout_complate_download);

        // 텍스트 뷰
        mTextView = (TextView) view.findViewById(R.id.update_text_datadownload_warning);

        // 버튼 클릭 이벤트 셋팅
        mDownloadButton = (Button) view.findViewById(R.id.update_btn_download);
        mDownloadButton.setOnClickListener(mClickListener);

        mActionListener.initView();

        return view;
    }

    // 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update_btn_download:
                    mActionListener.sync();
                    changeLayout();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        final MyToolbar toolbar = MyToolbar.getInstance();
        toolbar.setToolBarTitle( mTITLE );
        toolbar.switchBackground("image");
    }

    private void changeLayout() {
        mDownloadLayout.setVisibility( View.INVISIBLE );
        mDownloadComplateLayout.setVisibility( View.VISIBLE );
    }


    @Override
    public void onSuccessSync() {
        String msg = "동기화에 성공했습니다.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailedSync(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showView(String msg) {
        mTextView.setText(msg);
    }
}
