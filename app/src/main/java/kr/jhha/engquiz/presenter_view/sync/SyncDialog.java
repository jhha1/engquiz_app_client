package kr.jhha.engquiz.presenter_view.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyDialog;


/**
 * Created by Junyoung on 2016-06-23.
 */

public class SyncDialog implements SyncContract.View
{
    public interface SyncedCallback{
        void onSynced();
    }

    private Context mContext;
    private SyncContract.UserActionsListener mActionListener;

    private SyncedCallback mSyncedCallback;

    public SyncDialog(Context context) {
        mContext = context;
        mActionListener = new SyncPresenter( context, this, ScriptRepository.getInstance() );
    }

    public void show( SyncedCallback callback ){
        mSyncedCallback = callback;
        mActionListener.initView();
    }

    @Override
    public void onSyncReady(float sizeMB)
    {
        final MyDialog dialog = new MyDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.sync__title));
        dialog.setCancelable(true);
        View view = createSyncReadyView(dialog, sizeMB);
        dialog.setCustomView(view, mContext);
        dialog.showUp();
    }

    private View createSyncReadyView(final MyDialog dialog, float sizeMB)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.content_sync, null);

        // 텍스트 뷰
        TextView mSyncReadyTextView = (TextView) view.findViewById(R.id.sync_textview);
        TextView mDescription2 = (TextView) view.findViewById(R.id.update_text_description2);
        TextView mDescription1 = (TextView) view.findViewById(R.id.update_text_description1);

        int msgId = R.string.sync__description1_sync_ready;
        mDescription1.setText(mContext.getString(msgId));
        mDescription2.setVisibility(View.VISIBLE);
        String msg = mContext.getString(R.string.sync__description3_sync_ready1)
                + " " + sizeMB
                + mContext.getString(R.string.sync__description3_sync_ready2);
        mSyncReadyTextView.setText(msg);

        // 버튼 클릭 이벤트 셋팅
        Button mDownloadButton = (Button) view.findViewById(R.id.sync_btn_download);
        mDownloadButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sync_btn_download:
                        mActionListener.sync();
                        dialog.dismiss();
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onFailedSync(int msgId){
        MyDialog dialog = new MyDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.common__warning));
        dialog.setMessage( mContext.getString(msgId) );
        dialog.setPositiveButton();
        dialog.showUp();
    }

    public void onSuccessSync(){
        mSyncedCallback.onSynced();
        Toast.makeText(mContext,
                mContext.getString(R.string.sync__description1_synced),
                Toast.LENGTH_SHORT).show();
    }
}
