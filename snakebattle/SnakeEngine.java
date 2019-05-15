package com.example.snakebattle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


class SnakeEngine extends SurfaceView implements Runnable {

    //hilo de juego para el bucle principal del juego.
    private Thread thread = null;

    //Para mantener una referencia a la Activity
    private Context context;

    // Para seguir el movimiento de rumbo
    public enum Heading {UP, RIGHT, DOWN, LEFT, UPRIGHT,UPLEFT,DOWNRIGHT,DOWNLEFT}
    public enum Heading1 {UP1, RIGHT1, DOWN1, LEFT1, UPRIGHT1,UPLEFT1,DOWNRIGHT1,DOWNLEFT1}
    //Comience por dirigirse hacia abajo
    private Heading heading = Heading.DOWN;//RIGHT
    private Heading1 heading1;
    // Para mantener el tamaño de la pantalla en píxeles
    private int screenX;
    private int screenY;

    // largo de la serpiente
    private int snakeLength;

    //  Bob bala
    private int bobX;
    private int bobY;

    //enemigos
    private int[] EX;
    private int[] EY;

    // El tamaño en píxeles de un segmento de serpiente.
    private int blockSize;

    // El tamaño en segmentos del área jugable.
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    // Control de pausa entre actualizaciones.
    private long nextFrameTime;
    // Actualiza el juego 10 veces por segundo
    private final long FPS = 10;
    // 1000 milisegundos en un segundo
    private final long MILLIS_PER_SECOND = 1000;
    private int score;
    private int vidas;
    private int lvl;
    // La ubicación de todos los segmentos de la serpiente
    private int[] snakeXs;
    private int[] snakeYs;

    // para dibujar.
    // El juego está jugando actualmente
    private volatile boolean isPlaying;

    //lienzo .
    private Canvas canvas;

    //Requerido para usar lienzo
    private SurfaceHolder surfaceHolder;

    // pintura
    private Paint paint;

    // para mover radialmente
    private int tita = 0;
    private int cont = 0;
    private int activos = 0;
    private int dirbala = 0;
    private int Bobi = 0;
    private int contbobi = 0;
    private int band = 0;
    private int randx,randy;
    private int[] enemigosactivos;
    private String nombre;

    public SnakeEngine(Context context, Point size, String name) {
        super(context);
        context = context.getApplicationContext();
        nombre = name;
        screenX = size.x;
        screenY = size.y;
        // Calcula cuántos píxeles es cada bloque.
        blockSize = screenX / NUM_BLOCKS_WIDE;
        // Cuántos bloques del mismo tamaño cabrán en la altura
        numBlocksHigh = screenY / blockSize;

        // Inicializar los objetos de dibujo.
        surfaceHolder = getHolder();
        paint = new Paint();
        snakeXs = new int[200];
        snakeYs = new int[200];
        EX = new int[100];
        EY = new int[100];
        enemigosactivos = new int[100];
        lvl = 0;
        score = 0;
        vidas = 5;
        enemigos();
        enemigosActivos();
        newGame();
    }

