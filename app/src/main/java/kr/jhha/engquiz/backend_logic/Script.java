package kr.jhha.engquiz.backend_logic;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by jhha on 2016-10-24.
 */

public class Script
{
    public Integer index = 0;
    public Integer revision = 0;
    public String title = "";
    public List<Sentence> sentences = new LinkedList<Sentence>();

    /*
    private enum EValueName {
        INDEX ("index"), REVISION ("revision"),
        TITLE ("title"), SENTENCES ("sentences");

        private String value;
        private EValueName( String value ) {
            this.value = value;
        }
    };
*/
    public static final String INDEX = "index";
    public static final String REVISION = "revision";
    public static final String TITLE = "title";
    public static final String SENTENCES = "sentences";

    public Script() {}
    public Script( String filename, Integer index,
                  Integer revision, List<Sentence> sentences )
    {
        this.title = filename;
        this.index = index;
        this.revision = revision;
        this.sentences = sentences;
    }

    public Script( String textScript )
    {
        if( textScript == null || textScript.isEmpty() ) {
            System.out.println("ERROR invalied param. param script is null");
            return;
        }

        String rows[] = textScript.split(Parsor.QuizUnitSeperator);
        if(rows.length <= 2) {
            System.out.println("ERROR invalied param. invalid param format: "+textScript);
            return;
        }

        String title = rows[2];
        int index = Integer.parseInt( rows[0] );
        int revision = Integer.parseInt( rows[1] );
        LinkedList<Sentence> sentences = new LinkedList<Sentence>();
        for(int i=3; i<rows.length; ++i)
        {
            String row = rows[i];
            if(row.isEmpty())
                continue;

            String[] dividedRow = row.split("\t");
            if(dividedRow.length != 2)
                continue;

            Sentence unit = new Sentence();
            unit.korean = new StringBuffer(dividedRow[0].trim());
            unit.english = new StringBuffer(dividedRow[1].trim());
            sentences.add(unit);
        }

        this.title = title;
        this.index = index;
        this.revision = revision;
        this.sentences = sentences;
    }

    public Script( Map<String , Object> scriptMap )
    {
        try {
            int index = (Integer) scriptMap.get(INDEX);
            int revision = (Integer) scriptMap.get(REVISION);
            String title = (String) scriptMap.get(TITLE);
            List<HashMap> sentencesMap = (List<HashMap>) scriptMap.get(SENTENCES);

            if( index < 0 )
                throw new Exception("invalid index:"+index);
            if( revision < 0 )
                throw new Exception("invalid revision:"+revision);
            if( title == null || title.isEmpty() )
                throw new Exception("invalid title:"+title);
            if( sentencesMap == null || sentencesMap.size() == 0 )
                throw new Exception("invalid sentencesMap:"+sentencesMap);

            // parsing script list.  List<HashMap> -> List<Sentence>
            //  : 서버에서 Sentence Object를 json string으로 변환시에, HashMap포맷으로 변환된다.
            List<Sentence> parsedSentences = new LinkedList<Sentence>();
            for( HashMap sentencePair : sentencesMap )
            {
                String ko = null;
                String en = null;
                if( sentencePair.containsKey(Sentence.KOREAN) )
                    ko = (String) sentencePair.get(Sentence.KOREAN);
                if( sentencePair.containsKey(Sentence.ENGLIST) )
                    en = (String) sentencePair.get(Sentence.ENGLIST);

                Sentence s = new Sentence( ko, en );
                parsedSentences.add( s );
            }
            if( parsedSentences == null || parsedSentences.size() == 0 )
                throw new Exception("invalid parsedSentences:"+parsedSentences);

            this.index = index;
            this.revision = revision;
            this.title = title;
            this.sentences = parsedSentences;

        } catch ( Exception e ) {
            Log.e("AppContent", "ERROR invalid param. " +
                    "map[" + scriptMap.toString() + "]");
            e.printStackTrace();
            return;
        }
    }

    public String toTextFileFormat() {
        StringBuffer text = new StringBuffer();

        text.append(index + Parsor.QuizUnitSeperator);
        text.append(revision + Parsor.QuizUnitSeperator);
        text.append(title + Parsor.QuizUnitSeperator);
        for(Sentence unit : sentences)
        {
            text.append(unit.korean + "\t" + unit.english + Parsor.QuizUnitSeperator);
        }
        return text.toString();
    }

    // just for logging
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{[index: "+ index +"], " +
                    "[revision: "+ revision +"], " +
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
