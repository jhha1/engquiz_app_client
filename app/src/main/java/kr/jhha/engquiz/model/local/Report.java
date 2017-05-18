package kr.jhha.engquiz.model.local;

import java.util.Map;

import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;

/**
 * Created by thyone on 2017-04-08.
 */

public class Report {
    private transient Integer scriptId;
    private transient Integer sentenceId;
    private transient String textKo;
    private transient String textEn;

    private Integer modifyState;
    private MODIFY_TYPE modifyType;

    private String scriptName;

    public static final String Field_SCIPRT_ID = "SCRIPT_ID";
    public static final String Field_SENTENCE_ID = "SENTENCE_ID";
    public static final String Field_TEXT_KO = "TEXT_KO";
    public static final String Field_TEXT_EN = "TEXT_EN";

    public static final int STATE_REPORTED = 0;
    public static final int STATE_MODIFILED = 1;

    public enum MODIFY_TYPE {
        UPDATE(1),
        DEL(2),
        ADD(3);

        private Integer value;
        MODIFY_TYPE( Integer value )
        {
            this.value = value;
        }
        public int value() {return value.intValue(); }
    };

    public Report(){
        scriptId = 0;
        sentenceId = 0;
        textKo = "";
        textEn = "";
        modifyState = STATE_REPORTED;
    }

    public Report(String bundle){
        deserialize(bundle);
    }

    public boolean isEmpty(){
        if( scriptId == 0
                && sentenceId == 0
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
            this.scriptId = (Integer)bundleMap.get(Field_SCIPRT_ID);
            this.sentenceId = (Integer)bundleMap.get(Field_SENTENCE_ID);
            this.textKo = (String)bundleMap.get(Field_TEXT_KO);
            this.textEn = (String)bundleMap.get(Field_TEXT_EN);
            this.modifyState = STATE_REPORTED;
            this.scriptName = ScriptRepository.getInstance().getScriptTitleById(this.scriptId);
            return this;
        } catch (Exception e){
            String msg = "Failed Deserialize REPORT Object. " + e.getMessage();
            throw new MyIllegalStateException(msg);
        }
    }

    public String toString(){
        return "scriptId: " + scriptId
                + "sentenceId: " + sentenceId
                +", textKo: " + textKo
                +", textEn: " + textEn
                +", modifyState: " + modifyState;
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

    public Integer getModifyState() {
        return modifyState;
    }

    public void setModifyState(Integer modifyState) {
        this.modifyState = modifyState;
    }

    public Integer getScriptId() {
        return scriptId;
    }

    public void setScriptId(Integer scriptId) {
        this.scriptId = scriptId;
    }

    public MODIFY_TYPE getModifyType() {
        return modifyType;
    }

    public void setModifyType(MODIFY_TYPE modifyType) {
        this.modifyType = modifyType;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}
