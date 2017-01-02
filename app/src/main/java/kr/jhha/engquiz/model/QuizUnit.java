package kr.jhha.engquiz.model;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizUnit {
    public StringBuffer korean = new StringBuffer();
    public StringBuffer english = new StringBuffer();

    public QuizUnit() {
        ;
    }

    public QuizUnit(String korean, String english) {
        this.korean.append(korean);
        this.english.append(english);
    }

    public String toString() {
        return "ko:" + korean
                + ", en:" + english;
    }
}
