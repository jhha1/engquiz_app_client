package kr.jhha.engquiz.presenter_view.sync;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.SyncRepository;
import kr.jhha.engquiz.presenter_view.MyNavigationView;
import kr.jhha.engquiz.util.StringHelper;
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
        mActionListener = new SyncPresenter( context, this, SyncRepository.getInstance() );
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
        TextView mDescription1 = (TextView) view.findViewById(R.id.update_text_description1);

        String msg = mContext.getString(R.string.sync__description2_sync_ready);
        mDescription1.setText(StringHelper.formatHtml(msg));
        mSyncReadyTextView.setText(mContext.getString(R.string.sync__description3_sync_ready1)
                + " " + sizeMB
                + mContext.getString(R.string.sync__description3_sync_ready2));

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

    public void onSuccessSync()
    {
        detachSyncAlarmIcons();

        mSyncedCallback.onSynced();

        Toast.makeText(mContext,
                mContext.getString(R.string.sync__description1_synced),
                Toast.LENGTH_SHORT).show();
    }

    private void detachSyncAlarmIcons(){
        // 알람 아이콘 떼기 : 네비게이션 메뉴
        final MyNavigationView navigationView = MyNavigationView.getInstance();
        navigationView.detachAlarmIcon(R.id.nav_scripts);
        // 알람 아이콘 떼기 : floating btn
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.content_scripts, null);
        FloatingActionButton mSyncFabBtn = (FloatingActionButton) view.findViewById(R.id.script_sync_fab);
        mSyncFabBtn.setVisibility(View.GONE);
    }

    public static void attachSyncAlarmIcons(Context context){
        // 알람 띄우기 : 네비게이션 메뉴
        final MyNavigationView navigationView = MyNavigationView.getInstance();
        navigationView.attachAlarmIcon(R.id.nav_scripts);

        // 알람 아이콘 : floating btn
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.content_scripts, null);
        FloatingActionButton mSyncFabBtn = (FloatingActionButton) view.findViewById(R.id.script_sync_fab);
        mSyncFabBtn.setVisibility(View.VISIBLE);
    }
}
