package jp.gr.java_conf.oleeapps.mytask3;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by orisa on 2017/06/08.
 */

public class AddActivity extends AppCompatActivity {

    //部品の取得
    EditText addTask, addDate;
    Spinner spinner;
    Button okBtn, cancelBtn;

    //Spinner用の変数
    String[] items = {"AM", "PM"};

    //データを入れる用の変数
    String task, date, time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //部品の取得
        addTask = (EditText)findViewById(R.id.updTask);
        addDate = (EditText)findViewById(R.id.updDate);
        spinner = (Spinner)findViewById(R.id.spinner);
        okBtn = (Button)findViewById(R.id.okBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        //日付の入力の設定
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendarインスタンスの生成
                Calendar cal = Calendar.getInstance();
                //DatePickerDialogインスタンスの生成
                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //日付を取得して表示
                                addDate.setText(String.format("%d/%02d/%02d", year, month+1, dayOfMonth));
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                //DatePickerDialogの表示
                dialog.show();
            }
        });

        //AM/PM入力の設定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddActivity.this, android.R.layout.simple_spinner_dropdown_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinnerにリスナーを登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //選択されたStringを取得
                time = (String)parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //データベースの作成
        MyDBHelper helper = new MyDBHelper(AddActivity.this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //OKボタンクリック時の処理
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //taskとdateを取得
                task = addTask.getText().toString();
                date = addDate.getText().toString();
                //ContentValuesにデータを入れる
                ContentValues val = new ContentValues();
                val.put("task_col", task);
                val.put("date_col", date);
                val.put("time_col", time);
                val.put("checked_col", 0);
                //データベースに登録
               db.insert("default_tb", null, val);
                //activityの終了
                finish();
            }
        });

        //CANCELボタンクリック時の処理
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activityの終了
                finish();
            }
        });

    }
}
