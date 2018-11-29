package apps.snyder.mini_arcade;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SpriteTest extends AppCompatActivity {
    private int xValue = 1000;
    private int yValue = 500;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
        animation();
    }

    private void animation() {
        Thread animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                    //go up
                    xValue = 500;
                    yValue = 1000;
                    gameView.setXY(xValue, yValue);
                    Thread.sleep(2000);
                    //go left
                    xValue = 0;
                    yValue = 500;
                    gameView.setXY(xValue, yValue);
                    Thread.sleep(2000);
                    //go down
                    xValue = 500;
                    yValue = 0;
                    gameView.setXY(xValue, yValue);
                    Thread.sleep(2000);
                    //go right
                    xValue = 1000;
                    yValue = 500;
                    gameView.setXY(xValue, yValue);
                    Thread.sleep(2000);
                    } catch (Exception ex) {
                        Log.e("-----Important!----->", ex.toString());
                    }
                }
            }
        });
        animationThread.start();
    }

}
