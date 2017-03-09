package kr.jhha.engquiz.backend_logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.jhha.engquiz.ui.fragments.quizgroups.QuizGroupItem;

/**
 * Created by thyone on 2017-02-14.
 */

public class DBHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "/mnt/sdcard/" + "engquiz.db";
    public static final int DB_VERSION = 1;

    private static final String TB_QUIZGROUPS = "quizgroups";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper( Context context ) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 최초에 데이터베이스가 없을경우, 데이터베이스 생성을 위해 호출됨.
    // db 이름은 SQLiteOpenHelper 생성자 인자로 받음.
    // 테이블 생성하는 코드를 작성한다
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TB_QUIZGROUPS
                + " (_id integer primary key autoincrement," +
                    "tag integer not null, " +
                    "title text not null, " +
                    "desc text, " +
                    "script_indexes text not null, " +
                    "created_dt DEFAULT CURRENT_TIMESTAMP not null);";
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

    private String getDateTime( long currentTimeMillis ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format( currentTimeMillis );
    }

    public boolean insertNewQuizGroup( QuizGroupItem item )
    {
        Log.i("#####################", "insertNewQuizGroup called DB");
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values  = new ContentValues();

        values.put("tag", item.getTag());
        values.put("title", item.getTitle());
        values.put("desc", item.getDesc());
        values.put("script_indexes", Utils.list2json( item.getScriptIndexes() ));
        values.put("created_dt", getDateTime(item.getCreatedDateTime()));
        Log.i("#####################", "Insert DB" + values.toString());

        db.insert(TB_QUIZGROUPS, null, values);
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

    public List<QuizGroupItem> selectQuizGroups()
    {
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String query = "SELECT _id, tag, title, desc, script_indexes, strftime('%s', created_dt) " +
                        "FROM " + TB_QUIZGROUPS;

        // select 결과
        List<QuizGroupItem> selectedList = new ArrayList<>();

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery( query, null );
        while (cursor.moveToNext())
        {
            Integer tag = cursor.getInt(1);
            String title = cursor.getString(2);
            String desc = cursor.getString(3);
            String scriptIndexes = cursor.getString(4);
            long created_dt = cursor.getLong(5);

            String result = " _id: " + cursor.getInt(0)
                    + " tag: " + tag
                    + " title: " + title
                    + " desc: " + desc
                    + " script_indexes: " + scriptIndexes
                    + " created_dt: " + created_dt;
            Log.d("$$$$$$$$$$$$$$$$$$$$", " SELECT result : " + result);

            QuizGroupItem item = new QuizGroupItem();
            item.setTag( tag );
            item.setTitle( title );
            item.setDesc( desc );
            item.setScriptIndexes( (List<Integer>) Utils.json2Object(scriptIndexes) );
            item.setCreatedDateTime( created_dt );
            selectedList.add( item );
        }

        return selectedList;
    }
}