    @Override
    public void run() {

        while (isPlaying) {

            // Actualización 10 veces por segundo
            if(updateRequired()) {
                update();
                draw();
            }

        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
        //return score;
    }

    public void FIN(){
        Intent i=new Intent().setClass(getContext(),Main2Activity_Puntajes.class);
        String sc =Integer.toString(score);
        String total = score + " " + nombre;
        i.putExtra("puntos", total);
        //context.startActivity(i);
        (getContext()).startActivity(i);
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame() {
        // Comience con un solo segmento de serpiente
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;
        heading = Heading.DOWN;
        FinBob();
        snakeLength = lvl + 1;
        if(lvl == 5 && score == 200){
            //pause();
            FIN();
        }
        if(score == 200){
            //pause();
            FIN();
        }
        if(vidas == 0){
            //pause();
            FIN();
        }
        //vidas = 5;

        //enemigosActivos();
        // Configure nextFrameTime para que se active una actualización
        nextFrameTime = System.currentTimeMillis();
    }

    private boolean detectDeathEnemigos(){
        boolean dead = false;
        for(int i=0; i<100;i++){
            if(enemigosactivos[i] == 1) {
                if (EX[i] == bobX && EY[i] == bobY || EX[i] + 1 == bobX && EY[i] == bobY || EX[i] == bobX && EY[i] + 1 == bobY || EX[i] == bobX && EY[i] - 1 == bobY || EX[i] - 1 == bobX && EY[i] == bobY) {
                    enemigosactivos[i] = 0;
                    score = score + 10;
                    dead = true;
                }
            }
        }
        return dead;
    }

    public void enemigos(){
        Random rand = new Random();
        for(int i=0; i<100;i++){
            randx = rand.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
            randy = rand.nextInt(numBlocksHigh - 1) + 1;
            EX[i] =  randx;
            EY[i] =  randy;
            enemigosactivos[i] = 0;
        }
    }

    public void enemigoMover() {
        for(int i=0; i<100;i++){
            if(enemigosactivos[i] == 1) {
                if (EX[i] < snakeXs[0]) {
                    EX[i]++;
                } else {
                    EX[i]--;
                }
                if (EY[i] < snakeYs[0]) {
                    EY[i]++;
                } else {
                    EY[i]--;
                }
            }
        }
    }

    public void enemigosActivos() {

        if(lvl == 0){
            for(int i=0; i<2;i++){
                enemigosactivos[i] = 1;

            }
        }
        if(lvl == 1){
            for(int i=0; i<3;i++){
                enemigosactivos[i] = 1;

            }
        }
        if(lvl == 2){
            for(int i=0; i<4;i++){
                enemigosactivos[i] = 1;

            }
        }
        if(lvl == 3){
            for(int i=0; i<5;i++){
                enemigosactivos[i] = 1;

            }
        }
        if(lvl == 5){
            for(int i=0; i<6;i++){
                enemigosactivos[i] = 1;

            }
        }
    }

    public void spawnBob() {
        Bobi = 1;
        if(dirbala == 1){//right
            bobX = snakeXs[0]+1;
            bobY = snakeYs[0];
            heading1 = Heading1.RIGHT1;
        }
        if(dirbala == 3){//down
            bobX = snakeXs[0];
            bobY = snakeYs[0]+1;
            heading1 = Heading1.DOWN1;
        }
        if(dirbala == 2){//left
            bobX = snakeXs[0]-1;
            bobY = snakeYs[0];
            heading1 = Heading1.LEFT1;
        }
        if(dirbala == 0){//up
            bobX = snakeXs[0];
            bobY = snakeYs[0]-1;
            heading1 = Heading1.UP1;
        }
        if(dirbala == 4){//upleft
            bobX = snakeXs[0]-1;
            bobY = snakeYs[0]-1;
            heading1 = Heading1.UPLEFT1;
        }
        if(dirbala == 5){//upright
            bobX = snakeXs[0]+1;
            bobY = snakeYs[0]-1;
            heading1 = Heading1.RIGHT1;
        }
        if(dirbala == 6){//downleft
            bobX = snakeXs[0]-1;
            bobY = snakeYs[0]+1;
            heading1 = Heading1.DOWNLEFT1;
        }
        if(dirbala == 7){//downright
            bobX = snakeXs[0]+1;
            bobY = snakeYs[0]+1;
            heading1 = Heading1.DOWNRIGHT1;
        }
        moveBob();
    }

    public void FinBob(){
        Bobi  = 0;
        //para poder minimizar
    }

    private void moveBob(){
        switch (heading1) {
            case UP1:
                bobY = bobY-2;
                break;
            case RIGHT1:
                bobX = bobX+2;;
                break;
            case DOWN1:
                bobY = bobY+2;
                break;
            case LEFT1:
                bobX = bobX-2;;
                break;
            case UPRIGHT1:
                bobX++;
                bobY--;
                break;
            case DOWNRIGHT1:
                bobX++;
                bobY++;
                break;
            case DOWNLEFT1:
                bobX--;
                bobY++;
                break;
            case UPLEFT1:
                bobX--;
                bobY--;
                break;
        }
    }

    private void AlMorir(){
        Random rand = new Random();
        for(int i=0; i<100;i++){
            if(enemigosactivos[i] == 1){
                if (EX[i] == (NUM_BLOCKS_WIDE / 2) && EY[i] == (numBlocksHigh / 2) || EX[i]+1 == (NUM_BLOCKS_WIDE / 2) && EY[i] == (numBlocksHigh / 2) || EX[i] == (NUM_BLOCKS_WIDE / 2) && EY[i]+1 == (numBlocksHigh / 2) || EX[i] == (NUM_BLOCKS_WIDE / 2) && EY[i]-1 == (numBlocksHigh / 2) || EX[i]-1 == (NUM_BLOCKS_WIDE / 2) && EY[i] == (numBlocksHigh / 2)){
                    randx = rand.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
                    randy = rand.nextInt(numBlocksHigh - 1) + 1;
                    EX[i] =  randx;
                    EY[i] =  randy;
                }
            }
        }
    }

    private boolean detectDeathBob(){
        // Ha muerto Bob
        boolean deadBob = false;

        //Golpea el borde de la pantalla
        if (bobX == -1) deadBob = true;
        if (bobX >= NUM_BLOCKS_WIDE) deadBob = true;
        if (bobY == -1) deadBob = true;
        if (bobY == numBlocksHigh) deadBob = true;

        return deadBob;
    }

    private void moveSnake(){
        // mover el cuerpo
        for (int i = snakeLength; i > 0; i--) {

            //Empieza por atrás y muévete a la posición del segmento delante de él
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];

            //Excluir la cabeza porque
            //la cabeza no tiene nada delante
        }

        //Mueva la cabeza en el rumbo apropiado
        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
            case UPRIGHT:
                snakeXs[0]++;
                snakeYs[0]--;
                break;
            case DOWNRIGHT:
                snakeXs[0]++;
                snakeYs[0]++;
                break;
            case DOWNLEFT:
                snakeXs[0]--;
                snakeYs[0]++;
                break;
            case UPLEFT:
                snakeXs[0]--;
                snakeYs[0]--;
                break;
        }


    }

