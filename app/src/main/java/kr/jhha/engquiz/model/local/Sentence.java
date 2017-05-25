package kr.jhha.engquiz.model.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.model.remote.ObjectBundle;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.model.local.Script.CUSTOM_SCRIPT_ID_MIN;

/**
 * Created by jhha on 2016-10-14.
 */

public class Sentence {
    public Integer sentenceId;
    public Integer scriptId;
    public String textKo;
    public String textEn;
    public Integer state;

    public final static String Field_SENTENCE_ID = "SENTENCE_ID";
    public final static String Field_SCRIPT_ID = "SCRIPT_ID";
    public final static String Field_SENTENCE_KO = "SENTENCE_KO";
    public final static String Field_SENTENCE_EN = "SENTENCE_EN";

    public final static Integer STATE_NONE = 0;
    public final static Integer STATE_MADE_BY_SYSTEM = 1;
    public final static Integer STATE_MADE_BY_USER = 2;
    public final static Integer STATE_NEW_BUTTON = 3;

    public Sentence() {
        sentenceId = 0;
        scriptId = 0;
        textKo = StringHelper.EMPTY_STRING;
        textEn = StringHelper.EMPTY_STRING;
        state = STATE_NONE;
    }

    public Sentence(ObjectBundle bundle) {
        deserialize(bundle);
    }

    public Sentence deserialize(ObjectBundle bundle){
        if( bundle == null ){
            return null;
        }
        try {
            this.sentenceId = bundle.getInt(Sentence.Field_SENTENCE_ID);
            this.textKo = bundle.getString(Sentence.Field_SENTENCE_KO);
            this.textEn = bundle.getString(Sentence.Field_SENTENCE_EN);
            return this;
        } catch (Exception e){
            String msg = "Failed Deserialize Sentence Object. " + e.getMessage();
            throw new MyIllegalStateException(msg);
        }
    }

    public static boolean checkSentenceID( Object sentenceId ) {
        if( sentenceId == null ){
            MyLog.e("sentenceId is null. sentenceId:"+sentenceId );
            return false;
        }

        if( sentenceId instanceof Integer ) {
            return checkSentenceID( (Integer)sentenceId );
        } else {
            MyLog.e("sentenceId Type is not Integer. sentenceId:"+sentenceId );
            return false;
        }
    }

    public static boolean checkSentenceID( Integer sentenceId ) {
        if( sentenceId < 0 ){
            return false;
        }
        return true;
    }

    public static boolean checkRevision( Object revision ) {
        if( revision == null ){
            MyLog.e("revision is null. sentenceId:"+revision );
            return false;
        }

        if( revision instanceof Integer ) {
            return checkRevision( (Integer)revision );
        } else {
            MyLog.e("revision Type is not Integer. revision:"+revision );
            return false;
        }
    }

    public static boolean checkRevision( Integer revision ) {
        if( revision < 0 ){
            return false;
        }
        return true;
    }

    public static boolean checkKoreanSentence( Object koreanSentence ) {
        if( koreanSentence == null ){
            MyLog.e("koreanSentence is null. sentenceId:"+koreanSentence );
            return false;
        }

        if( koreanSentence instanceof String ) {
            return checkKoreanSentence( (String)koreanSentence );
        } else {
            MyLog.e("koreanSentence Type is not String:"+koreanSentence );
            return false;
        }
    }

    public static boolean checkKoreanSentence( String koreanSentence ) {
        if(StringHelper.isNull(koreanSentence)){
            return false;
        }
        return true;
    }

    public static boolean checkEnglishSentence( Object englishSentence ) {
        if( englishSentence == null ){
            MyLog.e("englishSentence is null. sentenceId:"+englishSentence );
            return false;
        }

        if( englishSentence instanceof String ) {
            return checkEnglishSentence( (String)englishSentence );
        } else {
            MyLog.e("englishSentence Type is not String:"+englishSentence );
            return false;
        }
    }

    public static boolean checkEnglishSentence( String englishSentence ) {
        if(StringHelper.isNull(englishSentence)){
            return false;
        }
        return true;
    }

