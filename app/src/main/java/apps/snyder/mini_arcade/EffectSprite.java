package apps.snyder.mini_arcade;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class EffectSprite {
    private float x;
    private float y;
    private Bitmap bmp;
    private int life = 4;
    private List<EffectSprite> sprites;

    public EffectSprite(List<EffectSprite> effects, GamePage.GameView view, float x, float y, Bitmap bmp) {
        this.x = Math.min(Math.max(x - bmp.getWidth() / 2, 0),
                view.getWidth() - bmp.getWidth());
        this.y = Math.min(Math.max(y - bmp.getHeight() / 2, 0),
                view.getHeight() - bmp.getHeight());
        this.bmp = bmp;
        this.sprites = effects;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x, y, null);
    }

    private void update() {
        if (--life < 1) {
            sprites.remove(this);
        }
    }
}
