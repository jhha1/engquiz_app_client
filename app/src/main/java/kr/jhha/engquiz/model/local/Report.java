package kr.jhha.engquiz.model.local;

import java.util.Map;

import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;

/**
 * Created by thyone on 2017-04-08.
 */

public class Report {
    private transient Integer sentenceId;
    private transient String textKo;
    private transient String textEn;
    private Integer state;

    public static final String Field_SENTENCE_ID = "SENTENCE_ID";
    public static final String Field_TEXT_KO = "TEXT_KO";
    public static final String Field_TEXT_EN = "TEXT_EN";

    public static final int STATE_REPORTED = 0;
    public static final int STATE_MODIFILED = 1;

    public Report(){
        sentenceId = 0;
        textKo = "";
        textEn = "";
        state = STATE_REPORTED;
    }

    public Report(String bundle){
        deserialize(bundle);
    }

    public boolean isEmpty(){
        if( sentenceId == 0
                && textKo == ""
                && textEn == "")
            return true;
        return false;
    }

    public Report deserialize(String bundle){
        if( bundle == null ){
            return null;
        }
        Map bundleMap = StringHelper.json2map(bundle);
        try {
            this.sentenceId = (Integer)bundleMap.get(Field_SENTENCE_ID);
            this.textKo = (String)bundleMap.get(Field_TEXT_KO);
            this.textEn = (String)bundleMap.get(Field_TEXT_EN);
            this.state = STATE_REPORTED;
            return this;
        } catch (Exception e){
            String msg = "Failed Deserialize Report Object. " + e.getMessage();
            throw new MyIllegalStateException(msg);
        }
    }

    public String toString(){
        return "sentenceId: " + sentenceId
                +", textKo: " + textKo
                +", textEn: " + textEn
                +", state: " + state;
    }

    public Integer getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(Integer sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getTextKo() {
        return textKo;
    }

    public void setTextKo(String textKo) {
        this.textKo = textKo;
    }

    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
