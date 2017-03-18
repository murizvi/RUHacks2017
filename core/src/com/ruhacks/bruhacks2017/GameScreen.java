package com.ruhacks.bruhacks2017;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;

public class GameScreen extends AbstractScreen  {

    private Player player;
    private BackgroundManager backgroundManager;
    private float[] backgroundColor;
    private float targetX, targetY;
    private ArrayList<Image> smokeList;
    private ArrayList<Float> smokeAlpha;
    private float smokeCounter;
    private Pixmap squarePixmap;
    private Texture squareTexture;
    private int lighteningCounter;
    private Image lighteningImage;
    private float lighteningAlpha;
    private RainManager rainManager;

    public GameScreen(final MainActivity game) {
        super(game);

        lighteningAlpha = 0;
        lighteningCounter = 10;
        smokeCounter = 0;
        player = new Player(UNIT_X, UNIT_Y);
        backgroundColor = new float[3];

        rainManager = new RainManager(UNIT_X, UNIT_Y);
        backgroundManager = new BackgroundManager(UNIT_X, UNIT_Y);
        backgroundManager.setMountainColor(Themes.BLUE_NIGHT.getColors());
        setBackgroundColor(Themes.BLUE_NIGHT);

        squarePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        squarePixmap.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        squarePixmap.fillRectangle(0, 0, 1, 1);
        squareTexture = new Texture(squarePixmap);
        smokeList = new ArrayList<Image>();
        smokeAlpha = new ArrayList<Float>();

        lighteningImage = new Image(squareTexture);
        lighteningImage.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        lighteningImage.setColor(1.0f, 1.0f, 1.0f, lighteningAlpha);

        //this.addActor(platformObject);
        this.addActor(lighteningImage);
        this.addActor(backgroundManager);
        this.addActor(player);
        this.addActor(rainManager);
    }

    public void setBackgroundColor(Themes theme) {
        float[] color = theme.getBackgroundColor();
        backgroundColor[0] = color[0];
        backgroundColor[1] = color[1];
        backgroundColor[2] = color[2];
    }

    @Override
    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        // set initial sizes and positions
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        targetX = screenX - player.getWidth() / 2f;
        targetY = SCREEN_HEIGHT - screenY - player.getHeight() / 2f;
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public void update() {
        rainManager.update();
        lighteningCounter--;
        if (lighteningCounter == 10) {
            lighteningImage.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            lighteningAlpha = 1.0f;
        } else if (lighteningCounter == 0) {
            lighteningImage.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            lighteningAlpha = 1.0f;
            lighteningCounter = (int)(Math.random() * 100 + 150);
        }

        if (lighteningAlpha > 0) {
            lighteningAlpha -= 0.05f;
            if (lighteningAlpha < 0) {
                lighteningAlpha = 0;
            }
            lighteningImage.setColor(1.0f, 1.0f, 1.0f, lighteningAlpha);
        }

        if (smokeCounter <= 0) {
            smokeCounter = 20;
            Image smoke = new Image(squareTexture);
            smoke.setSize(20f, 20f);
            if (player.getScaleX() > 0) {
                smoke.setX(player.getX() + player.getWidth() / 2f - (player.getWidth() / 2f + 9f - 20f / 2f) * (float) Math.cos(Math.toRadians(player.getRotation())) + (player.getHeight() / 2f + 20f) * (float) Math.sin(Math.toRadians(player.getRotation())));
            } else {
                smoke.setX(player.getX() + player.getWidth() / 2f + ((player.getWidth() / 2f + 9f - 20f / 2f) * (float) Math.cos(Math.toRadians(player.getRotation())) + (player.getHeight() / 2f + 20f) * (float) Math.sin(Math.toRadians(player.getRotation()))));
            }
            smoke.setY(player.getY() + player.getHeight() / 2f - (player.getHeight() / 2f + 20f) * (float)Math.cos(Math.toRadians(player.getRotation())) + (player.getWidth() / 2f + 9f - 20f / 2f) * (float)Math.sin(Math.toRadians(player.getRotation())));
            smokeList.add(smoke);
            this.addActor(smoke);
            smokeAlpha.add(0f);
        }

        for (int i = 0; i < smokeAlpha.size(); ++i) {
            Float a = smokeAlpha.get(i) - 0.05f;
            smokeAlpha.remove(i);
            smokeAlpha.add(i, a);
            smokeList.get(i).setColor(0.2f, 0.2f, 0.2f, a);
        }

        for (Image smoke : smokeList) {
            float size = smoke.getWidth() - 0.75f;
            smoke.setSize(size, size);
            smoke.setOrigin(size / 2f, size / 2f);
            smoke.setX(smoke.getX() + 0.005f);
            smoke.setY(smoke.getY() + 0.005f);
            smoke.rotateBy((float)Math.toDegrees(Math.PI / 2f + Math.PI / 2f * Math.random()));
        }

        if (!smokeList.isEmpty() && smokeList.get(0).getWidth() < 0.51f) {
            smokeList.get(0).remove();
            smokeList.remove(0);
            smokeAlpha.remove(0);
        }

        float xMove = (targetX - player.getX()) / 15f;
        float yMove = (targetY - player.getY()) / 15f;
        float speed = (float)Math.sqrt(xMove * xMove + yMove * yMove);
        smokeCounter -= speed;
        System.out.println(speed);

        backgroundManager.update((player.getX() - SCREEN_WIDTH / 2f) / SCREEN_WIDTH, (player.getY() - SCREEN_HEIGHT / 2f) / SCREEN_HEIGHT);

        if (xMove < 0 && player.getScaleX() > 0) {
            player.setScaleX(-1);
        } else if (xMove > 0 && player.getScaleX() < 0) {
            player.setScaleX(1);
        }

        float rotateAmount = Math.min(1f, (Math.abs(xMove) + Math.abs(yMove)) / 45f);
        float rotation = (float) Math.toDegrees(Math.PI / -2f + Math.atan(yMove / (xMove))) + ((xMove < 0) ? 180f : 0);
        player.rotateBy((rotation - player.getRotation()) / (1f / rotateAmount));

        player.setX(player.getX() + xMove);
        player.setY(player.getY() + yMove);

        player.update(speed);

        /*
        if (player.getY() <= platformObject.getY() + platformObject.getHeight() &&
                player.getX() + player.getWidth() > platformObject.getX() &&
                player.getX() < platformObject.getX() + platformObject.getWidth()) {
            player.setY(platformObject.getY() + platformObject.getHeight());
            player.setSpeed(-0.8f * player.getSpeed());
            if (Math.abs(player.getSpeed()) < 0.11f * UNIT) {
                player.stopFall();
            }
        }
        */
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.act(delta);
        this.draw();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
        backgroundManager.dispose();
        squareTexture.dispose();
        squarePixmap.dispose();
    }
}
