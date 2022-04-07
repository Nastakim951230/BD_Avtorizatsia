package com.example.bd_avtorizatsia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BD extends AppCompatActivity implements View.OnClickListener {

    Button btnAdd,  btnClear, btnNazad;
    EditText etFamila,etName,etTovar, etEmail;
    SQLiteDatabase database;
    DBHelper dbHelper;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bd);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnNazad=(Button)  findViewById(R.id.nazad);
        btnNazad.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etFamila=(EditText)findViewById(R.id.etFamila);
        etName = (EditText) findViewById(R.id.etName);
        etTovar=(EditText) findViewById(R.id.etTovar);
        etEmail = (EditText) findViewById(R.id.etEmail);
        dbHelper = new DBHelper(this);
        database=dbHelper.getWritableDatabase();

        UpdateTable();
    }
    public void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        TableLayout dbOutput=findViewById(R.id.dbOutrut);
        dbOutput.removeAllViews();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int familaIndex= cursor.getColumnIndex(DBHelper.KEY_FAMILIA);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int tovarIndex= cursor.getColumnIndex(DBHelper.KEY_TOVAR);
            int emailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL);
            do{
                TableRow dbOutputRow=new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID= new TextView(this);
                params.weight=1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);

                TextView outputFamilia= new TextView(this);
                params.weight=3.0f;
                outputFamilia.setLayoutParams(params);
                outputFamilia.setText(cursor.getString(familaIndex));
                dbOutputRow.addView(outputFamilia);

                TextView outputName= new TextView(this);
                params.weight=3.0f;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nameIndex));
                dbOutputRow.addView(outputName);

                TextView outputTovar= new TextView(this);
                params.weight=3.0f;
                outputTovar.setLayoutParams(params);
                outputTovar.setText(cursor.getString(tovarIndex));
                dbOutputRow.addView(outputTovar);

                TextView outputMail= new TextView(this);
                params.weight=3.0f;
                outputMail.setLayoutParams(params);
                outputMail.setText(cursor.getString(emailIndex));
                dbOutputRow.addView(outputMail);

                Button deleteBtn= new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight=1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить запись");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnAdd:
                String familia=etFamila.getText().toString();
                String name = etName.getText().toString();
                String tovar = etTovar.getText().toString();
                String email = etEmail.getText().toString();

                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_FAMILIA, familia);
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_TOVAR, tovar);
                contentValues.put(DBHelper.KEY_MAIL, email);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

                UpdateTable();
                break;

            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput=findViewById(R.id.dbOutrut);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            default:
                View outputDBRow =(View) v.getParent();
                ViewGroup outputDB =(ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID+"=?", new String[]{String.valueOf(v.getId())});

                contentValues=new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int familaIndex=cursorUpdater.getColumnIndex(DBHelper.KEY_FAMILIA);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int tovarIndex=cursorUpdater.getColumnIndex(DBHelper.KEY_TOVAR);
                    int emailIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MAIL);
                    int realID=1;
                    do{
                        if(cursorUpdater.getInt(idIndex)>realID)
                        {
                            contentValues.put(DBHelper.KEY_ID,realID);
                            contentValues.put(DBHelper.KEY_FAMILIA,cursorUpdater.getString(familaIndex));
                            contentValues.put(DBHelper.KEY_NAME,cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_TOVAR,cursorUpdater.getString(tovarIndex));
                            contentValues.put(DBHelper.KEY_MAIL,cursorUpdater.getString(emailIndex));
                            database.replace(DBHelper.TABLE_CONTACTS,null,contentValues);
                        }
                        realID++;
                    }while (cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast()){
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID+"=?",new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }

                break;
            case R.id.nazad:
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }

    }
}