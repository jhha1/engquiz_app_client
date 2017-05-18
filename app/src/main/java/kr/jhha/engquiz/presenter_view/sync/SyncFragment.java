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
    private TextView mDescription2;
    private TextView mDescription1;
    private Button mDownloadButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new SyncPresenter( getActivity(), this, ScriptRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_sync, container, false);

        setUpToolBar();

        // 텍스트 뷰
        mSyncReadyTextView = (TextView) view.findViewById(R.id.sync_textview);
        mDescription2 = (TextView) view.findViewById(R.id.update_text_description2);
        mDescription1 = (TextView) view.findViewById(R.id.update_text_description1);

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
        int msgId = R.string.sync__description1_sync_ready;
        mDescription1.setText(getString(msgId));
        mDescription2.setVisibility(View.VISIBLE);

        String msg = "약 " + sizeMB + getString(R.string.sync__description3_sync_ready);
        mSyncReadyTextView.setText(msg);
        mDownloadButton.setClickable(true);
    }

    @Override
    public void onSynced() {
        int msgId = R.string.sync__description1_synced;
        mDescription1.setText(getString(msgId));

        mDescription2.setVisibility(View.INVISIBLE);
        mSyncReadyTextView.setVisibility(View.INVISIBLE);
        mDownloadButton.setClickable(false);
    }

    @Override
    public void onFailedSync(int msgId) {
        mDescription2.setText( getString(msgId) );
        mSyncReadyTextView.setVisibility(View.INVISIBLE);
        mDownloadButton.setClickable(false);
    }
}