    private boolean detectDeath(){
        // Ha muerto la serpiente
        boolean dead = false;

        //Golpea el borde de la pantalla
        if (snakeXs[0] == -1) dead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (snakeYs[0] == -1) dead = true;
        if (snakeYs[0] == numBlocksHigh) dead = true;

        // Se ha comido
        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void update() {

        activos=0;
        moveSnake();
        for(int i=0; i<100;i++){
            if(enemigosactivos[i] == 1)
                activos++;
        }
        detectDeathEnemigos();
        if(band == 1) {
            enemigoMover();
        }
        if(Bobi == 1){
            moveBob();
        }
        if (detectDeathBob()) {
            FinBob();
        }

        if(band == 1)
            band = 0;
        else
            band = 1;

        if(  activos == 0 && lvl < 6){
            lvl = lvl + 1;

            enemigosActivos();
            newGame();
        }
        for (int i = snakeLength; i > 0; i--) {
            for(int j=0; j<100;j++) {
                if(enemigosactivos[j] == 1) {
                    if (snakeXs[i] == EX[j] && snakeYs[i] == EY[j]) {
                        vidas--;
                        AlMorir();
                        newGame();
                    }
                }
            }
        }
        if (detectDeath()) {
            vidas--;
            Bobi = 0;
            AlMorir();
            newGame();
        }
    }

    public void draw() {
        // Consigue un candado en el lienzo.
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Llena la pantalla con el código de juego
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Ajusta el color de la pintura para dibujar la serpiente blanca.
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Escala el texto de HUD
            paint.setTextSize(20);
            canvas.drawText("Score:" + score, 10, 30, paint);
            paint.setTextSize(20);
            canvas.drawText("Vidas:" + vidas, 110, 30, paint);
            paint.setTextSize(20);
            canvas.drawText("Nivel:" + lvl, 180, 30, paint);
            paint.setTextSize(20);
            canvas.drawText("Grados:" + ((tita)*45), 250, 30, paint);
            //paint.setTextSize(20);
            //canvas.drawText("Enemigos:" + activos, 360, 30, paint);

            // Dibuja la serpiente un bloque a la vez.
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            // Ajusta el color de la pintura para dibujar Bob rojo.
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Dibujar bob
            if(Bobi == 1) {
                canvas.drawRect(bobX * blockSize,
                        (bobY * blockSize),
                        (bobX * blockSize) + blockSize,
                        (bobY * blockSize) + blockSize,
                        paint);
            }
            paint.setColor(Color.argb(255, 0, 255, 0));
            // Dibujar ENEMIGOS
            for(int i=0; i<100;i++) {
                if(enemigosactivos[i] == 1){
                    canvas.drawRect(EX[i] * blockSize,
                            (EY[i] * blockSize),
                            (EX[i] * blockSize) + blockSize,
                            (EY[i] * blockSize) + blockSize,
                            paint);
                }
            }
            // Desbloquea el lienzo y revela los gráficos para este marco.
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {

        // Debemos actualizar el marco
        if(nextFrameTime <= System.currentTimeMillis()){
            // Ha pasado la décima de segundo

            // Configuración cuando se activará la próxima actualización
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Devuelve verdadero para que la actualización y el sorteo
            //            // funciones son ejecutadas
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if(Bobi == 0) {
                    spawnBob();
                    Bobi = 1;
                    contbobi = 0;
                }else
                    contbobi++;
                if(contbobi == 20){
                    FinBob();
                    spawnBob();
                    Bobi = 1;
                    contbobi = 0;
                }
            case MotionEvent.ACTION_DOWN:
                if(tita > 7)
                    tita = 0;
                if (motionEvent.getX() >= screenX / 2) {//derecho
                    tita ++;
                    switch(heading){
                        case UP:
                            cont = 0;
                            if(tita == 0)heading = Heading.UPRIGHT;
                            if(tita == 1)heading = Heading.RIGHT;
                            if(tita == 2)heading = Heading.DOWNRIGHT;
                            if(tita == 3)heading = Heading.DOWN;
                            if(tita == 4)heading = Heading.DOWNLEFT;
                            if(tita == 5)heading = Heading.LEFT;
                            if(tita == 6)heading = Heading.UPLEFT;
                            if(tita == 7)heading = Heading.UP;
                            break;
                        case RIGHT:
                            cont = 1;
                            if(tita == 0)heading = Heading.DOWNRIGHT;
                            if(tita == 1)heading = Heading.DOWN;
                            if(tita == 2)heading = Heading.DOWNLEFT;
                            if(tita == 3)heading = Heading.LEFT;
                            if(tita == 4)heading = Heading.UPLEFT;
                            if(tita == 5)heading = Heading.UP;
                            if(tita == 6)heading = Heading.UPRIGHT;
                            if(tita == 7)heading = Heading.RIGHT;
                            break;
                        case LEFT:
                            cont = 2;
                            if(tita == 0)heading = Heading.UPLEFT;
                            if(tita == 1)heading = Heading.UP;
                            if(tita == 2)heading = Heading.UPRIGHT;
                            if(tita == 3)heading = Heading.RIGHT;
                            if(tita == 4)heading = Heading.DOWNRIGHT;
                            if(tita == 5)heading = Heading.DOWN;
                            if(tita == 6)heading = Heading.DOWNLEFT;
                            if(tita == 7)heading = Heading.LEFT;
                            break;
                        case DOWN:
                            cont = 3;
                            if(tita == 0)heading = Heading.DOWNLEFT;
                            if(tita == 1)heading = Heading.LEFT;
                            if(tita == 2)heading = Heading.UPLEFT;
                            if(tita == 3)heading = Heading.UP;
                            if(tita == 4)heading = Heading.UPRIGHT;
                            if(tita == 5)heading = Heading.RIGHT;
                            if(tita == 6)heading = Heading.DOWNRIGHT;
                            if(tita == 7)heading = Heading.DOWN;
                            break;
                        case UPLEFT:
                            cont = 4;
                            if(tita == 0)heading = Heading.UP;
                            if(tita == 1)heading = Heading.UPRIGHT;
                            if(tita == 2)heading = Heading.RIGHT;
                            if(tita == 3)heading = Heading.DOWNRIGHT;
                            if(tita == 4)heading = Heading.DOWN;
                            if(tita == 5)heading = Heading.DOWNLEFT;
                            if(tita == 6)heading = Heading.LEFT;
                            if(tita == 7)heading = Heading.UPLEFT;
                            break;
                        case UPRIGHT:
                            cont = 5;
                            if(tita == 0)heading = Heading.RIGHT;
                            if(tita == 1)heading = Heading.DOWNRIGHT;
                            if(tita == 2)heading = Heading.DOWN;
                            if(tita == 3)heading = Heading.DOWNLEFT;
                            if(tita == 4)heading = Heading.LEFT;
                            if(tita == 5)heading = Heading.UPLEFT;
                            if(tita == 6)heading = Heading.UP;
                            if(tita == 7)heading = Heading.UPRIGHT;
                            break;
                        case DOWNLEFT:
                            cont = 6;
                            if(tita == 0)heading = Heading.LEFT;
                            if(tita == 1)heading = Heading.UPLEFT;
                            if(tita == 2)heading = Heading.UP;
                            if(tita == 3)heading = Heading.UPRIGHT;
                            if(tita == 4)heading = Heading.RIGHT;
                            if(tita == 5)heading = Heading.DOWNRIGHT;
                            if(tita == 6)heading = Heading.DOWN;
                            if(tita == 7)heading = Heading.DOWNLEFT;
                            break;
                        case DOWNRIGHT:
                            cont = 7;
                            if(tita == 0)heading = Heading.DOWN;
                            if(tita == 1)heading = Heading.DOWNLEFT;
                            if(tita == 2)heading = Heading.LEFT;
                            if(tita == 3)heading = Heading.UPLEFT;
                            if(tita == 4)heading = Heading.UP;
                            if(tita == 5)heading = Heading.UPRIGHT;
                            if(tita == 6)heading = Heading.RIGHT;
                            if(tita == 7)heading = Heading.DOWNRIGHT;
                            break;
                    }
                } else {//izquierdo
                    tita++;
                    switch(heading){
                        case UP:
                            cont = 0;
                            if(tita == 0)heading = Heading.UPLEFT;
                            if(tita == 1)heading = Heading.LEFT;
                            if(tita == 2)heading = Heading.DOWNLEFT;
                            if(tita == 3)heading = Heading.DOWN;
                            if(tita == 4)heading = Heading.DOWNRIGHT;
                            if(tita == 5)heading = Heading.RIGHT;
                            if(tita == 6)heading = Heading.UPRIGHT;
                            if(tita == 7)heading = Heading.UP;
                            break;
                        case RIGHT:
                            cont = 1;
                            if(tita == 0)heading = Heading.UPRIGHT;
                            if(tita == 1)heading = Heading.UP;
                            if(tita == 2)heading = Heading.UPLEFT;
                            if(tita == 3)heading = Heading.LEFT;
                            if(tita == 4)heading = Heading.DOWNLEFT;
                            if(tita == 5)heading = Heading.DOWN;
                            if(tita == 6)heading = Heading.DOWNRIGHT;
                            if(tita == 7)heading = Heading.RIGHT;
                            break;
                        case LEFT:
                            cont = 2;
                            if(tita == 0)heading = Heading.DOWNLEFT;
                            if(tita == 1)heading = Heading.DOWN;
                            if(tita == 2)heading = Heading.DOWNRIGHT;
                            if(tita == 3)heading = Heading.RIGHT;
                            if(tita == 4)heading = Heading.UPRIGHT;
                            if(tita == 5)heading = Heading.UP;
                            if(tita == 6)heading = Heading.UPLEFT;
                            if(tita == 7)heading = Heading.LEFT;
                            break;
                        case DOWN:
                            cont = 3;
                            if(tita == 0)heading = Heading.DOWNRIGHT;
                            if(tita == 1)heading = Heading.RIGHT;
                            if(tita == 2)heading = Heading.UPRIGHT;
                            if(tita == 3)heading = Heading.UP;
                            if(tita == 4)heading = Heading.UPLEFT;
                            if(tita == 5)heading = Heading.LEFT;
                            if(tita == 6)heading = Heading.DOWNLEFT;
                            if(tita == 7)heading = Heading.DOWN;
                            break;
                        case UPLEFT:
                            cont = 4;
                            if(tita == 0)heading = Heading.LEFT;
                            if(tita == 1)heading = Heading.DOWNLEFT;
                            if(tita == 2)heading = Heading.DOWN;
                            if(tita == 3)heading = Heading.DOWNRIGHT;
                            if(tita == 4)heading = Heading.RIGHT;
                            if(tita == 5)heading = Heading.UPRIGHT;
                            if(tita == 6)heading = Heading.UP;
                            if(tita == 7)heading = Heading.UPLEFT;
                            break;
                        case UPRIGHT:
                            cont = 5;
                            if(tita == 0)heading = Heading.UP;
                            if(tita == 1)heading = Heading.UPLEFT;
                            if(tita == 2)heading = Heading.LEFT;
                            if(tita == 3)heading = Heading.DOWNLEFT;
                            if(tita == 4)heading = Heading.DOWN;
                            if(tita == 5)heading = Heading.DOWNRIGHT;
                            if(tita == 6)heading = Heading.RIGHT;
                            if(tita == 7)heading = Heading.UPRIGHT;
                            break;
                        case DOWNLEFT:
                            cont = 6;
                            if(tita == 0)heading = Heading.DOWN;
                            if(tita == 1)heading = Heading.DOWNRIGHT;
                            if(tita == 2)heading = Heading.RIGHT;
                            if(tita == 3)heading = Heading.UPRIGHT;
                            if(tita == 4)heading = Heading.UP;
                            if(tita == 5)heading = Heading.UPLEFT;
                            if(tita == 6)heading = Heading.LEFT;
                            if(tita == 7)heading = Heading.DOWNLEFT;
                            break;
                        case DOWNRIGHT:
                            cont = 7;
                            if(tita == 0)heading = Heading.RIGHT;
                            if(tita == 1)heading = Heading.UPRIGHT;
                            if(tita == 2)heading = Heading.UP;
                            if(tita == 3)heading = Heading.UPLEFT;
                            if(tita == 4)heading = Heading.LEFT;
                            if(tita == 5)heading = Heading.DOWNLEFT;
                            if(tita == 6)heading = Heading.DOWN;
                            if(tita == 7)heading = Heading.DOWNRIGHT;
                            break;
                    }
                }
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    switch(heading){
                        case UP:
                            dirbala= 0;
                            break;
                        case RIGHT:
                            dirbala= 1;
                            break;
                        case LEFT:
                            dirbala= 2;
                            break;
                        case DOWN:
                            dirbala= 3;
                            break;
                        case UPLEFT:
                            dirbala= 0;
                            break;
                        case UPRIGHT:
                            dirbala= 1;
                            break;
                        case DOWNLEFT:
                            dirbala= 2;
                            break;
                        case DOWNRIGHT:
                            dirbala= 3;
                            break;
                    }
                } else {
                    switch(heading){
                        case UP:
                            dirbala= 0;
                            break;
                        case RIGHT:
                            dirbala= 1;
                            break;
                        case LEFT:
                            dirbala= 2;
                            break;
                        case DOWN:
                            dirbala= 3;
                            break;
                        case UPLEFT:
                            dirbala= 0;
                            break;
                        case UPRIGHT:
                            dirbala= 1;
                            break;
                        case DOWNLEFT:
                            dirbala= 2;
                            break;
                        case DOWNRIGHT:
                            dirbala= 3;
                            break;
                    }
                }

        }
        return true;
    }
}
