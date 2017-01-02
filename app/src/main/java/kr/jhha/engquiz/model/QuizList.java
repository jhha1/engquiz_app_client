package kr.jhha.engquiz.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jhha on 2016-10-24.
 */

public class QuizList
{
    public String title = "";
    public List<QuizUnit> quizList = new LinkedList<QuizUnit>();

    public QuizList() {

    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{[Title: "+ title +"], [QuizListLen("+ quizList.size() +").. ");
        for(QuizUnit s : quizList) {
            buf.append("\n[" + s.toString() + "]");
        }
        buf.append("]}");
        return buf.toString();
    }
}
