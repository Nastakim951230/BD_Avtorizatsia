package com.example.bd_avtorizatsia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView usernameField, passwordField;
    Button loginBtn, signinBtn;

    DBHelper dbHelper;
    SQLiteDatabase database;

    String adminUser = "admin";
    String adminPassword = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameField=findViewById(R.id.Username);
        passwordField=findViewById(R.id.Password);

        loginBtn=findViewById(R.id.Vhod);
        loginBtn.setOnClickListener(this);
        signinBtn=findViewById(R.id.registr);
        signinBtn.setOnClickListener(this);

        dbHelper= new DBHelper(this);
        database=dbHelper.getWritableDatabase();


        usernameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    usernameField.setHint("");
                else
                    usernameField.setHint("Логин");
            }
        });

        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    passwordField.setHint("");
                else
                    passwordField.setHint("Пароль");
            }
        });


        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_USER, adminUser);
        contentValues.put(DBHelper.KEY_PASSWORD, adminPassword);

        database.insert(DBHelper.TABLE_USERS, null, contentValues);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Vhod:
                Cursor logcursor=database.query(DBHelper.TABLE_USERS, null, null, null, null, null, null);

                boolean loqqed=false;
                if (logcursor.moveToFirst()){
                    int usernameIndex=logcursor.getColumnIndex(DBHelper.KEY_USER);
                    int passwordIndex=logcursor.getColumnIndex(DBHelper.KEY_PASSWORD);
                    do{
                        if(usernameField.getText().toString().equals(adminUser) && passwordField.getText().toString().equals(adminPassword)) {
                            startActivity(new Intent(this, BD.class));
                            loqqed = true;
                            break;
                        }
                        if(usernameField.getText().toString().equals(logcursor.getString(usernameIndex))&& passwordField.getText().toString().equals(logcursor.getString(passwordIndex))){
                            startActivity(new Intent(this,Prosmotr.class));
                            loqqed=true;
                            break;
                        }
                    }while (logcursor.moveToNext());
                }
                logcursor.close();
                if(!loqqed) Toast.makeText(this,"Введенная комбинация логина и пароля не была найдина.",Toast.LENGTH_LONG).show();

                break;
            case R.id.registr:
                Cursor Signcursor=database.query(DBHelper.TABLE_USERS, null, null, null, null, null, null);

                boolean finded=false;
                if(Signcursor.moveToFirst()){
                    int usernameIndex=Signcursor.getColumnIndex(DBHelper.KEY_USER);
                    do{
                        if (usernameField.getText().toString().equals(Signcursor.getString(usernameIndex))){
                            Toast.makeText(this,"Введенный вами логин уже зарегистрирован.",Toast.LENGTH_LONG).show();
                            finded=true;
                            break;
                        }
                    }while (Signcursor.moveToNext());
                }
                if(!finded){
                    ContentValues contentValues= new ContentValues();
                    contentValues.put(DBHelper.KEY_USER,usernameField.getText().toString());
                    contentValues.put(DBHelper.KEY_PASSWORD,passwordField.getText().toString());
                    database.insert(DBHelper.TABLE_USERS,null,contentValues);
                    Toast.makeText(this,"Вы успешно зарегистрировались",Toast.LENGTH_LONG).show();
                }
                Signcursor.close();
                break;
        }
    }
}