package com.example.snakebattle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;
import android.widget.Toast;

public class Main2Activity_Juego extends AppCompatActivity {

    SnakeEngine snakeEngine;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        // Consigue las dimensiones en píxeles de la pantalla.
        Display display = getWindowManager().getDefaultDisplay();

        //Inicializa el resultado en un objeto Point
        Point size = new Point();
        display.getSize(size);
        String player = getIntent().getStringExtra("jugador");
        //Toast.makeText(this, " Preparate " + player, Toast.LENGTH_SHORT).show();
        // Crear una nueva instancia de la clase SnakeEngine
        snakeEngine = new SnakeEngine(this, size, player);

        // Hacer que snakeEngine la vista de la Actividad.
        setContentView(snakeEngine);
        mp = MediaPlayer.create(this, R.raw.goats);
        mp.start();
        mp.setLooping(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mp.release();
        snakeEngine.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mp.stop();
        //mp.release();
        snakeEngine.pause();
    }
    @Override
    public void onBackPressed(){

    }
}
