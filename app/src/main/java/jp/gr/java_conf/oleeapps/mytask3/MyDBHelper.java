package jp.gr.java_conf.oleeapps.mytask3;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by orisa on 2017/06/08.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context){
        super(context, "TASK_DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL文を入れる
        String sql = "CREATE TABLE default_tb(_id INTEGER PRIMARY KEY NOT NULL, " +
                "task_col TEXT, date_col TEXT, time_col TEXT, checked_col INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
