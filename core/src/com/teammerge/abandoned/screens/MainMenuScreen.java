package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teammerge.abandoned.AbandonedGame;

public class MainMenuScreen implements Screen {

    private final AbandonedGame game;
// TODO: Ortho camera
    private final Stage stage;
    private final SpriteBatch batch;
    BitmapFont h1, h2;
    Label titleLabel;

    TextButton newGameButton, exitButton;
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

//  TODO: Search about viewports
    public MainMenuScreen(final AbandonedGame game) {
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Never Forget

        int row_height = Gdx.graphics.getHeight() / 16;
        int col_width = Gdx.graphics.getWidth() / 16;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TheoVanDoesburg.TTF"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();


//      Creating H1, H2 fonts

        parameter.size = 36;
        h1 = generator.generateFont(parameter);
        parameter.size = 28;
        h2 = generator.generateFont(parameter);
        generator.dispose();

        titleLabel = createLabel("Abandoned", h1);
        titleLabel.setPosition(col_width,Gdx.graphics.getHeight() - row_height * 3);

//        Creating New Game Button
        newGameButton = createTextButton("New Game", h2);
        newGameButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 6);
        newGameButton.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                newGameButton.getLabel().setColor(Color.YELLOW);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                newGameButton.getLabel().setColor(Color.WHITE);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                dispose();
                return false;
            }
        });

//        Creating Exit Button
        exitButton = createTextButton("Exit", h2);
        exitButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 8);
        exitButton.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exitButton.getLabel().setColor(Color.YELLOW);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                exitButton.getLabel().setColor(Color.WHITE);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return false;
            }

        });

        // Telling stage to include following actors in render
        stage.addActor(titleLabel);
        stage.addActor(newGameButton);
        stage.addActor(exitButton);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
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
        h1.dispose();
        h2.dispose();
        stage.dispose();
        batch.dispose();
    }

    private TextButton createTextButton(String text, BitmapFont font) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        TextButton button = new TextButton(text, buttonStyle);
        button.getLabel().setAlignment(Align.left);

        return button;
    }
    private Label createLabel(String text, BitmapFont font) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        return label;
    }
}
