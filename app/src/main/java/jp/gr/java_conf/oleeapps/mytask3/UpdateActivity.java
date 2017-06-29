package jp.gr.java_conf.oleeapps.mytask3;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by orisa on 2017/06/16.
 */

public class UpdateActivity extends AppCompatActivity {

    //部品の変数
    EditText updTask, updDate;
    Spinner spinner;
    Button updBtn, cancelBtn;

    //Spinner用の変数
    String[] items = {"AM", "PM"};

    //データを入れる用の変数
    String task, date, time;

    //intentからデータを受け取る用の変数
    String task_init;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //部品の取得
        updTask = (EditText)findViewById(R.id.updTask);
        updDate = (EditText)findViewById(R.id.updDate);
        spinner = (Spinner)findViewById(R.id.spinner);
        updBtn = (Button) findViewById(R.id.updBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        //日付入力の設定
        updDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendarインスタンスの生成
                Calendar cal = Calendar.getInstance();
                //DatePickerDialogの設定
                DatePickerDialog dialog = new DatePickerDialog(UpdateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //日付を表示
                                updDate.setText(String.format("%d/%02d/%02d", year, month+1, dayOfMonth));
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                //DatePickerDialogの表示
                dialog.show();
            }
        });

        //AM/PM入力の設定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinnerにリスナーを設定
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //データを格納
                time = (String)parent.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //UPDATEボタンにリスナーを設定
        updBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //データの取得
                task = updTask.getText().toString();
                date = updDate.getText().toString();
                //データベースの更新
                MyDBHelper helper = new MyDBHelper(UpdateActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues val = new ContentValues();
                val.put("task_col", task);
                val.put("date_col", date);
                val.put("time_col", time);
                db.update("default_tb", val, "task_col =?", new String[] {task_init});
                finish();
            }
        });

        //CANCELボタンにリスナーを設定
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activityの終了
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // intentのデータを受け取る
        Intent intent = getIntent();
        task_init = intent.getStringExtra("task_data");

        //データベースを検索
        MyDBHelper helper = new MyDBHelper(UpdateActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query("default_tb", new String[]{"date_col", "time_col"},
                "task_col =?", new String[]{task_init}, null, null, null);
        boolean bool = c.moveToFirst();
        while (bool){
            //dateとtimeを取得
            date = c.getString(0);
            time = c.getString(1);
            //Cursorを次に移動
            bool = c.moveToNext();
        }
        //Cursorをclose
        c.close();

        //データを表示
        updTask.setText(task_init);
        updDate.setText(date);
        int pos;
        if(time.equals("AM")){
            pos = 0;
        }
        else {
            pos = 1;
        }
        spinner.setSelection(pos);
    }
}
