package kr.jhha.engquiz.util.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.app.AlertDialog;
//import android.support.v7.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.MainActivity;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-04-13.
 */

public class MyDialog extends AlertDialog.Builder
{
    private Context mContext;

    AlertDialog mRealDialog;

    /** The custom_body layout */
    private View mDialogTitle;
   private View mDialogBody;

    /** optional dialog title layout */
    private TextView mTitle = null;
    /** optional alert dialog image */
    private ImageView mIcon = null;

    /** optional message displayed below title if title exists*/
    private TextView mMessage = null;
    /** The colored holo divider. You can set its color with the setDividerColor method */
    private View mDivider;
    private LinearLayout mCustomLinearLayout = null;
    private Button mButtonOK;
    private Button mButtonCancel;
    private Button mButtonNeutral;

    View.OnClickListener mDismissOnClick = new View.OnClickListener() {
        public void onClick(View arg0) {
            dismiss();
        }};

    public MyDialog(Context context) {
        super(context, R.style.MyAlertDialogStyle);

        this.mContext = context;

        mDialogTitle = View.inflate(mContext, R.layout.common_dialog_title, null);
       // mDialogView = View.inflate(context, R.layout.custom_dialog_layout, null);

        mTitle = (TextView) mDialogTitle.findViewById(R.id.alertTitle);
        mIcon = (ImageView) mDialogTitle.findViewById(R.id.mydialog_title_icon);
        setCustomTitle(mDialogTitle);

        mDialogBody = View.inflate(mContext, R.layout.common_dialog_message, null);
        mDivider = mDialogBody.findViewById(R.id.titleDivider);
        mMessage = (TextView) mDialogBody.findViewById(R.id.message);
        mCustomLinearLayout = ((LinearLayout)mDialogBody.findViewById(R.id.customPanel1));
        mButtonOK = (Button) mDialogBody.findViewById(R.id.mydialog_button_ok);
        mButtonCancel = (Button) mDialogBody.findViewById(R.id.mydialog_button_cancel);
        mButtonNeutral = (Button) mDialogBody.findViewById(R.id.mydialog_button_neutral);
        setView(mDialogBody);

        setCancelable(false); // Back키 눌렀을 경우 Dialog Cancle 안됨.
    }

    /**
     * Use this method to color the divider between the title and content.
     * Will not display if no title is set.
     *
     * @param colorString for passing "#ffffff"
     */
    public MyDialog setDividerColor(String colorString) {
        return setDividerColor( Color.parseColor(colorString));
    }

    public MyDialog setDividerColor(int colorId) {
        mDivider.setVisibility(View.VISIBLE);
        mDivider.setBackgroundColor(colorId);
        return this;
    }

