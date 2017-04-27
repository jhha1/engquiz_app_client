package kr.jhha.engquiz.util.ui;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by thyone on 2017-04-13.
 */

public class Actions {
    public static void hideKeyboard(Context context)
    {
        InputMethodManager immhide =
                (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
