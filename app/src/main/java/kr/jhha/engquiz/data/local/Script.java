package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.util.exception.system.IllegalArgumentException;


/**
 * Created by jhha on 2016-10-24.
 */

public class Script
{
    public Integer scriptId = 0;
    public String title = "";
    public List<Sentence> sentences = new LinkedList<Sentence>();

    /*
    private enum EValueName {
        INDEX ("scriptId"), REVISION ("revision"),
        TITLE ("title"), SENTENCES ("sentences");

        private String value;
        private EValueName( String value ) {
            this.value = value;
        }
    };
*/
    public static final String INDEX = "scriptId";
    public static final String TITLE = "title";
    public static final String SENTENCES = "sentences";

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
            System.out.println("ERROR invalied param. param script is null");
            return;
        }

        String rows[] = textScript.split(Parsor.MainSeperator);
        if(rows.length <= 2) {
            System.out.println("ERROR invalied param. invalid param format: "+textScript);
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

    public Script( Map<String , Object> scriptMap )
    {
        try {
            int index = (Integer) scriptMap.get(INDEX);
            String title = (String) scriptMap.get(TITLE);
            List<HashMap> sentencesMap = (List<HashMap>) scriptMap.get(SENTENCES);

            if( index < 0 )
                throw new Exception("invalid scriptId:"+index);
            if( title == null || title.isEmpty() )
                throw new Exception("invalid title:"+title);
            if( sentencesMap == null || sentencesMap.size() == 0 )
                throw new Exception("invalid sentencesMap:"+sentencesMap);

            // parsing script list.  List<HashMap> -> List<Sentence>
            //  : 서버에서 Sentence Object를 json string으로 변환시에, HashMap포맷으로 변환된다.
            List<Sentence> parsedSentences = new LinkedList<Sentence>();
            for( HashMap sentencePair : sentencesMap ) {
                parsedSentences.add( new Sentence(sentencePair) );
            }
            if( parsedSentences == null || parsedSentences.size() == 0 )
                throw new Exception("invalid parsedSentences:"+parsedSentences);

            this.scriptId = index;
            this.title = title;
            this.sentences = parsedSentences;

        } catch ( Exception e ) {
            Log.e("AppContent", "ERROR invalid param. " +
                    "map[" + scriptMap.toString() + "]");
            e.printStackTrace();
            return;
        }
    }

    public static boolean checkScriptID( Object scriptID ) {
        if( scriptID instanceof Integer ) {
            return checkScriptID( (Integer)scriptID );
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptID Type is not Integer. id:"+scriptID );
        }
    }

    public static boolean checkScriptID( Integer scriptID ) {
        if( scriptID <= 0){
            return false;
        }
        return true;
    }

    public String toTextFileFormat() {
        StringBuffer text = new StringBuffer();

        text.append(scriptId + Parsor.MainSeperator);
        text.append(title + Parsor.MainSeperator);
        for(Sentence unit : sentences)
        {
            text.append(unit.textKo + "\t" + unit.textEn + Parsor.MainSeperator);
        }
        return text.toString();
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
