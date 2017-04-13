package kr.jhha.engquiz.model.local;

import android.util.Log;

import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.model.remote.ObjectBundle;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;

/**
 * Created by jhha on 2016-10-14.
 */

public class Sentence {
    public Integer sentenceId;
    public Integer scriptId;
    public String textKo;
    public String textEn;
    public Integer src;

    public final static String Field_SENTENCE_ID = "SENTENCE_ID";
    public final static String Field_SCRIPT_ID = "SCRIPT_ID";
    public final static String Field_SENTENCE_KO = "SENTENCE_KO";
    public final static String Field_SENTENCE_EN = "SENTENCE_EN";

    public final static Integer SRC_NONE = 0;
    public final static Integer SRC_SYSTEM = 1;
    public final static Integer SRC_USER = 2;

    public Sentence() {
        sentenceId = 0;
        scriptId = 0;
        textKo = StringHelper.EMPTY_STRING();
        textEn = StringHelper.EMPTY_STRING();
        src = SRC_NONE;
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
            Log.e("######", "sentenceId is null. sentenceId:"+sentenceId );
            return false;
        }

        if( sentenceId instanceof Integer ) {
            return checkSentenceID( (Integer)sentenceId );
        } else {
            Log.e("######", "sentenceId Type is not Integer. sentenceId:"+sentenceId );
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
            Log.e("######", "revision is null. sentenceId:"+revision );
            return false;
        }

        if( revision instanceof Integer ) {
            return checkRevision( (Integer)revision );
        } else {
            Log.e("######", "revision Type is not Integer. revision:"+revision );
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
            Log.e("######", "koreanSentence is null. sentenceId:"+koreanSentence );
            return false;
        }

        if( koreanSentence instanceof String ) {
            return checkKoreanSentence( (String)koreanSentence );
        } else {
            Log.e("######", "koreanSentence Type is not String:"+koreanSentence );
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
            Log.e("######", "englishSentence is null. sentenceId:"+englishSentence );
            return false;
        }

        if( englishSentence instanceof String ) {
            return checkEnglishSentence( (String)englishSentence );
        } else {
            Log.e("######", "englishSentence Type is not String:"+englishSentence );
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
       Deserialize a Server Result
        : List<HashMap> -> List<Sentence>
    */
    public static Sentence deserialize( Map<String , Object> map )
    {
        Sentence sentence = new Sentence();
        if( map.containsKey(Field_SENTENCE_ID) ){
            Object id = map.get(Field_SENTENCE_ID);
            if(false == checkSentenceID( id )){
                Log.e("######", "Failed Init Sentence Object. sentenceId:"+id );
                return null;
            }
            sentence.sentenceId = (Integer)id;
        }

        if( map.containsKey(Field_SCRIPT_ID) ){
            Object scriptId = map.get(Field_SCRIPT_ID);
            if(false == Script.checkScriptID(scriptId)){
                Log.e("######", "Failed Init scriptId Object. sentenceId:"+sentence.sentenceId);
                return null;
            }
            sentence.scriptId = (Integer)scriptId;
        }

        if( map.containsKey(Sentence.Field_SENTENCE_KO) ){
            Object ko = map.get(Sentence.Field_SENTENCE_KO);
            if(false == checkKoreanSentence(ko)){
                Log.e("######", "Failed Init KOREAN Sentence Object. sentenceId:"+sentence.sentenceId);
                return null;
            }
            sentence.textKo = (String)ko;
        }

        if( map.containsKey(Sentence.Field_SENTENCE_EN) ){
            Object en = map.get(Sentence.Field_SENTENCE_EN);
            if(false == checkEnglishSentence(en)){
                Log.e("######", "Failed Init ENGLIST Sentence Object. sentenceId:"+sentence.sentenceId);
                return null;
            }
            sentence.textEn = (String)en;
        }

        return sentence;
    }

    public boolean isMadeByUser(){
        return this.src == SRC_USER;
    }

    /*
        User가 직접 만든 문장에 대해서만 사용한다.
        이 문장들은 각 클라이언트에만 존재하기 때문이다.

        영어수업용 스크립트 문장들은 전 유저가 공유하므로, 서버에서 ID를 부여한다.
     */
    public static Integer makeSenetenceId( Integer scriptId )
    {
        // 수업용 스크립트를 기준으로 sentenceID를 만들 수 없다.
        // User가 만든 문장들이 저장되는 script는 id가 10000 이후 부터이므로.
        if( scriptId < 10000 ){
            return -1;
        }

        Integer lastSentenceId = 0;
        final ScriptRepository repo = ScriptRepository.getInstance();
        Script script = repo.getScript(scriptId);
        List<Sentence> sentences = script.sentences;
        for(Sentence sentence : sentences){
            if(lastSentenceId < sentence.sentenceId)
                lastSentenceId = sentence.sentenceId;
        }

        Integer newSentenceId = 0;
        if( lastSentenceId == 0 ) {
            newSentenceId = scriptId * 10 + 1;
        } else {
            newSentenceId = lastSentenceId + 1;
        }

        return newSentenceId;
    }

    public static boolean isNull( Sentence sentence ){
        if( sentence == null )
            return true;

        if( sentence.sentenceId == 0
                && sentence.scriptId == 0
                && sentence.textKo == StringHelper.EMPTY_STRING()
                && sentence.textEn == StringHelper.EMPTY_STRING()
                && sentence.src == SRC_NONE)
            return true;

        return false;
    }


    public String toString() {
        return "sentenceId("+ sentenceId +"), "
                + "scriptId("+scriptId+"), "
                + "src("+src+"), "
                + "textKo("+textKo+"), "
                + "textEn("+textEn+")";
    }
}
