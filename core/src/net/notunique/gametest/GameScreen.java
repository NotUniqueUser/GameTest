package net.notunique.gametest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    final GameTest game;

    private Texture bucketTexture;
    private Texture dropTexture;

    private Rectangle bucket;
    private final Array<Rectangle> raindrops = new Array<Rectangle>();

    private OrthographicCamera camera;
    private final Vector3 pos = new Vector3();
    private float lastRain;

    private static int score = 0;

    public GameScreen(final GameTest game) {
        this.game = game;
        bucketTexture = new Texture(Gdx.files.internal("bucket.png"));
        dropTexture = new Texture(Gdx.files.internal("drop.png"));

        bucket = new Rectangle(
                800 / 2 - 64 / 2,
                20,
                64,
                64
        );

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle rectangle = new Rectangle(
                MathUtils.random(0, 800 - 32),
                400,
                32,
                32
        );
        raindrops.add(rectangle);
        lastRain = TimeUtils.nanoTime();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GOLDENROD);
        camera.update();

        for (Iterator<Rectangle> iterator = raindrops.iterator(); iterator.hasNext(); ) {
            Rectangle raindrop = iterator.next();
            raindrop.y -= 200 * delta * (score * 0.01 + 1);

            if(bucket.overlaps(raindrop)) {
                System.out.println("caught rain! score: " + ++score);
                iterator.remove();
            }

            if(raindrop.y < -32) iterator.remove();
        }

        // draw the bucket
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bucketTexture, bucket.x, bucket.y);
        for(Rectangle raindrop : raindrops) {
            game.batch.draw(dropTexture, raindrop.x, raindrop.y, 32, 32);
        }
        game.font.draw(game.batch, "SCORE: " + score, 30, 430);
        game.batch.end();

        if(Gdx.input.isTouched()) {
            pos.x = Gdx.input.getX();
            camera.unproject(pos);
            bucket.x = pos.x;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 400 * delta;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 400 * delta;

        bucket.x = MathUtils.clamp(bucket.x, 0, 800 - 64);

        if(TimeUtils.nanoTime() - lastRain > .5e+9) spawnRaindrop();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
