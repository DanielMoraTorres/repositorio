package com.example.snakebattle;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity_Puntajes extends AppCompatActivity {

    private TextView tv_first;
    private TextView tv_second;
    private TextView tv_third;
    private TextView tv_quarter;
    private TextView tv_fifth;
    private int numEntero;
    private String part2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__puntajes);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        tv_first = (TextView)findViewById(R.id.textView_first);
        tv_second = (TextView)findViewById(R.id.textView_second);
        tv_third = (TextView)findViewById(R.id.textView_third);
        tv_quarter = (TextView)findViewById(R.id.textView_quarter);
        tv_fifth = (TextView)findViewById(R.id.textView_fifth);
        //String player = getIntent().getStringExtra("jugadores");
        String punto = getIntent().getStringExtra("puntos");

        //tv_first.setText("Record: " + punto );
        String[] parts = punto.split(" ");
        String part1 = parts[0];
        part2 = parts[1];
        numEntero = Integer.parseInt(part1);
        Toast.makeText(this, "Puntuacion de  " + numEntero, Toast.LENGTH_SHORT).show();
        BaseDeDatos();
        best();

    }

    public void best(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery(
                "select * from puntaje where score = (select max(score) from puntaje)", null);
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            tv_fifth.setText( temp_nombre + " " + temp_score);
            //Toast.makeText(this, "Record: " + temp_score + " de " + temp_nombre + numEntero, Toast.LENGTH_SHORT).show();
            BD.close();
        } else {
            BD.close();
        }
    }

    public void next(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void BaseDeDatos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery("select * from puntaje where score = (select max(score) from puntaje)", null);
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);

            int bestScore = Integer.parseInt(temp_score);

            if(numEntero > bestScore){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre", part2);
                modificacion.put("score", numEntero);

                BD.update("puntaje", modificacion, "score=" + bestScore, null);
            }

            BD.close();

        } else {
            ContentValues insertar = new ContentValues();

            insertar.put("nombre", part2);
            insertar.put("score", numEntero);

            BD.insert("puntaje", null, insertar);
            BD.close();
        }
    }

    @Override
    public void onBackPressed(){

    }
}
