package kr.jhha.engquiz.model.local;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalArgumentException;
import kr.jhha.engquiz.util.ui.MyLog;


/**
 * Created by jhha on 2016-10-24.
 */

public class Script
{
    public Integer scriptId = 0;
    public String title = "";
    public List<Sentence> sentences = new LinkedList<Sentence>();

    public final static String Field_SCRIPT_ID = "SCRIPT_ID";
    public final static String Field_SCRIPT_TITLE = "SCRIPT_TITLE";
    public final static String Field_SENTENCES = "SENTENCES";

    public final static int SCRIPT_ID_MIN = 10000;

    public Script() {}
    public Script( String filename, Integer index,
                  List<Sentence> sentences )
    {
        this.title = filename;
        this.scriptId = index;
        this.sentences = sentences;
    }

    public Script( String textScript )
    {
        if( textScript == null || textScript.isEmpty() ) {
            MyLog.e("invalied param. param script is null");
            return;
        }

        String rows[] = textScript.split(Parsor.MainSeperator);
        if(rows.length <= 2) {
            MyLog.e("invalied param. invalid param format: "+textScript);
            return;
        }

        String title = rows[1];
        int index = Integer.parseInt( rows[0] );
        LinkedList<Sentence> sentences = new LinkedList<Sentence>();
        for(int i=2; i<rows.length; ++i)
        {
            String row = rows[i];
            if(row.isEmpty())
                continue;

            String[] dividedRow = row.split("\t");
            if(dividedRow.length != 2)
                continue;

            Sentence unit = new Sentence();
            unit.textKo = dividedRow[0].trim();
            unit.textEn = dividedRow[1].trim();
            sentences.add(unit);
        }

        this.title = title;
        this.scriptId = index;
        this.sentences = sentences;
    }



    public static boolean isNull( Script script ){
        if( script == null ){
            return true;
        }

        if( StringHelper.isNull(script.title)
                && script.scriptId == 0
                && (script.sentences == null
                    || script.sentences.isEmpty()))
            return true;

        return false;
    }

    public static boolean checkScriptID( Object scriptID ) {
        if( scriptID instanceof Integer ) {
            return checkScriptID( (Integer)scriptID );
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptID Type is not Integer. sentenceId:"+scriptID );
        }
    }

    public static boolean checkScriptID( Integer scriptID ) {
        if( scriptID <= 0){
            return false;
        }
        return true;
    }

    public String makeScriptFileName() {
        if( StringHelper.isNull(this.title) ){
            MyLog.e("cannot make script file name. script title is null");
            return null;
        }

        String title_pdf_removed = Parsor.removeExtensionFromScriptTitle(title, ".pdf");

        StringBuffer filename = new StringBuffer();
        filename.append(this.scriptId + Parsor.MainSeperator);
        filename.append(title_pdf_removed + ".txt");
        return filename.toString();
    }

    public static String makeScriptFileName(Integer scriptId, String scriptTitle) {
        if( scriptId <= 0 || StringHelper.isNull(scriptTitle)){
            return new String();
        }

        StringBuffer filename = new StringBuffer();
        filename.append(scriptId + Parsor.MainSeperator);
        filename.append(scriptTitle+ ".txt");
        return filename.toString();
    }

    public String makeScriptFileText() {
        StringBuffer fileText = new StringBuffer();
        for(Sentence unit : sentences)
        {
            fileText.append(unit.sentenceId + Parsor.TabSeperator);
            fileText.append(unit.textKo + Parsor.TabSeperator);
            fileText.append(unit.textEn + Parsor.MainSeperator);
        }
        return fileText.toString();
    }


    /*
        Deserialize a AddScript Server Result
     */
    public static Script deserialize( Map<String , Object> map )
    {
        Script script = new Script();
        try {
            int id = (Integer) map.get(Field_SCRIPT_ID);
            String title = (String) map.get(Field_SCRIPT_TITLE);
            List<Map<String, Object>> sentencesMap = (List<Map<String, Object>>) map.get(Field_SENTENCES);

            if( id < 0 )
                throw new Exception("invalid scriptId:"+id);
            if( title == null || title.isEmpty() )
                throw new Exception("invalid title:"+title);
            if( sentencesMap == null || sentencesMap.size() == 0 )
                throw new Exception("invalid sentencesMap:"+sentencesMap);

            // unserialize Sentence Objects
            List<Sentence> unserializedSentences = new LinkedList<Sentence>();
            for( Map<String, Object> sentence : sentencesMap ) {
                Sentence unserializedSentence = Sentence.deserialize(sentence);
                unserializedSentences.add( unserializedSentence );
            }
            if( unserializedSentences == null || unserializedSentences.size() == 0 )
                throw new Exception("invalid parsedSentences:"+unserializedSentences);

            script.scriptId = id;
            script.title = title;
            script.sentences = unserializedSentences;
            return script;

        } catch ( Exception e ) {
            MyLog.e("ERROR invalid param. " +
                    "map[" + script.toString() + "]");
            e.printStackTrace();
            return null;
        }
    }

    // just for logging
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{[scriptId: "+ scriptId +"], " +
                "[title: "+ title +"], " +
                "[sentence count("+ sentences.size() +").. ");
        for(Object o : sentences) {
            Sentence s = (Sentence) o;
            buf.append("\n[" + s.toString() + "]");
        }
        buf.append("]}");
        return buf.toString();
    }
}
