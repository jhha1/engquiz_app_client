package kr.jhha.engquiz.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by thyone on 2017-04-13.
 */

public class Dialogs {
    public static void showOKDialog(Context context, String title, String message, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        builder.show();
    }

    public static void showEditDialog(Context context, String title, EditText editText, DialogInterface.OnClickListener listener) {
        showEditDialog(context, title, "", editText, listener);
    }

    public static void showEditDialog(Context context, String title, String message, EditText editText, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog alertd = builder.create();
        alertd.setCancelable(false);
        alertd.show();
    }

    public static EditText makeEditText(Context context){
        return makeEditText(context, null);
    }

    public static EditText makeEditText(Context context, TextView.OnEditorActionListener listener){
        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        //editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if( listener != null )
            editText.setOnEditorActionListener(listener);

        return editText;
    }
}
