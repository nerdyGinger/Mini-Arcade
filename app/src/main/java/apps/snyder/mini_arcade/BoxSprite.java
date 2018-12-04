package apps.snyder.mini_arcade;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
This class allows a box to be draw to the screen and handles the blowing up animation.
Updated on: 12/4/18
 */

public class BoxSprite {
    private int x;
    private int y;
    private Bitmap bmp;
    private int width;
    private int height;
    private int explodeLife = 4;
    private boolean explode = false;
    private int currentFrame = 0;
    private int bmpColumns = 4;
    private int bmpRows = 1;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private Random random = new Random();
    private List<BoxSprite> boxes = new ArrayList<>();

    public BoxSprite(List<BoxSprite> currentBoxes, GameView view, Bitmap bmp) {
        this.boxes = currentBoxes;
        this.bmp = bmp;
        this.width = bmp.getWidth() / bmpColumns;
        this.height = bmp.getHeight() / bmpRows;
        this.x = random.nextInt(view.getWidth() - width / 2);
        this.y = random.nextInt(view.getHeight() - height / 2);
    }

    public boolean isCollision(int spriteX, int spriteY) {
        boolean collision =  (spriteX > x && spriteX < x + width && spriteY > y && spriteY < y + height);
        if (collision) {
            this.explode = true;
        }
        return collision;
    }

    public void onDraw(Canvas canvas) {
        update();
        int srcX = currentFrame * width;
        int srcY = (4 - explodeLife) * height;
        src.set(srcX, srcY, srcX + width, srcY + height);
        dst.set(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    private void update() {
        if (explode) {
            //we only need to update the image if we're exploding
            if (--explodeLife < 1) {
                //remove it if it's nuthin' but dust
                boxes.remove(this);
            }
            else {
                //otherwise, continue exploding!
                currentFrame = ++currentFrame % bmpColumns;
            }
        }
    }
}
