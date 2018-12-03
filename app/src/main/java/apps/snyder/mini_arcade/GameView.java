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

import java.util.ArrayList;
import java.util.List;

/*
Custom view class for handling game loop and user input. Thanks for idea and smarts from the author
of this tutorial: http://www.edu4java.com/en/androidgame/androidgame4.html
Updated on: 12/3/18
 */

public class GameView extends SurfaceView {
    private Bitmap bmp;
    private Bitmap eBmp;
    private SurfaceHolder holder;
    private Sprite sprite;
    private GameLoopThread loop;
    private int xValue;
    private int yValue;
    private List<EffectSprite> eSprites = new ArrayList<>();

    public GameView(Context context) {
        //public constructor, starts GameLoopThread and draws initial character sprite
        super(context);
        loop = new GameLoopThread(GameView.this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //create sprite
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.water_girl);
                eBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ring_effect);
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

    public void setXY(int x, int y, int button) {
        //sets x and y values with range (-512, 512)
        this.xValue = (x - 512);
        this.yValue = (y - 512);
        if (button == 0) {
            //fancy math to draw effect sprite on top of character sprite: x = sprite_position + 1/2_sprite_width + added_value, & vice versa
            eSprites.add(new EffectSprite(eSprites, this, sprite.getX() + (bmp.getWidth()/8) + xValue/25, sprite.getY() + (bmp.getHeight()/8) + yValue/25, eBmp));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        //draws to canvas updated sprite and effect sprite(s) if applicable
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);
        for (int i = eSprites.size()-1; i >= 0; i--) {
            eSprites.get(i).onDraw(canvas);
        }
        sprite.onDraw(canvas, xValue, yValue);
    }


    /*
    Inner class defines game loop thread (sounds like a catchy name!) for synchronized animation/event handling
    Updated on: 12/3/18
     */
    public class GameLoopThread extends Thread {
        static final long fps = 5;
        private GameView view;
        private boolean running = false;

        public GameLoopThread(GameView view) {
            //public constructor
            this.view = view;
        }

        public void setRunning(boolean run) {
            //is the supposed to be running?
            running = run;
        }

        @Override
        public void run() {
            //actual loop actions; calculates frames per sec for sprite drawing/loop synchronization
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
