package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.kotcrab.vis.ui.VisUI;
import com.teammerge.abandoned.AbandonedGame;

public class MainMenuScreen implements Screen {

    private final AbandonedGame game;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final SpriteBatch batch;

    public static final int row_height = Gdx.graphics.getHeight() / 16;
    public static final int col_width = Gdx.graphics.getWidth() / 16;
    BitmapFont h1, h2;
    Label titleLabel;

    TextButton newGameButton, exitButton;

//  TODO: Search about viewports
    public MainMenuScreen(final AbandonedGame game) {
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        Gdx.input.setInputProcessor(stage); // Never Forget
        VisUI.load();


//      Creating H1, H2 fonts
        h1 = generateFont("fonts/TheoVanDoesburg.TTF", 60);
        h2 = generateFont("fonts/TheoVanDoesburg.TTF", 28);

        titleLabel = new Label("Abandoned", new Label.LabelStyle(h1,Color.WHITE));
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
                game.setScreen(new GameScreen(game, 31, 31));
                dispose();
                return false;
            }
        });

//        Creating Exit Button
        exitButton = createTextButton("Exit", h2);
        exitButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 7);
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

//        Telling stage to include following actors in render
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
        camera.update();
        batch.setProjectionMatrix(camera.combined);
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
    private BitmapFont generateFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.spaceX = 4;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
