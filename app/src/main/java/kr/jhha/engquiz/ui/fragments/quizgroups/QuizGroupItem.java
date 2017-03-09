package kr.jhha.engquiz.ui.fragments.quizgroups;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jhha on 2016-12-17.
 */

public class QuizGroupItem
{
    private Integer dbIndex;
    private int tag;
    private String title;
    private String desc;
    private List<Integer> scriptIndexes = null;
    private long createdDateTime;

    public class TAG {
        public static final int PLAYING = 0x00000001;
        public static final int NEW = 0x00000010;
        public static final int DEFAULT = 0x00000002;
        public static final int BUTTON_MAKE_NEW = 0x00000004;
        public static final int OTHERS = 0x00000008;
    }

    public QuizGroupItem()
    {
        dbIndex = 0;
        createdDateTime = 0;
        tag = TAG.DEFAULT;
        this.scriptIndexes = new LinkedList<>();
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setScriptIndexes( List<Integer> indexes ) {
        scriptIndexes = indexes;
    }
    public void setCreatedDateTime( long dateTime ) {
        this.createdDateTime = dateTime;
    }

    public int getTag() { return tag; }
    public String getTitle() {
        return this.title;
    }
    public String getDesc() {
        return this.desc;
    }
    public long getCreatedDateTime() {
        return this.createdDateTime;
    }
    public final List<Integer> getScriptIndexes() {
        return scriptIndexes;
    }

    public void addScriptIndex( Integer newScriptIndex ) {
        scriptIndexes.add( newScriptIndex );
    }


}