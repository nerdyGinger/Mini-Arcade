package apps.snyder.mini_arcade;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//---> Class for sprite drawing and real-time game animation
public class GameView extends SurfaceView {
    private Bitmap bmp;
    private SurfaceHolder holder;
    private Sprite sprite;
    private GameLoopThread loop;
    private int xValue;
    private int yValue;

    public GameView(Context context) {
        super(context);
        loop = new GameLoopThread(GameView.this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //create sprite
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.water_girl);
                sprite = new Sprite(GameView.this, bmp);
                //start loop
                loop.setRunning(true);
                loop.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                loop.setRunning(false);
                while (retry) {
                    try {
                        loop.join();
                        retry = false;
                    } catch (InterruptedException ex) {
                        Log.e("-----Important!----->", ex.toString());
                    }
                }
            }
        });

    }

    public void setXY(int x, int y) {
        //sets x and y values with range (-512, 512)
        this.xValue = (x - 512);
        this.yValue = (y - 512);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);
        sprite.onDraw(canvas, xValue, yValue);
    }



    public class GameLoopThread extends Thread {
        static final long fps = 5;
        private GameView view;
        private boolean running = false;

        public GameLoopThread(GameView view) {
            this.view = view;
        }

        public void setRunning(boolean run) {
            running = run;
        }

        @Override
        public void run() {
            long ticksPS = 1000 / fps;
            long startTime;
            long sleepTime;

            while(running) {
                Canvas c = null;
                startTime = System.currentTimeMillis();
                try {
                    c = view.getHolder().lockCanvas();
                    synchronized (view.getHolder()) {
                        view.draw(c);
                    }
                } finally {
                    if (c != null) {
                        view.getHolder().unlockCanvasAndPost(c);
                    }
                }
                sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
                try {
                    if (sleepTime > 0) {
                        sleep(sleepTime);
                    } else {
                        sleep(10);
                    }
                } catch (Exception ex) {
                    Log.e("----->Important!----->", ex.toString());
                }
            }
        }

    }
}
