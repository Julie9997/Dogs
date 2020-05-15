package com.example.dogs;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    EditText nameBox;
    EditText yearBox;
    EditText breedBox;
    EditText dateBox;
    EditText featureBox;
    Button delButton;
    Button saveButton;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor dogCursor;
    long dogId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameBox = findViewById(R.id.editName);
        yearBox = findViewById(R.id.editAge);
        breedBox = findViewById(R.id.editType);
        dateBox = findViewById(R.id.editDate);
        featureBox = findViewById(R.id.editFeatures);
        delButton = findViewById(R.id.deleteButton);
        saveButton =findViewById(R.id.saveButton);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dogId = extras.getLong("id");
        }
        // если 0, то добавление
        if (dogId > 0) {
            // получаем элемент по id из бд
            dogCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(dogId)});
            dogCursor.moveToFirst();
            nameBox.setText(dogCursor.getString(1));
            yearBox.setText(String.valueOf(dogCursor.getInt(2)));
            breedBox.setText(dogCursor.getString(3));
            dateBox.setText(dogCursor.getString(4));
            featureBox.setText(dogCursor.getString(5));
            dogCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }

    public void save(View view){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, nameBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_YEAR, Integer.parseInt(yearBox.getText().toString()));
        cv.put(DatabaseHelper.COLUMN_BREED, breedBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_DATE, dateBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_FEATURE, featureBox.getText().toString());

        if (dogId > 0) {
            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(dogId), null);
        } else {
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
        goHome();
    }
    public void delete(View view){
        db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(dogId)});
        goHome();
    }
    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, AboutDogsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}