    @Override
    public MyDialog setTitle(CharSequence text) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(text);
        //setTitleColor(R.color.colorAccent);
        return this;
    }

    public MyDialog setTitleColor(String colorString) {
        return setTitleColor(Color.parseColor(colorString));
    }

    public MyDialog setTitleColor(int colorId) {
        mTitle.setTextColor(colorId);
        return this;
    }

    @Override
    public MyDialog setMessage(CharSequence msg) {
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText(msg);
        return this;
    }

    @Override
    public MyDialog setIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public MyDialog setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }


    /**
     * This allows you to specify a custom layout for the area below the title divider bar
     * in the dialog. As an example you can look at example_ip_address_layout.xml and how
     * I added it in TestDialogActivity.java
     *
     * @param resId  of the layout you would like to add
     * @param context
     */
    public MyDialog setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
       ((LinearLayout)mDialogBody.findViewById(R.id.customPanel1)).addView(customView);
        return this;
    }

    public MyDialog setListView(Context context, ListAdapter adapter,
                                final DialogInterface.OnClickListener listener)
    {
        ListView listView = new ListView(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // adapter item click listener로 event를 넘겨준다.
                // dialog.dismiss()가 DialogInterface.OnClickListener 에서만 먹혀서 이렇게 꼼수.
                listener.onClick(mRealDialog, position);
            }
        });
        mCustomLinearLayout = ((LinearLayout)mDialogBody.findViewById(R.id.customPanel1));
        mCustomLinearLayout.setVisibility(View.VISIBLE);
        mCustomLinearLayout.addView(listView);

        // listview 테두리: 레이아웃에 백그라운드 넣고 리스트 포함시켜놓고 Padding 주면.. 안됨 ㅜ
        // 그냥 listview 배경색 적당한거 넣어서 구분가도록함
        //int listviewColor = ContextCompat.getColor(context, R.color.yellow_10);
        //listView.setBackgroundColor(listviewColor);
        listView.setPadding(12, 0, 0, 0);
        return this;
    }

    public MyDialog setEditText(EditText editText) {
        setEditText(editText, null);
        return this;
    }

    public MyDialog setEditText(EditText editText1, EditText editText2) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        if( editText1 != null) {
            editText1.setSingleLine(true);
            layout.addView(editText1);
        }
        if( editText2 != null) {
            editText2.setSingleLine(true);
            layout.addView(editText2);
        }
        // 10 spacing, left and right
        layout.setPadding(30, 20, 30, 20);

        mCustomLinearLayout.addView(layout);

        return this;
    }
    public MyDialog setPositiveButton(){
        return setPositiveButton(mDismissOnClick);
    }

    public MyDialog setNegativeButton(){
        return setNegativeButton(mDismissOnClick);
    }

    public MyDialog setPositiveButton(View.OnClickListener listener) {
        String text = mContext.getString(android.R.string.ok);
        if( listener != null ) {
            setPositiveButton(text, listener);
        } else {
            setPositiveButton(text, mDismissOnClick);
        }
        return this;
    }

    public MyDialog setNegativeButton(View.OnClickListener listener) {
        String text = mContext.getString(android.R.string.cancel);
        if( listener != null ) {
            setNegativeButton(text, listener);
        } else {
            setNegativeButton(text, mDismissOnClick);
        }
        return this;
    }

    public AlertDialog.Builder setPositiveButton(String text, View.OnClickListener listener) {
        mButtonOK.setText(text);
        mButtonOK.setOnClickListener(listener);
        mButtonOK.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialog.Builder setNegativeButton(String text, View.OnClickListener listener) {
        mButtonCancel.setText(text);
        mButtonCancel.setOnClickListener(listener);
        mButtonCancel.setVisibility(View.VISIBLE);
        return this;
    }

    public MyDialog setNeutralButton(String text, View.OnClickListener listener) {
        mButtonNeutral.setText(text);
        if( listener != null ) {
            mButtonNeutral.setOnClickListener(listener);
        } else {
            mButtonNeutral.setOnClickListener(mDismissOnClick);
        }
        mButtonNeutral.setVisibility(View.VISIBLE);
        return this;
    }

    @Override
    public AlertDialog.Builder setCancelable(boolean cancelable) {
        return super.setCancelable(cancelable);
    }

    public void showUp() {
        // 안쓰는 레이아웃은 날려서 다이알로그 내 불필요한 빈공간이 생기는걸 방지
        boolean bNeedToHideTitle = (mTitle == null) || (mTitle.getText().equals(StringHelper.EMPTY_STRING));
        boolean bNeedToHideMessage = (mMessage == null) || (mMessage.getText().equals(StringHelper.EMPTY_STRING));
        if (bNeedToHideTitle) {
            mDialogTitle.setVisibility(View.GONE);
            mDivider.setVisibility(View.INVISIBLE);
        }
        if (bNeedToHideMessage) {
            mMessage.setVisibility(View.GONE);
        }

        mRealDialog = this.create();
        // 다이알로그 뒤에 흰색 사각형 판넬 안보이게
        mRealDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mRealDialog.show();
    }

    public void dismiss(){
        mRealDialog.dismiss();
    }

    /*
        Dialog Samples
     */
    public static void showDialogAndForcedCloseApp(final Context context, String msg ){
        final MyDialog dialog = new MyDialog(context);
        dialog.setTitle(context.getString(R.string.common__finish_app));
        dialog.setMessage(msg);
        dialog.setPositiveButton(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                ((MainActivity)context).finishApp();
                dialog.dismiss();
            }});
        dialog.showUp();
    }
}
