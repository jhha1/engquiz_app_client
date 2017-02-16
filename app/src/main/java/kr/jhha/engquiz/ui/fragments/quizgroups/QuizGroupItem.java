package kr.jhha.engquiz.ui.fragments.quizgroups;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jhha on 2016-12-17.
 */

public class QuizGroupItem
{
    private Drawable iconDrawable ;
    private String titleStr ;
    private String descStr ;
    private List<Integer> scriptIndexes = null;

    public QuizGroupItem() {
        this.scriptIndexes = new LinkedList<>();
    }

    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }
    public void setTitle(String title) {
        titleStr = title;
    }
    public void setDesc(String desc) {
        descStr = desc;
    }
    public void setScriptIndexes( List<Integer> indexes ) {
        scriptIndexes = indexes;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public final List<Integer> getScriptIndexes() {
        return scriptIndexes;
    }

    public void addScriptIndex( Integer newScriptIndex ) {
        scriptIndexes.add( newScriptIndex );
    }
}