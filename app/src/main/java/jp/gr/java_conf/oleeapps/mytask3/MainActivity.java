package jp.gr.java_conf.oleeapps.mytask3;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //部品の変数
    TextView showDate;
    ImageButton prevBtn, nextBtn;
    LinearLayout amLayout, pmLayout;
    FloatingActionButton fab;

    //日付を格納する変数
    String today_str;
    Calendar today_cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //部品の取得
        showDate = (TextView)findViewById(R.id.showDate);
        prevBtn = (ImageButton) findViewById(R.id.prevBtn);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        amLayout = (LinearLayout)findViewById(R.id.amLayout);
        pmLayout = (LinearLayout)findViewById(R.id.pmLayout);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        //日付表示欄にリスナーを登録
        showDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendarインスタンスの取得
                Calendar cal = Calendar.getInstance();
                //DatePickerDialogインスタンスの生成
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //日付を取得
                                today_str = String.format("%d/%02d/%02d", year, month+1, dayOfMonth);
                                today_cal.set(year, month, dayOfMonth);
                                //日付を表示
                                showDate.setText(today_str+" "+calToDay(today_cal));
                                //データベースを表示
                                showDB();
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                //DatePickerDialogの表示
                dialog.show();
            }
        });

        //矢印ボタンにリスナーを設定
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar変数を前日に設定する
                today_cal.add(Calendar.DAY_OF_MONTH, -1);
                today_str = calToStr(today_cal);
                //日付を表示
                showDate.setText(today_str+" "+calToDay(today_cal));
                //データベースを表示
                showDB();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar変数を翌日に設定する
                today_cal.add(Calendar.DAY_OF_MONTH, 1);
                today_str = calToStr(today_cal);
                //日付を表示
                showDate.setText(today_str+" "+calToDay(today_cal));
                //データベースを表示
                showDB();
            }
        });

        //FloatingActionButtonにリスナーを登録
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intentインスタンスの生成
                Intent intent = new Intent(getApplication(), AddActivity.class);
                //activityの開始
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //今日の日付を取得
        today_cal = Calendar.getInstance();
        today_str = calToStr(today_cal);
        //日付を表示
        showDate.setText(today_str+" "+calToDay(today_cal));

        //データベースを表示
        showDB();
    }


    //Action Bar関連
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //dialogの設定
        builder.setMessage("Delete all tasks?");

        //OKボタンの設定
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //データベースの全削除
                deleteAllDB();
                //データベースの表示
                showDB();
            }
        });

        //CANCELボタンの設定
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        switch (item.getItemId()){
            case R.id.deleteAll:
                //dialogの描画
                builder.create();
                builder.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //CalendarからStringに変換するメソッド
    protected String calToStr(Calendar cal){
        String str = String.format("%d/%02d/%02d", cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        return str;
    }

    //データベースを表示するメソッド
    protected void showDB(){
        //データベースの取得
        MyDBHelper helper = new MyDBHelper(MainActivity.this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //表示のクリア
        amLayout.removeAllViews();
        pmLayout.removeAllViews();

        //検索して表示
        final Cursor c_am = db.query("default_tb", new String[]{"task_col", "checked_col"},
                "date_col =? AND time_col =?", new String[] {today_str, "AM"},
                null, null, null);
        boolean bool = c_am.moveToFirst();
        while(bool){
            //CheckBoxの設定
            final CheckBox cb = new CheckBox(MainActivity.this);
            cb.setText(c_am.getString(0));
            if(Build.VERSION.SDK_INT < 23){
                cb.setTextAppearance(MainActivity.this, R.style.TextAppearance_AppCompat_Small);
            }else{
                cb.setTextAppearance(R.style.TextAppearance_AppCompat_Small);
            }

            //CheckBoxにリスナーをつける
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //DBのアップデート処理
                    ContentValues val = new ContentValues();
                    val.put("checked_col", 1);
                    db.update("default_tb", val, "task_col =?", new String[]{cb.getText().toString()});
                }
            });
            //checkedが1(=TRUE)なら、checkedにする
            if(c_am.getInt(1) == 1){
                cb.setChecked(true);
            }

            //全体のlayoutに追加
            amLayout.addView(setRow(cb));
            bool = c_am.moveToNext();
        }
        c_am.close();

        final Cursor c_pm = db.query("default_tb", new String[]{"task_col", "checked_col"},
                "date_col =? AND time_col =?", new String[] {today_str, "PM"},
                null, null, null);
        bool = c_pm.moveToFirst();
        while(bool){
            //CheckBoxの設定
            final CheckBox cb = new CheckBox(MainActivity.this);
            cb.setText(c_pm.getString(0));
            if(Build.VERSION.SDK_INT < 23){
                cb.setTextAppearance(MainActivity.this, R.style.TextAppearance_AppCompat_Small);
            }else{
                cb.setTextAppearance(R.style.TextAppearance_AppCompat_Small);
            }
            //CheckBoxにリスナーをつける
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //DBのアップデート処理
                    ContentValues val = new ContentValues();
                    val.put("checked_col", 1);
                    db.update("default_tb", val, "task_col =?", new String[]{cb.getText().toString()});
                }
            });
            //checkedが1(=TRUE)なら、checkedにする
            if(c_pm.getInt(1) == 1){
                cb.setChecked(true);
            }

            //全体のlayoutに追加
            pmLayout.addView(setRow(cb));
            bool = c_pm.moveToNext();
        }
        c_pm.close();
    }

    //showDBの中で、部品を配置するメソッド
    protected LinearLayout setRow(CheckBox cb_param){
        //rowの高さ
        int row_h = dpToPx(this, 25);
        int mar_l = dpToPx(this, 13);
        int mar_t = dpToPx(this, 7);

        //CheckBox変数
        final CheckBox cb = cb_param;
        LinearLayout.LayoutParams cb_lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, row_h);
        cb.setLayoutParams(cb_lp);

        //ボタンのLayoutParamsの設定
        LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(
                row_h, row_h);
        btn_params.setMargins(mar_l, 0, 0, 0);

        //アイコンの設定
        ImageButton edit_btn = new ImageButton(MainActivity.this);
        edit_btn.setImageResource(R.drawable.ic_edit);
        if(Build.VERSION.SDK_INT >= 16){
            edit_btn.setBackground(null);
        } else {
            edit_btn.setBackgroundDrawable(null);
        }
        edit_btn.setLayoutParams(btn_params);
        edit_btn.setScaleType(ImageButton.ScaleType.CENTER_CROP);
        edit_btn.setPadding(0, 0, 0, 0);

        ImageButton delete_btn = new ImageButton(MainActivity.this);
        delete_btn.setImageResource(R.drawable.ic_delete);
        if(Build.VERSION.SDK_INT >= 16){
            delete_btn.setBackground(null);
        } else {
            delete_btn.setBackgroundDrawable(null);
        }
        delete_btn.setLayoutParams(btn_params);
        delete_btn.setScaleType(ImageButton.ScaleType.CENTER_CROP);
        delete_btn.setPadding(0, 0, 0, 0);

        //リスナーの登録
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UPDATE画面に遷移
                Intent intent = new Intent(getApplication(), UpdateActivity.class);
                //intentにデータを渡す
                intent.putExtra("task_data", cb.getText().toString());
                startActivity(intent);
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //データベースを削除
                deleteDb(cb.getText().toString());

                //CheckBoxが入っているLayoutを取得
                LinearLayout ll = (LinearLayout)cb.getParent();
                //Layoutが入ってるLayoutを取得
                LinearLayout ll_p = (LinearLayout)ll.getParent();
                //CheckBoxの入っているLayoutのindexを取得
                int ll_index = ((ViewGroup)ll_p).indexOfChild(ll);
                //CheckBoxの入っているLayoutを削除
                ll_p.removeViewAt(ll_index);
            }
        });

        //LinearLayoutの設定
        LinearLayout layout = new LinearLayout(MainActivity.this);//インスタンスの生成
        layout.setGravity(Gravity.CENTER_VERTICAL);//Gravityの設定

        //CheckBoxとImageButtonの間をうめるやつ
        View view = new View(MainActivity.this);
        LinearLayout.LayoutParams v_lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 1, 1f);
        view.setLayoutParams(v_lp);

        //LinearLayoutに部品を追加
        layout.addView(cb);
        layout.addView(view);
        layout.addView(edit_btn);
        layout.addView(delete_btn);

        //layoutのLayoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, mar_t, 0, 0);
        layout.setLayoutParams(params);

        return layout;
    }

    //データベースを削除
    protected void deleteDb(String task_str){
        //データベースの取得
        MyDBHelper helper = new MyDBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        //データベースの削除
        db.delete("default_tb", "task_col =?", new String[]{task_str});
    }

    //データベースの全削除をするメソッド
    protected void deleteAllDB(){
        //データベースの取得
        MyDBHelper helper = new MyDBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        //データベース削除
        db.delete("default_tb", null, null);
    }

    protected String calToDay(Calendar cal){
        int day_int = cal.get(Calendar.DAY_OF_WEEK);
        switch (day_int){
            case 1:
                return getString(R.string.sun);
            case 2:
                return getString(R.string.mon);
            case 3:
                return getString(R.string.tue);
            case 4:
                return getString(R.string.wed);
            case 5:
                return getString(R.string.thu);
            case 6:
                return getString(R.string.fri);
            case 7:
                return getString(R.string.sat);
            default:
                return getString(R.string.error);
        }
    }

    protected int dpToPx(Context context, int dp){
        float d = context.getResources().getDisplayMetrics().density;
        return (int)((dp*d)+0.5);
    }
}
