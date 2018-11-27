package apps.snyder.mini_arcade;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/*
Original code from genius who wrote this tutorial: http://www.edu4java.com/en/androidgame/androidgame4.html
Retrofitted for my own personal purposes.
Edited On: 11/26/2018

Art credits: (sprites) OpenGameArt.org Author: Antifarea
 */

public class Sprite {
    //maps direction to animation: 0 up/back, 1 right, 2 down/front, 3 left
    private static final int[] animationMap = {2 ,3, 0, 1};
    private static final int bmpRows = 4;
    private static final int bmpColumns = 3;
    private int x = 0;
    private int y = 0;
    private int xSpeed = 0;
    private int ySpeed = 0;
    private Bitmap bmp;
    private GamePage.GameView gameView;
    private int currentFrame = 0;
    private int height;
    private int width;
    private Rect src = new Rect();
    private Rect dst = new Rect();

    public Sprite(GamePage.GameView gameView, Bitmap bm) {
        this.bmp = bm;
        this.gameView = gameView;
        this.width = bmp.getWidth() / bmpColumns;
        this.height = bmp.getHeight() / bmpRows;
    }

    private void update() {
        if (!(x >= gameView.getWidth() - width - xSpeed) && !(x + xSpeed <= 0)) {
            x = x + (xSpeed / 50);
        }

        if (!(y >= gameView.getHeight() - height - ySpeed) && !(y + ySpeed <= 0)) {
            y = y + (ySpeed / 50);
        }

        currentFrame = ++currentFrame % bmpColumns;
    }

    public void onDraw(Canvas canvas, int xValue, int yValue) {
        this.xSpeed = xValue - 512; //sets range (-512, 512)
        this.ySpeed = yValue - 512;
        update();
        int srcX = currentFrame * width;
        int srcY = getAnimationRow() * height;
        src.set(srcX, srcY, srcX + width, srcY + height);
        dst.set(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    private int getAnimationRow() {
        double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % bmpRows;
        return animationMap[direction];
    }

}
