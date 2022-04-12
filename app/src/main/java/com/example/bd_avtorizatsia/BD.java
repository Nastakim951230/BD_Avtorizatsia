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
    EditText etTovar, etKolvo, etPrice;
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


        etTovar=(EditText) findViewById(R.id.etTovar);
        etKolvo = (EditText) findViewById(R.id.etKolvo);
        etPrice=(EditText) findViewById(R.id.etPrice);
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
            int tovarIndex= cursor.getColumnIndex(DBHelper.KEY_TOVAR);
            int KolvoIndex = cursor.getColumnIndex(DBHelper.KEY_KOLVO);
            int PriceIndex = cursor.getColumnIndex(DBHelper.KEY_PRICE);
            do{
                TableRow dbOutputRow=new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID= new TextView(this);
                params.weight=1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);


                TextView outputTovar= new TextView(this);
                params.weight=3.0f;
                outputTovar.setLayoutParams(params);
                outputTovar.setText(cursor.getString(tovarIndex));
                dbOutputRow.addView(outputTovar);

                TextView outputKolvo= new TextView(this);
                params.weight=3.0f;
                outputKolvo.setLayoutParams(params);
                outputKolvo.setText(cursor.getString(KolvoIndex));
                dbOutputRow.addView(outputKolvo);

                TextView outputPrice= new TextView(this);
                params.weight=3.0f;
                outputPrice.setLayoutParams(params);
                outputPrice.setText(cursor.getString(PriceIndex));
                dbOutputRow.addView(outputPrice);

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

                String tovar = etTovar.getText().toString();
                String kolvo = etKolvo.getText().toString();
                String price = etPrice.getText().toString();

                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_TOVAR, tovar);
                contentValues.put(DBHelper.KEY_KOLVO, kolvo);
                contentValues.put(DBHelper.KEY_PRICE, price);

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

                    int tovarIndex=cursorUpdater.getColumnIndex(DBHelper.KEY_TOVAR);
                    int KolvoIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_KOLVO);
                    int PriceIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_PRICE);
                    int realID=1;
                    do{
                        if(cursorUpdater.getInt(idIndex)>realID)
                        {
                            contentValues.put(DBHelper.KEY_ID,realID);

                            contentValues.put(DBHelper.KEY_TOVAR,cursorUpdater.getString(tovarIndex));
                            contentValues.put(DBHelper.KEY_KOLVO,cursorUpdater.getString(KolvoIndex));
                            contentValues.put(DBHelper.KEY_PRICE,cursorUpdater.getString(PriceIndex));
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