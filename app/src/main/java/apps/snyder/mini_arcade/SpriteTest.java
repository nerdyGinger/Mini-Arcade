package apps.snyder.mini_arcade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SpriteTest extends AppCompatActivity {
    private int xValue = 1000;
    private int yValue = 500;
    private int button = 1;
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
                    //go down
                        xValue = 500;
                        yValue = 1000;
                        gameView.setXY(xValue, yValue, button);
                        Thread.sleep(2000);
                        //go left
                        button = 0;
                        xValue = 0;
                        yValue = 500;
                        gameView.setXY(xValue, yValue, button);
                        Thread.sleep(2000);
                        //go up
                        button = 1;
                        xValue = 500;
                        yValue = 0;
                        gameView.setXY(xValue, yValue, button);
                        Thread.sleep(2000);
                        //go right
                        xValue = 1000;
                        yValue = 500;
                        gameView.setXY(xValue, yValue, button);
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
