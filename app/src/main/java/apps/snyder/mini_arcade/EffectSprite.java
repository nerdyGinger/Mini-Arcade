package apps.snyder.mini_arcade;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

/*
Class for aura effect sprite that is activated on joystick button press. Received a lot of help from the
"Temporary Sprite" section of this tutorial: http://www.edu4java.com/en/androidgame/androidgame4.html
Updated On: 12/3/18
 */

public class EffectSprite {
    private int x;
    private int y;
    private Bitmap bmp;
    private int width;
    private int height;
    private int life = 4;
    private int currentFrame = 0;
    private int bmpColumns = 4;
    private int bmpRows = 4;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private List<EffectSprite> sprites;

    public EffectSprite(List<EffectSprite> effects, GameView view, float x, float y, Bitmap bmp) {
        //public constructor, sets coordinates to character sprite position (parameters x/y)
        this.bmp = bmp;
        this.width = bmp.getWidth() / bmpColumns;
        this.height = bmp.getHeight() / bmpRows;
        this.x = (int) x - width / 2;
        this.y = (int) y - height / 2;
        this.sprites = effects;
    }

    public void onDraw(Canvas canvas) {
        //public method to redraw effect sprite
        update();
        int srcX = currentFrame * width;
        int srcY = (4 - life) * height;
        src.set(srcX, srcY, srcX + width, srcY + height);
        dst.set(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    private void update() {
        //checks life of sprite; it is only onscreen for 4 GameLoopThread cycles
        if (--life < 1) {
            //if it's dead, get rid of it
            sprites.remove(this);
        } else {
            //otherwise, move it to the next stage of its life
            currentFrame = ++currentFrame % bmpColumns;
        }
    }
}
