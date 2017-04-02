package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.HashMap;

import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by jhha on 2016-10-14.
 */

public class Sentence {
    public Integer id = 0;
    public Integer scriptId = 0;
    public Integer revision = 0;
    public String textKo;
    public String textEn;

    public static final String KOREAN = "textKo";
    public static final String ENGLIST = "textEn";

    public Sentence() {
        ;
    }

    // parsing script list.  List<HashMap> -> List<Sentence>
    //  : 서버에서 Sentence Object를 json string으로 변환시에, HashMap포맷으로 변환된다.
    public Sentence( HashMap sentencePair )
    {
        if( sentencePair.containsKey("id") ){
            Object id = sentencePair.get("id");
            if(false == checkSentenceID( id )){
                Log.e("######", "Failed Init Sentence Object. sentenceId:"+id );
                return;
            }
            this.id = (Integer)id;
        }

        if( sentencePair.containsKey("scriptId") ){
            Object scriptId = sentencePair.get("scriptId");
            if(false == Script.checkScriptID(scriptId)){
                Log.e("######", "Failed Init scriptId Object. sentenceId:"+id );
                return;
            }
            this.scriptId = (Integer)scriptId;
        }

        if( sentencePair.containsKey("revision") ){
            Object revision = sentencePair.get("revision");
            if(false == checkRevision(revision)){
                Log.e("######", "Failed Init scriptId Object. sentenceId:"+id );
                return;
            }
            this.revision = (Integer)revision;
        }

        if( sentencePair.containsKey(Sentence.KOREAN) ){
            Object ko = sentencePair.get(Sentence.KOREAN);
            if(false == checkKoreanSentence(ko)){
                Log.e("######", "Failed Init KOREAN Sentence Object. sentenceId:"+id );
                return;
            }
            this.textKo = (String)ko;
        }

        if( sentencePair.containsKey(Sentence.ENGLIST) ){
            Object en = sentencePair.get(Sentence.ENGLIST);
            if(false == checkEnglishSentence(en)){
                Log.e("######", "Failed Init ENGLIST Sentence Object. sentenceId:"+id );
                return;
            }
            this.textEn = (String)en;
        }
    }

    public static boolean isNull( Sentence sentence ){
        if( sentence == null )
            return true;

        if( sentence.id == 0
                && sentence.scriptId == 0
                && sentence.revision == 0
                && sentence.textKo == null
                && sentence.textEn == null)
            return true;

        return false;
    }

    public static boolean checkSentenceID( Object sentenceId ) {
        if( sentenceId == null ){
            Log.e("######", "sentenceId is null. id:"+sentenceId );
            return false;
        }

        if( sentenceId instanceof Integer ) {
            return checkSentenceID( (Integer)sentenceId );
        } else {
            Log.e("######", "sentenceId Type is not Integer. id:"+sentenceId );
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
            Log.e("######", "revision is null. id:"+revision );
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
            Log.e("######", "koreanSentence is null. id:"+koreanSentence );
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
        if(StringHelper.isNullString(koreanSentence)){
            return false;
        }
        return true;
    }

    public static boolean checkEnglishSentence( Object englishSentence ) {
        if( englishSentence == null ){
            Log.e("######", "englishSentence is null. id:"+englishSentence );
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
        if(StringHelper.isNullString(englishSentence)){
            return false;
        }
        return true;
    }

    public String toString() {
        return "id("+id+"), "
                + "scriptId("+scriptId+"), "
                + "revision("+revision+"), "
                + "textKo("+textKo+"), "
                + "textEn("+textEn+")";
    }
}
