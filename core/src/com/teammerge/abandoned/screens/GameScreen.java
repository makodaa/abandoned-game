package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.entities.Player;

import java.util.Random;

public class GameScreen implements Screen {
    final AbandonedGame game;
    OrthographicCamera camera;
    long lastDecreaseTime;
    Label conditionLabel, saturationLabel, hydrationLabel, energyLabel;
    BitmapFont font;
    Stage stage;
    SpriteBatch batch;
    Player player;
    Random random;


    public GameScreen(final AbandonedGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        random = new Random();
        font = new BitmapFont();
        player = new Player();


    }

    private Label createLabel(String text, BitmapFont font, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label label = new Label(text, labelStyle);
        label.setColor(color);
        return label;
    }
    private void updateStatistics() {
        player.saturation -= random.nextInt(0,1);
        player.hydration -= random.nextInt(0,1);
        player.energy -= random.nextInt(0,1);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
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
