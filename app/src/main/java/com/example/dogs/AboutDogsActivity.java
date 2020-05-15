package com.example.dogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AboutDogsActivity extends Activity {

    TextView header;
    EditText dogFilter;
    ListView dogList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor dogCursor;
    SimpleCursorAdapter dogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutdogs);

        dogFilter = (EditText)findViewById(R.id.dogFilter);
        header = (TextView)findViewById(R.id.header);
        dogList = (ListView)findViewById(R.id.dog_list);
        dogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        dogCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_YEAR,
                DatabaseHelper.COLUMN_BREED, DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_FEATURE};
        // создаем адаптер, передаем в него курсор
        dogAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
                dogCursor, headers, new int[]{R.id.dog_name, R.id.age, R.id.breed, R.id.date, R.id.features}, 0);

        if(!dogFilter.getText().toString().isEmpty())
            dogAdapter.getFilter().filter(dogFilter.getText().toString());

        // установка слушателя изменения текста
        dogFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                dogAdapter.getFilter().filter(s.toString());
            }
        });
        dogAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {

                    return db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                            DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });
        header.setText("Собак в питомнике: " + String.valueOf(dogCursor.getCount()));
        dogList.setAdapter(dogAdapter);
    }

    // по нажатию на кнопку запускаем Activity для добавления данных
    public void add(View view){
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        dogCursor.close();
    }

}
