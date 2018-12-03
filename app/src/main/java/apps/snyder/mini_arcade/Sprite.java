package apps.snyder.mini_arcade;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.List;

/*
Original idea from this great tutorial: http://www.edu4java.com/en/androidgame/androidgame4.html
Retrofitted for my own personal purposes.
Edited On: 12/3/18
 */

public class Sprite {
    //creates sprite and handles animation according to input
    private static final int[] animationMap = {0, 2, 1, 3};
    // ^^^maps direction to animation: 0 up/back, 1 right, 2 down/front, 3 left
    private static final int bmpRows = 4;
    private static final int bmpColumns = 4;
    private int x;
    private int y;
    private int xSpeed = 5;
    private int ySpeed = 5;
    private Bitmap bmp;
    private GameView gameView;
    private int currentFrame = 0;
    private int height;
    private int width;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private List<EffectSprite> eSprites = new ArrayList<>();

    public Sprite(GameView gameView, Bitmap bm) {
        //public constructor
        this.x = gameView.getWidth() / 3;  //sets coordinates to center
        this.y = gameView.getHeight() / 3;
        this.bmp = bm;
        this.gameView = gameView;
        this.width = bmp.getWidth() / bmpColumns;
        this.height = bmp.getHeight() / bmpRows;
    }

    //getter methods, useful for sending coordinates to EffectSprite class
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    private void update() {
        //private method to update frame and next screen position
        if (!(x >= gameView.getWidth() - width - (xSpeed/25)) && !(x + (xSpeed/25) <= 0)) {
            x = x + (xSpeed / 25);
        }
        if (!(y >= gameView.getHeight() - height - (ySpeed/25)) && !(y + (ySpeed/25) <= 0)) {
            y = y + (ySpeed / 25);
        }
        currentFrame = ++currentFrame % bmpColumns;
    }

    public void onDraw(Canvas canvas, int xValue, int yValue) {
        //public method to redraw sprite according to screen position offset by passed in joystick x/y values
        this.xSpeed = xValue;
        this.ySpeed = yValue;
        update();
        int srcX = currentFrame * width;
        int srcY = getAnimationRow() * height;
        src.set(srcX, srcY, srcX + width, srcY + height);
        dst.set(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    public void onDraw(Canvas canvas) {
        //public method to redraw sprite without passed in values
        update();
        int srcX = currentFrame * width;
        int srcY = getAnimationRow() * height;
        src.set(srcX, srcY, srcX + width, srcY + height);
        dst.set(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    private int getAnimationRow() {
        //determines direction of sprite and returns appropriate bitmap row
        double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % bmpRows;
        //buffer for non-motion
        if (-20 < xSpeed && xSpeed < 20 && -20 < ySpeed && ySpeed < 20) {
            return animationMap[2];
        }
        return animationMap[direction];
    }
}
