package kr.jhha.engquiz.quizfolder.detail;

import android.graphics.drawable.Drawable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jhha on 2016-12-17.
 */

public class QuizFolderDetailItem
{
    private Drawable iconDrawable ;
    private String scriptTitle;

    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }
    public void setTitle(String title) {
        scriptTitle = title;
    }

    public String getTitle() {
        return scriptTitle;
    }
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
}