    /*
       Deserialize a Server CreateScriptResult
        : List<HashMap> -> List<Sentence>
    */
    public static Sentence deserialize( Map<String , Object> map )
    {
        Sentence sentence = new Sentence();
        if( map.containsKey(Field_SENTENCE_ID) ){
            Object id = map.get(Field_SENTENCE_ID);
            if(false == checkSentenceID( id )){
                MyLog.e("Failed Init Object 'Sentence'. sentenceId:"+id );
                return null;
            }
            sentence.sentenceId = (Integer)id;
        }

        if( map.containsKey(Field_SCRIPT_ID) ){
            Object scriptId = map.get(Field_SCRIPT_ID);
            if(false == Script.checkScriptID(scriptId)){
                MyLog.e("Failed Init Object 'scriptId'. " +
                        "scriptId:"+scriptId+", sentenceId:"+sentence.sentenceId);
                return null;
            }
            sentence.scriptId = (Integer)scriptId;
        }

        if( map.containsKey(Sentence.Field_SENTENCE_KO) ){
            Object ko = map.get(Sentence.Field_SENTENCE_KO);
            if(false == checkKoreanSentence(ko)){
                MyLog.e("Failed Init Object 'KOREAN Sentence'. " +
                        "sentence(Id:"+sentence.sentenceId+",data:"+ko+")");
                return null;
            }
            sentence.textKo = (String)ko;
        }

        if( map.containsKey(Sentence.Field_SENTENCE_EN) ){
            Object en = map.get(Sentence.Field_SENTENCE_EN);
            if(false == checkEnglishSentence(en)){
                MyLog.e("Failed Init Object 'ENGLIST Sentence'. " +
                        "sentence(Id:"+sentence.sentenceId+",data:"+en+")");
                return null;
            }
            sentence.textEn = (String)en;
        }

        return sentence;
    }

    public boolean isMadeByUser(){
        return this.state == STATE_MADE_BY_USER;
    }

    /*
        1. User가 직접 만든 문장에 대해서만 사용한다.
            이 문장들은 각 클라이언트에만 존재하기 때문이다.

        2. 영어수업용 스크립트 문장들은 전 유저가 공유하므로, 서버에서 ID를 부여한다.

        3. 위 1,2번 문장 별 다른 로직을 타는 경우에 구분을 위해,
        - 유저가 만든 문장의 SentenceID 는 유저가 만든 ScriptID의 100배수부터 시작한다.
            # 유저가만든 scriptID가 10002 라고 가정하면,
               SentenceID  == 10002 * 100 + 1 == 1000201
               (유저가 만든 ScriptId는 10000부터 시작함)
        - 선생님이 만든 문장의 SentenceID 는 1 부터 시작한다.
     */
    public static Integer makeSenetenceId( Integer scriptId, List<Sentence> sentences )
    {
        // 수업용 스크립트를 기준으로 sentenceID를 만들 수 없다.
        // User가 만든 문장들이 저장되는 script는 id가 10000 이후 부터이므로.
        if( scriptId < CUSTOM_SCRIPT_ID_MIN){
            return -1;
        }

        Integer lastSentenceId = 0;
        if( sentences == null ){
            sentences = new ArrayList<>();
        }

        if( sentences.isEmpty() ){
            Integer newSentenceId = scriptId * 100 + 1;
            return newSentenceId;
        }

        // find a largest Id.
        for(Sentence sentence : sentences){
            if(lastSentenceId < sentence.sentenceId)
                lastSentenceId = sentence.sentenceId;
        }
        Integer newSentenceId = lastSentenceId + 1;
        return newSentenceId;
    }

    public static boolean isNull( Sentence sentence ){
        if( sentence == null )
            return true;

        if( sentence.sentenceId == 0
                && sentence.scriptId == 0
                && sentence.textKo == StringHelper.EMPTY_STRING
                && sentence.textEn == StringHelper.EMPTY_STRING
                && sentence.state == STATE_NONE)
            return true;

        return false;
    }


    public String toString() {
        return "sentenceId("+ sentenceId +"), "
                + "scriptId("+scriptId+"), "
                + "state("+ state +"), "
                + "textKo("+textKo+"), "
                + "textEn("+textEn+")";
    }
}
