package kr.jhha.engquiz.util.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-04-19.
 */

public class Etc
{
    public static EditText makeEditText(Context context){
        return makeEditText(context, StringHelper.EMPTY_STRING, null);
    }

    public static EditText makeEditText(Context context, String initText){
        return makeEditText(context, initText,  null);
    }

    public static EditText makeEditText(Context context, String initText, TextView.OnEditorActionListener listener){
        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextColor(Color.DKGRAY);
        editText.setPadding(10, 5, 5, 5);
        editText.setText(initText);
        //editText.setImeOptions(EditorInfo.IME_ACTION_DONE);.////////
        if( listener != null )
            editText.setOnEditorActionListener(listener);

        return editText;
    }
}
