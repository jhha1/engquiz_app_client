package kr.jhha.engquiz.backend_logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thyone on 2017-02-14.
 */

public class DBHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "engquiz.db";
    public static final int DB_VERSION = 1;

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper( Context context ) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 최초에 데이터베이스가 없을경우, 데이터베이스 생성을 위해 호출됨.
    // db 이름은 SQLiteOpenHelper 생성자 인자로 받음.
    // 테이블 생성하는 코드를 작성한다
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table myquiz (_id integer primary key autoincrement," +
                "order_index integer not null, title text not null, file_indexs integer not null);";
        db.execSQL(sql);
    }

    /*
        / 데이터베이스의 버전이 바뀌었을 때 호출되는 콜백 메서드
        // 버전 바뀌었을 때 기존데이터베이스를 어떻게 변경할 것인지 작성한다
        // 각 버전의 변경 내용들을 버전마다 작성해야함
        String sql = "drop table mytable;"; // 테이블 드랍
        db.execSQL(sql);
        onCreate(db); // 다시 테이블 생성
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "db name");
        onCreate(db);
    }

    public boolean insertNewMyQuiz(int orderIndex, String title, String fileIndexes) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values  = new ContentValues();
        values.put("order_index", orderIndex);
        values.put("title", title);
        values.put("file_indexs", fileIndexes);
        Log.i("#####################", "Insert DB" + values.toString());
        db.insert("myquiz", null, values);
        db.close(); // Now close the DB Object
        return true;
    }

    public void update(String title, int index) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE myquiz SET title=" + title + " WHERE _id='" + index + "';");
        db.close();
    }

    public void delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM myquiz WHERE item='" + item + "';");
        db.close();
    }

    public String selectMyQuiz() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM myquiz", null);
        while (cursor.moveToNext()) {
            result += cursor.getInt(0)
                    + " order_index: "
                    + cursor.getInt(1)
                    + " title: "
                    + cursor.getString(2)
                    + " file indexes: "
                    + cursor.getString(3)
                    + "\n";
        }

        return result;
    }
}
